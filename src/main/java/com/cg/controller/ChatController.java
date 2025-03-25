package com.cg.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cg.entity.ChatMessage;
import com.cg.entity.User;
import com.cg.entity.Waste;
import com.cg.entity.view.VRelation;
import com.cg.service.ChatMessageService;
import com.cg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate  stringRedisTemplate;
    //获取联系人列表
    @GetMapping("/getMyRelationship")
    public SaResult getMyRelationship() {
        List<VRelation> relations = chatMessageService.getMyrelationship(StpUtil.getLoginIdAsLong());
    return SaResult.data(relations);
    }
    //添加联系人
    @PostMapping("/addToMyRelationship")
    public SaResult addToMyRelationship(@RequestParam String username) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount,username);
        Long uid = userService.getOne(queryWrapper).getId();
        try {
            chatMessageService.addToMyrelationship(StpUtil.getLoginIdAsLong(),uid);
        } catch (Exception e) {
            return SaResult.ok();
        }
        return SaResult.ok();
    }
    @GetMapping("/getChatMessage")
    public SaResult getChatMessage(@RequestParam String sendUserAccount, @RequestParam String acceptUserAccount)  {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey("chatMessage::" + sendUserAccount + "_" + acceptUserAccount))) {
            String s = stringRedisTemplate.opsForValue().get("chatMessage::" + sendUserAccount + "_" + acceptUserAccount);
            return JSON.parseObject(s, SaResult.class);
        }
        List<ChatMessage> chatMessages = chatMessageService.getchatlist(acceptUserAccount,sendUserAccount);
        stringRedisTemplate.opsForValue().set("chatMessage::" + sendUserAccount + "_" + acceptUserAccount, JSON.toJSONString(SaResult.data(chatMessages)),7, TimeUnit.DAYS);
        return SaResult.data(chatMessages);
    }
    //发送消息
    @PostMapping(value = "/sendChatMessage")
    public SaResult create(@RequestBody ChatMessage params) {


        //设置发送时间
        params.setSendTime(new Date());
        chatMessageService.save(params);
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
        }
        return SaResult.ok("created successfully");
    }
    @PostMapping("/toggleLongTermContact")
    public SaResult toggleLongTermContact(@RequestParam Long fid,@RequestParam Integer status) {
        chatMessageService.toggleLongTermContact(StpUtil.getLoginIdAsLong(),fid,status);
        return SaResult.ok();
    }
}
