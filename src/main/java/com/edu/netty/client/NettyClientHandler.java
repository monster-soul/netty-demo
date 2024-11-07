package com.edu.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 客户端连接标识
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf buf = Unpooled.copiedBuffer("你好,服务端=======>我是客户端".getBytes(CharsetUtil.UTF_8));
        ctx.writeAndFlush(buf);
    }

    //当通道建立后有事件时会触发，即服务端发送数据给客户端
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        log.info("收到服务端的消息是：" + buf.toString(CharsetUtil.UTF_8));
        log.info("服务端地址是：" + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        log.error("服务端已经关闭=======>" + cause.getMessage(), cause);
        cause.printStackTrace();//打印堆栈信息
        ctx.close();
        //TODO 服务器关闭
    }

}