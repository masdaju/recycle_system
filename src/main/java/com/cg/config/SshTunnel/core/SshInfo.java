package com.cg.config.SshTunnel.core;

import lombok.Data;
import java.util.List;

@Data

public class SshInfo {
    // SSH服务器基础信息
    private String sshHost;      // SSH服务器IP/域名
    private int sshPort = 22;    // SSH端口（默认22）
    private String sshUser;      // SSH登录用户名
    
    // 密钥登录配置
    private String privateKeyPath; // 本地私钥文件路径
    private String privateKeyPassphrase; // 私钥密码
    
    // 端口转发配置
    private List<Reverse> reverses;
    
    // 运行时属性
    private com.jcraft.jsch.Session sshSession;
}