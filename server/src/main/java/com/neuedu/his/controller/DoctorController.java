package com.neuedu.his.controller;

import com.neuedu.his.common.JsonResult;
import com.neuedu.his.entity.Disease;
import com.neuedu.his.entity.Drugs;
import com.neuedu.his.entity.FmedItem;
import com.neuedu.his.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 门诊医生站
 */
@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /** 候诊队列 */
    @GetMapping("/waiting-list")
    public JsonResult<List<Map<String, Object>>> waitingList(
            @RequestParam Integer doctorId,
            @RequestParam(required = false) Integer visitState,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return JsonResult.ok(doctorService.waitingList(doctorId, visitState, date));
    }

    /** 工作台概览 */
    @GetMapping("/overview")
    public JsonResult<Map<String, Object>> overview(@RequestParam Integer doctorId) {
        List<Map<String, Object>> all = doctorService.waitingList(doctorId, null, LocalDate.now());
        long waiting = all.stream().filter(m -> Integer.valueOf(1).equals(m.get("visitState"))).count();
        long visiting = all.stream().filter(m -> Integer.valueOf(2).equals(m.get("visitState"))).count();
        long finished = all.stream().filter(m -> Integer.valueOf(3).equals(m.get("visitState"))).count();

        return JsonResult.ok(Map.of(
                "total", all.size(),
                "waitingCount", waiting,
                "visitingCount", visiting,
                "finishedCount", finished,
                "patients", all
        ));
    }

    /** 接诊 */
    @PostMapping("/{registId}/accept")
    public JsonResult<Void> accept(@PathVariable Integer registId) {
        doctorService.accept(registId);
        return JsonResult.ok();
    }

    /** 查询病历 */
    @GetMapping("/medical-record/{registId}")
    public JsonResult<Map<String, Object>> medicalRecord(@PathVariable Integer registId) {
        return JsonResult.ok(doctorService.getMedicalRecord(registId));
    }

    /** 保存病历 */
    @PostMapping("/medical-record")
    public JsonResult<Map<String, Object>> saveMedicalRecord(@RequestBody Map<String, Object> body) {
        return JsonResult.ok(doctorService.saveMedicalRecord(body));
    }

    /** 药品检索 */
    @GetMapping("/drugs")
    public JsonResult<List<Drugs>> drugs(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return JsonResult.ok(doctorService.commonDrugs());
        }
        return JsonResult.ok(doctorService.searchDrugs(keyword, null));
    }

    /** 医技项目检索 */
    @GetMapping("/fmeditems")
    public JsonResult<List<FmedItem>> fmedItems(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Integer recordType) {
        return JsonResult.ok(doctorService.searchFmedItems(keyword, recordType));
    }

    /** 诊断检索 */
    @GetMapping("/diseases")
    public JsonResult<List<Disease>> diseases(@RequestParam(required = false) String keyword) {
        return JsonResult.ok(doctorService.searchDiseases(keyword));
    }

    /** 处方列表 */
    @GetMapping("/prescriptions")
    public JsonResult<List<Map<String, Object>>> prescriptions(@RequestParam Integer registId) {
        return JsonResult.ok(doctorService.listPrescriptions(registId));
    }

    /** 处方详情 */
    @GetMapping("/prescriptions/{id}")
    public JsonResult<Map<String, Object>> prescription(@PathVariable Integer id) {
        return JsonResult.ok(doctorService.getPrescription(id));
    }

    /** 开立处方 */
    @PostMapping("/prescriptions")
    public JsonResult<Map<String, Object>> createPrescription(@RequestBody Map<String, Object> body) {
        return JsonResult.ok(doctorService.createPrescription(body));
    }

    /** 作废处方 */
    @DeleteMapping("/prescriptions/{id}")
    public JsonResult<Void> deletePrescription(@PathVariable Integer id) {
        doctorService.deletePrescription(id);
        return JsonResult.ok();
    }

    /** 开立医技申请 */
    @PostMapping("/check-applies")
    public JsonResult<?> createCheckApply(@RequestBody Map<String, Object> body) {
        return JsonResult.ok(doctorService.createCheckApply(body));
    }
}
