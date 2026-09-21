package com.neuedu.his.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.neuedu.his.common.BusinessException;
import com.neuedu.his.entity.CheckApply;
import com.neuedu.his.entity.FmedItem;
import com.neuedu.his.entity.Register;
import com.neuedu.his.mapper.CheckApplyMapper;
import com.neuedu.his.mapper.FmedItemMapper;
import com.neuedu.his.mapper.RegisterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 医技服务：检验 / 检查 / 处置 的执行与结果录入
 */
@Service
public class TechService {

    @Autowired private CheckApplyMapper checkApplyMapper;
    @Autowired private FmedItemMapper fmedItemMapper;
    @Autowired private RegisterMapper registerMapper;
    @Autowired private RegisterService registerService;

    /**
     * 医技申请队列
     *
     * @param recordType 1检查 2检验 3处置；null 全部
     * @param state      0待执行 1已执行待出结果 2已出结果；null 全部
     */
    public List<Map<String, Object>> listApplies(Integer recordType, Integer state, String keyword) {
        LambdaQueryWrapper<CheckApply> wrapper = new LambdaQueryWrapper<CheckApply>()
                .eq(recordType != null, CheckApply::getRecordType, recordType)
                .eq(state != null, CheckApply::getState, state)
                .ne(CheckApply::getState, -1)   // 排除未收费的申请（-1 预留）
                .orderByAsc(CheckApply::getIsUrgent)
                .orderByDesc(CheckApply::getCreationTime);

        List<CheckApply> list = checkApplyMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CheckApply ca : list) {
            Map<String, Object> vo = toVo(ca);
            if (StringUtils.isNotBlank(keyword)) {
                String name = String.valueOf(vo.getOrDefault("patientName", ""));
                if (!name.contains(keyword)) {
                    continue;
                }
            }
            result.add(vo);
        }
        return result;
    }

    private Map<String, Object> toVo(CheckApply ca) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", ca.getId());
        m.put("registId", ca.getRegistId());
        m.put("itemId", ca.getItemId());
        m.put("name", ca.getName());
        m.put("objective", ca.getObjective());
        m.put("position", ca.getPosition());
        m.put("isUrgent", ca.getIsUrgent());
        m.put("num", ca.getNum());
        m.put("creationTime", ca.getCreationTime());
        m.put("doctorId", ca.getDoctorId());
        m.put("doctorName", registerService.userName(ca.getDoctorId()));
        m.put("state", ca.getState());
        m.put("stateText", stateText(ca.getState()));
        m.put("recordType", ca.getRecordType());
        m.put("recordTypeText", recordTypeText(ca.getRecordType()));
        m.put("result", ca.getResult());
        m.put("checkTime", ca.getCheckTime());
        m.put("resultTime", ca.getResultTime());
        m.put("price", fmedItemPrice(ca.getItemId()));

        Register r = registerMapper.selectById(ca.getRegistId());
        if (r != null) {
            m.put("patientName", r.getRealName());
            m.put("gender", r.getGender());
            m.put("genderText", r.getGender() != null && r.getGender() == 1 ? "男" : "女");
            m.put("age", r.getAge());
            m.put("caseNumber", r.getCaseNumber());
            m.put("deptName", registerService.deptName(r.getDeptId()));
        }
        return m;
    }

    private Object fmedItemPrice(Integer itemId) {
        if (itemId == null) {
            return null;
        }
        FmedItem item = fmedItemMapper.selectById(itemId);
        return item == null ? null : item.getPrice();
    }

    public static String stateText(Integer state) {
        if (state == null) {
            return "未知";
        }
        return switch (state) {
            case 0 -> "待收费";
            case 1 -> "待执行";
            case 2 -> "已出结果";
            default -> "未知";
        };
    }

    public static String recordTypeText(Integer type) {
        if (type == null) {
            return "未知";
        }
        return switch (type) {
            case 1 -> "检查";
            case 2 -> "检验";
            case 3 -> "处置";
            default -> "其他";
        };
    }

    /** 开始执行（采样 / 检查） */
    public boolean start(Integer id, Integer operId) {
        CheckApply ca = checkApplyMapper.selectById(id);
        if (ca == null) {
            throw new BusinessException("申请单不存在");
        }
        if (ca.getState() != null && ca.getState() == 0) {
            throw new BusinessException("该项目尚未收费，不能执行");
        }
        ca.setState(2);  // 已执行，待出结果
        ca.setCheckOperId(operId);
        ca.setCheckTime(LocalDateTime.now());
        return checkApplyMapper.updateById(ca) > 0;
    }

    /** 录入结果并完成 */
    public boolean submitResult(Integer id, String result, Integer operId) {
        CheckApply ca = checkApplyMapper.selectById(id);
        if (ca == null) {
            throw new BusinessException("申请单不存在");
        }
        ca.setResult(result);
        ca.setResultOperId(operId);
        ca.setResultTime(LocalDateTime.now());
        ca.setState(3);  // 已出结果
        if (ca.getCheckTime() == null) {
            ca.setCheckTime(LocalDateTime.now());
            ca.setCheckOperId(operId);
        }
        return checkApplyMapper.updateById(ca) > 0;
    }

    /** 医技工作台概览 */
    public Map<String, Object> getOverview() {
        LocalDate today = LocalDate.now();
        List<CheckApply> applies = checkApplyMapper.selectList(new LambdaQueryWrapper<CheckApply>()
                .ge(CheckApply::getCreationTime, today.atStartOfDay()));

        long pending = applies.stream().filter(a -> a.getState() != null && a.getState() == 1).count();
        long doing = applies.stream().filter(a -> a.getState() != null && a.getState() == 2).count();
        long done = applies.stream().filter(a -> a.getState() != null && a.getState() == 3).count();
        long urgent = applies.stream().filter(a -> a.getIsUrgent() != null && a.getIsUrgent() == 1).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayTotal", (long) applies.size());
        result.put("pendingCount", pending);
        result.put("doingCount", doing);
        result.put("doneCount", done);
        result.put("urgentCount", urgent);
        return result;
    }
}
