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
 * 挂号级别
 */
@Data
@TableName("registlevel")
public class RegistLevel implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 号别编码 */
    private String registCode;

    /** 号别名称 */
    private String registName;

    /** 显示顺序号 */
    private Integer sequenceNo;

    /** 挂号费 */
    private BigDecimal registFee;

    /** 挂号限额 */
    private Integer registQuota;

    /** 删除标记 */
    private Integer delMark;

}
