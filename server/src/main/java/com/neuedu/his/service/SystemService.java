package com.neuedu.his.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.neuedu.his.common.BusinessException;
import com.neuedu.his.entity.*;
import com.neuedu.his.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统管理服务：基础字典与排班的增删改查
 */
@Service
public class SystemService {

    @Autowired private DepartmentMapper departmentMapper;
    @Autowired private ConstantTypeMapper constantTypeMapper;
    @Autowired private ConstantItemMapper constantItemMapper;
    @Autowired private RegistLevelMapper registLevelMapper;
    @Autowired private SettleCategoryMapper settleCategoryMapper;
    @Autowired private DiseaseMapper diseaseMapper;
    @Autowired private DiseCategoryMapper diseCategoryMapper;
    @Autowired private ExpenseClassMapper expenseClassMapper;
    @Autowired private FmedItemMapper fmedItemMapper;
    @Autowired private SchedulingMapper schedulingMapper;
    @Autowired private UserMapper userMapper;

    // ---------- 科室 ----------
    public List<Department> listDepartments() {
        return departmentMapper.selectList(new LambdaQueryWrapper<Department>()
                .eq(Department::getDelMark, 1)
                .orderByAsc(Department::getId));
    }

    public Department saveDepartment(Department d) {
        if (StringUtils.isBlank(d.getDeptName())) {
            throw new BusinessException("科室名称不能为空");
        }
        if (d.getId() == null) {
            d.setDelMark(1);
            departmentMapper.insert(d);
        } else {
            departmentMapper.updateById(d);
        }
        return d;
    }

    public boolean deleteDepartment(Integer id) {
        return setDeleted(departmentMapper, id);
    }

    // ---------- 常数类别 ----------
    public List<ConstantType> listConstantTypes() {
        return constantTypeMapper.selectList(new LambdaQueryWrapper<ConstantType>()
                .eq(ConstantType::getDelMark, 1)
                .orderByAsc(ConstantType::getId));
    }

    public ConstantType saveConstantType(ConstantType t) {
        if (StringUtils.isBlank(t.getConstantTypeName())) {
            throw new BusinessException("类别名称不能为空");
        }
        if (t.getId() == null) {
            t.setDelMark(1);
            constantTypeMapper.insert(t);
        } else {
            constantTypeMapper.updateById(t);
        }
        return t;
    }

    public boolean deleteConstantType(Integer id) {
        return setDeleted(constantTypeMapper, id);
    }

    // ---------- 常数项 ----------
    public List<ConstantItem> listConstantItems(Integer typeId) {
        return constantItemMapper.selectList(new LambdaQueryWrapper<ConstantItem>()
                .eq(ConstantItem::getDelMark, 1)
                .eq(typeId != null, ConstantItem::getConstantTypeId, typeId)
                .orderByAsc(ConstantItem::getId));
    }

    public ConstantItem saveConstantItem(ConstantItem item) {
        if (StringUtils.isBlank(item.getConstantName())) {
            throw new BusinessException("常数项名称不能为空");
        }
        if (item.getId() == null) {
            item.setDelMark(1);
            constantItemMapper.insert(item);
        } else {
            constantItemMapper.updateById(item);
        }
        return item;
    }

    public boolean deleteConstantItem(Integer id) {
        return setDeleted(constantItemMapper, id);
    }

    // ---------- 挂号级别 ----------
    public List<RegistLevel> listRegistLevels() {
        return registLevelMapper.selectList(new LambdaQueryWrapper<RegistLevel>()
                .eq(RegistLevel::getDelMark, 1)
                .orderByAsc(RegistLevel::getSequenceNo));
    }

    public RegistLevel saveRegistLevel(RegistLevel l) {
        if (StringUtils.isBlank(l.getRegistName())) {
            throw new BusinessException("号别名称不能为空");
        }
        if (l.getId() == null) {
            l.setDelMark(1);
            l.setSequenceNo(l.getSequenceNo() == null ? 99 : l.getSequenceNo());
            l.setRegistQuota(l.getRegistQuota() == null ? 30 : l.getRegistQuota());
            registLevelMapper.insert(l);
        } else {
            registLevelMapper.updateById(l);
        }
        return l;
    }

    public boolean deleteRegistLevel(Integer id) {
        return setDeleted(registLevelMapper, id);
    }

    // ---------- 结算类别 ----------
    public List<SettleCategory> listSettleCategories() {
        return settleCategoryMapper.selectList(new LambdaQueryWrapper<SettleCategory>()
                .eq(SettleCategory::getDelMark, 1)
                .orderByAsc(SettleCategory::getSequenceNo));
    }

    public SettleCategory saveSettleCategory(SettleCategory s) {
        if (StringUtils.isBlank(s.getSettleName())) {
            throw new BusinessException("结算类别名称不能为空");
        }
        if (s.getId() == null) {
            s.setDelMark(1);
            settleCategoryMapper.insert(s);
        } else {
            settleCategoryMapper.updateById(s);
        }
        return s;
    }

    public boolean deleteSettleCategory(Integer id) {
        return setDeleted(settleCategoryMapper, id);
    }

    // ---------- 诊断目录 ----------
    public List<Disease> listDiseases(String keyword) {
        return diseaseMapper.selectList(new LambdaQueryWrapper<Disease>()
                .eq(Disease::getDelMark, 1)
                .and(StringUtils.isNotBlank(keyword), w -> w
                        .like(Disease::getDiseaseName, keyword)
                        .or().like(Disease::getDiseaseCode, keyword))
                .orderByAsc(Disease::getId));
    }

