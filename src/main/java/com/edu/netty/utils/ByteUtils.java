package com.edu.netty.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ByteUtils {

    /**
     * 提取字节数组的子数组
     *
     * @param start
     * @param end
     * @param bytes
     * @return
     */
    public static byte[] getByteByByte(int start, int end, byte[] bytes) {
        if (start < 0 || end > bytes.length || start > end) {
            throw new IllegalArgumentException("Invalid start or end index.");
        }
        return Arrays.copyOfRange(bytes, start, end);
    }

    /**
     * 根据基数将字节数组转换为字符串（适用于十六进制）
     *
     * @param bytes
     * @param radix
     * @return
     */
    public static String getStringByByte(byte[] bytes, int radix) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toString((b & 0xFF) + 0x100, radix).substring(1));
        }
        return sb.toString().toUpperCase(); // 转换为大写，如果需要
    }


    /**
     * 将字节数组转换为字符串，默认使用UTF-8编码
     *
     * @param bytes
     * @return
     */
    public static String getStringByByteArr(byte[] bytes) {
        try {
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert byte array to string.", e);
        }
    }


    /**
     * 将字节数组转换为字符串，使用UTF-8编码
     *
     * @param bytes
     * @return
     */
    public static String bytesToString(byte[] bytes) {
        try {
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Handle any exception that might occur during conversion
            throw new RuntimeException("Error converting byte array to string", e);
        }
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    /**
     * 示例进制转换成十进制
     *
     * @param hex
     * @return
     */
    public static int convertHexToDecimal(String hex) {
        return Integer.parseInt(hex, 16);
    }

    /**
     *  十六进制转二进制文件
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


}