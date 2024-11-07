package com.edu.service.impl;

import com.edu.model.input.SendCommandInput;
import com.edu.netty.utils.ByteUtils;
import com.edu.netty.utils.ObjectUtil;
import com.edu.service.CommandService;
import com.edu.web.exception.BaseException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author pengXiangJun
 * @date 2024年07月31日
 * @program
 * @description
 */

@Slf4j
@Service
public class CommandServiceImpl implements CommandService {


    @Override
    public Boolean sendCommand(SendCommandInput input) {
        return this.sendCommandToClient(input.getClineAddress(), input.getCommand());
    }


    /**
     * 发送命令行
     *
     * @param clientAddress
     * @param command
     * @return
     */
    public Boolean sendCommandToClient(String clientAddress, String command) {
        ChannelHandlerContext ctx = ObjectUtil.ctxMap.get(clientAddress);
        if (Objects.nonNull(ctx)) {
            if (ctx.channel().isActive()) {
                //TODO 需要验证 是否是16进制数据
                ByteBuf buf = Unpooled.copiedBuffer(ByteUtils.hexStringToByteArray(command));
                ctx.writeAndFlush(buf)
                        .addListener(future -> {
                            if (future.isSuccess()) {
                                log.info("Command send successfully.");
                            } else {
                                future.cause().printStackTrace();
                            }
                        });
                return true;
            } else {
                throw new BaseException("该设备不在线");
            }
        }
        throw new BaseException("该设备不在线");
    }
}
