package com.cg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.TransportSchedules;
import com.cg.entity.Vehicles;
import com.cg.entity.WasteRequests;
import com.cg.mapper.TransportSchedulesMapper;
import com.cg.service.TransportSchedulesService;
import com.cg.service.VehiclesService;
import com.cg.service.WasteRequestsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 *
 *
 *
 * <p>
 * 运输调度表 服务实现类
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-29
 */
@Service
public class TransportSchedulesServiceImpl extends ServiceImpl<TransportSchedulesMapper, TransportSchedules> implements TransportSchedulesService {

    @Resource
    private WasteRequestsService wasteRequestsService;
    @Resource
    private VehiclesService vehiclesService;
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.DEFAULT)
    public boolean create(TransportSchedules transportSchedules) {
        System.out.println(transportSchedules);
        try {
            // 对第一张表进行操作
            // 更新车辆状态改为已分配
            LambdaUpdateWrapper<Vehicles> updateWrapper2 = new LambdaUpdateWrapper<>();
            updateWrapper2.set(Vehicles::getStatus,2).eq(Vehicles::getVehicleId, transportSchedules.getVehicleId());
            vehiclesService.update(updateWrapper2);
            // 对第二张表进行操作
            LambdaUpdateWrapper<WasteRequests> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(WasteRequests::getStatus,1).eq(WasteRequests::getRequestId, transportSchedules.getRequestId());
            wasteRequestsService.update(updateWrapper);
            // 对第三张表进行操作
            save(transportSchedules);
        } catch (Exception e) {
            // 捕获任何异常，发生异常时事务会自动回滚，保证第一张表数据恢复到操作前状态
            // 这里可以根据实际需求记录日志或者进行其他错误处理
            System.err.println("操作出现异常，事务回滚: " + e.getMessage());
            throw e; // 重新抛出异常，以便上层调用者能感知到异常情况

        }
        return true;
    }

    @Override
    public Page<TransportSchedules> getpage(Integer current, Integer pageSize, Integer status) {
         Page<TransportSchedules> page = new Page<>(current, pageSize);
        return getBaseMapper().getpage(page,status);
    }


    @Override
    public Page<TransportSchedules> getpageForCollector(Integer current, Integer pageSize, Long collectorId, Integer status) {
        Page<TransportSchedules> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<TransportSchedules> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TransportSchedules::getCollectorId, collectorId)
                .eq(status!=null,TransportSchedules::getStatus, status);
        return page(page, queryWrapper);
    }

    @Override
    public boolean updateStatus(Long id) {
        try {
            LambdaUpdateWrapper<WasteRequests> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(WasteRequests::getStatus, 3).eq(WasteRequests::getRequestId, id);
            wasteRequestsService.update(updateWrapper);
        }catch (Exception e){
            return false;
        }
       return true;
    }

}
