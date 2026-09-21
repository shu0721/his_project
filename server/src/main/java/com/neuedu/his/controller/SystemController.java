package com.neuedu.his.controller;

import com.neuedu.his.common.JsonResult;
import com.neuedu.his.entity.*;
import com.neuedu.his.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 系统管理站（基础字典维护）
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private SystemService systemService;

    // ---------- 科室 ----------
    @GetMapping("/departments")
    public JsonResult<List<Department>> departments() {
        return JsonResult.ok(systemService.listDepartments());
    }

    @PostMapping("/departments")
    public JsonResult<Department> saveDepartment(@RequestBody Department d) {
        return JsonResult.ok(systemService.saveDepartment(d));
    }

    @DeleteMapping("/departments/{id}")
    public JsonResult<Void> deleteDepartment(@PathVariable Integer id) {
        systemService.deleteDepartment(id);
        return JsonResult.ok();
    }

    // ---------- 常数类别 ----------
    @GetMapping("/constant-types")
    public JsonResult<List<ConstantType>> constantTypes() {
        return JsonResult.ok(systemService.listConstantTypes());
    }

    @PostMapping("/constant-types")
    public JsonResult<ConstantType> saveConstantType(@RequestBody ConstantType t) {
        return JsonResult.ok(systemService.saveConstantType(t));
    }

    @DeleteMapping("/constant-types/{id}")
    public JsonResult<Void> deleteConstantType(@PathVariable Integer id) {
        systemService.deleteConstantType(id);
        return JsonResult.ok();
    }

    // ---------- 常数项 ----------
    @GetMapping("/constant-items")
    public JsonResult<List<ConstantItem>> constantItems(@RequestParam(required = false) Integer typeId) {
        return JsonResult.ok(systemService.listConstantItems(typeId));
    }

    @PostMapping("/constant-items")
    public JsonResult<ConstantItem> saveConstantItem(@RequestBody ConstantItem item) {
        return JsonResult.ok(systemService.saveConstantItem(item));
    }

    @DeleteMapping("/constant-items/{id}")
    public JsonResult<Void> deleteConstantItem(@PathVariable Integer id) {
        systemService.deleteConstantItem(id);
        return JsonResult.ok();
    }

    // ---------- 挂号级别 ----------
    @GetMapping("/regist-levels")
    public JsonResult<List<RegistLevel>> registLevels() {
        return JsonResult.ok(systemService.listRegistLevels());
    }

    @PostMapping("/regist-levels")
    public JsonResult<RegistLevel> saveRegistLevel(@RequestBody RegistLevel l) {
        return JsonResult.ok(systemService.saveRegistLevel(l));
    }

    @DeleteMapping("/regist-levels/{id}")
    public JsonResult<Void> deleteRegistLevel(@PathVariable Integer id) {
        systemService.deleteRegistLevel(id);
        return JsonResult.ok();
    }

    // ---------- 结算类别 ----------
    @GetMapping("/settle-categories")
    public JsonResult<List<SettleCategory>> settleCategories() {
        return JsonResult.ok(systemService.listSettleCategories());
    }

    @PostMapping("/settle-categories")
    public JsonResult<SettleCategory> saveSettleCategory(@RequestBody SettleCategory s) {
        return JsonResult.ok(systemService.saveSettleCategory(s));
    }

    @DeleteMapping("/settle-categories/{id}")
    public JsonResult<Void> deleteSettleCategory(@PathVariable Integer id) {
        systemService.deleteSettleCategory(id);
        return JsonResult.ok();
    }

    // ---------- 诊断目录 ----------
    @GetMapping("/diseases")
    public JsonResult<List<Disease>> diseases(@RequestParam(required = false) String keyword) {
        return JsonResult.ok(systemService.listDiseases(keyword));
    }

    @PostMapping("/diseases")
    public JsonResult<Disease> saveDisease(@RequestBody Disease d) {
        return JsonResult.ok(systemService.saveDisease(d));
    }

    @DeleteMapping("/diseases/{id}")
    public JsonResult<Void> deleteDisease(@PathVariable Integer id) {
        systemService.deleteDisease(id);
        return JsonResult.ok();
    }

    // ---------- 诊断类别 ----------
    @GetMapping("/dise-categories")
    public JsonResult<List<DiseCategory>> diseCategories() {
        return JsonResult.ok(systemService.listDiseCategories());
    }

    // ---------- 费用科目 ----------
    @GetMapping("/expense-classes")
    public JsonResult<List<ExpenseClass>> expenseClasses() {
        return JsonResult.ok(systemService.listExpenseClasses());
    }

    @PostMapping("/expense-classes")
    public JsonResult<ExpenseClass> saveExpenseClass(@RequestBody ExpenseClass e) {
        return JsonResult.ok(systemService.saveExpenseClass(e));
    }

    @DeleteMapping("/expense-classes/{id}")
    public JsonResult<Void> deleteExpenseClass(@PathVariable Integer id) {
        systemService.deleteExpenseClass(id);
        return JsonResult.ok();
    }

    // ---------- 非药品收费项目 ----------
    @GetMapping("/fmed-items")
    public JsonResult<List<FmedItem>> fmedItems(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Integer recordType) {
        return JsonResult.ok(systemService.listFmedItems(keyword, recordType));
    }

    @PostMapping("/fmed-items")
    public JsonResult<FmedItem> saveFmedItem(@RequestBody FmedItem item) {
        return JsonResult.ok(systemService.saveFmedItem(item));
    }

    @DeleteMapping("/fmed-items/{id}")
    public JsonResult<Void> deleteFmedItem(@PathVariable Integer id) {
        systemService.deleteFmedItem(id);
        return JsonResult.ok();
    }

    // ---------- 医生排班 ----------
    @GetMapping("/scheduling")
    public JsonResult<List<Map<String, Object>>> scheduling(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer deptId) {
        return JsonResult.ok(systemService.listScheduling(date, deptId));
    }

    @PostMapping("/scheduling")
    public JsonResult<Scheduling> saveScheduling(@RequestBody Scheduling s) {
        return JsonResult.ok(systemService.saveScheduling(s));
    }

    @DeleteMapping("/scheduling/{id}")
    public JsonResult<Void> deleteScheduling(@PathVariable Integer id) {
        systemService.deleteScheduling(id);
        return JsonResult.ok();
    }
}
