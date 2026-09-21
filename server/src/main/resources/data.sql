-- ============================================================
-- 东软云 HIS · 种子数据
-- 账号密码规则：登录名 + 数字后缀（原作者约定），统一为 123456
-- ============================================================

-- ---------- 用户（6 大角色） ----------
INSERT INTO `user` VALUES (1,'admin','123456','系统管理员',1,84,'否',1,0,1);
INSERT INTO `user` VALUES (2,'reg01','123456','收费员小周',2,84,'否',1,0,1);
INSERT INTO `user` VALUES (3,'reg02','123456','收费员小林',2,84,'否',1,0,1);
INSERT INTO `user` VALUES (10,'chenjx','123456','陈景行',3,81,'是',1,1,1);
INSERT INTO `user` VALUES (11,'zhoum','123456','周明',3,83,'是',1,2,1);
INSERT INTO `user` VALUES (12,'liuyy','123456','刘雨燕',3,81,'是',2,1,1);
INSERT INTO `user` VALUES (20,'wangxm','123456','王雪梅',4,84,'否',3,0,1);
INSERT INTO `user` VALUES (21,'zhaok','123456','赵凯',4,84,'否',3,0,1);
INSERT INTO `user` VALUES (30,'pharm01','123456','药房小何',5,84,'否',4,0,1);
INSERT INTO `user` VALUES (31,'pharm02','123456','药房小许',5,84,'否',4,0,1);
INSERT INTO `user` VALUES (40,'fin01','123456','财务小郑',6,84,'否',1,0,1);

-- ---------- 科室 ----------
INSERT INTO `department` VALUES (1,'XXGNK','心血管内科',11,165,1);
INSERT INTO `department` VALUES (2,'SJNK','神经内科',11,165,1);
INSERT INTO `department` VALUES (3,'HXNK','呼吸内科',11,165,1);
INSERT INTO `department` VALUES (4,'XYF','药房',12,166,1);
INSERT INTO `department` VALUES (5,'JYK','检验科',13,167,1);
INSERT INTO `department` VALUES (6,'YXK','影像科',13,167,1);
INSERT INTO `department` VALUES (7,'GHK','挂号处',14,168,1);

-- ---------- 常数类别 ----------
INSERT INTO `constanttype` VALUES (1,'Gender','性别',1);
INSERT INTO `constanttype` VALUES (2,'Noon','午别',1);
INSERT INTO `constanttype` VALUES (3,'CardType','就诊卡类型',1);
INSERT INTO `constanttype` VALUES (4,'DocTitle','医生职称',1);
INSERT INTO `constanttype` VALUES (5,'FeeType','收费方式',1);
INSERT INTO `constanttype` VALUES (6,'Relationship','亲属关系',1);

-- ---------- 常数项 ----------
INSERT INTO `constantitem` VALUES (1,1,'M','男',1);
INSERT INTO `constantitem` VALUES (2,1,'F','女',1);
INSERT INTO `constantitem` VALUES (3,2,'AM','上午',1);
INSERT INTO `constantitem` VALUES (4,2,'PM','下午',1);
INSERT INTO `constantitem` VALUES (5,3,'VIP','VIP卡',1);
INSERT INTO `constantitem` VALUES (6,3,'ORD','普通卡',1);
INSERT INTO `constantitem` VALUES (7,4,'ZGYS','主任医师',1);
INSERT INTO `constantitem` VALUES (8,4,'FZRYS','副主任医师',1);
INSERT INTO `constantitem` VALUES (9,4,'ZZYS','主治医师',1);
INSERT INTO `constantitem` VALUES (10,4,'ZYYSH','住院医师',1);
INSERT INTO `constantitem` VALUES (11,5,'CASH','现金',1);
INSERT INTO `constantitem` VALUES (12,5,'WX','微信',1);
INSERT INTO `constantitem` VALUES (13,5,'ZFB','支付宝',1);
INSERT INTO `constantitem` VALUES (14,5,'BANK','银行卡',1);
INSERT INTO `constantitem` VALUES (15,5,'YB','医保',1);
INSERT INTO `constantitem` VALUES (16,6,'SELF','本人',1);
INSERT INTO `constantitem` VALUES (17,6,'SPOUSE','配偶',1);
INSERT INTO `constantitem` VALUES (18,6,'CHILD','子女',1);
INSERT INTO `constantitem` VALUES (19,6,'PARENT','父母',1);

-- ---------- 挂号级别 ----------
INSERT INTO `registlevel` VALUES (1,'ZJH','专家号',1,50.00,20,1);
INSERT INTO `registlevel` VALUES (2,'PTH','普通号',2,8.00,30,1);
INSERT INTO `registlevel` VALUES (3,'ZXH','专科号',3,20.00,25,1);
INSERT INTO `registlevel` VALUES (4,'JBH','急诊号',4,15.00,40,1);

