package com.neuedu.his.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.his.entity.Prescription;
import org.apache.ibatis.annotations.Mapper;

/**
 * 处方 Mapper
 */
@Mapper
public interface PrescriptionMapper extends BaseMapper<Prescription> {
}
