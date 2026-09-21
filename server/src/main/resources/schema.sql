-- ============================================================
-- 南华大学附属医院 HIS · 核心闭环数据库结构（H2 兼容）
-- 依据 his_20231117.sql 精简重构，保留核心业务闭环所需表
-- ============================================================

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `UserName` VARCHAR(64) NOT NULL COMMENT '登录名',
  `Password` VARCHAR(64) NOT NULL COMMENT '密码',
  `RealName` VARCHAR(64) NOT NULL COMMENT '真实姓名',
  `UseType` INT DEFAULT NULL COMMENT '用户类别 1管理员 2挂号收费员 3门诊医生 4医技医生 5药房操作员 6财务管理员',
  `DocTitleID` INT DEFAULT 84 COMMENT '医生职称ID',
  `IsScheduling` CHAR(1) DEFAULT '否' COMMENT '是否参与排班',
  `DeptID` INT NOT NULL COMMENT '所在科室ID',
  `RegistLeID` INT DEFAULT 0 COMMENT '挂号级别ID',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `DeptCode` VARCHAR(64) NOT NULL COMMENT '科室编码',
  `DeptName` VARCHAR(64) NOT NULL COMMENT '科室名称',
  `DeptCategoryID` INT NOT NULL COMMENT '科室分类',
  `DeptType` INT DEFAULT NULL COMMENT '科室类型',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `constanttype`;
CREATE TABLE `constanttype` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `ConstantTypeCode` VARCHAR(64) NOT NULL COMMENT '常数类别编码',
  `ConstantTypeName` VARCHAR(64) NOT NULL COMMENT '常数类别名称',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `constantitem`;
CREATE TABLE `constantitem` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `ConstantTypeID` INT NOT NULL COMMENT '所属常数类别ID',
  `ConstantCode` VARCHAR(64) NOT NULL COMMENT '常数项编码',
  `ConstantName` VARCHAR(64) NOT NULL COMMENT '常数项名称',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `registlevel`;
CREATE TABLE `registlevel` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `RegistCode` VARCHAR(64) NOT NULL COMMENT '号别编码',
  `RegistName` VARCHAR(64) NOT NULL COMMENT '号别名称',
  `SequenceNo` INT NOT NULL COMMENT '显示顺序号',
  `RegistFee` DECIMAL(8,2) DEFAULT NULL COMMENT '挂号费',
  `RegistQuota` INT NOT NULL COMMENT '挂号限额',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `settlecategory`;
CREATE TABLE `settlecategory` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `SettleCode` VARCHAR(64) NOT NULL COMMENT '类别编码',
  `SettleName` VARCHAR(64) NOT NULL COMMENT '类别名称',
  `SequenceNo` INT DEFAULT NULL COMMENT '显示顺序号',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `expenseclass`;
CREATE TABLE `expenseclass` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `ExpCode` VARCHAR(64) NOT NULL COMMENT '科目编码',
  `ExpName` VARCHAR(64) NOT NULL COMMENT '科目名称',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `disecategory`;
CREATE TABLE `disecategory` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `DicaCode` VARCHAR(64) NOT NULL COMMENT '诊断类别编码',
  `DicaName` VARCHAR(64) NOT NULL COMMENT '诊断类别名称',
  `SequenceNo` INT DEFAULT NULL COMMENT '显示顺序号',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `disease`;
CREATE TABLE `disease` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `DiseaseCode` VARCHAR(64) NOT NULL COMMENT '诊断编码 ICD-10',
  `DiseaseName` VARCHAR(255) NOT NULL COMMENT '诊断名称',
  `DiseaseICD` VARCHAR(64) DEFAULT NULL COMMENT 'ICD编码',
  `DiseCategoryID` INT DEFAULT NULL COMMENT '诊断类别ID',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `realName` VARCHAR(64) NOT NULL,
  `gender` INT NOT NULL,
  `idnumber` CHAR(18) NOT NULL,
  `birthdate` DATE DEFAULT NULL,
  `phone` CHAR(11) DEFAULT NULL,
  `createDate` DATE DEFAULT NULL,
  `channel` INT DEFAULT NULL,
  `delMark` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `medicalcard`;
