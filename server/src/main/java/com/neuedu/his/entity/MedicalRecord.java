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
 * 门诊病历
 */
@Data
@TableName("medicalrecord")
public class MedicalRecord implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 病历号 */
    private String caseNumber;

    /** 挂号ID */
    private Integer registId;

    /** 主诉 */
    private String readme;

    /** 现病史 */
    private String present;

    /** 现病治疗情况 */
    private String presentTreat;

    /** 既往史 */
    private String history;

    /** 过敏史 */
    private String allergy;

    /** 体格检查 */
    private String physique;

    /** 检查建议 */
    private String proposal;

    /** 注意事项 */
    private String careful;

    /** 检查结果 */
    private String checkResult;

    /** 诊断结果 */
    private String diagnosis;

    /** 处理意见 */
    private String handling;

    /** 病历状态 1暂存 2已提交 3诊毕 */
    private Integer caseState;

}
