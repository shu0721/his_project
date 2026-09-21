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
 * 结算类别
 */
@Data
@TableName("settlecategory")
public class SettleCategory implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 类别编码 */
    private String settleCode;

    /** 类别名称 */
    private String settleName;

    /** 显示顺序号 */
    private Integer sequenceNo;

    /** 删除标记 */
    private Integer delMark;

}
