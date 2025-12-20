package com.cg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.ChatMessage;
import com.cg.entity.view.VRelation;

import java.util.List;


public interface ChatMessageService extends IService<ChatMessage> {
    List<VRelation> getMyrelationship(Long loginIdAsLong);

    void addToMyrelationship(Long uid, Long friendId);

    List<ChatMessage> getchatlist(String acceptUserAccount, String sendUserAccount);

    void toggleLongTermContact(Long loginIdAsLong, Long fid, Integer status);

    boolean sendChatMessage(ChatMessage params);

}
