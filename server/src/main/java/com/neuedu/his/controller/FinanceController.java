package com.neuedu.his.controller;

import com.neuedu.his.common.JsonResult;
import com.neuedu.his.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 财务管理站：营收统计与工作量统计
 */
@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    @Autowired
    private RegisterService registerService;

    /** 营收总览（含近 7 日趋势与费用构成） */
    @GetMapping("/revenue")
    public JsonResult<Map<String, Object>> revenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return JsonResult.ok(registerService.getRevenueSummary(date));
    }

    /** 医生工作量统计 */
    @GetMapping("/doctor-workload")
    public JsonResult<List<Map<String, Object>>> doctorWorkload(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return JsonResult.ok(registerService.getDoctorWorkload(date));
    }
}
