package com.example.modbus.codec;

import java.util.Locale;

/**
 * 单个 16 bit 寄存器内部的字节顺序。
 *
 * <p>Modbus 标准寄存器一般是高字节在前，但部分设备协议会把低字节放前面。</p>
 */
public enum RegisterByteOrder {
    /** 高字节在前，例如寄存器 0x1234 展开为 12 34。 */
    BIG_ENDIAN,
    /** 低字节在前，例如寄存器 0x1234 展开为 34 12。 */
    LITTLE_ENDIAN;

    /**
     * 从接口参数解析字节序，兼容常见写法。
     *
     * @param value 用户传入的字节序字符串
     * @return 解析后的字节序
     */
    public static RegisterByteOrder from(String value) {
        if (value == null || value.isBlank()) {
            return BIG_ENDIAN;
        }
        String normalized = value.trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);

        return switch (normalized) {
            case "BIG", "BE", "BIG_ENDIAN", "HIGH_LOW", "AB" -> BIG_ENDIAN;
            case "LITTLE", "LE", "LITTLE_ENDIAN", "LOW_HIGH", "BA" -> LITTLE_ENDIAN;
            default -> throw new IllegalArgumentException("byteOrder 只支持 BIG_ENDIAN 或 LITTLE_ENDIAN");
        };
    }
}
