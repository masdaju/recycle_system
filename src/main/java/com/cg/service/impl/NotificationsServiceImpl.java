package com.cg.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.Notifications;
import com.cg.mapper.NotificationsMapper;
import com.cg.service.NotificationsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息通知表 服务实现类
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@Service
public class NotificationsServiceImpl extends ServiceImpl<NotificationsMapper, Notifications> implements NotificationsService {

    @Override
    public Page<Notifications> MyMsgForApp(Long lastId, Integer pageSize, Long userId) {
        Page<Notifications> page = new Page<>();
        page.setSize(pageSize);
        page.setCurrent(lastId);
        page.setRecords(baseMapper.MyMsgForApp(lastId, pageSize, userId));
        return page;
    }
}
