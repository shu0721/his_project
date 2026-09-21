package com.neuedu.his.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.his.entity.MedicalDisease;
import org.apache.ibatis.annotations.Mapper;

/**
 * 病历诊断关联 Mapper
 */
@Mapper
public interface MedicalDiseaseMapper extends BaseMapper<MedicalDisease> {
}
