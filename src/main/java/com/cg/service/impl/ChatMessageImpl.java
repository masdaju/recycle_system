package com.cg.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.ChatMessage;
import com.cg.entity.view.VRelation;
import com.cg.mapper.ChatMessageMapper;
import com.cg.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * ChatMessageService 接口的实现类，继承自 MyBatis-Plus 的 ServiceImpl 类，
 * 负责处理与聊天消息及关系相关的业务逻辑。
 */
@Service
public class ChatMessageImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
    @Autowired
   private StringRedisTemplate stringRedisTemplate;
    /**
     * 根据用户的登录 ID 获取其好友关系列表。
     *
     * @param loginIdAsLong 用户的登录 ID
     * @return 包含用户好友关系信息的 VRelation 列表
     */
    @Override
    public List<VRelation> getMyrelationship(Long loginIdAsLong) {
        // 调用基类的 getBaseMapper 方法获取 ChatMessageMapper 实例，
        // 并调用其 getMyrelationship 方法查询用户的好友关系列表
        return getBaseMapper().getMyrelationship(loginIdAsLong);
    }

    /**
     * 将指定用户和好友添加到好友关系中。
     *
     * @param uid      用户的 ID
     * @param friendId 好友的 ID
     */
    @Override
    public void addToMyrelationship(Long uid, Long friendId) {
        // 调用基类的 getBaseMapper 方法获取 ChatMessageMapper 实例，
        // 并调用其 addToMyrelationship 方法将用户和好友添加到好友关系中
        getBaseMapper().addToMyrelationship(uid, friendId);
    }

    /**
     * 根据接收用户账号和发送用户账号获取聊天消息列表。
     *
     * @param acceptUserAccount 接收用户的账号
     * @param sendUserAccount   发送用户的账号
     * @return 包含指定用户之间聊天消息的 ChatMessage 列表
     */
    @Override
    public List<ChatMessage> getchatlist(String acceptUserAccount, String sendUserAccount) {
        // 调用基类的 baseMapper 属性获取 ChatMessageMapper 实例，
        // 并调用其 getchatlist 方法查询指定用户之间的聊天消息列表
        return baseMapper.getchatlist(acceptUserAccount, sendUserAccount);
    }

    /**
     * 切换用户与好友的长期联系状态。
     *
     * @param uid    用户的 ID
     * @param fid    好友的 ID
     * @param status 当前的状态，0 表示非长期联系，1 表示长期联系
     */
    @Override
    public void toggleLongTermContact(Long uid, Long fid, Integer status) {
        int a;
        // 根据传入的状态值进行不同的处理
        switch (status) {
            case 0:
                // 若当前状态为 0（非长期联系），则将目标状态设为 1（长期联系）
                a = 1;
                // 调用基类的 baseMapper 属性获取 ChatMessageMapper 实例，
                // 并调用其 toggleLongTermContact 方法更新用户与好友的长期联系状态
                baseMapper.toggleLongTermContact(uid, fid, a);
                break;
            case 1:
                // 若当前状态为 1（长期联系），则将目标状态设为 0（非长期联系）
                a = 0;
                // 调用基类的 baseMapper 属性获取 ChatMessageMapper 实例，
                // 并调用其 toggleLongTermContact 方法更新用户与好友的长期联系状态
                baseMapper.toggleLongTermContact(uid, fid, a);
                break;
            default:
                // 若传入的状态值不是 0 或 1，则抛出运行时异常
                throw new RuntimeException("参数错误");
        }
    }

    @Override
    public boolean sendChatMessage(ChatMessage params) {
        //设置发送时间
        params.setSendTime(new Date());
        save(params);
        //更新缓存消息
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey("chatMessage::" + params.getSendUserAccount() + "_" + params.getAcceptUserAccount())))
        {
            System.out.println("更新缓存消息");
            String jsonString = stringRedisTemplate.opsForValue().get("chatMessage::" + params.getSendUserAccount() + "_" + params.getAcceptUserAccount());
            SaResult saResult = JSON.parseObject(jsonString, SaResult.class);
            System.out.println(saResult.getData());

            List<ChatMessage> chatMessages = JSON.parseArray(saResult.getData().toString(), ChatMessage.class);
            chatMessages.add(params);
            saResult.setData(chatMessages);
            stringRedisTemplate.opsForValue().set("chatMessage::" + params.getSendUserAccount() + "_" + params.getAcceptUserAccount(), JSON.toJSONString(saResult));

            stringRedisTemplate.opsForValue().set("chatMessage::" + params.getAcceptUserAccount() + "_" + params.getSendUserAccount(), JSON.toJSONString(saResult));
        }
        return true;
    }


//    @Scheduled(cron ="*/3 * * * * ?")
//    public void scheduledTask() {
//        // 每周日0点执行
//        if (baseMapper.deleteMyRelations()) {
//            System.out.println("删除我的关系成功");
//        }
//    }



}