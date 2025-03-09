package com.cg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cg.entity.RequestWaste;
import com.cg.entity.TransportSchedules;
import com.cg.entity.Vehicles;
import com.cg.entity.WasteRequests;
import com.cg.entity.view.VWaste;
import com.cg.mapper.RequestWasteMapper;
import com.cg.mapper.TransportSchedulesMapper;
import com.cg.mapper.VehiclesMapper;
import com.cg.mapper.WasteRequestsMapper;
import com.cg.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 每个申请Id对应的废品 服务实现类
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@Service
public class RequestWasteServiceImpl extends ServiceImpl<RequestWasteMapper, RequestWaste> implements RequestWasteService {
    @Autowired
    private TransportSchedulesMapper transportSchedulesMapper;

    @Autowired
    private WasteRequestsMapper wasteRequestsMapper;;
    @Autowired
    private VehiclesMapper vehiclesMapper;
    @Autowired
    private VWasteService vWasteMapper;
//    @Override
//    @Transactional
//    public BigDecimal checkQuantity(Map<Long, BigDecimal> map, Long requestId) {
//
//        map.forEach((k, v) -> {
//            LambdaUpdateWrapper<RequestWaste> wrapper = new LambdaUpdateWrapper<>();
//            wrapper.eq(RequestWaste::getRequestId, requestId).eq(RequestWaste::getWasteId, k).set(RequestWaste::getQuantity, v);
//            update(wrapper);
//        });
//        LambdaQueryWrapper<VWaste> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(VWaste::getRequestId, requestId);
//        List<VWaste> requestPrices  = vWasteMapper.list(queryWrapper);
//
//        List<BigDecimal> results = new ArrayList<>();
//        // 遍历 requestPrices 列表
//        requestPrices.forEach(requestPrice -> {
//            results.add(map.get(requestPrice.getWasteId()).multiply(requestPrice.getPrice()));
//
//        });
//        // 计算总和
//        BigDecimal totalSum  = results.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
//        //更新运输计划状态
//        LambdaQueryWrapper<TransportSchedules> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(TransportSchedules::getRequestId, requestId);
//        TransportSchedules transportSchedules = transportSchedulesMapper.selectOne(wrapper);
//        transportSchedules.setStatus(2);
//        transportSchedulesMapper.updateById(transportSchedules);
//        //更新车辆状态
//        LambdaUpdateWrapper<Vehicles> wrapper3 = new LambdaUpdateWrapper<>();
//        wrapper3.set(Vehicles::getStatus, 1).eq(Vehicles::getVehicleId, transportSchedules.getVehicleId());
//        vehiclesMapper.update(wrapper3);
//        //更新废品申请状态
//        LambdaUpdateWrapper<WasteRequests> wrapper2 = new LambdaUpdateWrapper<>();
//        wrapper2.eq(WasteRequests::getRequestId, requestId).set(WasteRequests::getStatus, 2);
//        wasteRequestsMapper.update(wrapper2);
//        //返回总金额
//        return totalSum;
//    }


    @Override
    @Transactional
    public BigDecimal checkQuantity(Map<Long, BigDecimal> map, Long requestId) {
        updateWasteQuantities(map, requestId);
        BigDecimal totalSum = calculateTotalAmount(map,requestId);
        updateSystemStatuses(requestId);
        return totalSum;
    }

    private void updateWasteQuantities(Map<Long, BigDecimal> map, Long requestId) {
        // 更新废品质量
        map.forEach((wasteId, quantity) -> {
            LambdaUpdateWrapper<RequestWaste> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(RequestWaste::getRequestId, requestId)
                    .eq(RequestWaste::getWasteId, wasteId)
                    .set(RequestWaste::getQuantity, quantity);
            update(wrapper);
        });
    }

    private BigDecimal calculateTotalAmount(Map<Long, BigDecimal> map,Long requestId) {
        //获取废品信息
        List<VWaste> requestPrices = vWasteMapper.list(
                new LambdaQueryWrapper<VWaste>().eq(VWaste::getRequestId, requestId));
        // 计算数总和值精确到小数点后两位并返回
        return requestPrices.stream()
                .map(rp -> rp.getPrice().multiply(map.get(rp.getWasteId())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_DOWN);
    }

    private void updateSystemStatuses(Long requestId) {
        updateTransportANDVehicle(requestId);
        updateWasteRequestStatus(requestId);
    }

    private void updateWasteRequestStatus(Long requestId) {
        //更新废品申请状态
        LambdaUpdateWrapper<WasteRequests> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WasteRequests::getRequestId, requestId).set(WasteRequests::getStatus, 2);
        wasteRequestsMapper.update(wrapper);

    }

    private void updateTransportANDVehicle(Long requestId) {
        //更新车辆状态和运输状态
        LambdaQueryWrapper<TransportSchedules> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TransportSchedules::getRequestId, requestId);
        TransportSchedules transportSchedules = transportSchedulesMapper.selectOne(queryWrapper);
        transportSchedules.setStatus(2);
        transportSchedulesMapper.updateById(transportSchedules);
        //更新车辆状态
        LambdaUpdateWrapper<Vehicles> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Vehicles::getStatus, 1).eq(Vehicles::getVehicleId, transportSchedules.getVehicleId());
        vehiclesMapper.update(updateWrapper);
    }
}
