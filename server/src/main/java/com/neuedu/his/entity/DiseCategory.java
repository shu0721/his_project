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
 * 诊断类别
 */
@Data
@TableName("disecategory")
public class DiseCategory implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 诊断类别编码 */
    private String dicaCode;

    /** 诊断类别名称 */
    private String dicaName;

    /** 显示顺序号 */
    private Integer sequenceNo;

    /** 删除标记 */
    private Integer delMark;

}