-- ---------- 结算类别 ----------
INSERT INTO `settlecategory` VALUES (1,'js001','自费',1,1);
INSERT INTO `settlecategory` VALUES (2,'js002','市医保',2,1);
INSERT INTO `settlecategory` VALUES (3,'js003','省医保',3,1);
INSERT INTO `settlecategory` VALUES (4,'js004','商业保险',4,1);

-- ---------- 费用科目 ----------
INSERT INTO `expenseclass` VALUES (1,'FY001','挂号费',1);
INSERT INTO `expenseclass` VALUES (2,'FY002','检查费',1);
INSERT INTO `expenseclass` VALUES (3,'FY003','检验费',1);
INSERT INTO `expenseclass` VALUES (4,'FY004','处置费',1);
INSERT INTO `expenseclass` VALUES (5,'FY005','药品费',1);
INSERT INTO `expenseclass` VALUES (6,'FY006','材料费',1);

-- ---------- 诊断类别 ----------
INSERT INTO `disecategory` VALUES (1,'A00-B99','某些传染病和寄生虫病',1,1);
INSERT INTO `disecategory` VALUES (2,'J00-J99','呼吸系统疾病',2,1);
INSERT INTO `disecategory` VALUES (3,'I00-I99','循环系统疾病',3,1);
INSERT INTO `disecategory` VALUES (4,'K00-K93','消化系统疾病',4,1);

-- ---------- 诊断目录（ICD-10） ----------
INSERT INTO `disease` VALUES (1,'J06.900','急性上呼吸道感染','J06.9',2,1);
INSERT INTO `disease` VALUES (2,'J18.900','肺炎','J18.9',2,1);
INSERT INTO `disease` VALUES (3,'J45.900','支气管哮喘','J45.9',2,1);
INSERT INTO `disease` VALUES (4,'I10.x00','高血压','I10',3,1);
INSERT INTO `disease` VALUES (5,'I25.100','冠心病','I25.1',3,1);
INSERT INTO `disease` VALUES (6,'K29.700','慢性胃炎','K29.7',4,1);
INSERT INTO `disease` VALUES (7,'K35.900','急性阑尾炎','K35.9',4,1);
INSERT INTO `disease` VALUES (8,'A09.900','感染性腹泻','A09.9',1,1);

-- ---------- 患者（客户） ----------
INSERT INTO `customer` VALUES (1,'张伟',1,'110101199401151234','1994-01-15','13800138001','2026-09-01',2,1);
INSERT INTO `customer` VALUES (2,'李芳',2,'110101198103220045','1981-03-22','13800138002','2026-09-02',2,1);
INSERT INTO `customer` VALUES (3,'王强',1,'110101200507088899','2005-07-08','13800138003','2026-09-05',1,1);
INSERT INTO `customer` VALUES (4,'刘敏',2,'110101197512019988','1975-12-01','13800138004','2026-09-10',2,1);

-- ---------- 就诊卡 ----------
INSERT INTO `medicalcard` VALUES (1,'张伟',1,'110101199401151234','1994-01-15','13800138001','北京市朝阳区建国路88号',2,'M00000001',1,1,'2026-09-01',2,1);
INSERT INTO `medicalcard` VALUES (2,'李芳',2,'110101198103220045','1981-03-22','13800138002','北京市海淀区中关村大街1号',2,'M00000002',2,1,'2026-09-02',2,1);
INSERT INTO `medicalcard` VALUES (3,'王强',1,'110101200507088899','2005-07-08','13800138003','北京市西城区西单北大街5号',2,'M00000003',3,1,'2026-09-05',1,1);
INSERT INTO `medicalcard` VALUES (4,'刘敏',2,'110101197512019988','1975-12-01','13800138004','北京市东城区王府井大街20号',2,'M00000004',4,1,'2026-09-10',2,1);

-- ---------- 排班（今日） ----------
INSERT INTO `scheduling` VALUES (1,CURRENT_DATE,1,10,'上午',1,1,5);
INSERT INTO `scheduling` VALUES (2,CURRENT_DATE,1,10,'下午',1,1,3);
INSERT INTO `scheduling` VALUES (3,CURRENT_DATE,3,11,'上午',1,1,8);
INSERT INTO `scheduling` VALUES (4,CURRENT_DATE,3,11,'下午',1,1,4);
INSERT INTO `scheduling` VALUES (5,CURRENT_DATE,2,12,'上午',1,1,2);
INSERT INTO `scheduling` VALUES (6,CURRENT_DATE,2,12,'下午',1,1,1);

