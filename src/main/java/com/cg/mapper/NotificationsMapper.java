package com.cg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cg.entity.Notifications;

import java.util.List;

/**
 * <p>
 * 消息通知表 Mapper 接口
 * </p>

 */
public interface NotificationsMapper extends BaseMapper<Notifications> {
    List<Notifications> MyMsgForApp(Long lastId, Integer pageSize, Long userId);

}
