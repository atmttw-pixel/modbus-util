package com.example.modbus.codec;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Modbus 原始 payload 编解码工具。
 *
 * <p>这里不理解具体业务点位，只负责把寄存器或 bit 转成更容易排查的格式。</p>
 */
public final class ModbusPayloadCodec {

    private ModbusPayloadCodec() {
    }

    /**
     * 把 16 bit 寄存器数组按指定字节序展开成 byte 数组。
     *
     * @param registers 原始寄存器值
     * @param byteOrder 单个寄存器内部的字节顺序
     * @return 展开后的字节数组
     */
    public static byte[] registersToBytes(short[] registers, RegisterByteOrder byteOrder) {
        byte[] bytes = new byte[registers.length * 2];
        for (int i = 0; i < registers.length; i++) {
            int value = registers[i] & 0xFFFF;
            byte high = (byte) ((value >>> 8) & 0xFF);
            byte low = (byte) (value & 0xFF);
            int index = i * 2;
            if (byteOrder == RegisterByteOrder.BIG_ENDIAN) {
                bytes[index] = high;
                bytes[index + 1] = low;
            } else {
                bytes[index] = low;
                bytes[index + 1] = high;
            }
        }
        return bytes;
    }

    /**
     * 把 Java short 转成 0-65535 的无符号寄存器值。
     */
    public static List<Integer> toUnsignedRegisters(short[] registers) {
        List<Integer> values = new ArrayList<>(registers.length);
        for (short register : registers) {
            values.add(register & 0xFFFF);
        }
        return values;
    }

    /**
     * 把 Java byte 转成 0-255 的无符号字节值。
     */
    public static List<Integer> toUnsignedBytes(byte[] bytes) {
        List<Integer> values = new ArrayList<>(bytes.length);
        for (byte value : bytes) {
            values.add(value & 0xFF);
        }
        return values;
    }

    /**
     * 转成空格分隔的十六进制字符串，方便和厂家协议文档核对。
     */
    public static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 3);
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                builder.append(' ');
            }
            int value = bytes[i] & 0xFF;
            if (value < 0x10) {
                builder.append('0');
            }
            builder.append(Integer.toHexString(value).toUpperCase());
        }
        return builder.toString();
    }

    /**
     * 转成 Base64，适合把原始二进制安全传给其他系统。
     */
    public static String toBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 转成 ASCII 预览，不可见字符用点号代替。
     */
    public static String toAsciiPreview(byte[] bytes) {
        String ascii = new String(bytes, StandardCharsets.US_ASCII);
        StringBuilder builder = new StringBuilder(ascii.length());
        for (int i = 0; i < ascii.length(); i++) {
            char value = ascii.charAt(i);
            if (value >= 32 && value <= 126) {
                builder.append(value);
            } else {
                builder.append('.');
            }
        }
        return builder.toString();
    }

    /**
     * 转成按字节分组的二进制字符串。
     */
    public static String toBinaryString(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 9);
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                builder.append(' ');
            }
            String binary = Integer.toBinaryString(bytes[i] & 0xFF);
            builder.append("0".repeat(8 - binary.length()));
            builder.append(binary);
        }
        return builder.toString();
    }

    /**
     * 把 boolean 数组转成连续 0/1 字符串。
     */
    public static String bitsToBinaryString(boolean[] values) {
        StringBuilder builder = new StringBuilder(values.length);
        for (boolean value : values) {
            builder.append(value ? '1' : '0');
        }
        return builder.toString();
    }

    /**
     * 把 boolean[] 转成 List<Boolean>，便于 JSON 返回。
     */
    public static List<Boolean> toBooleanList(boolean[] values) {
        List<Boolean> result = new ArrayList<>(values.length);
        for (boolean value : values) {
            result.add(value);
        }
        return result;
    }
}
