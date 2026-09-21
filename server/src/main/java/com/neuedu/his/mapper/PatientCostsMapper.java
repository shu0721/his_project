package com.neuedu.his.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.his.entity.PatientCosts;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者费用明细 Mapper
 */
@Mapper
public interface PatientCostsMapper extends BaseMapper<PatientCosts> {
}