-- ---------- 挂号记录（今日，含候诊） ----------
INSERT INTO `register` VALUES (1,'MZ20260919001','张伟',1,'110101199401151234','1994-01-15',32,'岁','北京市朝阳区建国路88号',CURRENT_DATE,'上午',1,10,1,2,'否',TIMESTAMPADD(MINUTE, 552, CURRENT_DATE),2,1,'M00000001',1,2);
INSERT INTO `register` VALUES (2,'MZ20260919002','李芳',2,'110101198103220045','1981-03-22',45,'岁','北京市海淀区中关村大街1号',CURRENT_DATE,'上午',2,12,1,3,'否',TIMESTAMPADD(MINUTE, 527, CURRENT_DATE),2,2,'M00000002',1,2);
INSERT INTO `register` VALUES (3,'MZ20260919003','王强',1,'110101200507088899','2005-07-08',21,'岁','北京市西城区西单北大街5号',CURRENT_DATE,'上午',1,10,2,1,'否',TIMESTAMPADD(MINUTE, 575, CURRENT_DATE),3,1,'M00000003',2,2);
INSERT INTO `register` VALUES (4,'MZ20260919004','刘敏',2,'110101197512019988','1975-12-01',50,'岁','北京市东城区王府井大街20号',CURRENT_DATE,'下午',3,11,1,2,'否',TIMESTAMPADD(MINUTE, 800, CURRENT_DATE),2,1,'M00000004',1,1);
INSERT INTO `register` VALUES (5,'MZ20260919005','张伟',1,'110101199401151234','1994-01-15',32,'岁','北京市朝阳区建国路88号',CURRENT_DATE,'下午',1,10,2,2,'否',TIMESTAMPADD(MINUTE, 845, CURRENT_DATE),3,1,'M00000001',1,1);

-- ---------- 药品（含库存与效期） ----------
INSERT INTO `drugs` VALUES (1,'XY001000000001','阿莫西林胶囊','0.25g×24粒','盒','华北制药股份有限公司',1,1,18.00,'AMXLJN',320,50,'2027-06-30','2026-01-10',NULL,1);
INSERT INTO `drugs` VALUES (2,'XY001000000002','连花清瘟颗粒','6g×10袋','盒','石家庄以岭药业',1,1,25.00,'LHQLWKL',186,50,'2026-12-31','2026-02-15',NULL,1);
INSERT INTO `drugs` VALUES (3,'XY001000000003','布洛芬缓释胶囊','0.3g×20粒','盒','中美天津史克',1,1,22.50,'BLFHSJN',8,50,'2027-03-31','2026-01-20',NULL,1);
INSERT INTO `drugs` VALUES (4,'XY001000000004','头孢克洛干混悬剂','0.125g×12袋','盒','苏州中化药品',1,1,32.80,'TBKLCGXJ',95,30,'2027-01-31','2026-03-01',NULL,1);
INSERT INTO `drugs` VALUES (5,'XY001000000005','奥美拉唑肠溶胶囊','20mg×14粒','盒','阿斯利康制药',1,1,45.00,'AMLZCRJN',140,40,'2027-08-31','2026-01-05',NULL,1);
INSERT INTO `drugs` VALUES (6,'XY001000000006','复方甘草片','100片','瓶','太极集团',1,1,9.80,'FFGCP',210,60,'2027-05-31','2025-12-20',NULL,1);
INSERT INTO `drugs` VALUES (7,'XY001000000007','维生素C片','100mg×100片','瓶','华中药业',1,1,6.50,'WSSCP',360,80,'2027-09-30','2026-04-01',NULL,1);
INSERT INTO `drugs` VALUES (8,'XY001000000008','硝苯地平缓释片','20mg×30片','盒','拜耳医药',1,1,28.60,'XBDPHSP',72,40,'2026-11-30','2026-02-01',NULL,1);
INSERT INTO `drugs` VALUES (9,'XY001000000009','阿奇霉素分散片','0.25g×6片','盒','辉瑞制药',1,1,38.00,'AQMSFSP',56,30,'2027-04-30','2026-03-15',NULL,1);
INSERT INTO `drugs` VALUES (10,'XY001000000010','氯雷他定片','10mg×12片','盒','上海先灵葆雅',1,1,19.90,'LLTDP',12,40,'2027-07-31','2026-01-25',NULL,1);

