package com.cg.config;
import com.jcraft.jsch.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class SshTunnelConfig {

    @Value("${ssh.host}")
    private String sshHost;

    @Value("${ssh.port}")
    private int sshPort;

    @Value("${ssh.user}")
    private String sshUser;

    @Value("${ssh.password}")  // 可选：密码或私钥
    private String sshPassword;
//数据库信息
    @Value("${remote.db.host}")
    private String remoteDbHost;

    @Value("${remote.db.port}")
    private int remoteDbPort;

    @Value("${local.bind.port}")
    private int localPort;
    //redis信息
    @Value("${redis.remote.host}")
    //远程redis地址
    private String redisRemoteHost;
    @Value("${redis.remote.port}")
    //远程redis端口
    private List<Integer> redisRemotePort;
    @Value("${local.redis.port}")
    //绑定本地的端口
    private List<Integer> localRedisPort;
    private Session session;


    @PostConstruct
    public void init() throws JSchException {
        JSch jsch = new JSch();
       try {
        // 使用密码认证
        session = jsch.getSession(sshUser, sshHost, sshPort);
        session.setPassword(sshPassword);
        // 关闭严格主机密钥检查（生产环境需谨慎）
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
    }catch (Exception e){
           log.error("SSH连接失败,检查确认密码（密钥）/账号准确无误/确保服务器可以被正确连接",e);
       }
        int assignedPort = session.setPortForwardingL(localPort, remoteDbHost, remoteDbPort);
//        System.out.println("SSH-Mysql隧道已建立，本地端口: " + assignedPort);
        log.info("SSH-Mysql隧道已建立，本地端口: " + assignedPort);
        log.info(redisRemotePort +"====="+localRedisPort);
//        int redisPort = session.setPortForwardingL(localRedisPort, redisRemoteHost, redisRemotePort);
//        System.out.println("SSH-Redis隧道已建立，本地端口: " + redisPort);
        for (int i = 0; i < localRedisPort.size(); i++) {

            log.info(redisRemotePort.get(i)+"==============="+redisRemotePort.get(i));
            int assignedRedisPort = session.setPortForwardingL(localRedisPort.get(i), redisRemoteHost, redisRemotePort.get(i));
            log.info("SSH-Redis隧道已建立，本地端口: " + assignedRedisPort);        }


    }

    @PreDestroy
    public void cleanup() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}