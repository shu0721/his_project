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
 * 诊断目录（ICD-10）
 */
@Data
@TableName("disease")
public class Disease implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 诊断编码 */
    private String diseaseCode;

    /** 诊断名称 */
    private String diseaseName;

    /** ICD 编码 */
    private String diseaseIcd;

    /** 诊断类别ID */
    private Integer diseCategoryId;

    /** 删除标记 */
    private Integer delMark;

}
