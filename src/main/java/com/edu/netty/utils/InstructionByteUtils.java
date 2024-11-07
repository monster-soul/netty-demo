package com.edu.netty.utils;

import lombok.Data;

/**
 * @author pengXiangJun
 * @date 2024年07月29日
 * @program
 * @description
 */
@Data
public class InstructionByteUtils {

    public static short byteArrayToShort(byte[] array, int offset, boolean littleEndian) {
        if (array == null || offset < 0 || offset + 1 >= array.length) {
            throw new IllegalArgumentException("Invalid parameters.");
        }
        if (littleEndian) {
            return (short) ((array[offset] & 0xFF) | ((array[offset + 1] & 0xFF) << 8));
        } else {
            return (short) (((array[offset] & 0xFF) << 8) | (array[offset + 1] & 0xFF));
        }
    }
}
