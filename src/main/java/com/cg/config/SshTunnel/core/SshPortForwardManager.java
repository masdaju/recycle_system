package com.cg.config.SshTunnel.core;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Properties;

@Slf4j
@Component

public class SshPortForwardManager {

    /**
     * 初始化SSH连接（密钥登录版）
     */
    public void initSshConnection(SshInfo sshInfo) throws JSchException {
        JSch jsch = new JSch();

        File privateKeyFile = new File(sshInfo.getPrivateKeyPath());
        if (!privateKeyFile.exists()) {
            throw new JSchException("私钥文件不存在：" + sshInfo.getPrivateKeyPath());
        }

        jsch.addIdentity(sshInfo.getPrivateKeyPath(),sshInfo.getPrivateKeyPassphrase());


        Session session = jsch.getSession(sshInfo.getSshUser(), sshInfo.getSshHost(), sshInfo.getSshPort());
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
//        session.setPassword("123456");
        session.setConfig(config);

        log.info("开始连接SSH服务器（密钥登录）：{}:{}", sshInfo.getSshHost(), sshInfo.getSshPort());
        session.connect(30000); // 30秒连接超时，避免无限等待
        sshInfo.setSshSession(session); // 保存已连接的Session

        if (sshInfo.getReverses() != null && !sshInfo.getReverses().isEmpty()) {
            for (Reverse reverse : sshInfo.getReverses()) {
                if (isPortInUse(reverse.getLport())) {
                    throw new JSchException("本地端口" + reverse.getLport() + "已被占用，无法建立转发");
                }
                session.setPortForwardingL(reverse.getLport(), reverse.getHost(), reverse.getRport());
                log.info("端口转发成功：本地{} → SSH服务器{} → 目标{}:{}",
                        reverse.getLport(), sshInfo.getSshHost(), reverse.getHost(), reverse.getRport());
            }
        }
        log.info("SSH服务器{}连接及端口转发初始化完成", sshInfo.getSshHost());
    }

    public void closeSshConnection(SshInfo sshInfo) {
        Session session = sshInfo.getSshSession();
        if (session == null) {
            log.debug("SSH服务器{}无有效Session，无需关闭", sshInfo.getSshHost());
            return;
        }
        if (session.isConnected()) {
            try {
                session.disconnect();
                log.info("SSH服务器{}连接已关闭", sshInfo.getSshHost());
            } catch (Exception e) {
                log.error("关闭SSH服务器{}连接失败", sshInfo.getSshHost(), e);
            }
        }
        sshInfo.setSshSession(null);
    }

    private boolean isPortInUse(int port) {
        try (java.net.ServerSocket socket = new java.net.ServerSocket(port)) {
            return false; // 端口未被占用
        } catch (java.io.IOException e) {
            return true; // 端口已被占用
        }
    }
}