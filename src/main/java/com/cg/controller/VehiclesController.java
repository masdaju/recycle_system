package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Vehicles;
import com.cg.service.VehiclesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 载具表 前端控制器
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@RestController
@RequestMapping("/vehicles")
public class VehiclesController {

    @Autowired
    private VehiclesService vehiclesService;

    @GetMapping
    public SaResult list(@RequestParam(required = false) Integer current,
                         @RequestParam(required = false) Integer pageSize,
                         @RequestParam(required = false) String licensePlate,
    @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Vehicles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(licensePlate!= null, Vehicles::getLicensePlate, licensePlate)
                .eq(status!= null, Vehicles::getStatus, status);

        if (current == null || pageSize == null) {
            // 查询所有数据
            List<Vehicles> allVehicles = vehiclesService.list(queryWrapper);

            Page<Vehicles> aPage = new Page<>();
            aPage.setTotal(allVehicles.size());
            aPage.setRecords(allVehicles);
            return SaResult.data(aPage);
        } else {
            // 正常分页查询
            Page<Vehicles> aPage = vehiclesService.page(new Page<>(current, pageSize), queryWrapper);
            return SaResult.data(aPage);
        }
    }

    @PostMapping(value = "/create")
    public SaResult create(@RequestBody Vehicles params) {
        try {
            vehiclesService.save(params);
            return SaResult.ok("载具创建成功");
        } catch (Exception e) {
            return SaResult.error("载具创建失败: " + e.getMessage());
        }
    }
    //通过id批量删除
    @PostMapping(value = "/delete")
    public SaResult delete(@RequestBody List<Long> ids) {
        try {
            vehiclesService.removeBatchByIds(ids);
            return SaResult.ok("载具删除成功");
        } catch (Exception e) {
            return SaResult.error("载具删除失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/update")
    public SaResult update(@RequestBody Vehicles params) {
        try {
            vehiclesService.updateById(params);
            return SaResult.ok("载具更新成功");
        } catch (Exception e) {
            return SaResult.error("载具更新失败: " + e.getMessage());
        }
    }
}