package com.neuedu.his.controller;

import com.neuedu.his.common.JsonResult;
import com.neuedu.his.entity.Drugs;
import com.neuedu.his.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 药房管理站
 */
@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    @Autowired
    private PharmacyService pharmacyService;

    /** 药房工作台概览 */
    @GetMapping("/overview")
    public JsonResult<Map<String, Object>> overview() {
        return JsonResult.ok(pharmacyService.getOverview());
    }

    /** 处方列表（待发药 / 已发药） */
    @GetMapping("/prescriptions")
    public JsonResult<List<Map<String, Object>>> prescriptions(
            @RequestParam(required = false) Integer state,
            @RequestParam(required = false) String keyword) {
        return JsonResult.ok(pharmacyService.listPrescriptions(state, keyword));
    }

    /** 确认发药 */
    @PostMapping("/prescriptions/{id}/dispense")
    public JsonResult<Map<String, Object>> dispense(@PathVariable Integer id) {
        return JsonResult.ok(pharmacyService.dispense(id));
    }

    /** 药品列表 */
    @GetMapping("/drugs")
    public JsonResult<List<Drugs>> drugs(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Integer typeId) {
        return JsonResult.ok(pharmacyService.listDrugs(keyword, typeId));
    }

    /** 药品详情 */
    @GetMapping("/drugs/{id}")
    public JsonResult<Drugs> drug(@PathVariable Integer id) {
        return JsonResult.ok(pharmacyService.getDrug(id));
    }

    /** 新增 / 修改药品 */
    @PostMapping("/drugs")
    public JsonResult<Drugs> saveDrug(@RequestBody Drugs drug) {
        return JsonResult.ok(pharmacyService.saveDrug(drug));
    }

    /** 停用药品 */
    @DeleteMapping("/drugs/{id}")
    public JsonResult<Void> deleteDrug(@PathVariable Integer id) {
        pharmacyService.deleteDrug(id);
        return JsonResult.ok();
    }

    /** 药品入库 */
    @PostMapping("/drugs/{id}/stock-in")
    public JsonResult<Drugs> stockIn(@PathVariable Integer id, @RequestParam Integer count) {
        return JsonResult.ok(pharmacyService.addStock(id, count));
    }

    /** 库存与效期预警 */
    @GetMapping("/warnings")
    public JsonResult<Map<String, Object>> warnings(@RequestParam(required = false) Integer days) {
        return JsonResult.ok(pharmacyService.getWarnings(days));
    }
}
