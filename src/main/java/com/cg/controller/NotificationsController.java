package com.cg.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Notifications;
import com.cg.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 消息通知表 前端控制器
 * 该控制器主要处理与消息通知相关的HTTP请求，包括查询、创建、删除和更新消息通知等操作。
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    // 自动注入 NotificationsService 实例，用于调用业务逻辑方法
    @Autowired
    private NotificationsService notificationsService;

    /**
     * 查询当前用户的消息通知列表，支持分页和时间范围筛选。
     *
     * @param current   当前页码，若未提供则默认为1
     * @param pageSize  每页显示的记录数，若未提供则默认为10
     * @param startTime 消息发送的开始时间，用于筛选消息
     * @param endTime   消息发送的结束时间，用于筛选消息
     * @return 包含分页消息通知列表的 SaResult 对象
     */
    @GetMapping("/MyMsg")
    @Cacheable(value = "notifications", key = "#current + '-' + #pageSize", sync = true,condition = "#startTime == null && #endTime == null")
    public SaResult list(@RequestParam(required = false) Integer current,
                         @RequestParam(required = false) Integer pageSize,
                         @RequestParam(required = false) String startTime,
                         @RequestParam(required = false) String endTime) {
        // 若当前页码或每页记录数未提供，则设置默认值
        if (current == null || pageSize == null) {
            current = 1;
            pageSize = 10;
        }
        // 创建 LambdaQueryWrapper 对象，用于构建查询条件
        LambdaQueryWrapper<Notifications> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件：查询当前用户的消息通知
        queryWrapper.eq(Notifications::getUserId, StpUtil.getLoginIdAsLong())
                // 添加时间范围查询条件，仅当开始时间和结束时间都不为空时生效
                .between(startTime != null && endTime != null, Notifications::getSentAt, startTime, endTime)
                // 按消息发送时间降序排序
                .orderByDesc(Notifications::getSentAt);
        //查看消息后设置为已查看
        notificationsService.update(new LambdaUpdateWrapper<Notifications>().set(Notifications::getIsRead, 2).eq(Notifications::getUserId, StpUtil.getLoginIdAsLong()));
        // 调用服务层的分页查询方法，获取分页结果
        Page<Notifications> aPage = notificationsService.page(new Page<>(current, pageSize), queryWrapper);
        // 将分页结果封装到 SaResult 对象中并返回
        return SaResult.data(aPage);
    }
    //为移动端写的深分页查询接口
    @GetMapping("MyMsgForApp")
    public SaResult MyMsgForApp(@RequestParam(defaultValue = "0") Long lastId, Integer pageSize) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        // 调用服务层的分页查询方法，获取分页结果
        Page<Notifications> aPage = notificationsService.MyMsgForApp(lastId, pageSize, userId);
        // 将分页结果封装到 SaResult 对象中并返回
        return SaResult.data(aPage);
    }

    /**
     * 根据消息通知的ID查询单个消息通知。
     *
     * @param id 消息通知的ID
     * @return 包含消息通知


    }

    /**
     * 根据消息通知的ID查询单个消息通知。
     *
     * @param id 消息通知的ID
     * @return 包含消息通知信息的 SaResult 对象，若未找到则返回错误信息
     */
    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        // 调用服务层的根据ID查询方法，获取消息通知对象
        Notifications notification = notificationsService.getById(id);
        // 若查询到消息通知，则将其封装到 SaResult 对象中返回
        if (notification != null) {
            return SaResult.data(notification);
        } else {
            // 若未查询到消息通知，则返回错误信息
            return SaResult.error("未找到指定消息通知");
        }
    }

    /**
     * 创建一条新的消息通知。
     *
     * @param message 消息内容
     * @param userId  接收消息的用户ID
     * @return 包含创建结果的 SaResult 对象
     */
    @PostMapping(value = "/setNotification")
    @CacheEvict(value = "notifications", allEntries = true)
    public SaResult create(@RequestParam String message, @RequestParam Long userId) {
        try {
            // 创建 Notifications 对象，用于封装消息通知信息
            Notifications params = new Notifications();
            // 设置消息内容
            params.setMessage(message);
            // 设置接收消息的用户ID
            params.setUserId(userId);
            // 设置消息发送时间为当前时间
            params.setSentAt(new Date());
            params.setIsRead(1);
            // 调用服务层的保存方法，将消息通知保存到数据库
            notificationsService.save(params);
            // 返回创建成功的信息
            return SaResult.ok("消息通知创建成功");
        } catch (Exception e) {
            // 若创建过程中出现异常，则返回错误信息
            return SaResult.error("消息通知创建失败: " + e.getMessage());
        }
    }

    /**
     * 根据消息通知的ID列表批量删除消息通知。
     *
     * @param ids 要删除的消息通知的ID列表
     * @return 包含删除结果的 SaResult 对象
     */
    @PostMapping(value = "deleteByIds")
    @CacheEvict(value = "notifications", allEntries = true)
    public SaResult getById(@RequestBody List<Long> ids) {
        try {
            // 调用服务层的批量删除方法，根据ID列表删除消息通知
            notificationsService.removeBatchByIds(ids);
        } catch (Exception e) {
            // 若删除过程中出现异常，则返回错误信息
            return SaResult.error("删除失败: " + e.getMessage());
        }
        // 返回删除成功的信息
        return SaResult.ok("删除成功");
    }

    /**
     * 根据消息通知对象更新消息通知信息。
     *
     * @param params 包含要更新信息的消息通知对象
     * @return 包含更新结果的 SaResult 对象
     */
    @PostMapping(value = "/update")
    public SaResult update(@RequestBody Notifications params) {
        try {
            // 调用服务层的更新方法，根据消息通知对象更新数据库中的记录
            notificationsService.updateById(params);
            // 返回更新成功的信息
            return SaResult.ok("消息通知更新成功");
        } catch (Exception e) {
            // 若更新过程中出现异常，则返回错误信息
            return SaResult.error("消息通知更新失败: " + e.getMessage());
        }
    }

    @GetMapping(value = "/unreadCount")
    public SaResult getUnreadCount() {
        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Notifications> notificationsLambdaQueryWrapper =new LambdaQueryWrapper<>();
        notificationsLambdaQueryWrapper.eq(Notifications::getUserId,userId).eq(Notifications::getIsRead,1);
        // 调用服务层的方法，获取未读消息通知的数量
        long unreadCount = notificationsService.count(notificationsLambdaQueryWrapper);
        // 返回未读消息通知的数量
        return SaResult.data(unreadCount);
    }
    @PostMapping(value = "/read")
    public SaResult read() {

        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();
    LambdaUpdateWrapper<Notifications> wrapper =new LambdaUpdateWrapper<>();
    wrapper.set(Notifications::getIsRead,2).eq(Notifications::getUserId,userId).set(Notifications::getIsRead,2);

        // 调用服务层的方法，获取未读消息通知的数量
        notificationsService.update(wrapper);
        // 返回未读消息通知的数量
        return SaResult.ok();

    }

}