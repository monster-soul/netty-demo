package com.edu.netty.utils;

import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author pengXiangJun
 * @date 2024年07月22日
 * @program
 * @description
 */

@Slf4j
public class ObjectUtil {

    /**
     * 构建Map
     */
    public static Map<String, ChannelHandlerContext> ctxMap = Maps.newConcurrentMap();


    /**
     *   解析ip
     * @param param
     * @return
     */
    public static String parseIp(String param) {

        // 使用正则表达式匹配IP地址部分
        Pattern pattern = Pattern.compile("/(\\d+\\.\\d+\\.\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(param);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return param;
    }

}
