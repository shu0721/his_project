package com.neuedu.his.controller;

import com.neuedu.his.common.JsonResult;
import com.neuedu.his.entity.Department;
import com.neuedu.his.entity.RegistLevel;
import com.neuedu.his.entity.SettleCategory;
import com.neuedu.his.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 挂号收费站
 */
@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    /** 工作台概览 */
    @GetMapping("/overview")
    public JsonResult<Map<String, Object>> overview() {
        return JsonResult.ok(registerService.getRevenueSummary(null));
    }

    /** 科室列表 */
    @GetMapping("/departments")
    public JsonResult<List<Department>> departments(@RequestParam(required = false) Integer deptType) {
        return JsonResult.ok(registerService.listDepartments(deptType));
    }

    /** 挂号级别 */
    @GetMapping("/regist-levels")
    public JsonResult<List<RegistLevel>> registLevels() {
        return JsonResult.ok(registerService.listRegistLevels());
    }

    /** 结算类别 */
    @GetMapping("/settle-categories")
    public JsonResult<List<SettleCategory>> settleCategories() {
        return JsonResult.ok(registerService.listSettleCategories());
    }

    /** 出诊医生（按科室 + 日期 + 午别） */
    @GetMapping("/doctors")
    public JsonResult<List<Map<String, Object>>> doctors(
            @RequestParam(required = false) Integer deptId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String noon) {
        return JsonResult.ok(registerService.listDoctors(deptId, date, noon));
    }

    /** 挂号记录列表 */
    @GetMapping("/list")
    public JsonResult<List<Map<String, Object>>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer visitState,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer deptId,
            @RequestParam(required = false) String keyword) {
        return JsonResult.ok(registerService.listRegisters(date, visitState, userId, deptId, keyword));
    }

    /** 现场挂号 */
    @PostMapping("/create")
    public JsonResult<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        return JsonResult.ok(registerService.createRegister(body));
    }

    /** 退号 */
    @PostMapping("/{id}/refund")
    public JsonResult<Void> refund(@PathVariable Integer id,
                                   @RequestParam(required = false, defaultValue = "1") Integer operId) {
        registerService.refundRegister(id, operId);
        return JsonResult.ok();
    }

    /** 待收费清单 */
    @GetMapping("/{id}/charge-list")
    public JsonResult<Map<String, Object>> chargeList(@PathVariable Integer id) {
        return JsonResult.ok(registerService.getChargeList(id));
    }

    /** 收费结算 */
    @PostMapping("/pay")
    public JsonResult<Map<String, Object>> pay(@RequestBody Map<String, Object> body) {
        return JsonResult.ok(registerService.pay(body));
    }
}
