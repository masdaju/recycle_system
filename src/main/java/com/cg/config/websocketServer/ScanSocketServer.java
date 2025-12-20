package com.cg.config.websocketServer;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cg.service.UserService;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: MIZUGI
 * Date: 2025/11/16
 * Description:
 */
@ServerEndpoint("/scan/{code}")
@Service
public class ScanSocketServer {
    @Autowired
    UserService userService;
    private static final Logger log = LoggerFactory.getLogger(ScanSocketServer.class);
    //ConcurrentHashMap保证多人连接时的线程安全
    public static final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();
    @OnOpen
    public void onOpen(Session session, @PathParam("code") String code) {
        sessionMap.put(code, session);
        log.info("新的二维码连接建立{}，有个客户端{}带登录, ", code, sessionMap.size());
    }
//    @OnMessage
    public void onMessage(String message) {

        JSONObject obj = JSONUtil.parseObj(message);
        Session session = sessionMap.get(obj.getStr("code"));
        if (obj.getBool("confirm")) {
            SaResult saResult = userService.loginByScan(obj.getStr("satoken"));
            sendMessage(JSONUtil.toJsonStr(saResult), session);
        }else {
            JSONObject json =new JSONObject();
            json.putOnce("status", "waiting");
            //待确认登录
            sendMessage(JSONUtil.toJsonStr(json), session);
        }



    }
    @OnClose
    public void onClose(Session session, @PathParam("code") String code) {
        sessionMap.remove(code,session);
        log.info("客户端连接关闭，code={}, 待登录的客户端数量：{}", code, sessionMap.size());
    }
    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

}
