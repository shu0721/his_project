import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 批量生成实体类（一次性脚手架，生成后可删除）
 */
public class GenEntities {

    record Field(String type, String name, String comment) {}

    static void write(String dir, String className, String tableName, String comment, List<Field> fields) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.neuedu.his.entity;\n\n");
        sb.append("import com.baomidou.mybatisplus.annotation.IdType;\n");
        sb.append("import com.baomidou.mybatisplus.annotation.TableId;\n");
        sb.append("import com.baomidou.mybatisplus.annotation.TableName;\n");
        sb.append("import lombok.Data;\n\n");
        sb.append("import java.math.BigDecimal;\n");
        sb.append("import java.time.LocalDate;\n");
        sb.append("import java.time.LocalDateTime;\n");
        sb.append("import java.io.Serializable;\n\n");
        sb.append("/**\n * ").append(comment).append("\n */\n");
        sb.append("@Data\n");
        sb.append("@TableName(\"").append(tableName).append("\")\n");
        sb.append("public class ").append(className).append(" implements Serializable {\n\n");
        sb.append("    @TableId(value = \"ID\", type = IdType.AUTO)\n");
        sb.append("    private Integer id;\n\n");
        for (Field f : fields) {
            if (f.comment() != null && !f.comment().isBlank()) {
                sb.append("    /** ").append(f.comment()).append(" */\n");
            }
            sb.append("    private ").append(f.type()).append(" ").append(f.name()).append(";\n\n");
        }
        sb.append("}\n");

        Path p = Paths.get(dir, className + ".java");
        Files.write(p, sb.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println("generated: " + p);
    }

    static Field f(String type, String name, String comment) {
        return new Field(type, name, comment);
    }

