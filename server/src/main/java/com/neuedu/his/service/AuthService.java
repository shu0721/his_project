package com.neuedu.his.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.neuedu.his.common.BusinessException;
import com.neuedu.his.common.JwtUtils;
import com.neuedu.his.entity.User;
import com.neuedu.his.mapper.DepartmentMapper;
import com.neuedu.his.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证与用户服务
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 登录：校验用户名密码，返回用户信息 + JWT
     */
    public Map<String, Object> login(String userName, String password) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            throw new BusinessException("用户名和密码不能为空");
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, userName)
                .eq(User::getDelMark, 1));

        if (user == null) {
            throw new BusinessException("用户名不存在");
        }
        if (!password.equals(user.getPassword())) {
            throw new BusinessException("密码不正确");
        }

        String token = JwtUtils.sign(user.getId(), user.getUserName(), user.getUseType());

        Map<String, Object> data = new HashMap<>(4);
        data.put("token", token);
        data.put("user", buildUserInfo(user));
        return data;
    }

    /** 组装用户展示信息（补充科室名与角色名） */
    public Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new HashMap<>(8);
        info.put("id", user.getId());
        info.put("userName", user.getUserName());
        info.put("realName", user.getRealName());
        info.put("useType", user.getUseType());
        info.put("useTypeName", roleName(user.getUseType()));
        info.put("deptId", user.getDeptId());
        info.put("docTitleId", user.getDocTitleId());
        info.put("isScheduling", user.getIsScheduling());

        if (user.getDeptId() != null) {
            var dept = departmentMapper.selectById(user.getDeptId());
            info.put("deptName", dept == null ? "" : dept.getDeptName());
        }
        return info;
    }

    /** 用户类别 -> 角色名称 */
    public static String roleName(Integer useType) {
        if (useType == null) {
            return "未知角色";
        }
        return switch (useType) {
            case 1 -> "医院管理员";
            case 2 -> "挂号收费员";
            case 3 -> "门诊医生";
            case 4 -> "医技医生";
            case 5 -> "药房操作员";
            case 6 -> "财务管理员";
            default -> "其他角色";
        };
    }

    /** 查询用户列表 */
    public List<User> listUsers(Integer useType, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getDelMark, 1)
                .eq(useType != null, User::getUseType, useType)
                .and(StringUtils.isNotBlank(keyword), w -> w
                        .like(User::getRealName, keyword)
                        .or().like(User::getUserName, keyword))
                .orderByAsc(User::getUseType)
                .orderByAsc(User::getId);
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(u -> {
            u.setUseTypeName(roleName(u.getUseType()));
            u.setDeptName(deptName(u.getDeptId()));
            u.setPassword(null);
        });
        return users;
    }

    private String deptName(Integer deptId) {
        if (deptId == null) {
            return "";
        }
        var dept = departmentMapper.selectById(deptId);
        return dept == null ? "" : dept.getDeptName();
    }

    /** 新增用户，默认密码 123456 */
    public User addUser(User user) {
        if (StringUtils.isBlank(user.getUserName())) {
            throw new BusinessException("登录名不能为空");
        }
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, user.getUserName())
                .eq(User::getDelMark, 1));
        if (exists != null && exists > 0) {
            throw new BusinessException("登录名已存在");
        }
        user.setId(null);
        user.setPassword("123456");
        user.setDelMark(1);
        userMapper.insert(user);
        return user;
    }

    /** 修改用户 */
    public boolean updateUser(User user) {
        if (user.getId() == null) {
            throw new BusinessException("用户 ID 不能为空");
        }
        // 不允许通过该接口改密码
        user.setPassword(null);
        return userMapper.updateById(user) > 0;
    }

    /** 启用 / 停用（逻辑删除） */
    public boolean toggleUser(Integer id, boolean enabled) {
        User user = new User();
        user.setId(id);
        user.setDelMark(enabled ? 1 : 0);
        return userMapper.updateById(user) > 0;
    }

    /** 修改密码 */
    public boolean changePassword(Integer userId, String oldPwd, String newPwd) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!user.getPassword().equals(oldPwd)) {
            throw new BusinessException("原密码不正确");
        }
        User update = new User();
        update.setId(userId);
        update.setPassword(newPwd);
        return userMapper.updateById(update) > 0;
    }

    /** 重置密码为 123456 */
    public boolean resetPassword(Integer userId) {
        User update = new User();
        update.setId(userId);
        update.setPassword("123456");
        return userMapper.updateById(update) > 0;
    }
}
