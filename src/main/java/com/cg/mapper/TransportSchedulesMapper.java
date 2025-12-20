package com.cg.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.TransportSchedules;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 运输调度表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2024-12-29
 */
public interface TransportSchedulesMapper extends BaseMapper<TransportSchedules> {

    Page<TransportSchedules> getpage(Page<TransportSchedules> page, Integer status);
}
