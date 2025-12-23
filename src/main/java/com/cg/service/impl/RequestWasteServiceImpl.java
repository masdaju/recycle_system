package com.cg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.RequestWaste;
import com.cg.entity.TransportSchedules;
import com.cg.entity.Vehicles;
import com.cg.entity.WasteRequests;
import com.cg.entity.view.VWaste;
import com.cg.mapper.RequestWasteMapper;
import com.cg.mapper.TransportSchedulesMapper;
import com.cg.mapper.VehiclesMapper;
import com.cg.mapper.WasteRequestsMapper;
import com.cg.service.RequestWasteService;
import com.cg.service.VWasteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 每个申请Id对应的废品 服务实现类
 * 该类实现了 RequestWasteService 接口，处理与每个申请ID对应的废品相关的业务逻辑。
 * </p>
 */
@Service
public class RequestWasteServiceImpl extends ServiceImpl<RequestWasteMapper, RequestWaste> implements RequestWasteService {
    // 自动注入 TransportSchedulesMapper 实例，用于操作 TransportSchedules 表
    @Autowired
    private TransportSchedulesMapper transportSchedulesMapper;
    // 自动注入 WasteRequestsMapper 实例，用于操作 WasteRequests 表
    @Autowired
    private WasteRequestsMapper wasteRequestsMapper;
    // 自动注入 VehiclesMapper 实例，用于操作 Vehicles 表
    @Autowired
    private VehiclesMapper vehiclesMapper;
    // 自动注入 VWasteService 实例，用于操作 VWaste 视图
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

    //草泥马重构一下
    /**
     * 检查废品数量并更新相关信息，同时计算总金额。
     * 该方法使用了事务管理，确保操作的原子性。
     *
     * @param map       包含废品ID和对应数量的映射
     * @param requestId 废品申请的ID
     * @return 计算得到的总金额，精确到小数点后两位
     */
    @Override
    //Propagation.REQUIRED: 如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务
    @Transactional(propagation = Propagation.REQUIRED)
    public BigDecimal checkQuantity(Map<Long, BigDecimal> map, Long requestId) {
        // 创建可重入锁，保证线程安全
        ReentrantLock lock = new ReentrantLock();
        // 调用 calculateTotalAmount 方法计算总金额
        BigDecimal totalSum = calculateTotalAmount(map, requestId);
        try {
            // 获取锁
            lock.lock();
            // 调用 updateWasteQuantities 方法更新废品数量
            updateWasteQuantities(map, requestId);
            // 调用 updateSystemStatuses 方法更新系统状态
            updateSystemStatuses(requestId);
        } catch (Exception e) {
            // 若出现异常，抛出运行时异常
            throw new RuntimeException("更新失败");
        } finally {
            // 释放锁
            lock.unlock();
        }
        return totalSum;
    }

