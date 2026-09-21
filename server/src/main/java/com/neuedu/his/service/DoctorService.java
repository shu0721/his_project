package com.neuedu.his.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.neuedu.his.common.BusinessException;
import com.neuedu.his.entity.*;
import com.neuedu.his.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 医生站服务：候诊队列、病历、处方开立
 */
@Service
public class DoctorService {

    @Autowired private RegisterMapper registerMapper;
    @Autowired private MedicalRecordMapper medicalRecordMapper;
    @Autowired private MedicalDiseaseMapper medicalDiseaseMapper;
    @Autowired private DiseaseMapper diseaseMapper;
    @Autowired private DrugsMapper drugsMapper;
    @Autowired private FmedItemMapper fmedItemMapper;
    @Autowired private PrescriptionMapper prescriptionMapper;
    @Autowired private PrescriptionDetailMapper prescriptionDetailMapper;
    @Autowired private CheckApplyMapper checkApplyMapper;
    @Autowired private RegisterService registerService;

    /**
     * 医生候诊队列
     *
     * @param visitState null 表示全部（待接诊 + 就诊中 + 已诊毕）
     */
    public List<Map<String, Object>> waitingList(Integer doctorId, Integer visitState, LocalDate date) {
        return registerService.listRegisters(date, visitState, doctorId, null, null);
    }

    /** 接诊：把状态改为就诊中 */
    public boolean accept(Integer registId) {
        Register r = registerMapper.selectById(registId);
        if (r == null) {
            throw new BusinessException("挂号记录不存在");
        }
        if (r.getVisitState() != null && r.getVisitState() == 3) {
            throw new BusinessException("该患者已诊毕");
        }
        r.setVisitState(2);
        return registerMapper.updateById(r) > 0;
    }

    // ==================== 病历 ====================

