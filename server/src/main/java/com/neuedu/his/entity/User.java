package com.neuedu.his.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户（医院工作人员）
 */
@Data
@TableName("`user`")
public class User implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 登录名 */
    private String userName;

    /** 密码 */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 用户类别 1管理员 2挂号收费员 3门诊医生 4医技医生 5药房操作员 6财务管理员 */
    private Integer useType;

    /** 医生职称 ID */
    private Integer docTitleId;

    /** 是否参与排班 */
    private String isScheduling;

    /** 所在科室 ID */
    private Integer deptId;

    /** 挂号级别 ID */
    private Integer registLeId;

    private Integer delMark;

    // ---------- 非数据库字段（联表展示用） ----------

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String deptName;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String docTitle;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String useTypeName;
}
