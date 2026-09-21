package com.neuedu.his.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 挂号记录
 */
@Data
@TableName("register")
public class Register implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 病历号 */
    private String caseNumber;

    /** 姓名 */
    private String realName;

    /** 性别 1男 2女 */
    private Integer gender;

    /** 身份证号 */
    private String idnumber;

    /** 出生日期 */
    private LocalDate birthDate;

    /** 年龄 */
    private Integer age;

    /** 年龄类型 */
    private String ageType;

    /** 家庭住址 */
    private String homeAddress;

    /** 本次看诊日期 */
    private LocalDate visitDate;

    /** 午别 */
    private String noon;

    /** 本次挂号科室ID */
    private Integer deptId;

    /** 本次挂号医生ID */
    private Integer userId;

    /** 本次挂号级别ID */
    private Integer registLeId;

    /** 结算类别ID */
    private Integer settleId;

    /** 病历本要否 */
    private String isBook;

    /** 挂号时间 */
    private LocalDateTime registTime;

    /** 挂号员ID */
    private Integer registerId;

    /** 看诊状态 1已挂号 2已接诊 3已诊毕 4已退号 */
    private Integer visitState;

    /** 就诊卡号 */
    private String medicalCardId;

    /** 时间段 */
    private Integer timeInterval;

    /** 渠道 1窗口 2小程序 */
    private Integer channel;

}
