package com.cg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.RequestWaste;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 每个申请Id对应的废品 服务类
 * </p>
 */
public interface RequestWasteService extends IService<RequestWaste> {


    BigDecimal checkQuantity(Map<Long, BigDecimal> map, Long requestId);

    BigDecimal getFromMyRequest(Integer uid);
}
