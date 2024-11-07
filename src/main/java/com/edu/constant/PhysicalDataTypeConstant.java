package com.edu.constant;

import lombok.Data;

/**
 * 命令类型编码常量
 *
 * @author pengXiangJun
 * @date 2024年07月30日
 * @program
 * @description
 */
@Data
public class PhysicalDataTypeConstant {


    /**
     * 电压正常类型
     */
    public static String VOLTAGE_TYPE = "A0";


    /**
     * 电流
     */
    public static String CURRENT_TYPE = "A1";

    /**
     * 功率
     */
    public static String POWER_TYPE = "E2";

    /**
     * 电压频率
     */
    public static String FREQUENCY_TYPE = "A3";


    /**
     * 用电量
     */
    public static String ELECTRICITY_TYPE = "E5";

    /**
     * 继电器状态
     */
    public static String RELAYS_TYPE = "86";

    /**
     * 功率因数
     */
    public static String FACTOR_TYPE = "84";

    /**
     * 温度
     */
    public static String TEMPERATURE_TYPE = "89";

    /**
     * 时间格式
     */
    public static String Date_TYPE = "E8";

    /**
     *  另一种时间格式
     */
    public static String DATE_TYPE_OTHER = "04";


    /**
     * 十六进制
     */
    private static Integer hexadecimal = 16;

}
