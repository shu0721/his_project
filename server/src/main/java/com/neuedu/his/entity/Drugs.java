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
 * 药品目录
 */
@Data
@TableName("drugs")
public class Drugs implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 药品编码 */
    private String drugsCode;

    /** 药品名称 */
    private String drugsName;

    /** 药品规格 */
    private String drugsFormat;

    /** 包装单位 */
    private String drugsUnit;

    /** 生产厂家 */
    private String manufacturer;

    /** 药品剂型 */
    private Integer drugsDosageId;

    /** 药品类型 */
    private Integer drugsTypeId;

    /** 药品单价 */
    private BigDecimal drugsPrice;

    /** 拼音助记码 */
    private String mnemonicCode;

    /** 库存数量 */
    private Integer stock;

    /** 库存警戒线 */
    private Integer warnStock;

    /** 有效期至 */
    private LocalDate expiryDate;

    /** 创建时间 */
    private LocalDateTime creationDate;

    /** 最后修改时间 */
    private LocalDateTime lastUpdateDate;

    /** 删除标记 */
    private Integer delMark;

}