    public Disease saveDisease(Disease d) {
        if (StringUtils.isBlank(d.getDiseaseName())) {
            throw new BusinessException("诊断名称不能为空");
        }
        if (d.getId() == null) {
            d.setDelMark(1);
            diseaseMapper.insert(d);
        } else {
            diseaseMapper.updateById(d);
        }
        return d;
    }

    public boolean deleteDisease(Integer id) {
        return setDeleted(diseaseMapper, id);
    }

    // ---------- 诊断类别 ----------
    public List<DiseCategory> listDiseCategories() {
        return diseCategoryMapper.selectList(new LambdaQueryWrapper<DiseCategory>()
                .eq(DiseCategory::getDelMark, 1)
                .orderByAsc(DiseCategory::getSequenceNo));
    }

    // ---------- 费用科目 ----------
    public List<ExpenseClass> listExpenseClasses() {
        return expenseClassMapper.selectList(new LambdaQueryWrapper<ExpenseClass>()
                .eq(ExpenseClass::getDelMark, 1)
                .orderByAsc(ExpenseClass::getId));
    }

    public ExpenseClass saveExpenseClass(ExpenseClass e) {
        if (e.getId() == null) {
            e.setDelMark(1);
            expenseClassMapper.insert(e);
        } else {
            expenseClassMapper.updateById(e);
        }
        return e;
    }

    public boolean deleteExpenseClass(Integer id) {
        return setDeleted(expenseClassMapper, id);
    }

    // ---------- 非药品收费项目 ----------
    public List<FmedItem> listFmedItems(String keyword, Integer recordType) {
        return fmedItemMapper.selectList(new LambdaQueryWrapper<FmedItem>()
                .eq(FmedItem::getDelMark, 1)
                .eq(recordType != null, FmedItem::getRecordType, recordType)
                .and(StringUtils.isNotBlank(keyword), w -> w
                        .like(FmedItem::getItemName, keyword)
                        .or().like(FmedItem::getItemCode, keyword))
                .orderByAsc(FmedItem::getId));
    }

    public FmedItem saveFmedItem(FmedItem item) {
        if (StringUtils.isBlank(item.getItemName())) {
            throw new BusinessException("项目名称不能为空");
        }
        if (item.getId() == null) {
            item.setDelMark(1);
            fmedItemMapper.insert(item);
        } else {
            fmedItemMapper.updateById(item);
        }
        return item;
    }

    public boolean deleteFmedItem(Integer id) {
        return setDeleted(fmedItemMapper, id);
    }

    // ---------- 医生排班 ----------
    public List<Map<String, Object>> listScheduling(LocalDate date, Integer deptId) {
        LocalDate d = date == null ? LocalDate.now() : date;
        List<Scheduling> list = schedulingMapper.selectList(new LambdaQueryWrapper<Scheduling>()
                .eq(Scheduling::getDelMark, 1)
                .eq(Scheduling::getSchedDate, d)
                .eq(deptId != null, Scheduling::getDeptId, deptId)
                .orderByAsc(Scheduling::getDeptId)
                .orderByAsc(Scheduling::getNoon));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Scheduling s : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", s.getId());
            m.put("schedDate", s.getSchedDate());
            m.put("deptId", s.getDeptId());
            m.put("deptName", deptName(s.getDeptId()));
            m.put("userId", s.getUserId());
            m.put("doctorName", userName(s.getUserId()));
            m.put("noon", s.getNoon());
            m.put("regNum", s.getRegNum());
            m.put("ruleId", s.getRuleId());
            result.add(m);
        }
        return result;
    }

    public Scheduling saveScheduling(Scheduling s) {
        if (s.getUserId() == null || s.getDeptId() == null) {
            throw new BusinessException("医生与科室不能为空");
        }
        if (s.getSchedDate() == null) {
            s.setSchedDate(LocalDate.now());
        }
        if (s.getNoon() == null || s.getNoon().isBlank()) {
            s.setNoon("上午");
        }
        if (s.getId() == null) {
            s.setDelMark(1);
            s.setRegNum(0);
            s.setRuleId(s.getRuleId() == null ? 1 : s.getRuleId());
            schedulingMapper.insert(s);
        } else {
            schedulingMapper.updateById(s);
        }
        return s;
    }

    public boolean deleteScheduling(Integer id) {
        return setDeleted(schedulingMapper, id);
    }

    // ---------- 通用逻辑删除 ----------
    @SuppressWarnings("unchecked")
    private <T> boolean setDeleted(com.baomidou.mybatisplus.core.mapper.BaseMapper<T> mapper, Integer id) {
        try {
            T entity = mapper.selectById(id);
            if (entity == null) {
                return false;
            }
            // 反射设置 DelMark = 0
            var m = entity.getClass().getMethod("setDelMark", Integer.class);
            m.invoke(entity, 0);
            return mapper.updateById(entity) > 0;
        } catch (Exception e) {
            throw new BusinessException("删除失败：" + e.getMessage());
        }
    }

    private String deptName(Integer deptId) {
        if (deptId == null) {
            return "";
        }
        Department d = departmentMapper.selectById(deptId);
        return d == null ? "" : d.getDeptName();
    }

    private String userName(Integer userId) {
        if (userId == null) {
            return "";
        }
        User u = userMapper.selectById(userId);
        return u == null ? "" : u.getRealName();
    }
}
