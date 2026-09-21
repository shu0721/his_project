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
 * 医技申请（检查/检验/处置）
 */
@Data
@TableName("checkapply")
public class CheckApply implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 病历ID */
    private Integer medicalId;

    /** 挂号ID */
    private Integer registId;

    /** 项目ID */
    private Integer itemId;

    /** 项目名称 */
    private String name;

    /** 目的要求 */
    private String objective;

    /** 检查部位 */
    private String position;

    /** 是否加急 1是 0否 */
    private Integer isUrgent;

    /** 数量 */
    private Integer num;

    /** 开立时间 */
    private LocalDateTime creationTime;

    /** 开立医生ID */
    private Integer doctorId;

    /** 检查人员ID */
    private Integer checkOperId;

    /** 结果录入人员ID */
    private Integer resultOperId;

    /** 检查时间 */
    private LocalDateTime checkTime;

    /** 检查结果 */
    private String result;

    /** 结果时间 */
    private LocalDateTime resultTime;

    /** 状态 0待检查 1已检查 2已出结果 */
    private Integer state;

    /** 记录类型 1检查 2检验 3处置 */
    private Integer recordType;

}
