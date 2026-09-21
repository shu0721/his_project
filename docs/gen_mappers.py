# -*- coding: utf-8 -*-
"""一次性脚手架：批量生成 MyBatis-Plus Mapper 接口"""
import os

BASE = r"C:\workspace\his-cloud\server\src\main\java\com\neuedu\his\mapper"

TEMPLATE = '''package com.neuedu.his.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.his.entity.{cls};
import org.apache.ibatis.annotations.Mapper;

/**
 * {comment} Mapper
 */
@Mapper
public interface {cls}Mapper extends BaseMapper<{cls}> {{
}}
'''

ENTITIES = {
    "User": "用户",
    "Department": "科室",
    "RegistLevel": "挂号级别",
    "SettleCategory": "结算类别",
    "ConstantType": "常数类别",
    "ConstantItem": "常数项",
    "ExpenseClass": "费用科目",
    "DiseCategory": "诊断类别",
    "Disease": "诊断目录",
    "Customer": "患者",
    "MedicalCard": "就诊卡",
    "Scheduling": "医生排班",
    "Register": "挂号记录",
    "MedicalRecord": "门诊病历",
    "MedicalDisease": "病历诊断关联",
    "Drugs": "药品目录",
    "FmedItem": "非药品收费项目",
    "Prescription": "处方",
    "PrescriptionDetail": "处方明细",
    "CheckApply": "医技申请",
    "Invoice": "发票",
    "PatientCosts": "患者费用明细",
}

os.makedirs(BASE, exist_ok=True)
for cls, comment in ENTITIES.items():
    with open(os.path.join(BASE, cls + "Mapper.java"), "w", encoding="utf-8") as fp:
        fp.write(TEMPLATE.format(cls=cls, comment=comment))

print("生成 Mapper %d 个" % len(ENTITIES))