    @Override
    public BigDecimal getFromMyRequest(Integer uid) {
        List<WasteRequests> wasteRequests = wasteRequestsMapper.selectList(new LambdaQueryWrapper<WasteRequests>().eq(WasteRequests::getUserId, uid));
        // 获取用户所有废品申请的ID
        List<Long> longList = wasteRequests.stream().map(WasteRequests::getRequestId).toList();
        //
        LambdaQueryWrapper<VWaste> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(VWaste::getRequestId, longList);
        List<VWaste> vWastes = vWasteMapper.list(queryWrapper);
        // 计算总和
        return vWastes.stream().map(VWaste::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * 根据废品ID和对应数量更新废品的质量信息。
     *
     * @param map       包含废品ID和对应数量的映射
     * @param requestId 废品申请的ID
     */
    private void updateWasteQuantities(Map<Long, BigDecimal> map, Long requestId) {
        // 遍历映射中的每个废品ID和数量
        map.forEach((wasteId, quantity) -> {
            // 创建 LambdaUpdateWrapper 对象，用于构建更新条件
            LambdaUpdateWrapper<RequestWaste> wrapper = new LambdaUpdateWrapper<>();
            // 添加更新条件：根据申请ID和废品ID进行筛选
            wrapper.eq(RequestWaste::getRequestId, requestId)
                    .eq(RequestWaste::getWasteId, wasteId)
                    // 设置更新的字段：将废品数量更新为传入的数量
                    .set(RequestWaste::getQuantity, quantity);
            // 调用 update 方法进行更新操作
            update(wrapper);
        });
    }

    /**
     * 根据废品ID和对应数量计算总金额。
     *
     * @param map       包含废品ID和对应数量的映射
     * @param requestId 废品申请的ID
     * @return 计算得到的总金额，精确到小数点后两位
     */
    private BigDecimal calculateTotalAmount(Map<Long, BigDecimal> map, Long requestId) {
        // 根据申请ID查询废品信息
        List<VWaste> requestPrices = vWasteMapper.list(
                new LambdaQueryWrapper<VWaste>().eq(VWaste::getRequestId, requestId));
        // 使用流操作计算总金额
        return requestPrices.stream()
                // 将每个废品的价格乘以对应的数量
                .map(rp -> rp.getPrice().multiply(map.get(rp.getWasteId())))
                // 对所有结果进行累加
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                // 精确到小数点后两位，使用四舍五入模式
                .setScale(2, RoundingMode.HALF_DOWN);
    }

    /**
     * 更新系统中与废品申请相关的状态信息。
     * 包括运输状态、车辆状态和废品申请状态。
     *
     * @param requestId 废品申请的ID
     */
    private void updateSystemStatuses(Long requestId) {
        // 调用 updateTransportANDVehicle 方法更新运输和车辆状态
        updateTransportANDVehicle(requestId);
        // 调用 updateWasteRequestStatus 方法更新废品申请状态
        updateWasteRequestStatus(requestId);
    }

    /**
     * 更新废品申请的状态。
     *
     * @param requestId 废品申请的ID
     */
    private void updateWasteRequestStatus(Long requestId) {
        // 创建 LambdaUpdateWrapper 对象，用于构建更新条件
        LambdaUpdateWrapper<WasteRequests> wrapper = new LambdaUpdateWrapper<>();
        // 添加更新条件：根据申请ID进行筛选
        wrapper.eq(WasteRequests::getRequestId, requestId)
                // 设置更新的字段：将废品申请状态更新为 2
                .set(WasteRequests::getStatus, 2);
        // 调用 wasteRequestsMapper 的 update 方法进行更新操作
        wasteRequestsMapper.update(wrapper);
    }

    /**
     * 更新运输计划和车辆的状态。
     *
     * @param requestId 废品申请的ID
     */
    private void updateTransportANDVehicle(Long requestId) {
        // 创建 LambdaQueryWrapper 对象，用于构建查询条件
        LambdaQueryWrapper<TransportSchedules> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件：根据申请ID进行筛选
        queryWrapper.eq(TransportSchedules::getRequestId, requestId);
        // 调用 transportSchedulesMapper 的 selectOne 方法查询运输计划信息
        TransportSchedules transportSchedules = transportSchedulesMapper.selectOne(queryWrapper);
        // 将运输计划的状态更新为 2
        transportSchedules.setStatus(2);
        // 调用 transportSchedulesMapper 的 updateById 方法更新运输计划信息
        transportSchedulesMapper.updateById(transportSchedules);
        // 创建 LambdaUpdateWrapper 对象，用于构建更新条件
        LambdaUpdateWrapper<Vehicles> updateWrapper = new LambdaUpdateWrapper<>();
        // 设置更新的字段：将车辆状态更新为 1
        updateWrapper.set(Vehicles::getStatus, 1)
                // 添加更新条件：根据车辆ID进行筛选
                .eq(Vehicles::getVehicleId, transportSchedules.getVehicleId());
        // 调用 vehiclesMapper 的 update 方法更新车辆信息
        vehiclesMapper.update(updateWrapper);
    }
}