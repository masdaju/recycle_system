package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.*;
import com.cg.entity.view.VWaste;
import com.cg.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 每个申请Id对应的废品 前端控制器
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@RestController
@RequestMapping("/request-waste")
public class RequestWasteController {

    @Autowired
    private RequestWasteService requestWasteService;

    @Autowired
    private VWasteService vWasteService;

    @Autowired
    private UserService userService;

    @Autowired
    private WasteRequestsService wasteRequestsService;;

    @GetMapping
    public SaResult list(@RequestParam Long requestId
           ) {
        LambdaQueryWrapper<VWaste> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq( VWaste::getRequestId, requestId);
        List<VWaste> list = vWasteService.list(wrapper);
        return SaResult.data(list);
    }

    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        RequestWaste requestWaste = requestWasteService.getById(id);
        if (requestWaste!= null) {
            return SaResult.data(requestWaste);
        } else {
            return SaResult.error("未找到指定申请对应的废品");
        }
    }

    @PostMapping("checkQuantity")
    public SaResult checkQuantity(@RequestBody Map<Long, BigDecimal> map, @RequestParam Long requestId) {
//        创建一个锁对象
        ReentrantLock lock = new ReentrantLock();
        //保证同一时间只有一个线程可以执行
        try {
            lock.lock();
            //更新用户金额
            BigDecimal amount = requestWasteService.checkQuantity(map, requestId);
//            BigDecimal amount = vWasteService.toamount(requestId);
            LambdaUpdateWrapper<User> wrapper1 = new LambdaUpdateWrapper<>();
            wrapper1.eq(User::getId, (wasteRequestsService.getById(requestId)).getUserId()).setSql("amount = amount + " + amount);
            userService.update(wrapper1);
            return SaResult.data(amount);
        }catch (Exception e){
            return SaResult.error("更新失败: " + e.getMessage());
        }finally {
            lock.unlock();
        }
    }

    @PostMapping(value = "/create")
    public SaResult create(@RequestBody RequestWaste params) {
        try {
            requestWasteService.save(params);
            return SaResult.ok("废品申请创建成功");
        } catch (Exception e) {
            return SaResult.error("废品申请创建失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "deleteByIds")
    public SaResult getById(@RequestBody List<Long> ids) {
        System.out.println(ids);
        try {
            requestWasteService.removeBatchByIds(ids);
        } catch (Exception e) {
            SaResult.error("删除失败: " + e.getMessage());
        }
        return SaResult.ok("删除成功");
    }

    @PostMapping(value = "/update")
    public SaResult update(@RequestBody RequestWaste params) {
        try {
            requestWasteService.updateById(params);
            return SaResult.ok("废品申请更新成功");
        } catch (Exception e) {
            return SaResult.error("废品申请更新失败: " + e.getMessage());
        }
    }
}