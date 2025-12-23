package com.cg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.WasteRequests;

import java.util.List;

/**
 * <p>
 * 废品请求表 服务类
 * </p>
 */
public interface WasteRequestsService extends IService<WasteRequests> {

    boolean saveWasteRequests(Long requestId, List<Long> wid);

    boolean updateWasteRequests(Long requestId, List<Long> wid);

    Page<WasteRequests> getPage(Integer current, Integer pageSize);

    Page<WasteRequests> getRequestByStatuspage(Integer current, Integer pageSize, Integer status);
}
