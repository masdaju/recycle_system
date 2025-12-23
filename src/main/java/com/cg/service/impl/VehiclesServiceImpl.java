package com.cg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.Vehicles;
import com.cg.mapper.VehiclesMapper;
import com.cg.service.VehiclesService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 载具表 服务实现类
 * </p>
 */
@Service
public class VehiclesServiceImpl extends ServiceImpl<VehiclesMapper, Vehicles> implements VehiclesService {

}
