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
 * 处方
 */
@Data
@TableName("prescription")
public class Prescription implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 病历ID */
    private Integer medicalId;

    /** 挂号ID */
    private Integer registId;

    /** 开立医生ID */
    private Integer userId;

    /** 处方名称 */
    private String prescriptionName;

    /** 开立时间 */
    private LocalDateTime prescriptionTime;

    /** 处方状态 1已开立 2已收费 3已发药 */
    private Integer prescriptionState;

    /** 处方总金额 */
    private BigDecimal totalAmount;

}
