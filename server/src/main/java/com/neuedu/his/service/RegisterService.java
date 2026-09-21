package com.neuedu.his.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.neuedu.his.common.BusinessException;
import com.neuedu.his.entity.*;
import com.neuedu.his.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 挂号与收费服务
 */
@Service
public class RegisterService {

    @Autowired private RegisterMapper registerMapper;
    @Autowired private DepartmentMapper departmentMapper;
    @Autowired private RegistLevelMapper registLevelMapper;
    @Autowired private SettleCategoryMapper settleCategoryMapper;
    @Autowired private SchedulingMapper schedulingMapper;
    @Autowired private MedicalCardMapper medicalCardMapper;
    @Autowired private CustomerMapper customerMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private InvoiceMapper invoiceMapper;
    @Autowired private PatientCostsMapper patientCostsMapper;
    @Autowired private PrescriptionMapper prescriptionMapper;
    @Autowired private PrescriptionDetailMapper prescriptionDetailMapper;
    @Autowired private DrugsMapper drugsMapper;
    @Autowired private FmedItemMapper fmedItemMapper;
    @Autowired private CheckApplyMapper checkApplyMapper;

    private static final DateTimeFormatter CASE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // ==================== 查询 ====================

    /** 挂号级别列表 */
    public List<RegistLevel> listRegistLevels() {
        return registLevelMapper.selectList(new LambdaQueryWrapper<RegistLevel>()
                .eq(RegistLevel::getDelMark, 1)
                .orderByAsc(RegistLevel::getSequenceNo));
    }

    /** 结算类别列表 */
    public List<SettleCategory> listSettleCategories() {
        return settleCategoryMapper.selectList(new LambdaQueryWrapper<SettleCategory>()
                .eq(SettleCategory::getDelMark, 1)
                .orderByAsc(SettleCategory::getSequenceNo));
    }

    /** 科室列表（可选按类型过滤） */
    public List<Department> listDepartments(Integer deptType) {
        return departmentMapper.selectList(new LambdaQueryWrapper<Department>()
                .eq(Department::getDelMark, 1)
                .eq(deptType != null, Department::getDeptType, deptType)
                .orderByAsc(Department::getId));
    }