    /** 查询病历（不存在则返回挂号信息骨架） */
    public Map<String, Object> getMedicalRecord(Integer registId) {
        Register r = registerMapper.selectById(registId);
        if (r == null) {
            throw new BusinessException("挂号记录不存在");
        }
        MedicalRecord mr = medicalRecordMapper.selectOne(new LambdaQueryWrapper<MedicalRecord>()
                .eq(MedicalRecord::getRegistId, registId)
                .last("limit 1"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("register", registerService.listRegisters(r.getVisitDate(), null, null, null, null)
                .stream().filter(m -> registId.equals(m.get("id"))).findFirst().orElse(new LinkedHashMap<>()));
        result.put("record", mr);

        // 关联诊断
        if (mr != null) {
            List<MedicalDisease> mds = medicalDiseaseMapper.selectList(new LambdaQueryWrapper<MedicalDisease>()
                    .eq(MedicalDisease::getMedicalId, mr.getId()));
            List<Map<String, Object>> diseases = new ArrayList<>();
            for (MedicalDisease md : mds) {
                Disease d = diseaseMapper.selectById(md.getDiseaseId());
                if (d != null) {
                    Map<String, Object> node = new LinkedHashMap<>();
                    node.put("medicalDiseaseId", md.getId());
                    node.put("diseaseId", d.getId());
                    node.put("diseaseCode", d.getDiseaseCode());
                    node.put("diseaseName", d.getDiseaseName());
                    node.put("diagnosisType", md.getDiagnosisType());
                    diseases.add(node);
                }
            }
            result.put("diseases", diseases);
        } else {
            result.put("diseases", List.of());
        }
        return result;
    }

    /** 保存病历（暂存 / 提交 / 诊毕），同时刷新诊断关联 */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveMedicalRecord(Map<String, Object> body) {
        Integer registId = RegisterService.intOf(body.get("registId"));
        if (registId == null) {
            throw new BusinessException("挂号 ID 不能为空");
        }
        Register r = registerMapper.selectById(registId);
        if (r == null) {
            throw new BusinessException("挂号记录不存在");
        }

        Integer caseState = RegisterService.intOf(body.get("caseState"));
        if (caseState == null) {
            caseState = 1;
        }

        MedicalRecord mr = medicalRecordMapper.selectOne(new LambdaQueryWrapper<MedicalRecord>()
                .eq(MedicalRecord::getRegistId, registId)
                .last("limit 1"));
        boolean isNew = mr == null;
        if (isNew) {
            mr = new MedicalRecord();
            mr.setRegistId(registId);
            mr.setCaseNumber(r.getCaseNumber());
        }

        mr.setReadme(str(body.get("readme")));
        mr.setPresent(str(body.get("present")));
        mr.setPresentTreat(str(body.get("presentTreat")));
        mr.setHistory(str(body.get("history")));
        mr.setAllergy(str(body.get("allergy")));
        mr.setPhysique(str(body.get("physique")));
        mr.setProposal(str(body.get("proposal")));
        mr.setCareful(str(body.get("careful")));
        mr.setCheckResult(str(body.get("checkResult")));
        mr.setDiagnosis(str(body.get("diagnosis")));
        mr.setHandling(str(body.get("handling")));
        mr.setCaseState(caseState);

        if (isNew) {
            medicalRecordMapper.insert(mr);
        } else {
            medicalRecordMapper.updateById(mr);
        }

        // 刷新诊断关联
        Object diseaseIds = body.get("diseaseIds");
        if (diseaseIds instanceof List<?> list) {
            medicalDiseaseMapper.delete(new LambdaQueryWrapper<MedicalDisease>()
                    .eq(MedicalDisease::getRegistId, registId));
            Integer doctorId = RegisterService.intOf(body.get("doctorId"));
            for (Object o : list) {
                Integer diseaseId = RegisterService.intOf(o);
                if (diseaseId == null) {
                    continue;
                }
                MedicalDisease md = new MedicalDisease();
                md.setMedicalId(mr.getId());
                md.setRegistId(registId);
                md.setDiseaseId(diseaseId);
                md.setDiagnosisType(1);
                md.setDiagnosisDoctorId(doctorId == null ? 1 : doctorId);
                md.setDiagnosisTime(LocalDateTime.now());
                medicalDiseaseMapper.insert(md);
            }
        }

        // 诊毕 -> 更新挂号状态
        if (caseState == 3) {
            r.setVisitState(3);
            registerMapper.updateById(r);
        } else if (r.getVisitState() != null && r.getVisitState() == 1) {
            r.setVisitState(2);
            registerMapper.updateById(r);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recordId", mr.getId());
        result.put("caseState", caseState);
        return result;
    }

    // ==================== 药品 / 项目检索 ====================

    /** 药品检索（按名称 / 助记码） */
    public List<Drugs> searchDrugs(String keyword, String type) {
        LambdaQueryWrapper<Drugs> wrapper = new LambdaQueryWrapper<Drugs>()
                .eq(Drugs::getDelMark, 1)
                .and(StringUtils.isNotBlank(keyword), w -> w
                        .like(Drugs::getDrugsName, keyword)
                        .or().like(Drugs::getMnemonicCode, keyword.toUpperCase()))
                .orderByAsc(Drugs::getId)
                .last("limit 50");
        return drugsMapper.selectList(wrapper);
    }

    /** 常用药品（默认返回前 N 条） */
    public List<Drugs> commonDrugs() {
        return drugsMapper.selectList(new LambdaQueryWrapper<Drugs>()
                .eq(Drugs::getDelMark, 1)
                .orderByAsc(Drugs::getId)
                .last("limit 10"));
    }

    /** 医技项目检索 */
    public List<FmedItem> searchFmedItems(String keyword, Integer recordType) {
        return fmedItemMapper.selectList(new LambdaQueryWrapper<FmedItem>()
                .eq(FmedItem::getDelMark, 1)
                .eq(recordType != null, FmedItem::getRecordType, recordType)
                .and(StringUtils.isNotBlank(keyword), w -> w
                        .like(FmedItem::getItemName, keyword)
                        .or().like(FmedItem::getMnemonicCode, keyword == null ? "" : keyword.toUpperCase()))
                .orderByAsc(FmedItem::getId)
                .last("limit 50"));
    }

    /** 诊断目录检索 */
    public List<Disease> searchDiseases(String keyword) {
        return diseaseMapper.selectList(new LambdaQueryWrapper<Disease>()
                .eq(Disease::getDelMark, 1)
                .and(StringUtils.isNotBlank(keyword), w -> w
                        .like(Disease::getDiseaseName, keyword)
                        .or().like(Disease::getDiseaseCode, keyword))
                .orderByAsc(Disease::getId)
                .last("limit 50"));
    }

    // ==================== 处方 ====================

    /**
     * 查询某次挂号的处方列表（含明细）
     */
    public List<Map<String, Object>> listPrescriptions(Integer registId) {
        List<Prescription> list = prescriptionMapper.selectList(new LambdaQueryWrapper<Prescription>()
                .eq(registId != null, Prescription::getRegistId, registId)
                .orderByDesc(Prescription::getId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Prescription p : list) {
            result.add(toPrescriptionVo(p));
        }
        return result;
    }

    /** 处方详情 */
    public Map<String, Object> getPrescription(Integer prescriptionId) {
        Prescription p = prescriptionMapper.selectById(prescriptionId);
        if (p == null) {
            throw new BusinessException("处方不存在");
        }
        return toPrescriptionVo(p);
    }

    private Map<String, Object> toPrescriptionVo(Prescription p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("registId", p.getRegistId());
        m.put("medicalId", p.getMedicalId());
        m.put("userId", p.getUserId());
        m.put("doctorName", registerService.userName(p.getUserId()));
        m.put("prescriptionName", p.getPrescriptionName());
        m.put("prescriptionTime", p.getPrescriptionTime());
        m.put("prescriptionState", p.getPrescriptionState());
        m.put("prescriptionStateText", prescriptionStateText(p.getPrescriptionState()));
        m.put("totalAmount", p.getTotalAmount());

        Register r = registerMapper.selectById(p.getRegistId());
        if (r != null) {
            m.put("caseNumber", r.getCaseNumber());
            m.put("realName", r.getRealName());
            m.put("gender", r.getGender());
            m.put("genderText", r.getGender() != null && r.getGender() == 1 ? "男" : "女");
            m.put("age", r.getAge());
            m.put("deptName", registerService.deptName(r.getDeptId()));
        }

        List<PrescriptionDetail> details = prescriptionDetailMapper.selectList(
                new LambdaQueryWrapper<PrescriptionDetail>()
                        .eq(PrescriptionDetail::getPrescriptionId, p.getId()));
        List<Map<String, Object>> items = new ArrayList<>();
        for (PrescriptionDetail d : details) {
            Drugs drug = drugsMapper.selectById(d.getDrugsId());
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("detailId", d.getId());
            node.put("drugsId", d.getDrugsId());
            node.put("state", d.getState());
            if (drug != null) {
                node.put("drugsName", drug.getDrugsName());
                node.put("drugsFormat", drug.getDrugsFormat());
                node.put("drugsUnit", drug.getDrugsUnit());
                node.put("price", drug.getDrugsPrice());
                node.put("stock", drug.getStock());
            }
            node.put("drugsUsage", d.getDrugsUsage());
            node.put("dosage", d.getDosage());
            node.put("frequency", d.getFrequency());
            node.put("amount", d.getAmount());
            items.add(node);
        }
        m.put("items", items);
        m.put("itemCount", items.size());
        return m;
    }

    public static String prescriptionStateText(Integer state) {
        if (state == null) {
            return "未知";
        }
        return switch (state) {
            case 1 -> "待收费";
            case 2 -> "待发药";
            case 3 -> "已发药";
            default -> "未知";
        };
    }

    /**
     * 开立成药处方
     *
     * @param body registId / doctorId / medicalId / items[{drugsId,drugsUsage,dosage,frequency,amount}]
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createPrescription(Map<String, Object> body) {
        Integer registId = RegisterService.intOf(body.get("registId"));
        Integer doctorId = RegisterService.intOf(body.get("doctorId"));
        if (registId == null) {
            throw new BusinessException("挂号 ID 不能为空");
        }
        Object itemsObj = body.get("items");
        if (!(itemsObj instanceof List<?> items) || items.isEmpty()) {
            throw new BusinessException("请至少添加一种药品");
        }

        Register r = registerMapper.selectById(registId);
        if (r == null) {
            throw new BusinessException("挂号记录不存在");
        }

        // 关联病历
        MedicalRecord mr = medicalRecordMapper.selectOne(new LambdaQueryWrapper<MedicalRecord>()
                .eq(MedicalRecord::getRegistId, registId)
                .last("limit 1"));
        if (mr == null) {
            mr = new MedicalRecord();
            mr.setRegistId(registId);
            mr.setCaseNumber(r.getCaseNumber());
            mr.setCaseState(1);
            medicalRecordMapper.insert(mr);
        }

        BigDecimal total = BigDecimal.ZERO;
        List<Map<String, Object>> detailNodes = new ArrayList<>();

        for (Object o : items) {
            if (!(o instanceof Map<?, ?> item)) {
                continue;
            }
            Integer drugsId = RegisterService.intOf(item.get("drugsId"));
            BigDecimal amount = decimalOf(item.get("amount"));
            if (drugsId == null || amount == null) {
                continue;
            }
            Drugs drug = drugsMapper.selectById(drugsId);
            if (drug == null) {
                continue;
            }
            // 库存校验
            if (drug.getStock() != null && drug.getStock() < amount.intValue()) {
                throw new BusinessException("【" + drug.getDrugsName() + "】库存不足，当前库存 "
                        + drug.getStock() + drug.getDrugsUnit());
            }
            total = total.add(drug.getDrugsPrice().multiply(amount));

            Map<String, Object> node = new LinkedHashMap<>();
            node.put("drugsId", drugsId);
            node.put("drugsName", drug.getDrugsName());
            node.put("drugsUsage", item.get("drugsUsage"));
            node.put("dosage", item.get("dosage"));
            node.put("frequency", item.get("frequency"));
            node.put("amount", amount);
            node.put("price", drug.getDrugsPrice());
            node.put("sum", drug.getDrugsPrice().multiply(amount));
            detailNodes.add(node);
        }

        if (detailNodes.isEmpty()) {
            throw new BusinessException("处方中没有有效药品");
        }

        Prescription p = new Prescription();
        p.setMedicalId(mr.getId());
        p.setRegistId(registId);
        p.setUserId(doctorId == null ? 1 : doctorId);
        p.setPrescriptionName(str(body.get("prescriptionName")) == null
                ? "成药处方" : str(body.get("prescriptionName")));
        p.setPrescriptionTime(LocalDateTime.now());
        p.setPrescriptionState(1);
        p.setTotalAmount(total);
        prescriptionMapper.insert(p);

        for (Map<String, Object> node : detailNodes) {
            PrescriptionDetail d = new PrescriptionDetail();
            d.setPrescriptionId(p.getId());
            d.setDrugsId((Integer) node.get("drugsId"));
            d.setDrugsUsage(str(node.get("drugsUsage")));
            d.setDosage(str(node.get("dosage")));
            d.setFrequency(str(node.get("frequency")));
            d.setAmount((BigDecimal) node.get("amount"));
            d.setState(2);
            prescriptionDetailMapper.insert(d);
        }

        Map<String, Object> result = toPrescriptionVo(p);
        result.put("detailNodes", detailNodes);
        return result;
    }

    /** 作废处方 */
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePrescription(Integer prescriptionId) {
        Prescription p = prescriptionMapper.selectById(prescriptionId);
        if (p == null) {
            throw new BusinessException("处方不存在");
        }
        if (p.getPrescriptionState() != null && p.getPrescriptionState() == 3) {
            throw new BusinessException("已发药的处方不能作废");
        }
        prescriptionDetailMapper.delete(new LambdaQueryWrapper<PrescriptionDetail>()
                .eq(PrescriptionDetail::getPrescriptionId, prescriptionId));
        prescriptionMapper.deleteById(prescriptionId);
        return true;
    }

    // ==================== 医技申请 ====================

    /** 医生开立检查 / 检验 / 处置申请 */
    @Transactional(rollbackFor = Exception.class)
    public List<CheckApply> createCheckApply(Map<String, Object> body) {
        Integer registId = RegisterService.intOf(body.get("registId"));
        Integer doctorId = RegisterService.intOf(body.get("doctorId"));
        if (registId == null) {
            throw new BusinessException("挂号 ID 不能为空");
        }
        Object itemsObj = body.get("items");
        if (!(itemsObj instanceof List<?> items) || items.isEmpty()) {
            throw new BusinessException("请至少选择一个项目");
        }
        Register r = registerMapper.selectById(registId);
        if (r == null) {
            throw new BusinessException("挂号记录不存在");
        }

        MedicalRecord mr = medicalRecordMapper.selectOne(new LambdaQueryWrapper<MedicalRecord>()
                .eq(MedicalRecord::getRegistId, registId)
                .last("limit 1"));

        List<CheckApply> created = new ArrayList<>();
        for (Object o : items) {
            if (!(o instanceof Map<?, ?> item)) {
                continue;
            }
            Integer itemId = RegisterService.intOf(item.get("itemId"));
            if (itemId == null) {
                continue;
            }
            FmedItem fmed = fmedItemMapper.selectById(itemId);
            if (fmed == null) {
                continue;
            }
            CheckApply ca = new CheckApply();
            ca.setMedicalId(mr == null ? 0 : mr.getId());
            ca.setRegistId(registId);
            ca.setItemId(itemId);
            ca.setName(fmed.getItemName());
            ca.setObjective(str(item.get("objective")));
            ca.setPosition(str(item.get("position")));
            ca.setIsUrgent(RegisterService.intOf(item.get("isUrgent")) == null
                    ? 0 : RegisterService.intOf(item.get("isUrgent")));
            ca.setNum(item.get("num") == null ? 1 : RegisterService.intOf(item.get("num")));
            ca.setCreationTime(LocalDateTime.now());
            ca.setDoctorId(doctorId == null ? 1 : doctorId);
            ca.setState(0);
            ca.setRecordType(fmed.getRecordType() == null ? 1 : fmed.getRecordType());
            checkApplyMapper.insert(ca);
            created.add(ca);
        }
        return created;
    }

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }

    private static BigDecimal decimalOf(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof BigDecimal b) {
            return b;
        }
        if (o instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        try {
            return new BigDecimal(o.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
