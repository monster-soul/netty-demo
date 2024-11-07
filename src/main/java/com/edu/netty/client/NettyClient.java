package com.edu.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        Channel channel = getChannel();
        for (int i = 0; i < 100; i++) {
            assert channel != null;
            channel.writeAndFlush("测试信息发送".getBytes(StandardCharsets.UTF_8));
        }
        ChannelFuture channelFuture = channel.closeFuture();
        channelFuture.sync();
    }

    public static Channel getChannel() {
        //客户端需要一个事件循环组
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            //创建客户端启动对象
            //注意客户端使用的不是SocketBootstrap而是Bootstrap
            Bootstrap bootstrap = new Bootstrap();
            // 设置相关参数
            bootstrap.group(group) //设置线程组
                    .channel(NioSocketChannel.class)// 使用NioSocketChannel作为客户端的通道实现
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addFirst(new NettyClientHandler());
                        }
                    });

            log.info("netty 客户端启动...");
            ChannelFuture cf = bootstrap.connect("127.0.0.1", 4476).sync();
            Channel channel = cf.channel();
            channel.closeFuture().sync();
            // 注册监听器来处理成功或失败的情况
            cf.addListener((ChannelFutureListener) f -> {
                if (!f.isSuccess()) {
                    System.err.println("Failed to send the message.");
                    f.cause().printStackTrace();
                }
            });
            return channel;
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            log.info("关闭客户端");
            group.shutdownGracefully();
        }

    }
}