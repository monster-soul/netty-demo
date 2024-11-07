package com.edu.netty.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author pengXiangJun
 * @date 2024年07月22日
 * @program
 * @description
 */

@Data
@Component
public class NettyConfig {


    /**
     * netty 端口
     */
    @Value("${cat.openapi.netty.port:4476}")
    private Integer port;

    /**
     * netty地址
     */
    @Value("${cat.openapi.netty.ip:127.0.0.1}")
    private String address;

    /**
     * 通道
     */
    @Value("${cat.openapi.netty.channel:1024}")
    private Integer channel;

}
