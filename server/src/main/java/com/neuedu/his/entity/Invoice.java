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
 * 发票
 */
@Data
@TableName("invoice")
public class Invoice implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 发票号码 */
    private String invoiceNum;

    /** 发票金额 */
    private BigDecimal money;

    /** 发票状态 1正常 2作废 */
    private Integer state;

    /** 收/退费时间 */
    private LocalDateTime creationTime;

    /** 收/退费人员ID */
    private Integer userId;

    /** 挂号ID */
    private Integer registId;

    /** 收费方式 1现金 2微信 3支付宝 4银行卡 5医保 */
    private Integer feeType;

    /** 冲红发票号码 */
    private String back;

    /** 日结状态 0未日结 1已提交 2已审核 */
    private Integer dailyState;

}
