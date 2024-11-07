package com.edu.constant;

import lombok.Data;

/**
 * @author pengXiangJun
 * @date 2024年08月01日
 * @program
 * @description
 */
@Data
public class GatewayCommandConstant {

    /**
     * 心跳检测
     */
    public static final String PING = "p";
    public static final String PONG = "q";

    /**
     * 前缀：统一为 0x414C4D；(3字节)
     */
    public static String PREFIX = "414C4D";

    /**
     * 包长
     */
    public static String PACKAGE_LENGTH = "002B";

    /**
     * 间隔
     */
    public static String PACKAGE_LENGTH_INTERVAL = "002E";


    /**
     * 设备类型 ME 开头设备设置42
     */
    public static String DEVICE_TYPE = "0080";

    /**
     * 蓝牙标签
     */
    public static String DEVICE_TYPE_TAG = "0070";

    /**
     * 数据头 默认5743
     */
    public static String DATA_HEADER = "5743";

    /**
     * 总长度
     */
    public static String TOTO_LENGTH = "1B";

    /**
     * 间隔总长度
     */
    public static String TOTO_LENGTH_INTERVAL = "1E";

    /**
     * 设备类型
     */
    public static String DEVICE_SPECIFIC_TYPE = "80";

    /**
     *  蓝牙标签
     */
    public static String DEVICE_SPECIFIC_TYPE_TAG = "70";

    /**
     * 命令类型 (0x90 C→B 命令请求 )
     * C 代表云端服务器；
     * B 代表蓝牙；
     * W 代表 wifi；
     * D 代表与服务器建立连接的设备
     */
    public static String COMMAND_REQUEST = "90";

    /**
     * 数据长度
     */
    public static String DATA_LENGTH = "16";

    /**
     * 间隔总长度
     */
    public static String DATA_LENGTH_INTERVAL = "19";

    /**
     * msgID
     */
    public static String MSG_ID = "10";
    /**
     * 透传Id
     */
    public static String Transparent_ID = "42";

    /**
     * 当前包
     */
    public static String CURRENT_PACKAGE = "0001";

    /**
     * linkTime
     */
    public static String LINK_TIME = "0A";

    /**
     * disconnTim
     */
    public static String DIS_CONN_TIM = "14";


    /**
     * 不知道这个什么意思
     */
    public static String PLACEHOLDERS = "05";

    /**
     * flag
     * 1=write ，
     * 2=read ，
     * 3=notify，
     * 4=write&notify
     */
    public static String FLAG = "01";

    /**
     * Server UUID 长度
     */
    public static String SERVER_UUID_LENGTH = "02";

    /**
     * Server UUID
     */
    public static String Server_UUID = "FEF0";


    /**
     * Characteristic UUID 长度
     */
    public static String CHARACTERISTIC_UUID_LENGTH = "02";

    /**
     * Characteristic UUID
     */
    public static String CHARACTERISTIC_UUID = "FE40";

    /**
     * Characteristic UUID (间隔)
     */
    public static String CHARACTERISTIC_UUID_INTERVAL = "FE20";

    /**
     * 透传数据长度
     */
    public static String TRANSPARENT_LENGTH = "02";

    /**
     * 透传数据长度(间隔)
     */
    public static String TRANSPARENT_LENGTH_INTERVAL = "05";

    /**
     * 继电器开
     */
    public static String OPEN = "0101";

    /**
     * 继电器关
     */
    public static String CLOSE = "0100";


    /**
     * 默认值
     */
    public static String DEFAULT_INTERVAL = "1101";

    /**
     * 时间单位毫秒
     */
    public static String DEFAULT_TIMESTAMP_UNIT = "FF";

    /**
     * 时间搓类型
     */
    public static String TIMESTAMP_TYPE = "00";

    /**
     * 校验和           （固定）
     */
    public static String CHECKSUM = "8A";

    /**
     * 结束符   （固定）
     */
    public static String END_CHARACTER = "0A";


    /**
     * 执行继电器开命令
     *
     * @param gatewayMac 网关mac地址
     * @param deviceMac  设备mac地址
     * @param relays     继电器开关
     * @return
     */
    public static String getDeviceRelaysCommand(String gatewayMac,
                                                String deviceMac,
                                                Boolean relays) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX)
                .append(PACKAGE_LENGTH)
                .append(gatewayMac)
                .append(DEVICE_TYPE)
                .append(DATA_HEADER)
                .append(TOTO_LENGTH)
                .append(DEVICE_SPECIFIC_TYPE)
                .append(COMMAND_REQUEST)
                .append(DATA_LENGTH)
                .append(MSG_ID)
                .append(Transparent_ID)
                .append(CURRENT_PACKAGE)
                .append(deviceMac)
                .append(DIS_CONN_TIM)
                .append(PLACEHOLDERS)
                .append(FLAG)
                .append(SERVER_UUID_LENGTH)
                .append(Server_UUID)
                .append(CHARACTERISTIC_UUID_LENGTH)
                .append(CHARACTERISTIC_UUID)
                .append(TRANSPARENT_LENGTH);
        if (relays) {
            sb.append(OPEN);
        } else {
            sb.append(CLOSE);
        }
        sb.append(TIMESTAMP_TYPE)
                .append(CHECKSUM)
                .append(END_CHARACTER);
        return sb.toString();
    }


    /**
     * 执行上传间隔时间命令
     *
     * @param gatewayMac  网关mac地址
     * @param deviceMac   设备mac地址
     * @param hexInterval 继电器开关
     * @return
     */
    public static String getDeviceUploadIntervalCommand(String gatewayMac,
                                                        String deviceMac,
                                                        String hexInterval) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX)
                .append(PACKAGE_LENGTH_INTERVAL)
                .append(gatewayMac)
                .append(DEVICE_TYPE)
                .append(DATA_HEADER)
                .append(TOTO_LENGTH_INTERVAL)
                .append(DEVICE_SPECIFIC_TYPE)
                .append(COMMAND_REQUEST)
                .append(DATA_LENGTH_INTERVAL)
                .append(MSG_ID)
                .append(Transparent_ID)
                .append(CURRENT_PACKAGE)
                .append(deviceMac)
                .append(DIS_CONN_TIM)
                .append(PLACEHOLDERS)
                .append(FLAG)
                .append(SERVER_UUID_LENGTH)
                .append(Server_UUID)
                .append(CHARACTERISTIC_UUID_LENGTH)
                .append(CHARACTERISTIC_UUID_INTERVAL)
                .append(TRANSPARENT_LENGTH_INTERVAL)
                .append(DEFAULT_INTERVAL)
                .append(hexInterval)
                .append(DEFAULT_TIMESTAMP_UNIT)
                .append(TIMESTAMP_TYPE)
                .append(CHECKSUM)
                .append(END_CHARACTER);
        return sb.toString();
    }


    public static void main(String[] args) {
        String gatewayMac = "DE556375359D";
        String deviceMac = "DCDA0CCDFC4A";

        System.out.println("上传间隔 = " + getDeviceUploadIntervalCommand(gatewayMac, deviceMac, "03E8"));

    }

}
