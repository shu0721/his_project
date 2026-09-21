package com.neuedu.his.controller;

import com.neuedu.his.common.JsonResult;
import com.neuedu.his.service.TechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 医技管理站（检验 / 检查 / 处置）
 */
@RestController
@RequestMapping("/api/tech")
public class TechController {

    @Autowired
    private TechService techService;

    /** 工作台概览 */
    @GetMapping("/overview")
    public JsonResult<Map<String, Object>> overview() {
        return JsonResult.ok(techService.getOverview());
    }

    /** 申请队列 */
    @GetMapping("/applies")
    public JsonResult<List<Map<String, Object>>> applies(
            @RequestParam(required = false) Integer recordType,
            @RequestParam(required = false) Integer state,
            @RequestParam(required = false) String keyword) {
        return JsonResult.ok(techService.listApplies(recordType, state, keyword));
    }

    /** 开始执行 */
    @PostMapping("/applies/{id}/start")
    public JsonResult<Void> start(@PathVariable Integer id,
                                  @RequestParam(required = false, defaultValue = "20") Integer operId) {
        techService.start(id, operId);
        return JsonResult.ok();
    }

    /** 录入结果 */
    @PostMapping("/applies/{id}/result")
    public JsonResult<Void> result(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Object operId = body.get("operId");
        techService.submitResult(id,
                String.valueOf(body.get("result")),
                operId == null ? 20 : Integer.valueOf(String.valueOf(operId)));
        return JsonResult.ok();
    }
}
