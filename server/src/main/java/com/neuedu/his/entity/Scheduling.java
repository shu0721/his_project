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
 * 医生排班
 */
@Data
@TableName("scheduling")
public class Scheduling implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 排班日期 */
    private LocalDate schedDate;

    /** 科室ID */
    private Integer deptId;

    /** 医生ID */
    private Integer userId;

    /** 午别 */
    private String noon;

    /** 排班规则ID */
    private Integer ruleId;

    /** 删除标记 */
    private Integer delMark;

    /** 已挂号数 */
    private Integer regNum;

}
