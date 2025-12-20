package com.cg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Notifications;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 消息通知表 服务类
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
public interface NotificationsService extends IService<Notifications> {

    Page<Notifications> MyMsgForApp(Long lastId, Integer pageSize, Long userId);
}