    public static void main(String[] args) throws IOException {
        String dir = args[0];

        write(dir, "Department", "department", "科室", List.of(
                f("String", "deptCode", "科室编码"),
                f("String", "deptName", "科室名称"),
                f("Integer", "deptCategoryId", "科室分类"),
                f("Integer", "deptType", "科室类型"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "RegistLevel", "registlevel", "挂号级别", List.of(
                f("String", "registCode", "号别编码"),
                f("String", "registName", "号别名称"),
                f("Integer", "sequenceNo", "显示顺序号"),
                f("BigDecimal", "registFee", "挂号费"),
                f("Integer", "registQuota", "挂号限额"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "SettleCategory", "settlecategory", "结算类别", List.of(
                f("String", "settleCode", "类别编码"),
                f("String", "settleName", "类别名称"),
                f("Integer", "sequenceNo", "显示顺序号"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "ConstantType", "constanttype", "常数类别", List.of(
                f("String", "constantTypeCode", "常数类别编码"),
                f("String", "constantTypeName", "常数类别名称"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "ConstantItem", "constantitem", "常数项", List.of(
                f("Integer", "constantTypeId", "所属常数类别ID"),
                f("String", "constantCode", "常数项编码"),
                f("String", "constantName", "常数项名称"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "ExpenseClass", "expenseclass", "费用科目", List.of(
                f("String", "expCode", "科目编码"),
                f("String", "expName", "科目名称"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "DiseCategory", "disecategory", "诊断类别", List.of(
                f("String", "dicaCode", "诊断类别编码"),
                f("String", "dicaName", "诊断类别名称"),
                f("Integer", "sequenceNo", "显示顺序号"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "Disease", "disease", "诊断目录（ICD-10）", List.of(
                f("String", "diseaseCode", "诊断编码"),
                f("String", "diseaseName", "诊断名称"),
                f("String", "diseaseIcd", "ICD 编码"),
                f("Integer", "diseCategoryId", "诊断类别ID"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "Customer", "customer", "患者（客户）", List.of(
                f("String", "realName", "真实姓名"),
                f("Integer", "gender", "性别 1男 2女"),
                f("String", "idnumber", "身份证号"),
                f("LocalDate", "birthdate", "出生日期"),
                f("String", "phone", "手机号"),
                f("LocalDate", "createDate", "建档日期"),
                f("Integer", "channel", "渠道 1窗口 2小程序"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "MedicalCard", "medicalcard", "就诊卡", List.of(
                f("String", "realName", "真实姓名"),
                f("Integer", "gender", "性别 1男 2女"),
                f("String", "idnumber", "身份证号"),
                f("LocalDate", "birthdate", "出生日期"),
                f("String", "phone", "手机号"),
                f("String", "addr", "家庭住址"),
                f("Integer", "cardtype", "卡类型"),
                f("String", "cardNo", "卡号"),
                f("Integer", "customerId", "所属客户ID"),
                f("Integer", "relationship", "亲属关系"),
                f("LocalDate", "createDate", "建卡日期"),
                f("Integer", "channel", "渠道"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "Scheduling", "scheduling", "医生排班", List.of(
                f("LocalDate", "schedDate", "排班日期"),
                f("Integer", "deptId", "科室ID"),
                f("Integer", "userId", "医生ID"),
                f("String", "noon", "午别"),
                f("Integer", "ruleId", "排班规则ID"),
                f("Integer", "delMark", "删除标记"),
                f("Integer", "regNum", "已挂号数")
        ));

        write(dir, "Register", "register", "挂号记录", List.of(
                f("String", "caseNumber", "病历号"),
                f("String", "realName", "姓名"),
                f("Integer", "gender", "性别 1男 2女"),
                f("String", "idnumber", "身份证号"),
                f("LocalDate", "birthDate", "出生日期"),
                f("Integer", "age", "年龄"),
                f("String", "ageType", "年龄类型"),
                f("String", "homeAddress", "家庭住址"),
                f("LocalDate", "visitDate", "本次看诊日期"),
                f("String", "noon", "午别"),
                f("Integer", "deptId", "本次挂号科室ID"),
                f("Integer", "userId", "本次挂号医生ID"),
                f("Integer", "registLeId", "本次挂号级别ID"),
                f("Integer", "settleId", "结算类别ID"),
                f("String", "isBook", "病历本要否"),
                f("LocalDateTime", "registTime", "挂号时间"),
                f("Integer", "registerId", "挂号员ID"),
                f("Integer", "visitState", "看诊状态 1已挂号 2已接诊 3已诊毕 4已退号"),
                f("String", "medicalCardId", "就诊卡号"),
                f("Integer", "timeInterval", "时间段"),
                f("Integer", "channel", "渠道 1窗口 2小程序")
        ));

        write(dir, "MedicalRecord", "medicalrecord", "门诊病历", List.of(
                f("String", "caseNumber", "病历号"),
                f("Integer", "registId", "挂号ID"),
                f("String", "readme", "主诉"),
                f("String", "present", "现病史"),
                f("String", "presentTreat", "现病治疗情况"),
                f("String", "history", "既往史"),
                f("String", "allergy", "过敏史"),
                f("String", "physique", "体格检查"),
                f("String", "proposal", "检查建议"),
                f("String", "careful", "注意事项"),
                f("String", "checkResult", "检查结果"),
                f("String", "diagnosis", "诊断结果"),
                f("String", "handling", "处理意见"),
                f("Integer", "caseState", "病历状态 1暂存 2已提交 3诊毕")
        ));

        write(dir, "MedicalDisease", "medicaldisease", "病历诊断关联", List.of(
                f("Integer", "medicalId", "病历ID"),
                f("Integer", "registId", "挂号ID"),
                f("Integer", "diseaseId", "诊断ID"),
                f("Integer", "diagnosisType", "诊断类型 1初诊 2复诊"),
                f("Integer", "diagnosisDoctorId", "诊断医生ID"),
                f("LocalDateTime", "diagnosisTime", "诊断时间")
        ));

        write(dir, "Drugs", "drugs", "药品目录", List.of(
                f("String", "drugsCode", "药品编码"),
                f("String", "drugsName", "药品名称"),
                f("String", "drugsFormat", "药品规格"),
                f("String", "drugsUnit", "包装单位"),
                f("String", "manufacturer", "生产厂家"),
                f("Integer", "drugsDosageId", "药品剂型"),
                f("Integer", "drugsTypeId", "药品类型"),
                f("BigDecimal", "drugsPrice", "药品单价"),
                f("String", "mnemonicCode", "拼音助记码"),
                f("Integer", "stock", "库存数量"),
                f("Integer", "warnStock", "库存警戒线"),
                f("LocalDate", "expiryDate", "有效期至"),
                f("LocalDateTime", "creationDate", "创建时间"),
                f("LocalDateTime", "lastUpdateDate", "最后修改时间"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "FmedItem", "fmeditem", "非药品收费项目", List.of(
                f("String", "itemCode", "项目编码"),
                f("String", "itemName", "项目名称"),
                f("String", "format", "规格"),
                f("BigDecimal", "price", "单价"),
                f("Integer", "expClassId", "所属费用科目ID"),
                f("Integer", "deptId", "执行科室ID"),
                f("String", "mnemonicCode", "拼音助记码"),
                f("LocalDateTime", "creationDate", "创建时间"),
                f("LocalDateTime", "lastUpdateDate", "最后修改时间"),
                f("Integer", "recordType", "项目类型 1检查 2检验 3处置"),
                f("Integer", "delMark", "删除标记")
        ));

        write(dir, "Prescription", "prescription", "处方", List.of(
                f("Integer", "medicalId", "病历ID"),
                f("Integer", "registId", "挂号ID"),
                f("Integer", "userId", "开立医生ID"),
                f("String", "prescriptionName", "处方名称"),
                f("LocalDateTime", "prescriptionTime", "开立时间"),
                f("Integer", "prescriptionState", "处方状态 1已开立 2已收费 3已发药"),
                f("BigDecimal", "totalAmount", "处方总金额")
        ));

        write(dir, "PrescriptionDetail", "prescriptiondetailed", "处方明细", List.of(
                f("Integer", "prescriptionId", "处方ID"),
                f("Integer", "drugsId", "药品ID"),
                f("String", "drugsUsage", "用法"),
                f("String", "dosage", "用量"),
                f("String", "frequency", "频次"),
                f("BigDecimal", "amount", "数量"),
                f("Integer", "state", "状态 1已退药 2正常 3已发药")
        ));

        write(dir, "CheckApply", "checkapply", "医技申请（检查/检验/处置）", List.of(
                f("Integer", "medicalId", "病历ID"),
                f("Integer", "registId", "挂号ID"),
                f("Integer", "itemId", "项目ID"),
                f("String", "name", "项目名称"),
                f("String", "objective", "目的要求"),
                f("String", "position", "检查部位"),
                f("Integer", "isUrgent", "是否加急 1是 0否"),
                f("Integer", "num", "数量"),
                f("LocalDateTime", "creationTime", "开立时间"),
                f("Integer", "doctorId", "开立医生ID"),
                f("Integer", "checkOperId", "检查人员ID"),
                f("Integer", "resultOperId", "结果录入人员ID"),
                f("LocalDateTime", "checkTime", "检查时间"),
                f("String", "result", "检查结果"),
                f("LocalDateTime", "resultTime", "结果时间"),
                f("Integer", "state", "状态 0待检查 1已检查 2已出结果"),
                f("Integer", "recordType", "记录类型 1检查 2检验 3处置")
        ));

        write(dir, "Invoice", "invoice", "发票", List.of(
                f("String", "invoiceNum", "发票号码"),
                f("BigDecimal", "money", "发票金额"),
                f("Integer", "state", "发票状态 1正常 2作废"),
                f("LocalDateTime", "creationTime", "收/退费时间"),
                f("Integer", "userId", "收/退费人员ID"),
                f("Integer", "registId", "挂号ID"),
                f("Integer", "feeType", "收费方式 1现金 2微信 3支付宝 4银行卡 5医保"),
                f("String", "back", "冲红发票号码"),
                f("Integer", "dailyState", "日结状态 0未日结 1已提交 2已审核")
        ));

        write(dir, "PatientCosts", "patientcosts", "患者费用明细", List.of(
                f("Integer", "registId", "挂号ID"),
                f("Integer", "invoiceId", "发票ID"),
                f("Integer", "itemId", "项目ID"),
                f("Integer", "itemType", "项目类型 1非药品 2药品"),
                f("String", "name", "项目名称"),
                f("BigDecimal", "price", "项目单价"),
                f("BigDecimal", "amount", "数量"),
                f("Integer", "deptId", "执行科室ID"),
                f("LocalDateTime", "createtime", "开立时间"),
                f("Integer", "createOperId", "开立人员ID"),
                f("LocalDateTime", "payTime", "收/退费时间"),
                f("Integer", "registerId", "收/退费人员ID"),
                f("Integer", "feeType", "收费方式"),
                f("Integer", "backId", "退费对应记录ID")
        ));
    }
}
