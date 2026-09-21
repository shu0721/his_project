package com.neuedu.his.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.his.entity.MedicalRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门诊病历 Mapper
 */
@Mapper
public interface MedicalRecordMapper extends BaseMapper<MedicalRecord> {
}
