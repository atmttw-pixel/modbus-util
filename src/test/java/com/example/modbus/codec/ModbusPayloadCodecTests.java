package com.example.modbus.codec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Modbus 原始 payload 编码工具测试。
 */
class ModbusPayloadCodecTests {

    /**
     * 验证大端字节序：寄存器高字节在前。
     */
    @Test
    void convertsRegistersToBigEndianBytes() {
        short[] registers = {(short) 0x1234, (short) 0xABCD};

        byte[] bytes = ModbusPayloadCodec.registersToBytes(registers, RegisterByteOrder.BIG_ENDIAN);

        assertThat(ModbusPayloadCodec.toUnsignedBytes(bytes)).isEqualTo(List.of(0x12, 0x34, 0xAB, 0xCD));
        assertThat(ModbusPayloadCodec.toHex(bytes)).isEqualTo("12 34 AB CD");
        assertThat(ModbusPayloadCodec.toBinaryString(bytes)).isEqualTo("00010010 00110100 10101011 11001101");
    }

    /**
     * 验证小端字节序：寄存器低字节在前。
     */
    @Test
    void convertsRegistersToLittleEndianBytes() {
        short[] registers = {(short) 0x1234, (short) 0xABCD};

        byte[] bytes = ModbusPayloadCodec.registersToBytes(registers, RegisterByteOrder.LITTLE_ENDIAN);

        assertThat(ModbusPayloadCodec.toUnsignedBytes(bytes)).isEqualTo(List.of(0x34, 0x12, 0xCD, 0xAB));
        assertThat(ModbusPayloadCodec.toHex(bytes)).isEqualTo("34 12 CD AB");
    }
}