CREATE TABLE `medicalcard` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `realName` VARCHAR(64) NOT NULL,
  `gender` INT NOT NULL,
  `idnumber` CHAR(18) NOT NULL,
  `birthdate` DATE DEFAULT NULL,
  `phone` CHAR(11) DEFAULT NULL,
  `addr` VARCHAR(255) DEFAULT NULL,
  `cardtype` INT DEFAULT NULL,
  `cardNo` CHAR(9) DEFAULT NULL,
  `customerId` INT DEFAULT NULL,
  `Relationship` INT DEFAULT NULL,
  `createDate` DATE DEFAULT NULL,
  `channel` INT DEFAULT NULL,
  `delMark` INT DEFAULT 1,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `scheduling`;
CREATE TABLE `scheduling` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `SchedDate` DATE NOT NULL COMMENT '排班日期',
  `DeptID` INT NOT NULL COMMENT '科室ID',
  `UserID` INT NOT NULL COMMENT '医生ID',
  `Noon` CHAR(2) NOT NULL COMMENT '午别',
  `RuleID` INT NOT NULL COMMENT '排班规则ID',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  `regNum` INT NOT NULL DEFAULT 0 COMMENT '已挂号数',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `rule`;
CREATE TABLE `rule` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `RuleName` VARCHAR(64) NOT NULL COMMENT '规则名称',
  `DeptID` INT NOT NULL COMMENT '科室ID',
  `UserID` INT NOT NULL COMMENT '医生ID',
  `Weekday` INT NOT NULL COMMENT '星期',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `registwork`;
CREATE TABLE `registwork` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `RegistID` INT NOT NULL COMMENT '挂号ID',
  `RegistTime` DATETIME DEFAULT NULL,
  `RegistDate` DATE DEFAULT NULL,
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `register`;
CREATE TABLE `register` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `CaseNumber` VARCHAR(64) NOT NULL COMMENT '病历号',
  `RealName` VARCHAR(64) DEFAULT NULL COMMENT '姓名',
  `Gender` INT DEFAULT NULL COMMENT '性别 1男 2女',
  `IDnumber` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `BirthDate` DATE DEFAULT NULL COMMENT '出生日期',
  `Age` INT DEFAULT NULL COMMENT '年龄',
  `AgeType` CHAR(1) DEFAULT NULL COMMENT '年龄类型 岁/月/天',
  `HomeAddress` VARCHAR(64) DEFAULT NULL COMMENT '家庭住址',
  `VisitDate` DATE NOT NULL COMMENT '本次看诊日期',
  `Noon` CHAR(2) NOT NULL COMMENT '午别',
  `DeptID` INT NOT NULL COMMENT '本次挂号科室ID',
  `UserID` INT NOT NULL COMMENT '本次挂号医生ID',
  `RegistLeID` INT NOT NULL COMMENT '本次挂号级别ID',
  `SettleID` INT NOT NULL COMMENT '结算类别ID',
  `IsBook` CHAR(1) DEFAULT '否' COMMENT '病历本要否',
  `RegistTime` DATETIME NOT NULL COMMENT '挂号时间',
  `RegisterID` INT NOT NULL COMMENT '挂号员ID',
  `VisitState` INT DEFAULT NULL COMMENT '本次看诊状态 1-已挂号 2-已接诊 3-已诊毕 4-已退号',
  `medicalCardId` VARCHAR(64) DEFAULT NULL COMMENT '就诊卡号',
  `TimeInterval` INT DEFAULT NULL COMMENT '时间段',
  `Channel` INT DEFAULT NULL COMMENT '渠道 1窗口 2小程序',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `medicalrecord`;
CREATE TABLE `medicalrecord` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `CaseNumber` VARCHAR(64) NOT NULL COMMENT '病历号',
  `RegistID` INT NOT NULL COMMENT '挂号ID',
  `Readme` VARCHAR(512) DEFAULT NULL COMMENT '主诉',
  `Present` VARCHAR(512) DEFAULT NULL COMMENT '现病史',
  `PresentTreat` VARCHAR(512) DEFAULT NULL COMMENT '现病治疗情况',
  `History` VARCHAR(512) DEFAULT NULL COMMENT '既往史',
  `Allergy` VARCHAR(512) DEFAULT NULL COMMENT '过敏史',
  `Physique` VARCHAR(512) DEFAULT NULL COMMENT '体格检查',
  `Proposal` VARCHAR(512) DEFAULT NULL COMMENT '检查建议',
  `Careful` VARCHAR(512) DEFAULT NULL COMMENT '注意事项',
  `CheckResult` VARCHAR(512) DEFAULT NULL COMMENT '检查结果',
  `Diagnosis` VARCHAR(512) DEFAULT NULL COMMENT '诊断结果',
  `Handling` VARCHAR(512) DEFAULT NULL COMMENT '处理意见',
  `CaseState` INT DEFAULT NULL COMMENT '病历状态 1-暂存 2-已提交 3-诊毕',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `medicaldisease`;
