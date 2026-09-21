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
 * 非药品收费项目
 */
@Data
@TableName("fmeditem")
public class FmedItem implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 项目编码 */
    private String itemCode;

    /** 项目名称 */
    private String itemName;

    /** 规格 */
    private String format;

    /** 单价 */
    private BigDecimal price;

    /** 所属费用科目ID */
    private Integer expClassId;

    /** 执行科室ID */
    private Integer deptId;

    /** 拼音助记码 */
    private String mnemonicCode;

    /** 创建时间 */
    private LocalDateTime creationDate;

    /** 最后修改时间 */
    private LocalDateTime lastUpdateDate;

    /** 项目类型 1检查 2检验 3处置 */
    private Integer recordType;

    /** 删除标记 */
    private Integer delMark;

}
