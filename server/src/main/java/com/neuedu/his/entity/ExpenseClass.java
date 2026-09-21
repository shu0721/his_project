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
 * 费用科目
 */
@Data
@TableName("expenseclass")
public class ExpenseClass implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 科目编码 */
    private String expCode;

    /** 科目名称 */
    private String expName;

    /** 删除标记 */
    private Integer delMark;

}
