package com.neuedu.his.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.his.entity.Invoice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 发票 Mapper
 */
@Mapper
public interface InvoiceMapper extends BaseMapper<Invoice> {
}