    /**
     * 查询某科室某午别的出诊医生（基于当日排班）
     */
    public List<Map<String, Object>> listDoctors(Integer deptId, LocalDate date, String noon) {
        LocalDate queryDate = date == null ? LocalDate.now() : date;
        List<Scheduling> scheds = schedulingMapper.selectList(new LambdaQueryWrapper<Scheduling>()
                .eq(Scheduling::getDelMark, 1)
                .eq(deptId != null, Scheduling::getDeptId, deptId)
                .eq(Scheduling::getSchedDate, queryDate)
                .eq(StringUtils.isNotBlank(noon), Scheduling::getNoon, noon)
                .orderByAsc(Scheduling::getId));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Scheduling s : scheds) {
            User doctor = userMapper.selectById(s.getUserId());
            if (doctor == null) {
                continue;
            }
            RegistLevel level = registLevelMapper.selectById(doctor.getRegistLeId());

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("schedulingId", s.getId());
            item.put("userId", doctor.getId());
            item.put("realName", doctor.getRealName());
            item.put("deptId", s.getDeptId());
            item.put("deptName", deptName(s.getDeptId()));
            item.put("noon", s.getNoon());
            item.put("registLeId", doctor.getRegistLeId());
            item.put("registName", level == null ? "普通号" : level.getRegistName());
            item.put("registFee", level == null ? BigDecimal.ZERO : level.getRegistFee());
            item.put("quota", level == null ? 0 : level.getRegistQuota());
            item.put("regNum", s.getRegNum() == null ? 0 : s.getRegNum());
            result.add(item);
        }
        return result;
    }

    /**
     * 挂号记录查询（多条件）
     *
     * @param visitState 看诊状态过滤
     * @param userId     医生过滤（医生站候诊队列用）
     */
    public List<Map<String, Object>> listRegisters(LocalDate date, Integer visitState, Integer userId,
                                                   Integer deptId, String keyword) {
        LambdaQueryWrapper<Register> wrapper = new LambdaQueryWrapper<Register>()
                .eq(date != null, Register::getVisitDate, date == null ? LocalDate.now() : date)
                .eq(visitState != null, Register::getVisitState, visitState)
                .eq(userId != null, Register::getUserId, userId)
                .eq(deptId != null, Register::getDeptId, deptId)
                .and(StringUtils.isNotBlank(keyword), w -> w
                        .like(Register::getRealName, keyword)
                        .or().like(Register::getCaseNumber, keyword))
                .orderByDesc(Register::getRegistTime);

        List<Register> list = registerMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Register r : list) {
            result.add(toRegisterVo(r));
        }
        return result;
    }

    /** 挂号记录 -> 展示对象 */
    private Map<String, Object> toRegisterVo(Register r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId());
        m.put("caseNumber", r.getCaseNumber());
        m.put("realName", r.getRealName());
        m.put("gender", r.getGender());
        m.put("genderText", r.getGender() != null && r.getGender() == 1 ? "男" : "女");
        m.put("age", r.getAge());
        m.put("idnumber", r.getIdnumber());
        m.put("homeAddress", r.getHomeAddress());
        m.put("visitDate", r.getVisitDate());
        m.put("noon", r.getNoon());
        m.put("deptId", r.getDeptId());
        m.put("deptName", deptName(r.getDeptId()));
        m.put("userId", r.getUserId());
        m.put("doctorName", userName(r.getUserId()));
        m.put("registLeId", r.getRegistLeId());
        m.put("registName", registName(r.getRegistLeId()));
        m.put("settleId", r.getSettleId());
        m.put("settleName", settleName(r.getSettleId()));
        m.put("registTime", r.getRegistTime());
        m.put("visitState", r.getVisitState());
        m.put("visitStateText", visitStateText(r.getVisitState()));
        m.put("medicalCardId", r.getMedicalCardId());
        m.put("channel", r.getChannel());
        return m;
    }

    // ==================== 挂号 ====================

    /**
     * 现场挂号
     *
     * @param body 挂号参数（realName/gender/idnumber/age/deptId/userId/registLeId/settleId/noon/registerId/channel）
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> createRegister(Map<String, Object> body) {
        Integer deptId = intOf(body.get("deptId"));
        Integer userId = intOf(body.get("userId"));
        Integer registLeId = intOf(body.get("registLeId"));
        Integer settleId = intOf(body.get("settleId"));
        Integer registerId = intOf(body.get("registerId"));

        if (deptId == null || userId == null || registLeId == null) {
            throw new BusinessException("科室、医生、挂号级别不能为空");
        }

        LocalDate today = LocalDate.now();
        String noon = body.get("noon") == null ? "上午" : body.get("noon").toString();

        // 校验号源
        Scheduling sched = schedulingMapper.selectOne(new LambdaQueryWrapper<Scheduling>()
                .eq(Scheduling::getDelMark, 1)
                .eq(Scheduling::getDeptId, deptId)
                .eq(Scheduling::getUserId, userId)
                .eq(Scheduling::getSchedDate, today)
                .eq(Scheduling::getNoon, noon)
                .last("limit 1"));
        if (sched != null) {
            int quota = registLevelMapper.selectById(registLeId) == null
                    ? 0 : registLevelMapper.selectById(registLeId).getRegistQuota();
            int used = sched.getRegNum() == null ? 0 : sched.getRegNum();
            if (quota > 0 && used >= quota) {
                throw new BusinessException("该医生当前时段号源已挂满");
            }
        }

        Register r = new Register();
        r.setCaseNumber(generateCaseNumber());
        r.setRealName(strOf(body.get("realName")));
        r.setGender(intOf(body.get("gender")));
        r.setIdnumber(strOf(body.get("idnumber")));
        r.setAge(intOf(body.get("age")));
        r.setAgeType("岁");
        r.setHomeAddress(strOf(body.get("homeAddress")));
        r.setVisitDate(today);
        r.setNoon(noon);
        r.setDeptId(deptId);
        r.setUserId(userId);
        r.setRegistLeId(registLeId);
        r.setSettleId(settleId == null ? 1 : settleId);
        r.setIsBook("否");
        r.setRegistTime(LocalDateTime.now());
        r.setRegisterId(registerId == null ? 1 : registerId);
        r.setVisitState(1);  // 已挂号待接诊
        r.setMedicalCardId(strOf(body.get("medicalCardId")));
        r.setChannel(intOf(body.get("channel")) == null ? 1 : intOf(body.get("channel")));
        registerMapper.insert(r);

        // 号源 +1
        if (sched != null) {
            sched.setRegNum((sched.getRegNum() == null ? 0 : sched.getRegNum()) + 1);
            schedulingMapper.updateById(sched);
        }

        // 挂号费直接计入待收费
        RegistLevel level = registLevelMapper.selectById(registLeId);
        if (level != null && level.getRegistFee() != null
                && level.getRegistFee().compareTo(BigDecimal.ZERO) > 0) {
            createCost(r.getId(), 0, 1, "挂号费(" + level.getRegistName() + ")",
                    level.getRegistFee(), BigDecimal.ONE, deptId, registerId);
        }

        return toRegisterVo(registerMapper.selectById(r.getId()));
    }

    /** 退号 */
    public boolean refundRegister(Integer registerId, Integer operId) {
        Register r = registerMapper.selectById(registerId);
        if (r == null) {
            throw new BusinessException("挂号记录不存在");
        }
        if (r.getVisitState() != null && r.getVisitState() == 3) {
            throw new BusinessException("已诊毕的记录不能退号");
        }
        r.setVisitState(4);
        registerMapper.updateById(r);

        // 释放号源
        Scheduling sched = schedulingMapper.selectOne(new LambdaQueryWrapper<Scheduling>()
                .eq(Scheduling::getDelMark, 1)
                .eq(Scheduling::getDeptId, r.getDeptId())
                .eq(Scheduling::getUserId, r.getUserId())
                .eq(Scheduling::getSchedDate, r.getVisitDate())
                .eq(Scheduling::getNoon, r.getNoon())
                .last("limit 1"));
        if (sched != null && sched.getRegNum() != null && sched.getRegNum() > 0) {
            sched.setRegNum(sched.getRegNum() - 1);
            schedulingMapper.updateById(sched);
        }

        // 挂号费退费
        List<PatientCosts> costs = patientCostsMapper.selectList(new LambdaQueryWrapper<PatientCosts>()
                .eq(PatientCosts::getRegistId, registerId)
                .eq(PatientCosts::getItemType, 1)
                .isNull(PatientCosts::getBackId));
        for (PatientCosts c : costs) {
            if ("挂号费".equals(c.getName()) || (c.getName() != null && c.getName().startsWith("挂号费"))) {
                createCost(registerId, 0, 1, c.getName(),
                        c.getPrice().negate(), BigDecimal.ONE, c.getDeptId(), operId);
            }
        }
        return true;
    }

    /** 挂号记录状态 -> 文本 */
    public static String visitStateText(Integer state) {
        if (state == null) {
            return "未知";
        }
        return switch (state) {
            case 1 -> "待接诊";
            case 2 -> "就诊中";
            case 3 -> "已诊毕";
            case 4 -> "已退号";
            default -> "未知";
        };
    }

    // ==================== 收费区 ====================

    /**
     * 待收费清单：按挂号聚合该患者未收费的药品 + 非药品费用
     */
    public Map<String, Object> getChargeList(Integer registId) {
        Register r = registerMapper.selectById(registId);
        if (r == null) {
            throw new BusinessException("挂号记录不存在");
        }

        List<Map<String, Object>> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // 1) 挂号费
        List<PatientCosts> regCosts = patientCostsMapper.selectList(new LambdaQueryWrapper<PatientCosts>()
                .eq(PatientCosts::getRegistId, registId)
                .eq(PatientCosts::getInvoiceId, 0));
        for (PatientCosts c : regCosts) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("costId", c.getId());
            item.put("itemType", c.getItemType());
            item.put("name", c.getName());
            item.put("price", c.getPrice());
            item.put("amount", c.getAmount());
            item.put("sum", c.getPrice().multiply(c.getAmount()));
            items.add(item);
            total = total.add(c.getPrice().multiply(c.getAmount()));
        }

        // 2) 未收费处方（药品）
        List<Prescription> pres = prescriptionMapper.selectList(new LambdaQueryWrapper<Prescription>()
                .eq(Prescription::getRegistId, registId)
                .eq(Prescription::getPrescriptionState, 1)
                .orderByAsc(Prescription::getId));
        for (Prescription p : pres) {
            List<PrescriptionDetail> details = prescriptionDetailMapper.selectList(
                    new LambdaQueryWrapper<PrescriptionDetail>()
                            .eq(PrescriptionDetail::getPrescriptionId, p.getId())
                            .eq(PrescriptionDetail::getState, 2));
            for (PrescriptionDetail d : details) {
                Drugs drug = drugsMapper.selectById(d.getDrugsId());
                if (drug == null) {
                    continue;
                }
                BigDecimal sum = drug.getDrugsPrice().multiply(d.getAmount());
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("itemType", 2);
                item.put("itemId", d.getDrugsId());
                item.put("name", drug.getDrugsName());
                item.put("format", drug.getDrugsFormat());
                item.put("price", drug.getDrugsPrice());
                item.put("amount", d.getAmount());
                item.put("sum", sum);
                item.put("prescriptionId", p.getId());
                items.add(item);
                total = total.add(sum);
            }
        }

        // 3) 未收费医技申请
        List<CheckApply> checks = checkApplyMapper.selectList(new LambdaQueryWrapper<CheckApply>()
                .eq(CheckApply::getRegistId, registId)
                .eq(CheckApply::getState, 0));
        for (CheckApply ca : checks) {
            FmedItem item = fmedItemMapper.selectById(ca.getItemId());
            if (item == null) {
                continue;
            }
            BigDecimal sum = item.getPrice().multiply(BigDecimal.valueOf(ca.getNum() == null ? 1 : ca.getNum()));
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("itemType", 1);
            node.put("itemId", ca.getItemId());
            node.put("checkApplyId", ca.getId());
            node.put("name", ca.getName());
            node.put("format", item.getFormat());
            node.put("price", item.getPrice());
            node.put("amount", ca.getNum() == null ? 1 : ca.getNum());
            node.put("sum", sum);
            node.put("recordType", ca.getRecordType());
            items.add(node);
            total = total.add(sum);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("register", toRegisterVo(r));
        result.put("items", items);
        result.put("total", total);
        return result;
    }

    /**
     * 收费结算：生成发票 + 写费用明细 + 更新处方/申请状态
     */
    public Map<String, Object> pay(Map<String, Object> body) {
        Integer registId = intOf(body.get("registId"));
        Integer feeType = intOf(body.get("feeType"));
        Integer operId = intOf(body.get("operId"));

        if (registId == null) {
            throw new BusinessException("挂号 ID 不能为空");
        }

        Map<String, Object> charge = getChargeList(registId);
        BigDecimal total = (BigDecimal) charge.get("total");
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("该患者没有待收费项目");
        }

        LocalDateTime now = LocalDateTime.now();

        // 1) 生成发票
        Invoice invoice = new Invoice();
        invoice.setInvoiceNum(generateInvoiceNum());
        invoice.setMoney(total);
        invoice.setState(1);
        invoice.setCreationTime(now);
        invoice.setUserId(operId == null ? 1 : operId);
        invoice.setRegistId(registId);
        invoice.setFeeType(feeType == null ? 1 : feeType);
        invoice.setDailyState(0);
        invoiceMapper.insert(invoice);

        // 2) 更新挂号费记录的发票号
        List<PatientCosts> regCosts = patientCostsMapper.selectList(new LambdaQueryWrapper<PatientCosts>()
                .eq(PatientCosts::getRegistId, registId)
                .eq(PatientCosts::getInvoiceId, 0));
        for (PatientCosts c : regCosts) {
            c.setInvoiceId(invoice.getId());
            c.setPayTime(now);
            c.setRegisterId(operId == null ? 1 : operId);
            c.setFeeType(feeType == null ? 1 : feeType);
            patientCostsMapper.updateById(c);
        }

        // 3) 未收费处方 -> 已收费，明细入库
        List<Prescription> pres = prescriptionMapper.selectList(new LambdaQueryWrapper<Prescription>()
                .eq(Prescription::getRegistId, registId)
                .eq(Prescription::getPrescriptionState, 1));
        for (Prescription p : pres) {
            List<PrescriptionDetail> details = prescriptionDetailMapper.selectList(
                    new LambdaQueryWrapper<PrescriptionDetail>()
                            .eq(PrescriptionDetail::getPrescriptionId, p.getId()));
            for (PrescriptionDetail d : details) {
                Drugs drug = drugsMapper.selectById(d.getDrugsId());
                if (drug == null) {
                    continue;
                }
                createCost(registId, invoice.getId(), 2, drug.getDrugsName(),
                        drug.getDrugsPrice(), d.getAmount(), 4, operId);
            }
            p.setPrescriptionState(2);
            prescriptionMapper.updateById(p);
        }

        // 4) 医技申请 -> 已收费
        List<CheckApply> checks = checkApplyMapper.selectList(new LambdaQueryWrapper<CheckApply>()
                .eq(CheckApply::getRegistId, registId)
                .eq(CheckApply::getState, 0));
        for (CheckApply ca : checks) {
            FmedItem item = fmedItemMapper.selectById(ca.getItemId());
            if (item == null) {
                continue;
            }
            createCost(registId, invoice.getId(), 1, ca.getName(), item.getPrice(),
                    BigDecimal.valueOf(ca.getNum() == null ? 1 : ca.getNum()), item.getDeptId(), operId);
            ca.setState(1); // 已收费待执行
            checkApplyMapper.updateById(ca);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("invoiceNum", invoice.getInvoiceNum());
        result.put("money", invoice.getMoney());
        result.put("feeType", invoice.getFeeType());
        result.put("payTime", now);
        return result;
    }

    /** 写一条费用明细 */
    private void createCost(Integer registId, Integer invoiceId, Integer itemType, String name,
                            BigDecimal price, BigDecimal amount, Integer deptId, Integer operId) {
        PatientCosts c = new PatientCosts();
        c.setRegistId(registId);
        c.setInvoiceId(invoiceId == null ? 0 : invoiceId);
        c.setItemId(0);
        c.setItemType(itemType);
        c.setName(name);
        c.setPrice(price);
        c.setAmount(amount);
        c.setDeptId(deptId == null ? 0 : deptId);
        c.setCreatetime(LocalDateTime.now());
        c.setCreateOperId(operId == null ? 1 : operId);
        c.setPayTime(LocalDateTime.now());
        c.setRegisterId(operId == null ? 1 : operId);
        c.setFeeType(1);
        patientCostsMapper.insert(c);
    }

    // ==================== 财务统计 ====================

    /** 营收总览 + 本周趋势 */
    public Map<String, Object> getRevenueSummary(LocalDate date) {
        LocalDate today = date == null ? LocalDate.now() : date;

        List<Invoice> todayInvoices = invoiceMapper.selectList(new LambdaQueryWrapper<Invoice>()
                .eq(Invoice::getState, 1)
                .ge(Invoice::getCreationTime, today.atStartOfDay())
                .lt(Invoice::getCreationTime, today.plusDays(1).atStartOfDay()));
        BigDecimal todayRevenue = todayInvoices.stream()
                .map(Invoice::getMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 挂号量
        Long registCount = registerMapper.selectCount(new LambdaQueryWrapper<Register>()
                .eq(Register::getVisitDate, today));

        // 处方量
        Long presCount = prescriptionMapper.selectCount(new LambdaQueryWrapper<Prescription>()
                .ge(Prescription::getPrescriptionTime, today.atStartOfDay())
                .lt(Prescription::getPrescriptionTime, today.plusDays(1).atStartOfDay()));

        // 本周趋势（近 7 天）
        List<Map<String, Object>> trend = new ArrayList<>();
        String[] weekNames = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            List<Invoice> invoices = invoiceMapper.selectList(new LambdaQueryWrapper<Invoice>()
                    .eq(Invoice::getState, 1)
                    .ge(Invoice::getCreationTime, d.atStartOfDay())
                    .lt(Invoice::getCreationTime, d.plusDays(1).atStartOfDay()));
            BigDecimal money = invoices.stream().map(Invoice::getMoney)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("date", d.toString());
            node.put("label", weekNames[d.getDayOfWeek().getValue() - 1]);
            node.put("money", money);
            trend.add(node);
        }

        // 费用科目分布
        List<Map<String, Object>> byType = new ArrayList<>();
        for (int type = 1; type <= 2; type++) {
            List<PatientCosts> costs = patientCostsMapper.selectList(new LambdaQueryWrapper<PatientCosts>()
                    .eq(PatientCosts::getItemType, type)
                    .gt(PatientCosts::getInvoiceId, 0)
                    .ge(PatientCosts::getPayTime, today.atStartOfDay())
                    .lt(PatientCosts::getPayTime, today.plusDays(1).atStartOfDay()));
            BigDecimal money = costs.stream()
                    .map(c -> c.getPrice().multiply(c.getAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("type", type);
            node.put("name", type == 2 ? "药品费" : "非药品费");
            node.put("money", money);
            byType.add(node);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", today.toString());
        result.put("todayRevenue", todayRevenue);
        result.put("registCount", registCount);
        result.put("prescriptionCount", presCount);
        result.put("trend", trend);
        result.put("byType", byType);
        return result;
    }

    /** 医生工作量统计 */
    public List<Map<String, Object>> getDoctorWorkload(LocalDate date) {
        LocalDate today = date == null ? LocalDate.now() : date;
        List<Register> all = registerMapper.selectList(new LambdaQueryWrapper<Register>()
                .eq(Register::getVisitDate, today));

        Map<Integer, Map<String, Object>> grouped = new LinkedHashMap<>();
        for (Register r : all) {
            Map<String, Object> node = grouped.computeIfAbsent(r.getUserId(), k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("userId", k);
                m.put("doctorName", userName(k));
                m.put("deptName", deptName(r.getDeptId()));
                m.put("registerCount", 0);
                m.put("visitedCount", 0);
                return m;
            });
            node.put("registerCount", (int) node.get("registerCount") + 1);
            if (r.getVisitState() != null && r.getVisitState() == 3) {
                node.put("visitedCount", (int) node.get("visitedCount") + 1);
            }
        }
        return new ArrayList<>(grouped.values());
    }

    // ==================== 工具 ====================

    private String generateCaseNumber() {
        String datePart = LocalDate.now().format(CASE_FMT);
        Long count = registerMapper.selectCount(new LambdaQueryWrapper<Register>()
                .likeRight(Register::getCaseNumber, "MZ" + datePart));
        return String.format("MZ%s%03d", datePart, (count == null ? 0 : count) + 1);
    }

    private String generateInvoiceNum() {
        String datePart = LocalDate.now().format(CASE_FMT);
        Long count = invoiceMapper.selectCount(new LambdaQueryWrapper<Invoice>()
                .likeRight(Invoice::getInvoiceNum, "FP" + datePart));
        return String.format("FP%s%04d", datePart, (count == null ? 0 : count) + 1);
    }

    public String deptName(Integer deptId) {
        if (deptId == null) {
            return "";
        }
        Department d = departmentMapper.selectById(deptId);
        return d == null ? "" : d.getDeptName();
    }

    public String userName(Integer userId) {
        if (userId == null) {
            return "";
        }
        User u = userMapper.selectById(userId);
        return u == null ? "" : u.getRealName();
    }

    public String registName(Integer registLeId) {
        if (registLeId == null) {
            return "";
        }
        RegistLevel l = registLevelMapper.selectById(registLeId);
        return l == null ? "" : l.getRegistName();
    }

    public String settleName(Integer settleId) {
        if (settleId == null) {
            return "";
        }
        SettleCategory s = settleCategoryMapper.selectById(settleId);
        return s == null ? "" : s.getSettleName();
    }

    static Integer intOf(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.valueOf(o.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static String strOf(Object o) {
        return o == null ? null : o.toString();
    }
}
