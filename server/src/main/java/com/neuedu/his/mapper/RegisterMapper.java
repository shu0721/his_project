package com.neuedu.his.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.his.entity.Register;
import org.apache.ibatis.annotations.Mapper;

/**
 * 挂号记录 Mapper
 */
@Mapper
public interface RegisterMapper extends BaseMapper<Register> {
}
