package com.neuedu.his.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.his.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者 Mapper
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
