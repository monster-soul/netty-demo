package com.edu.netty.utils;

import com.edu.constant.PhysicalDataTypeConstant;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 数据类型处理
 *
 * @author pengXiangJun
 * @date 2024年07月30日
 * @program
 * @description
 */
public class PhysicalDataTypeHandleUtils {

    public static final String Empty = "";
    public static final Double Thousand = 1000.0;
    public static final Double Hundred = 100.0;
    public static final Double Ten = 10.0;

    /**
     * 电压
     *
     * @param voltageData
     * @return
     */
    public static String handleVoltage(String voltageData) {
        if (voltageData.startsWith(PhysicalDataTypeConstant.VOLTAGE_TYPE)) {
            voltageData = voltageData.replaceFirst(PhysicalDataTypeConstant.VOLTAGE_TYPE, Empty);
            int voltageValue = ByteUtils.convertHexToDecimal(voltageData);
            Double voltage = voltageValue / Hundred;
            return String.valueOf(voltage);
        }
        return null;
    }

    /**
     * 处理电流数据
     *
     * @param currentData
     * @return
     */
    public static String handleCurrent(String currentData) {
        if (currentData.startsWith(PhysicalDataTypeConstant.CURRENT_TYPE)) {
            currentData = currentData.replaceFirst(PhysicalDataTypeConstant.CURRENT_TYPE, Empty);
            int currentValue = ByteUtils.convertHexToDecimal(currentData);
            Double current = currentValue / Thousand;
            return String.valueOf(current);
        }
        return null;
    }

    /**
     * 功率
     *
     * @param powerData
     * @return
     */
    public static String handlePower(String powerData) {
        if (powerData.startsWith(PhysicalDataTypeConstant.POWER_TYPE)) {
            powerData = powerData.replaceFirst(PhysicalDataTypeConstant.POWER_TYPE, Empty);
            int resultValue = ByteUtils.convertHexToDecimal(powerData);
            Double result = resultValue / Thousand;
            return String.valueOf(result);
        }
        return null;
    }

    /**
     * 频率
     *
     * @param frequencyData
     * @return
     */
    public static String handleFrequency(String frequencyData) {
        if (frequencyData.startsWith(PhysicalDataTypeConstant.FREQUENCY_TYPE)) {
            frequencyData = frequencyData.replaceFirst(PhysicalDataTypeConstant.FREQUENCY_TYPE, Empty);
            int resultValue = ByteUtils.convertHexToDecimal(frequencyData);
            Double result = resultValue / Hundred;
            return String.valueOf(result);
        }
        return null;
    }


    /**
     * 用电量
     *
     * @param electricityData
     * @return
     */
    public static String handleElectricity(String electricityData) {
        if (electricityData.startsWith(PhysicalDataTypeConstant.ELECTRICITY_TYPE)) {
            electricityData = electricityData.replaceFirst(PhysicalDataTypeConstant.ELECTRICITY_TYPE, Empty);
            Integer resultValue = ByteUtils.convertHexToDecimal(electricityData);
            BigDecimal divide = new BigDecimal(String.valueOf(resultValue));
            BigDecimal dividend = new BigDecimal("1000000");
            BigDecimal divide1 = divide.divide(dividend);
            return String.valueOf(divide1);
        }
        return null;
    }

    /**
     * 处理温度
     *
     * @param temperatureData
     * @return
     */
    public static String handleTemperature(String temperatureData) {
        if (temperatureData.startsWith(PhysicalDataTypeConstant.TEMPERATURE_TYPE)) {
            temperatureData = temperatureData.replaceFirst(PhysicalDataTypeConstant.TEMPERATURE_TYPE, Empty);
            int resultValue = ByteUtils.convertHexToDecimal(temperatureData);
            Double result = resultValue / Ten;
            return String.valueOf(result);
        }
        return null;
    }

    /**
     * 继电器开关
     *
     * @param relaysData
     * @return
     */
    public static String handleRelays(String relaysData) {
        if (relaysData.startsWith(PhysicalDataTypeConstant.RELAYS_TYPE)) {
            relaysData = relaysData.replaceFirst(PhysicalDataTypeConstant.RELAYS_TYPE, Empty);
            Integer resultValue = ByteUtils.convertHexToDecimal(relaysData);
            if (Objects.equals(1, resultValue)) {
                return "1";
            }
        }
        return "0";
    }

    /**
     * 信号强度
     *
     * @param signalStrength
     * @return
     */
    public static String handleSignalStrength(String signalStrength) {
        int resultValue = ByteUtils.convertHexToDecimal(signalStrength);
        return String.valueOf(resultValue);
    }

    /**
     * 功率因数
     *
     * @param factorData
     * @return
     */
    public static String handleFactorData(String factorData) {
        if (factorData.startsWith(PhysicalDataTypeConstant.FACTOR_TYPE)) {
            factorData = factorData.replaceFirst(PhysicalDataTypeConstant.FACTOR_TYPE, Empty);
            int resultValue = ByteUtils.convertHexToDecimal(factorData);
            Double result = resultValue / Hundred;
            return String.valueOf(result);
        }
        return null;
    }

    /**
     * 同步实际
     *
     * @param syncDateTime
     * @return
     */
    public static String handleSyncDateTime(String syncDateTime) {

        if (syncDateTime.startsWith(PhysicalDataTypeConstant.DATE_TYPE_OTHER)) {
            syncDateTime = syncDateTime.replaceFirst(PhysicalDataTypeConstant.DATE_TYPE_OTHER, Empty);
            int resultValue = ByteUtils.convertHexToDecimal(syncDateTime);
            return convertTimestamp(resultValue);
        }else if (syncDateTime.startsWith(PhysicalDataTypeConstant.Date_TYPE)) {
            syncDateTime = syncDateTime.replaceFirst(PhysicalDataTypeConstant.Date_TYPE, Empty);
            int resultValue = ByteUtils.convertHexToDecimal(syncDateTime);
            return convertTimestamp(resultValue);
        }
        return null;
    }


    /**
     * 时间戳转换
     *
     * @param timestamp
     * @return
     */
    public static String convertTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }


}
