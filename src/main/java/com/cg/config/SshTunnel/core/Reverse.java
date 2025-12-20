package com.cg.config.SshTunnel.core;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Reverse {
    private int lport;
    private String host;
    private int rport;
}