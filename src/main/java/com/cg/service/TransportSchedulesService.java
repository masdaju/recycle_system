package com.cg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.TransportSchedules;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 运输调度表 服务类
 * </p>
 *
 * @author 
 * @since 2024-12-29
 */
public interface TransportSchedulesService extends IService<TransportSchedules> {



    boolean create(TransportSchedules params);

    Page<TransportSchedules> getpage(Integer current, Integer pageSize, Integer status);

    Page<TransportSchedules> getpageForCollector(Integer current, Integer pageSize, Long collectorId, Integer status);

    boolean updateStatus(Long id);
}
