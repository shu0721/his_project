package com.neuedu.his.controller;

import com.neuedu.his.common.JsonResult;
import com.neuedu.his.entity.User;
import com.neuedu.his.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 认证与用户管理
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /** 登录 */
    @PostMapping("/login")
    public JsonResult<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        Map<String, Object> data = authService.login(body.get("userName"), body.get("password"));
        return JsonResult.ok(data, String.valueOf(data.get("token")));
    }

    /** 角色字典（登录页的 6 个角色入口） */
    @GetMapping("/roles")
    public JsonResult<List<Map<String, Object>>> roles() {
        List<Map<String, Object>> roles = List.of(
                role(1, "医院管理员", "系统设置与基础数据维护", "admin"),
                role(2, "挂号收费员", "现场挂号、收费结算与退费", "reg01"),
                role(3, "门诊医生", "接诊、书写病历、开立处方", "chenjx"),
                role(4, "医技医生", "检验检查执行与结果录入", "wangxm"),
                role(5, "药房操作员", "处方审核发药与药品管理", "pharm01"),
                role(6, "财务管理员", "营收统计与费用科目管理", "fin01")
        );
        return JsonResult.ok(roles);
    }

    private Map<String, Object> role(int useType, String name, String desc, String demoAccount) {
        return Map.of(
                "useType", useType,
                "name", name,
                "desc", desc,
                "demoAccount", demoAccount
        );
    }

    /** 当前登录用户信息 */
    @GetMapping("/info/{userId}")
    public JsonResult<Map<String, Object>> info(@PathVariable Integer userId) {
        User user = authService.listUsers(null, null).stream()
                .filter(u -> userId.equals(u.getId()))
                .findFirst()
                .orElse(null);
        if (user == null) {
            return JsonResult.fail("用户不存在");
        }
        return JsonResult.ok(authService.buildUserInfo(user));
    }

    /** 用户列表 */
    @GetMapping("/users")
    public JsonResult<List<User>> users(@RequestParam(required = false) Integer useType,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Integer role) {
        Integer filterType = useType != null ? useType : role;
        return JsonResult.ok(authService.listUsers(filterType, keyword));
    }

    /** 新增用户 */
    @PostMapping("/users")
    public JsonResult<User> addUser(@RequestBody User user) {
        return JsonResult.ok(authService.addUser(user));
    }

    /** 修改用户 */
    @PutMapping("/users")
    public JsonResult<User> updateUser(@RequestBody User user) {
        boolean ok = authService.updateUser(user);
        if (!ok) {
            return JsonResult.fail("修改失败");
        }
        return JsonResult.ok(user);
    }

    /** 启用 / 停用 */
    @PostMapping("/users/{id}/toggle")
    public JsonResult<Void> toggle(@PathVariable Integer id,
                                   @RequestParam boolean enabled) {
        authService.toggleUser(id, enabled);
        return JsonResult.ok();
    }

    /** 重置密码 */
    @PostMapping("/users/{id}/reset-password")
    public JsonResult<Void> resetPassword(@PathVariable Integer id) {
        authService.resetPassword(id);
        return JsonResult.ok();
    }

    /** 修改密码 */
    @PostMapping("/change-password")
    public JsonResult<Void> changePassword(@RequestBody Map<String, Object> body) {
        Integer uid = Integer.valueOf(String.valueOf(body.get("uid")));
        authService.changePassword(uid,
                String.valueOf(body.get("oldPwd")),
                String.valueOf(body.get("newPwd")));
        return JsonResult.ok();
    }
}
