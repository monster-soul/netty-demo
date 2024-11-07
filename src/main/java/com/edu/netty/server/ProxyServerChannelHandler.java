package com.edu.netty.server;


import com.edu.config.ThreadPoolSingleton;
import com.edu.constant.BaseTypeConstant;
import com.edu.netty.utils.ByteUtils;
import com.edu.netty.utils.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ChannelHandler.Sharable
public class ProxyServerChannelHandler extends ChannelInboundHandlerAdapter {
    private static final String PING = "p";
    private static final String HEARTBEAT = "700A";
    private static final ByteBuf PONG = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("q".getBytes()));
    private static final AtomicInteger connect = new AtomicInteger();
    private int count = 0;


    private final ThreadPoolExecutor executor = ThreadPoolSingleton.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(ProxyServerChannelHandler.class);


    /**
     * 设备连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String deviceIp = ctx.channel()
                .remoteAddress()
                .toString();
        //TODO 需要处理ip 地址字符串拼接
        ObjectUtil
                .ctxMap
                .put(deviceIp,
                        ctx);
        connect.incrementAndGet();
        log.info(String.format("客户端: [%s] 已经成功连接", deviceIp));
        //TODO  设备是否在线
    }

    /**
     * 设备停止连接
     *
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        String deviceIp = ObjectUtil.parseIp(ctx.channel()
                .remoteAddress()
                .toString());
        log.info(String.format("客户端: [%s] 已经停止", deviceIp));
        //TODO 需要处理ip 地址字符串拼接
        ObjectUtil.ctxMap.remove(deviceIp);
        connect.decrementAndGet();
        ctx.close();

        //TODO 设备离线
    }


    /**
     * 数据读取
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        // 将接收到的消息转换为 ByteBuf
        ByteBuf byteBuf = (ByteBuf) msg;
        // 创建一个字节数组来存储消息内容
        byte[] bytes = new byte[byteBuf.readableBytes()];
        // 将 ByteBuf 中的内容读取到字节数组中
        byteBuf.readBytes(bytes);
        // 将字节数组转换为字符串
        String message = new String(bytes, StandardCharsets.UTF_8);
        // 输出接收到的消息
        System.out.println("收到客户端的消息：" + message);

        // 发送响应消息
        ctx.writeAndFlush("Server received your message: " + message);

//
//        String ip = ObjectUtil.parseIp(ctx.channel()
//                .remoteAddress()
//                .toString());
//        ByteBuf buf = (ByteBuf) msg;
//        try {
//            byte[] bytes = new byte[buf.readableBytes()];
//            buf.readBytes(bytes);
//            if (bytes.length < 5) {
//                String dataHeader = ByteUtils.getStringByByte(ByteUtils.getByteByByte(0, bytes.length, bytes), BaseTypeConstant.hexadecimal);
//                if (HEARTBEAT.equalsIgnoreCase(dataHeader)) {
//                    ctx.writeAndFlush(PONG.duplicate());//回复设备 心跳
//                }
//                return;
//            }
//
//            CompletableFuture.runAsync(() -> {
//                this.handleActualData(bytes, ip);
//            }, executor);
//
//        } catch (Exception e) {
//            logger.error("read Exception {}", e.getMessage(), e);
//        } finally {
//            ReferenceCountUtil.release(buf);
//        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    //数据读取完成
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("Server received your message: ===>" );
    }

    /**
     * 事件触发
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE: //读空闲
                    count++;
                    logger.info("P 读空闲:{}, ctx:{}, count:{}", event.state(), ctx.hashCode(), count);
                    if (count > 1) {
                        ctx.channel().close();
                    }
                    break;
                case WRITER_IDLE: //写空闲
                    count++;
                    logger.info("P 写空闲:{}, ctx:{}, count:{}", event.state(), ctx.hashCode(), count);
                    break;
                case ALL_IDLE: //读写空闲
                    count++;
                    logger.info("P event state:{}, 30s未收到客户端的心跳包, ctx:{}, count:{}, send q...", event.state(), ctx.hashCode(), count);
                    //ctx.channel().writeAndFlush(PONG.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    if (count > 1) {
                        logger.info("P close tcp channel, ctx:{}", ctx.hashCode());
                        count = 0;
                        ctx.channel().close();
                    }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 根据规则处理实际传输数据
     *
     * @param bytes
     */
    private void handleActualData(byte[] bytes, String ip) {
//
//        DeviceMonitoringHistory entity = new DeviceMonitoringHistory();
//        entity.setAddress(ip);
//
//
//        String gatewayMac = ByteUtils.getStringByByte(ByteUtils.getByteByByte(5, 11, bytes), BaseTypeConstant.hexadecimal);
//        String gatewayType = ByteUtils.getStringByByte(ByteUtils.getByteByByte(12, 13, bytes), BaseTypeConstant.hexadecimal);
//        entity.setGatewayMac(gatewayMac);
//        entity.setGatewayType(gatewayType);
//
//        if (bytes.length > 30) {
//            String hex = ByteUtils.bytesToHex(bytes);
//            entity.setHexData(hex);
//
//            //设备类型
//            String gatewayDeviceType = ByteUtils.getStringByByte(ByteUtils.getByteByByte(16, 17, bytes), BaseTypeConstant.hexadecimal);
//            entity.setGatewayType(gatewayDeviceType);
//            String devType = ByteUtils.getStringByByte(ByteUtils.getByteByByte(19, 20, bytes), BaseTypeConstant.hexadecimal);
//
//            entity.setDeviceType(devType);
//            //命令类型
//            String commandType = ByteUtils.getStringByByte(ByteUtils.getByteByByte(17, 18, bytes), BaseTypeConstant.hexadecimal);
//            entity.setCommandType(commandType);
//
//            //设备编号
//            String deviceMac = ByteUtils.getStringByByte(ByteUtils.getByteByByte(20, 26, bytes), BaseTypeConstant.hexadecimal);
//            entity.setDeviceMac(deviceMac);
//
//            //信号强度
//            String signalStrength = ByteUtils.getStringByByte(ByteUtils.getByteByByte(26, 27, bytes), BaseTypeConstant.hexadecimal);
//            String signal = PhysicalDataTypeHandleUtils.handleSignalStrength(signalStrength);
//            entity.setSignalStrength(signal);
//
//            //如果是定位器,存到redis中,后面根据redis进行获取
//            if (ExternalDeviceTypeEnumCode.INK_SCREEN.getValue().toString().equals(devType)) {
////                if (deviceMac.equals("726D6963AC18")) {
////                    log.info("设备号:726D6963AC18,当前时间:" + LocalDateTime.now());
////                }
////                if (deviceMac.equals("726D6963AC21")) {
////                    log.info("设备号:726D6963AC21,当前时间:" + LocalDateTime.now());
////                }
////
////                if (deviceMac.equals("726D6963AC1D")) {
////                    log.info("设备号:726D6963AC1D,当前时间:" + LocalDateTime.now());
////                }
//
////                if (deviceMac.equals("726D6963AC1C")) {
////                    log.info("设备号:726D6963AC1C,当前时间:" + LocalDateTime.now());
////                }
////                 if (deviceMac.equals("726D6963AC15")) {
////                    log.info("设备号:726D6963AC15,当前时间:" + LocalDateTime.now());
////                }
//
//                RBucket<String> bucket = this.redissonClient.getBucket(RedisPrefixConstant.DEVICE_TAG_MONITORING_DATA_PREFIX + deviceMac);
//                bucket.set(signal);
//                return;
//            }
//            if (deviceMac.equals("DCDA0CCDFC4A")) {
//                log.info("设备号:" + deviceMac + ",当前时间:" + LocalDateTime.now());
//            }
//            //标识id
//            String identification = ByteUtils.getStringByByte(ByteUtils.getByteByByte(27, 28, bytes), BaseTypeConstant.hexadecimal);
//            entity.setIdentification(identification);
//
//            //电压数据
//            String voltageData = ByteUtils.getStringByByte(ByteUtils.getByteByByte(28, 31, bytes), BaseTypeConstant.hexadecimal);
//            String voltage = PhysicalDataTypeHandleUtils.handleVoltage(voltageData);
//            entity.setVoltage(voltage);
//
//            //电流数据
//            String currentData = ByteUtils.getStringByByte(ByteUtils.getByteByByte(31, 34, bytes), BaseTypeConstant.hexadecimal);
//            String current = PhysicalDataTypeHandleUtils.handleCurrent(currentData);
//            entity.setElectricCurrent(current);
//
//            //功率数据
//            String powerData = ByteUtils.getStringByByte(ByteUtils.getByteByByte(34, 39, bytes), BaseTypeConstant.hexadecimal);
//            String power = PhysicalDataTypeHandleUtils.handlePower(powerData);
//            entity.setPower(power);
//
//            //频率数据
//            String frequencyData = ByteUtils.getStringByByte(ByteUtils.getByteByByte(39, 42, bytes), BaseTypeConstant.hexadecimal);
//            String frequency = PhysicalDataTypeHandleUtils.handleFrequency(frequencyData);
//            entity.setFrequency(frequency);
//
//
//            String data = ByteUtils.getStringByByte(ByteUtils.getByteByByte(42, 44, bytes), BaseTypeConstant.hexadecimal);
//            //温度
//            if (data.startsWith(PhysicalDataTypeConstant.TEMPERATURE_TYPE)) {
//                String temperature = PhysicalDataTypeHandleUtils.handleTemperature(data);
//                entity.setTemperature(temperature);
//                //因数
//            } else if (data.startsWith(PhysicalDataTypeConstant.FACTOR_TYPE)) {
//                String factor = PhysicalDataTypeHandleUtils.handleFactorData(data);
//                entity.setFactor(factor);
//            }
//
//            //用电量数据
//            String electricityData = ByteUtils.getStringByByte(ByteUtils.getByteByByte(44, 49, bytes), BaseTypeConstant.hexadecimal);
//            String electricity = PhysicalDataTypeHandleUtils.handleElectricity(electricityData);
//            entity.setElectricity(electricity);
//
//            //继电器
//            String relaysData = ByteUtils.getStringByByte(ByteUtils.getByteByByte(49, 51, bytes), BaseTypeConstant.hexadecimal);
//            String relays = PhysicalDataTypeHandleUtils.handleRelays(relaysData);
//            entity.setRelays(relays);
//
//            //秒级时间戳
//            String syncDateTime = ByteUtils.getStringByByte(ByteUtils.getByteByByte(51, 56, bytes), BaseTypeConstant.hexadecimal);
//            String dateTime = PhysicalDataTypeHandleUtils.handleSyncDateTime(syncDateTime);
//            entity.setDeviceDateTime(dateTime);
//
//            //TODO 更新实时数据
//            try {
//                this.historyService.save(entity);
//                DeviceMonitoringReal deviceMonitoringReal = BeanHelper.copyProperties(entity, DeviceMonitoringReal.class);
//                this.realService.updateDeviceMonitoringRealEntityByDeviceMac(deviceMonitoringReal);
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//            }
//
//        }
    }

}
