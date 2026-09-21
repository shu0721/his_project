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
 * 患者费用明细
 */
@Data
@TableName("patientcosts")
public class PatientCosts implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 挂号ID */
    private Integer registId;

    /** 发票ID */
    private Integer invoiceId;

    /** 项目ID */
    private Integer itemId;

    /** 项目类型 1非药品 2药品 */
    private Integer itemType;

    /** 项目名称 */
    private String name;

    /** 项目单价 */
    private BigDecimal price;

    /** 数量 */
    private BigDecimal amount;

    /** 执行科室ID */
    private Integer deptId;

    /** 开立时间 */
    private LocalDateTime createtime;

    /** 开立人员ID */
    private Integer createOperId;

    /** 收/退费时间 */
    private LocalDateTime payTime;

    /** 收/退费人员ID */
    private Integer registerId;

    /** 收费方式 */
    private Integer feeType;

    /** 退费对应记录ID */
    private Integer backId;

}