CREATE TABLE `medicaldisease` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `MedicalID` INT NOT NULL COMMENT '病历ID',
  `RegistID` INT NOT NULL COMMENT '挂号ID',
  `DiseaseID` INT NOT NULL COMMENT '诊断ID',
  `DiagnosisType` INT DEFAULT NULL COMMENT '诊断类型 1-初诊 2-复诊',
  `DiagnosisDoctorID` INT NOT NULL COMMENT '诊断医生ID',
  `DiagnosisTime` DATETIME DEFAULT NULL COMMENT '诊断时间',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `drugs`;
CREATE TABLE `drugs` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `DrugsCode` CHAR(14) NOT NULL COMMENT '药品编码',
  `DrugsName` VARCHAR(512) NOT NULL COMMENT '药品名称',
  `DrugsFormat` VARCHAR(512) DEFAULT NULL COMMENT '药品规格',
  `DrugsUnit` VARCHAR(64) DEFAULT NULL COMMENT '包装单位',
  `Manufacturer` VARCHAR(512) DEFAULT NULL COMMENT '生产厂家',
  `DrugsDosageID` INT DEFAULT NULL COMMENT '药品剂型',
  `DrugsTypeID` INT DEFAULT NULL COMMENT '药品类型',
  `DrugsPrice` DECIMAL(8,2) NOT NULL COMMENT '药品单价',
  `MnemonicCode` VARCHAR(64) DEFAULT NULL COMMENT '拼音助记码',
  `Stock` INT DEFAULT 0 COMMENT '库存数量',
  `WarnStock` INT DEFAULT 20 COMMENT '库存警戒线',
  `ExpiryDate` DATE DEFAULT NULL COMMENT '有效期至',
  `CreationDate` DATETIME NOT NULL COMMENT '创建时间',
  `LastUpdateDate` DATETIME DEFAULT NULL COMMENT '最后修改时间',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `fmeditem`;
CREATE TABLE `fmeditem` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `ItemCode` VARCHAR(64) NOT NULL COMMENT '项目编码',
  `ItemName` VARCHAR(64) NOT NULL COMMENT '项目名称',
  `Format` VARCHAR(64) DEFAULT NULL COMMENT '规格',
  `Price` DECIMAL(8,2) NOT NULL COMMENT '单价',
  `ExpClassID` INT DEFAULT NULL COMMENT '所属费用科目ID',
  `DeptID` INT DEFAULT NULL COMMENT '执行科室ID',
  `MnemonicCode` VARCHAR(64) DEFAULT NULL COMMENT '拼音助记码',
  `CreationDate` DATETIME NOT NULL COMMENT '创建时间',
  `LastUpdateDate` DATETIME DEFAULT NULL COMMENT '最后修改时间',
  `RecordType` INT DEFAULT NULL COMMENT '项目类型 1-检查 2-检验 3-处置',
  `DelMark` INT NOT NULL DEFAULT 1 COMMENT '删除标记',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `prescription`;
CREATE TABLE `prescription` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `MedicalID` INT NOT NULL COMMENT '病历ID',
  `RegistID` INT NOT NULL COMMENT '挂号ID',
  `UserID` INT NOT NULL COMMENT '开立医生ID',
  `PrescriptionName` VARCHAR(64) NOT NULL COMMENT '处方名称',
  `PrescriptionTime` DATETIME NOT NULL COMMENT '开立时间',
  `PrescriptionState` INT NOT NULL COMMENT '处方状态 1-已开立 2-已收费 3-已发药',
  `TotalAmount` DECIMAL(10,2) DEFAULT 0 COMMENT '处方总金额',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `prescriptiondetailed`;
