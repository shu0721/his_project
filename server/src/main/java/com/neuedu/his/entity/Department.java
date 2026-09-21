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
 * 科室
 */
@Data
@TableName("department")
public class Department implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 科室编码 */
    private String deptCode;

    /** 科室名称 */
    private String deptName;

    /** 科室分类 */
    private Integer deptCategoryId;

    /** 科室类型 */
    private Integer deptType;

    /** 删除标记 */
    private Integer delMark;

}
