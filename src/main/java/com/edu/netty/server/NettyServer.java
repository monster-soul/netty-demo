package com.edu.netty.server;


import com.edu.init.OnInitializeApplication;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.stereotype.Component;

import com.edu.netty.config.NettyConfig;

@Slf4j
@Component
public class NettyServer implements OnInitializeApplication {

    private final NettyConfig nettyConfig;


    public NettyServer(
                       NettyConfig nettyConfig) {
        this.nettyConfig = nettyConfig;
    }

    @Override
    public void onInitializeApplication(ApplicationStartedEvent event) {
        //创建两个线程组bossGroup和workerGroup，含有的子线程NioEventLoop的个数默认是CPU的两倍
        //bossGroup只是处理连接请求，真正的和客户端业务处理，会交给workerGroup完成
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            //创建服务器端的启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            //使用链式编程来配置参数
            bootstrap.group(bossGroup, workerGroup)//设置两个线程组
                    .channel(NioServerSocketChannel.class)//使用NioServerSocketChannel作为服务器的通道实现
                    //初始化服务器连接队列大小，服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
                    //多个客户端同时来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理
                    .option(ChannelOption.SO_BACKLOG, this.nettyConfig.getChannel())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            //对workerGroup的SocketChannel设置处理器
                            channel.pipeline().addLast(new ProxyServerChannelHandler());
                        }
                    });

            log.info("netty 服务端启动...");

            //绑定一个端口并且同步生成一个ChannelFuture异步对象，通过isDone()等方法可以判断异步事件的执行情况
            //启动服务器（并绑定的端口），bind是异步操作，sync方法是等待异步操作执行完毕
            ChannelFuture cf =
                    bootstrap
                            .bind(this.nettyConfig.getPort())
                            .sync();

            //给cf注册监听器，监听我们关心的事件
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if (cf.isSuccess()) {
                    log.info(String.format("========>netty监听端口[%s]成功", nettyConfig.getPort()));
                } else {
                    log.error(String.format("========>netty监听端口[%s]失败", nettyConfig.getPort()));
                }
            });
            //等待服务端监听端口关闭，closeFuture是异步操作
            //通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成，内部调用的是Object的wait()方法
            cf.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}