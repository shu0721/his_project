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
 * 常数类别
 */
@Data
@TableName("constanttype")
public class ConstantType implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 常数类别编码 */
    private String constantTypeCode;

    /** 常数类别名称 */
    private String constantTypeName;

    /** 删除标记 */
    private Integer delMark;

}
