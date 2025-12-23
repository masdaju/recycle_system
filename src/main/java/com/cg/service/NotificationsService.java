package com.cg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.Notifications;

/**
 * <p>
 * 消息通知表 服务类
 * </p>
 */
public interface NotificationsService extends IService<Notifications> {

    Page<Notifications> MyMsgForApp(Long lastId, Integer pageSize, Long userId);
}