-- ---------- 非药品收费项目 ----------
INSERT INTO `fmeditem` VALUES (1,'JC001','血常规(五分类)','项',80.00,3,5,'XCG','2026-01-01',NULL,2,1);
INSERT INTO `fmeditem` VALUES (2,'JC002','尿常规','项',25.00,3,5,'NCG','2026-01-01',NULL,2,1);
INSERT INTO `fmeditem` VALUES (3,'JC003','凝血四项','项',120.00,3,5,'NXSX','2026-01-01',NULL,2,1);
INSERT INTO `fmeditem` VALUES (4,'JC004','心肌酶谱','项',150.00,3,5,'XJMP','2026-01-01',NULL,2,1);
INSERT INTO `fmeditem` VALUES (5,'JC005','肝功能八项','项',180.00,3,5,'GGNBX','2026-01-01',NULL,2,1);
INSERT INTO `fmeditem` VALUES (6,'JC006','腹部B超','次',200.00,2,6,'FBBBC','2026-01-01',NULL,1,1);
INSERT INTO `fmeditem` VALUES (7,'JC007','胸部正位片','次',120.00,2,6,'XBZWP','2026-01-01',NULL,1,1);
INSERT INTO `fmeditem` VALUES (8,'JC008','心电图','次',40.00,1,6,'XDT','2026-01-01',NULL,1,1);
INSERT INTO `fmeditem` VALUES (9,'CZ001','静脉输液','次',45.00,4,1,'JMSY','2026-01-01',NULL,3,1);
INSERT INTO `fmeditem` VALUES (10,'CZ002','换药','次',30.00,4,1,'HY','2026-01-01',NULL,3,1);
INSERT INTO `fmeditem` VALUES (11,'CZ003','清创缝合','次',150.00,4,1,'QCFH','2026-01-01',NULL,3,1);
INSERT INTO `fmeditem` VALUES (12,'CZ004','雾化吸入','次',35.00,4,3,'WHXR','2026-01-01',NULL,3,1);

-- ---------- 排班规则 ----------
INSERT INTO `rule` VALUES (1,'心血管内科周一上午排班',1,10,1,1);
INSERT INTO `rule` VALUES (2,'呼吸内科周一上午排班',3,11,1,1);

-- ---------- 病历（张伟 · 已完成接诊） ----------
INSERT INTO `medicalrecord` VALUES (1,'MZ20260919001',1,'咳嗽、咽痛 3 天','患者 3 天前受凉后出现咳嗽，以干咳为主，伴咽痛、低热，最高体温 37.8℃，无胸闷气促，无咳血。','自服连花清瘟颗粒，症状未见明显缓解。','平素体健，否认高血压、糖尿病史。','青霉素过敏','T 37.5℃，P 82 次/分，R 18 次/分，BP 118/76 mmHg。咽部充血，扁桃体I度肿大，双肺呼吸音清，未闻及干湿性啰音。','建议血常规检查明确感染性质。','多饮温水，注意休息，忌辛辣刺激饮食。',NULL,'急性上呼吸道感染','对症抗感染治疗。',3);

-- ---------- 诊断关联 ----------
INSERT INTO `medicaldisease` VALUES (1,1,1,1,1,10,TIMESTAMPADD(MINUTE, 570, CURRENT_DATE));

-- ---------- 检验申请 ----------
INSERT INTO `checkapply` VALUES (1,1,1,1,'血常规(五分类)','明确感染性质','静脉血',0,1,TIMESTAMPADD(MINUTE, 565, CURRENT_DATE),10,NULL,NULL,NULL,'白细胞 11.2×10^9/L，中性粒细胞比例 78%，提示细菌感染',TIMESTAMPADD(MINUTE, 585, CURRENT_DATE),2,2);
INSERT INTO `checkapply` VALUES (2,2,2,3,'凝血四项','术前常规筛查','静脉血',0,1,TIMESTAMPADD(MINUTE, 535, CURRENT_DATE),12,20,NULL,NULL,NULL,NULL,1,2);
INSERT INTO `checkapply` VALUES (3,2,2,4,'心肌酶谱','排查心肌损伤','静脉血',1,1,TIMESTAMPADD(MINUTE, 538, CURRENT_DATE),12,NULL,NULL,NULL,NULL,NULL,0,2);
INSERT INTO `checkapply` VALUES (4,4,4,7,'胸部正位片','排查肺部感染','胸部',0,1,TIMESTAMPADD(MINUTE, 810, CURRENT_DATE),11,NULL,NULL,NULL,NULL,NULL,0,1);

-- ---------- 处方（张伟 · 已开立待发药） ----------
INSERT INTO `prescription` VALUES (1,1,1,10,'成药处方',TIMESTAMPADD(MINUTE, 590, CURRENT_DATE),1,61.00);
INSERT INTO `prescription` VALUES (2,2,2,12,'成药处方',TIMESTAMPADD(MINUTE, 545, CURRENT_DATE),3,128.50);

INSERT INTO `prescriptiondetailed` VALUES (1,1,1,'口服','2粒','每日3次',2.00,2);
INSERT INTO `prescriptiondetailed` VALUES (2,1,2,'开水冲服','1袋','每日3次',1.00,2);
INSERT INTO `prescriptiondetailed` VALUES (3,2,5,'口服','1粒','每日1次',1.00,3);
INSERT INTO `prescriptiondetailed` VALUES (4,2,3,'口服','1粒','每日2次',1.00,2);
INSERT INTO `prescriptiondetailed` VALUES (5,2,10,'口服','1片','每日1次',2.00,2);
