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
 * 就诊卡
 */
@Data
@TableName("medicalcard")
public class MedicalCard implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 真实姓名 */
    private String realName;

    /** 性别 1男 2女 */
    private Integer gender;

    /** 身份证号 */
    private String idnumber;

    /** 出生日期 */
    private LocalDate birthdate;

    /** 手机号 */
    private String phone;

    /** 家庭住址 */
    private String addr;

    /** 卡类型 */
    private Integer cardtype;

    /** 卡号 */
    private String cardNo;

    /** 所属客户ID */
    private Integer customerId;

    /** 亲属关系 */
    private Integer relationship;

    /** 建卡日期 */
    private LocalDate createDate;

    /** 渠道 */
    private Integer channel;

    /** 删除标记 */
    private Integer delMark;

}
