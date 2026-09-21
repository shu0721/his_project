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
 * 常数项
 */
@Data
@TableName("constantitem")
public class ConstantItem implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 所属常数类别ID */
    private Integer constantTypeId;

    /** 常数项编码 */
    private String constantCode;

    /** 常数项名称 */
    private String constantName;

    /** 删除标记 */
    private Integer delMark;

}
