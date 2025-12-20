package com.cg.config.SshTunnel;

import com.cg.config.SshTunnel.core.SshInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Author: MIZUGI
 * Date: 2025/12/19
 * Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "ssh") // 绑定ssh前缀
@Slf4j
public class BeanMap {
    //吧赋值好的对象放入map后续通轮循map来实例化bean
    private Map<String, SshInfo> tunnels;

}