CREATE TABLE `prescriptiondetailed` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `PrescriptionID` INT NOT NULL COMMENT '成药处方ID',
  `DrugsID` INT NOT NULL COMMENT '药品ID',
  `DrugsUsage` VARCHAR(64) DEFAULT NULL COMMENT '用法',
  `Dosage` VARCHAR(64) DEFAULT NULL COMMENT '用量',
  `Frequency` VARCHAR(64) DEFAULT NULL COMMENT '频次',
  `Amount` DECIMAL(8,2) NOT NULL COMMENT '数量',
  `State` INT NOT NULL DEFAULT 2 COMMENT '状态 1-已退药 2-正常 3-已发药',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `checkapply`;
CREATE TABLE `checkapply` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `MedicalID` INT NOT NULL COMMENT '病历ID',
  `RegistID` INT NOT NULL COMMENT '挂号ID',
  `ItemID` INT NOT NULL COMMENT '项目ID',
  `Name` VARCHAR(64) NOT NULL COMMENT '项目名称',
  `Objective` VARCHAR(512) DEFAULT NULL COMMENT '目的要求',
  `Position` VARCHAR(64) DEFAULT NULL COMMENT '检查部位',
  `IsUrgent` INT DEFAULT 0 COMMENT '是否加急',
  `Num` INT DEFAULT NULL COMMENT '数量',
  `CreationTime` DATETIME NOT NULL COMMENT '开立时间',
  `DoctorID` INT DEFAULT NULL COMMENT '开立医生ID',
  `CheckOperID` INT DEFAULT NULL COMMENT '检查人员ID',
  `ResultOperID` INT DEFAULT NULL COMMENT '结果录入人员ID',
  `CheckTime` DATETIME DEFAULT NULL COMMENT '检查时间',
  `Result` VARCHAR(512) DEFAULT NULL COMMENT '检查结果',
  `ResultTime` DATETIME DEFAULT NULL COMMENT '结果时间',
  `State` INT DEFAULT NULL COMMENT '状态 0-待检查 1-已检查 2-已出结果',
  `RecordType` INT NOT NULL COMMENT '记录类型 1-检查 2-检验 3-处置',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `invoice`;
CREATE TABLE `invoice` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `InvoiceNum` VARCHAR(64) NOT NULL COMMENT '发票号码',
  `Money` DECIMAL(8,2) NOT NULL COMMENT '发票金额',
  `State` INT NOT NULL COMMENT '发票状态 1-正常 2-作废',
  `CreationTime` DATETIME NOT NULL COMMENT '收/退费时间',
  `UserID` INT NOT NULL COMMENT '收/退费人员ID',
  `RegistID` INT DEFAULT NULL COMMENT '挂号ID',
  `FeeType` INT DEFAULT NULL COMMENT '收费方式 1-现金 2-微信 3-支付宝 4-银行卡 5-医保',
  `Back` VARCHAR(64) DEFAULT NULL COMMENT '冲红发票号码',
  `DailyState` INT NOT NULL DEFAULT 0 COMMENT '发票状态 0-未日结 1-已提交 2-已审核',
  PRIMARY KEY (`ID`)
);

DROP TABLE IF EXISTS `patientcosts`;
CREATE TABLE `patientcosts` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
  `RegistID` INT NOT NULL COMMENT '挂号ID',
  `InvoiceID` INT NOT NULL COMMENT '发票ID',
  `ItemID` INT NOT NULL COMMENT '项目ID',
  `ItemType` INT NOT NULL COMMENT '项目类型 1-非药品 2-药品',
  `Name` VARCHAR(64) NOT NULL COMMENT '项目名称',
  `Price` DECIMAL(8,2) NOT NULL COMMENT '项目单价',
  `Amount` DECIMAL(8,2) NOT NULL COMMENT '数量',
  `DeptID` INT NOT NULL COMMENT '执行科室ID',
  `Createtime` DATETIME NOT NULL COMMENT '开立时间',
  `CreateOperID` INT NOT NULL COMMENT '开立人员ID',
  `PayTime` DATETIME NOT NULL COMMENT '收/退费时间',
  `RegisterID` INT NOT NULL COMMENT '收/退费人员ID',
  `FeeType` INT NOT NULL COMMENT '收费方式',
  `BackID` INT DEFAULT NULL COMMENT '退费对应记录ID',
  PRIMARY KEY (`ID`)
);
