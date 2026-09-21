package com.neuedu.his.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.his.entity.Scheduling;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生排班 Mapper
 */
@Mapper
public interface SchedulingMapper extends BaseMapper<Scheduling> {
}
