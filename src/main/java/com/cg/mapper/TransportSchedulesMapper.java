package com.cg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.TransportSchedules;

/**
 * <p>
 * 运输调度表 Mapper 接口
 * </p>
 */
public interface TransportSchedulesMapper extends BaseMapper<TransportSchedules> {

    Page<TransportSchedules> getpage(Page<TransportSchedules> page, Integer status);
}
