package com.cg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cg.entity.ChatMessage;
import com.cg.entity.view.VRelation;

import java.util.List;

public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    List<VRelation> getMyrelationship(Long uid);

    void addToMyrelationship(Long uid, Long friendId);
//    查询最近500条聊天记录

    List<ChatMessage> getchatlist(String acceptUserAccount, String sendUserAccount);
    void toggleLongTermContact(Long uid, Long fid, int a);

    boolean deleteMyRelations();

    String getCollectorName(Long requestId);
}
