package com.cg.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.TransportSchedules;
import com.cg.entity.WasteRequests;
import com.cg.service.TransportSchedulesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 运输调度表 前端控制器
 * </p>
 *
 * @author 
 * @since 2024-12-29
 */
@RestController
@Slf4j
@RequestMapping("/transport-schedules")
public class TransportSchedulesController {


    @Autowired
    private TransportSchedulesService transportSchedulesService;

    @GetMapping
    public SaResult list(@RequestParam(required = false) Integer current
            , @RequestParam(required = false) Integer pageSize
           , @RequestParam(required = false) Integer status) {
        if (current == null||pageSize == null) {
            current = 1;
            pageSize = 10;
        }
        Page<TransportSchedules> aPage = transportSchedulesService.getpage(current,pageSize,status);
        return SaResult.data(aPage);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TransportSchedules> getById(@PathVariable("id") String id) {
        return new ResponseEntity<>(transportSchedulesService.getById(id), HttpStatus.OK);
    }
    //处理废品请求把状态设为1（已接收）+添加运输调度和运输人员
    @PostMapping(value = "/create")
    public SaResult create(@RequestBody TransportSchedules params) {
        try {
               transportSchedulesService.create(params);
        } catch (Exception e) {
            return SaResult.error("添加失败");
        }
        return SaResult.ok("添加成功");
    }
    //审核不通过把状态设为3（审核不通过）
    @PostMapping(value = "/notPass")
    public SaResult updateStatus(@RequestParam Long id) {
        if (transportSchedulesService.updateStatus(id)) {
            return SaResult.ok("修改成功");
        }
        return SaResult.error("修改失败");
    }

    @PostMapping(value = "/sendCar")
    public SaResult sedCar(@RequestBody TransportSchedules params) {
        try {
               transportSchedulesService.updateById(params);
        } catch (Exception e) {
            return SaResult.error("添加失败");
        }
        return SaResult.ok("添加成功");
    }

    @GetMapping("getForCollector")
    public SaResult getForCollector(@RequestParam(required = false) Integer current
                                    , @RequestParam(required = false) Integer pageSize
            , @RequestParam(required = false) Integer status
                                   ) {
        if (current == null||pageSize == null) {
            current = 1;
            pageSize = 10;
        }
        Long collectorId = StpUtil.getLoginIdAsLong();
        Page<TransportSchedules> aPage = transportSchedulesService.getpageForCollector(current,pageSize,collectorId,status);
        return SaResult.data(aPage);
    }
    @PostMapping(value = "deleteByIds")
    public SaResult getById(@RequestBody List<Long> ids) {
//        log.info(ids.toString());
        try {
            transportSchedulesService.removeBatchByIds(ids);
        } catch (Exception e) {
            SaResult.error("删除失败: " + e.getMessage());
        }
        return SaResult.ok("删除成功");
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Object> update(@RequestBody TransportSchedules params) {
        transportSchedulesService.updateById(params);
        return new ResponseEntity<>("updated successfully", HttpStatus.OK);
    }
}
