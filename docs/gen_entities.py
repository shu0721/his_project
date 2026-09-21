# -*- coding: utf-8 -*-
"""一次性脚手架：批量生成实体类到 server/src/main/java/com/neuedu/his/entity"""
import os

BASE = r"C:\workspace\his-cloud\server\src\main\java\com\neuedu\his\entity"

HEADER = '''package com.neuedu.his.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * {comment}
 */
@Data
@TableName("{table}")
public class {cls} implements Serializable {{

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

'''

ENTITIES = {
    "Department": ("department", "科室", [
        ("String", "deptCode", "科室编码"),
        ("String", "deptName", "科室名称"),
        ("Integer", "deptCategoryId", "科室分类"),
        ("Integer", "deptType", "科室类型"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "RegistLevel": ("registlevel", "挂号级别", [
        ("String", "registCode", "号别编码"),
        ("String", "registName", "号别名称"),
        ("Integer", "sequenceNo", "显示顺序号"),
        ("BigDecimal", "registFee", "挂号费"),
        ("Integer", "registQuota", "挂号限额"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "SettleCategory": ("settlecategory", "结算类别", [
        ("String", "settleCode", "类别编码"),
        ("String", "settleName", "类别名称"),
        ("Integer", "sequenceNo", "显示顺序号"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "ConstantType": ("constanttype", "常数类别", [
        ("String", "constantTypeCode", "常数类别编码"),
        ("String", "constantTypeName", "常数类别名称"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "ConstantItem": ("constantitem", "常数项", [
        ("Integer", "constantTypeId", "所属常数类别ID"),
        ("String", "constantCode", "常数项编码"),
        ("String", "constantName", "常数项名称"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "ExpenseClass": ("expenseclass", "费用科目", [
        ("String", "expCode", "科目编码"),
        ("String", "expName", "科目名称"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "DiseCategory": ("disecategory", "诊断类别", [
        ("String", "dicaCode", "诊断类别编码"),
        ("String", "dicaName", "诊断类别名称"),
        ("Integer", "sequenceNo", "显示顺序号"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "Disease": ("disease", "诊断目录（ICD-10）", [
        ("String", "diseaseCode", "诊断编码"),
        ("String", "diseaseName", "诊断名称"),
        ("String", "diseaseIcd", "ICD 编码"),
        ("Integer", "diseCategoryId", "诊断类别ID"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "Customer": ("customer", "患者（客户）", [
        ("String", "realName", "真实姓名"),
        ("Integer", "gender", "性别 1男 2女"),
        ("String", "idnumber", "身份证号"),
        ("LocalDate", "birthdate", "出生日期"),
        ("String", "phone", "手机号"),
        ("LocalDate", "createDate", "建档日期"),
        ("Integer", "channel", "渠道 1窗口 2小程序"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "MedicalCard": ("medicalcard", "就诊卡", [
        ("String", "realName", "真实姓名"),
        ("Integer", "gender", "性别 1男 2女"),
        ("String", "idnumber", "身份证号"),
        ("LocalDate", "birthdate", "出生日期"),
        ("String", "phone", "手机号"),
        ("String", "addr", "家庭住址"),
        ("Integer", "cardtype", "卡类型"),
        ("String", "cardNo", "卡号"),
        ("Integer", "customerId", "所属客户ID"),
        ("Integer", "relationship", "亲属关系"),
        ("LocalDate", "createDate", "建卡日期"),
        ("Integer", "channel", "渠道"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "Scheduling": ("scheduling", "医生排班", [
        ("LocalDate", "schedDate", "排班日期"),
        ("Integer", "deptId", "科室ID"),
        ("Integer", "userId", "医生ID"),
        ("String", "noon", "午别"),
        ("Integer", "ruleId", "排班规则ID"),
        ("Integer", "delMark", "删除标记"),
        ("Integer", "regNum", "已挂号数"),
    ]),
    "Register": ("register", "挂号记录", [
        ("String", "caseNumber", "病历号"),
        ("String", "realName", "姓名"),
        ("Integer", "gender", "性别 1男 2女"),
        ("String", "idnumber", "身份证号"),
        ("LocalDate", "birthDate", "出生日期"),
        ("Integer", "age", "年龄"),
        ("String", "ageType", "年龄类型"),
        ("String", "homeAddress", "家庭住址"),
        ("LocalDate", "visitDate", "本次看诊日期"),
        ("String", "noon", "午别"),
        ("Integer", "deptId", "本次挂号科室ID"),
        ("Integer", "userId", "本次挂号医生ID"),
        ("Integer", "registLeId", "本次挂号级别ID"),
        ("Integer", "settleId", "结算类别ID"),
        ("String", "isBook", "病历本要否"),
        ("LocalDateTime", "registTime", "挂号时间"),
        ("Integer", "registerId", "挂号员ID"),
        ("Integer", "visitState", "看诊状态 1已挂号 2已接诊 3已诊毕 4已退号"),
        ("String", "medicalCardId", "就诊卡号"),
        ("Integer", "timeInterval", "时间段"),
        ("Integer", "channel", "渠道 1窗口 2小程序"),
    ]),
    "MedicalRecord": ("medicalrecord", "门诊病历", [
        ("String", "caseNumber", "病历号"),
        ("Integer", "registId", "挂号ID"),
        ("String", "readme", "主诉"),
        ("String", "present", "现病史"),
        ("String", "presentTreat", "现病治疗情况"),
        ("String", "history", "既往史"),
        ("String", "allergy", "过敏史"),
        ("String", "physique", "体格检查"),
        ("String", "proposal", "检查建议"),
        ("String", "careful", "注意事项"),
        ("String", "checkResult", "检查结果"),
        ("String", "diagnosis", "诊断结果"),
        ("String", "handling", "处理意见"),
        ("Integer", "caseState", "病历状态 1暂存 2已提交 3诊毕"),
    ]),
    "MedicalDisease": ("medicaldisease", "病历诊断关联", [
        ("Integer", "medicalId", "病历ID"),
        ("Integer", "registId", "挂号ID"),
        ("Integer", "diseaseId", "诊断ID"),
        ("Integer", "diagnosisType", "诊断类型 1初诊 2复诊"),
        ("Integer", "diagnosisDoctorId", "诊断医生ID"),
        ("LocalDateTime", "diagnosisTime", "诊断时间"),
    ]),
    "Drugs": ("drugs", "药品目录", [
        ("String", "drugsCode", "药品编码"),
        ("String", "drugsName", "药品名称"),
        ("String", "drugsFormat", "药品规格"),
        ("String", "drugsUnit", "包装单位"),
        ("String", "manufacturer", "生产厂家"),
        ("Integer", "drugsDosageId", "药品剂型"),
        ("Integer", "drugsTypeId", "药品类型"),
        ("BigDecimal", "drugsPrice", "药品单价"),
        ("String", "mnemonicCode", "拼音助记码"),
        ("Integer", "stock", "库存数量"),
        ("Integer", "warnStock", "库存警戒线"),
        ("LocalDate", "expiryDate", "有效期至"),
        ("LocalDateTime", "creationDate", "创建时间"),
        ("LocalDateTime", "lastUpdateDate", "最后修改时间"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "FmedItem": ("fmeditem", "非药品收费项目", [
        ("String", "itemCode", "项目编码"),
        ("String", "itemName", "项目名称"),
        ("String", "format", "规格"),
        ("BigDecimal", "price", "单价"),
        ("Integer", "expClassId", "所属费用科目ID"),
        ("Integer", "deptId", "执行科室ID"),
        ("String", "mnemonicCode", "拼音助记码"),
        ("LocalDateTime", "creationDate", "创建时间"),
        ("LocalDateTime", "lastUpdateDate", "最后修改时间"),
        ("Integer", "recordType", "项目类型 1检查 2检验 3处置"),
        ("Integer", "delMark", "删除标记"),
    ]),
    "Prescription": ("prescription", "处方", [
        ("Integer", "medicalId", "病历ID"),
        ("Integer", "registId", "挂号ID"),
        ("Integer", "userId", "开立医生ID"),
        ("String", "prescriptionName", "处方名称"),
        ("LocalDateTime", "prescriptionTime", "开立时间"),
        ("Integer", "prescriptionState", "处方状态 1已开立 2已收费 3已发药"),
        ("BigDecimal", "totalAmount", "处方总金额"),
    ]),
    "PrescriptionDetail": ("prescriptiondetailed", "处方明细", [
        ("Integer", "prescriptionId", "处方ID"),
        ("Integer", "drugsId", "药品ID"),
        ("String", "drugsUsage", "用法"),
        ("String", "dosage", "用量"),
        ("String", "frequency", "频次"),
        ("BigDecimal", "amount", "数量"),
        ("Integer", "state", "状态 1已退药 2正常 3已发药"),
    ]),
    "CheckApply": ("checkapply", "医技申请（检查/检验/处置）", [
        ("Integer", "medicalId", "病历ID"),
        ("Integer", "registId", "挂号ID"),
        ("Integer", "itemId", "项目ID"),
        ("String", "name", "项目名称"),
        ("String", "objective", "目的要求"),
        ("String", "position", "检查部位"),
        ("Integer", "isUrgent", "是否加急 1是 0否"),
        ("Integer", "num", "数量"),
        ("LocalDateTime", "creationTime", "开立时间"),
        ("Integer", "doctorId", "开立医生ID"),
        ("Integer", "checkOperId", "检查人员ID"),
        ("Integer", "resultOperId", "结果录入人员ID"),
        ("LocalDateTime", "checkTime", "检查时间"),
        ("String", "result", "检查结果"),
        ("LocalDateTime", "resultTime", "结果时间"),
        ("Integer", "state", "状态 0待检查 1已检查 2已出结果"),
        ("Integer", "recordType", "记录类型 1检查 2检验 3处置"),
    ]),
    "Invoice": ("invoice", "发票", [
        ("String", "invoiceNum", "发票号码"),
        ("BigDecimal", "money", "发票金额"),
        ("Integer", "state", "发票状态 1正常 2作废"),
        ("LocalDateTime", "creationTime", "收/退费时间"),
        ("Integer", "userId", "收/退费人员ID"),
        ("Integer", "registId", "挂号ID"),
        ("Integer", "feeType", "收费方式 1现金 2微信 3支付宝 4银行卡 5医保"),
        ("String", "back", "冲红发票号码"),
        ("Integer", "dailyState", "日结状态 0未日结 1已提交 2已审核"),
    ]),
    "PatientCosts": ("patientcosts", "患者费用明细", [
        ("Integer", "registId", "挂号ID"),
        ("Integer", "invoiceId", "发票ID"),
        ("Integer", "itemId", "项目ID"),
        ("Integer", "itemType", "项目类型 1非药品 2药品"),
        ("String", "name", "项目名称"),
        ("BigDecimal", "price", "项目单价"),
        ("BigDecimal", "amount", "数量"),
        ("Integer", "deptId", "执行科室ID"),
        ("LocalDateTime", "createtime", "开立时间"),
        ("Integer", "createOperId", "开立人员ID"),
        ("LocalDateTime", "payTime", "收/退费时间"),
        ("Integer", "registerId", "收/退费人员ID"),
        ("Integer", "feeType", "收费方式"),
        ("Integer", "backId", "退费对应记录ID"),
    ]),
}

os.makedirs(BASE, exist_ok=True)
count = 0
for cls, (table, comment, fields) in ENTITIES.items():
    body = HEADER.format(comment=comment, table=table, cls=cls)
    for ftype, fname, fcomment in fields:
        body += "    /** %s */\n    private %s %s;\n\n" % (fcomment, ftype, fname)
    body += "}\n"
    with open(os.path.join(BASE, cls + ".java"), "w", encoding="utf-8") as fp:
        fp.write(body)
    count += 1

print("生成实体类 %d 个 -> %s" % (count, BASE))
