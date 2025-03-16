package com.cg.config;
import com.jcraft.jsch.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SshTunnelConfig {

    @Value("${ssh.host}")
    private String sshHost;

    @Value("${ssh.port}")
    private int sshPort;

    @Value("${ssh.user}")
    private String sshUser;

    @Value("${ssh.password}")  // 可选：密码或私钥
    private String sshPassword;

    @Value("${remote.db.host}")
    private String remoteDbHost;

    @Value("${remote.db.port}")
    private int remoteDbPort;

    @Value("${local.bind.port}")
    private int localPort;


    
    private Session session;

    @PostConstruct
    public void init() throws JSchException {
        JSch jsch = new JSch();

        // 使用密码认证
         session = jsch.getSession(sshUser, sshHost, sshPort);
         session.setPassword(sshPassword);

        // 关闭严格主机密钥检查（生产环境需谨慎）
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        int assignedPort = session.setPortForwardingL(localPort, remoteDbHost, remoteDbPort);
        System.out.println("SSH 隧道已建立，本地端口: " + assignedPort);
    }

    @PreDestroy
    public void cleanup() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}