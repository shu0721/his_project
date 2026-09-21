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
 * 病历诊断关联
 */
@Data
@TableName("medicaldisease")
public class MedicalDisease implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /** 病历ID */
    private Integer medicalId;

    /** 挂号ID */
    private Integer registId;

    /** 诊断ID */
    private Integer diseaseId;

    /** 诊断类型 1初诊 2复诊 */
    private Integer diagnosisType;

    /** 诊断医生ID */
    private Integer diagnosisDoctorId;

    /** 诊断时间 */
    private LocalDateTime diagnosisTime;

}
