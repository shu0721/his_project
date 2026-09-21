package com.neuedu.his.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.neuedu.his.common.BusinessException;
import com.neuedu.his.entity.Drugs;
import com.neuedu.his.entity.Prescription;
import com.neuedu.his.entity.PrescriptionDetail;
import com.neuedu.his.mapper.DrugsMapper;
import com.neuedu.his.mapper.PrescriptionDetailMapper;
import com.neuedu.his.mapper.PrescriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 药房服务：发药、药品目录维护、库存与效期预警
 */
@Service
public class PharmacyService {

    @Autowired private PrescriptionMapper prescriptionMapper;
    @Autowired private PrescriptionDetailMapper prescriptionDetailMapper;
    @Autowired private DrugsMapper drugsMapper;
    @Autowired private DoctorService doctorService;

    /**
     * 待发药处方列表
     *
     * @param state 处方状态：2 待发药 / 3 已发药；null 表示全部
     */
    public List<Map<String, Object>> listPrescriptions(Integer state, String keyword) {
        LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<Prescription>()
                .eq(state != null, Prescription::getPrescriptionState, state)
                .orderByDesc(Prescription::getPrescriptionTime);

        List<Prescription> list = prescriptionMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Prescription p : list) {
            Map<String, Object> vo = doctorService.getPrescription(p.getId());
            if (StringUtils.isNotBlank(keyword)) {
                String name = String.valueOf(vo.getOrDefault("realName", ""));
                String no = String.valueOf(vo.getOrDefault("caseNumber", ""));
                if (!name.contains(keyword) && !no.contains(keyword)) {
                    continue;
                }
            }
            result.add(vo);
        }
        return result;
    }

    /**
     * 确认发药：扣减库存 + 处方与明细状态更新
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> dispense(Integer prescriptionId) {
        Prescription p = prescriptionMapper.selectById(prescriptionId);
        if (p == null) {
            throw new BusinessException("处方不存在");
        }
        if (p.getPrescriptionState() != null && p.getPrescriptionState() == 3) {
            throw new BusinessException("该处方已发药，请勿重复操作");
        }
        if (p.getPrescriptionState() != null && p.getPrescriptionState() == 1) {
            throw new BusinessException("该处方尚未收费，不能发药");
        }

        List<PrescriptionDetail> details = prescriptionDetailMapper.selectList(
                new LambdaQueryWrapper<PrescriptionDetail>()
                        .eq(PrescriptionDetail::getPrescriptionId, prescriptionId));

        List<Map<String, Object>> dispensed = new ArrayList<>();
        for (PrescriptionDetail d : details) {
            Drugs drug = drugsMapper.selectById(d.getDrugsId());
            if (drug == null) {
                continue;
            }
            int need = d.getAmount() == null ? 0 : d.getAmount().intValue();
            int stock = drug.getStock() == null ? 0 : drug.getStock();
            if (stock < need) {
                throw new BusinessException("【" + drug.getDrugsName() + "】库存不足（现有 "
                        + stock + "，需 " + need + "），请先补货");
            }
            drug.setStock(stock - need);
            drug.setLastUpdateDate(LocalDateTime.now());
            drugsMapper.updateById(drug);

            d.setState(3);
            prescriptionDetailMapper.updateById(d);

            Map<String, Object> node = new LinkedHashMap<>();
            node.put("drugsName", drug.getDrugsName());
            node.put("amount", need);
            node.put("remainStock", drug.getStock());
            dispensed.add(node);
        }

        p.setPrescriptionState(3);
        prescriptionMapper.updateById(p);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("prescriptionId", prescriptionId);
        result.put("prescriptionState", 3);
        result.put("dispenseTime", LocalDateTime.now());
        result.put("items", dispensed);
        return result;
    }

    // ==================== 药品目录 ====================

    /** 药品列表 */
    public List<Drugs> listDrugs(String keyword, Integer typeId) {
        return drugsMapper.selectList(new LambdaQueryWrapper<Drugs>()
                .eq(Drugs::getDelMark, 1)
                .eq(typeId != null, Drugs::getDrugsTypeId, typeId)
                .and(StringUtils.isNotBlank(keyword), w -> w
                        .like(Drugs::getDrugsName, keyword)
                        .or().like(Drugs::getMnemonicCode, keyword == null ? "" : keyword.toUpperCase()))
                .orderByAsc(Drugs::getId));
    }

    /** 药品详情 */
    public Drugs getDrug(Integer id) {
        Drugs d = drugsMapper.selectById(id);
        if (d == null) {
            throw new BusinessException("药品不存在");
        }
        return d;
    }

    public Drugs saveDrug(Drugs drug) {
        if (StringUtils.isBlank(drug.getDrugsName())) {
            throw new BusinessException("药品名称不能为空");
        }
        if (drug.getId() == null) {
            drug.setDelMark(1);
            drug.setCreationDate(LocalDateTime.now());
            drug.setStock(drug.getStock() == null ? 0 : drug.getStock());
            drug.setWarnStock(drug.getWarnStock() == null ? 20 : drug.getWarnStock());
            drugsMapper.insert(drug);
        } else {
            drug.setLastUpdateDate(LocalDateTime.now());
            drugsMapper.updateById(drug);
        }
        return drug;
    }

    public boolean deleteDrug(Integer id) {
        Drugs d = new Drugs();
        d.setId(id);
        d.setDelMark(0);
        return drugsMapper.updateById(d) > 0;
    }

    /** 入库（补货） */
    public Drugs addStock(Integer id, Integer count) {
        if (count == null || count <= 0) {
            throw new BusinessException("入库数量必须大于 0");
        }
        Drugs d = getDrug(id);
        d.setStock((d.getStock() == null ? 0 : d.getStock()) + count);
        d.setLastUpdateDate(LocalDateTime.now());
        drugsMapper.updateById(d);
        return d;
    }

    /**
     * 库存与效期预警
     *
     * @param days 距有效期预警天数（默认 90 天）
     */
    public Map<String, Object> getWarnings(Integer days) {
        int warnDays = days == null ? 90 : days;
        List<Drugs> all = drugsMapper.selectList(new LambdaQueryWrapper<Drugs>()
                .eq(Drugs::getDelMark, 1));

        LocalDate today = LocalDate.now();
        List<Map<String, Object>> lowStock = new ArrayList<>();
        List<Map<String, Object>> nearExpiry = new ArrayList<>();

        for (Drugs d : all) {
            int stock = d.getStock() == null ? 0 : d.getStock();
            int warn = d.getWarnStock() == null ? 20 : d.getWarnStock();
            if (stock <= warn) {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("id", d.getId());
                node.put("drugsName", d.getDrugsName());
                node.put("drugsFormat", d.getDrugsFormat());
                node.put("stock", stock);
                node.put("warnStock", warn);
                node.put("unit", d.getDrugsUnit());
                node.put("level", stock == 0 ? "danger" : "warning");
                lowStock.add(node);
            }
            if (d.getExpiryDate() != null) {
                long remain = ChronoUnit.DAYS.between(today, d.getExpiryDate());
                if (remain <= warnDays) {
                    Map<String, Object> node = new LinkedHashMap<>();
                    node.put("id", d.getId());
                    node.put("drugsName", d.getDrugsName());
                    node.put("drugsFormat", d.getDrugsFormat());
                    node.put("expiryDate", d.getExpiryDate().toString());
                    node.put("remainDays", remain);
                    node.put("level", remain <= 30 ? "danger" : "warning");
                    nearExpiry.add(node);
                }
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("lowStock", lowStock);
        result.put("nearExpiry", nearExpiry);
        result.put("lowStockCount", lowStock.size());
        result.put("nearExpiryCount", nearExpiry.size());
        return result;
    }

    /** 药房工作台概览 */
    public Map<String, Object> getOverview() {
        LocalDate today = LocalDate.now();
        Long pending = prescriptionMapper.selectCount(new LambdaQueryWrapper<Prescription>()
                .eq(Prescription::getPrescriptionState, 2));
        Long dispensed = prescriptionMapper.selectCount(new LambdaQueryWrapper<Prescription>()
                .eq(Prescription::getPrescriptionState, 3)
                .ge(Prescription::getPrescriptionTime, today.atStartOfDay())
                .lt(Prescription::getPrescriptionTime, today.plusDays(1).atStartOfDay()));

        Map<String, Object> warnings = getWarnings(null);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pendingCount", pending);
        result.put("dispensedToday", dispensed);
        result.put("lowStockCount", warnings.get("lowStockCount"));
        result.put("nearExpiryCount", warnings.get("nearExpiryCount"));
        return result;
    }
}
