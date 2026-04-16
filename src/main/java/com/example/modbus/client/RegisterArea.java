package com.example.modbus.client;

import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.ReadInputRegistersRequest;

/**
 * Modbus 寄存器数据区。
 *
 * <p>功能码 03 和 04 读取的都是 16 bit 寄存器，生产上的二进制 payload
 * 通常就是拆在这一段连续寄存器里。</p>
 */
public enum RegisterArea {
    /** 功能码 03：保持寄存器，可读写。 */
    HOLDING(3),
    /** 功能码 04：输入寄存器，只读。 */
    INPUT(4);

    /** 对应的 Modbus 功能码。 */
    private final int functionCode;

    RegisterArea(int functionCode) {
        this.functionCode = functionCode;
    }

    public int getFunctionCode() {
        return functionCode;
    }

    /**
     * 根据当前寄存器区类型创建读取请求。
     *
     * @param slaveId 从站 ID
     * @param offset 起始地址，按 modbus4j 约定从 0 开始
     * @param quantity 读取寄存器数量
     * @return modbus4j 读取请求对象
     * @throws ModbusTransportException 请求参数不合法时抛出
     */
    public ModbusRequest createReadRequest(int slaveId, int offset, int quantity)
            throws ModbusTransportException {
        return switch (this) {
            case HOLDING -> new ReadHoldingRegistersRequest(slaveId, offset, quantity);
            case INPUT -> new ReadInputRegistersRequest(slaveId, offset, quantity);
        };
    }

    /**
     * 把功能码转换为寄存器数据区枚举。
     *
     * @param functionCode 功能码，只支持 3 或 4
     * @return 对应的数据区
     */
    public static RegisterArea fromFunctionCode(int functionCode) {
        for (RegisterArea area : values()) {
            if (area.functionCode == functionCode) {
                return area;
            }
        }
        throw new IllegalArgumentException("functionCode 只支持 3(holding register) 或 4(input register)");
    }
}
