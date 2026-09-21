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
 * 处方明细
 */
@Data
@TableName("prescriptiondetailed")
public class PrescriptionDetail implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 处方ID */
    private Integer prescriptionId;

    /** 药品ID */
    private Integer drugsId;

    /** 用法 */
    private String drugsUsage;

    /** 用量 */
    private String dosage;

    /** 频次 */
    private String frequency;

    /** 数量 */
    private BigDecimal amount;

    /** 状态 1已退药 2正常 3已发药 */
    private Integer state;

}
