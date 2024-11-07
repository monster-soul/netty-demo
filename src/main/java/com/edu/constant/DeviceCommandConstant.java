package com.edu.constant;

import lombok.Data;

/**
 * @author pengXiangJun
 * @date 2024年08月01日
 * @program
 * @description
 */
@Data
public class DeviceCommandConstant {

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
    public static String LENGTH__RELAYS = "0017";

    /**
     * 包长
     */
    public static String LENGTH_SYNC_TIME = "001A";

    /**
     *  上传间隔
     */
    public static String LENGTH_INTERVAL = "0018";

    /**
     * 设备类型 ME 开头设备设置42
     */
    public static String DEVICE_TYPE = "0042";

    /**
     * 数据头 默认5743
     */
    public static String DATA_HEADER = "5743";

    /**
     * 数据长度
     */
    public static String DATA_LENGTH = "07";
    public static String DATA_LENGTH_SYNC_TIME = "05";
    public static String DATA_LENGTH_INTERVAL = "08";


    /**
     * 设备类型 (智能计量插座设备 类型 0x42)
     */
    public static String SHORTEN_RECEPTACLE_DEVICE_TYPE = "42";

    /**
     * 命令类型 (0x91 设置设备参数)
     */
    public static String COMMAND_TYPE = "91";

    /**
     * 同步时间
     */
    public static String COMMAND_TYPE_SYNC_TIME = "02";

    /**
     * 实际区数据长度
     */
    public static String ACTUAL_DATA_LENGTH = "02";

    /**
     * msgID
     */
    public static String MSG_ID = "03";

    /**
     * 开
     */
    public static String OPEN = "01";

    /**
     * 关
     */
    public static String CLOSE = "00";


    /**
     * 时间戳类型      (固定）
     */
    public static String PASS_STATUS = "00";

    /**
     *
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
     * @param deviceMac 命令
     * @param relays    继电器开关
     * @return
     */
    public static String getDeviceRelaysCommand(String deviceMac, Boolean relays) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX)
                .append(LENGTH__RELAYS)
                .append(deviceMac)
                .append(DEVICE_TYPE)
                .append(DATA_HEADER)
                .append(DATA_LENGTH)
                .append(SHORTEN_RECEPTACLE_DEVICE_TYPE)
                .append(COMMAND_TYPE)
                .append(ACTUAL_DATA_LENGTH)
                .append(MSG_ID);

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
     * 同步设备时间
     *
     * @param deviceMac 命令
     * @return
     */
    public static String getSyncDeviceTimeCommand(String deviceMac, String secondLevelHex) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX)
                .append(LENGTH_SYNC_TIME)
                .append(deviceMac)
                .append(DEVICE_TYPE)
                .append(DATA_HEADER)
                .append(END_CHARACTER)
                .append(SHORTEN_RECEPTACLE_DEVICE_TYPE)
                .append(COMMAND_TYPE_SYNC_TIME)
                .append(DATA_LENGTH_SYNC_TIME)
                .append(secondLevelHex)
                .append(PASS_STATUS)
                .append(TIMESTAMP_TYPE)
                .append(CHECKSUM)
                .append(END_CHARACTER);
        return sb.toString();
    }

    /**
     * 插座设备上传间隔
     *
     * @param deviceMac 命令
     * @return
     */
    public static String getUploadIntervalDeviceTimeCommand(String deviceMac, String interval) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX)
                .append(LENGTH_INTERVAL)
                .append(deviceMac)
                .append(DEVICE_TYPE)
                .append(DATA_HEADER)
                .append(DATA_LENGTH_INTERVAL)
                .append(SHORTEN_RECEPTACLE_DEVICE_TYPE)
                .append(COMMAND_TYPE)
                .append(MSG_ID)
                .append(ACTUAL_DATA_LENGTH)
                .append(interval)
                .append(TIMESTAMP_TYPE)
                .append(CHECKSUM)
                .append(END_CHARACTER);
        return sb.toString();
    }


    public static void main(String[] args) {
        String mac = "DCDA0CCDFC4A";
        String sync = "66A9D88E";
        String interval = "000A";
        System.out.println("getDeviceRelaysCommand() = " + getDeviceRelaysCommand(mac, true));

        System.out.println("getDeviceRelaysCommand() = " + getDeviceRelaysCommand(mac, false));

        System.out.println("getSyncDeviceTimeCommand(mac,sync) = " + getSyncDeviceTimeCommand(mac, sync));

        System.out.println("getUploadIntervalDeviceTimeCommand(sync,interval) = " + getUploadIntervalDeviceTimeCommand(mac, interval));

    }
}
