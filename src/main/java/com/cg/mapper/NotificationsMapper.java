package com.cg.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Notifications;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 消息通知表 Mapper 接口
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
public interface NotificationsMapper extends BaseMapper<Notifications> {
    List<Notifications> MyMsgForApp(Long lastId, Integer pageSize, Long userId);

}
