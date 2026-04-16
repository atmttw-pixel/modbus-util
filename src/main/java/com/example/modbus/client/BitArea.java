package com.example.modbus.client;

import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ReadCoilsRequest;
import com.serotonin.modbus4j.msg.ReadDiscreteInputsRequest;

/**
 * Modbus 位数据区。
 *
 * <p>功能码 01 和 02 读取的都是单 bit 数据，返回结果会被转换成 boolean 数组。</p>
 */
public enum BitArea {
    /** 功能码 01：线圈，可读写。 */
    COIL(1),
    /** 功能码 02：离散输入，只读。 */
    DISCRETE_INPUT(2);

    /** 对应的 Modbus 功能码。 */
    private final int functionCode;

    BitArea(int functionCode) {
        this.functionCode = functionCode;
    }

    public int getFunctionCode() {
        return functionCode;
    }

    /**
     * 根据当前位区类型创建读取请求。
     *
     * @param slaveId 从站 ID
     * @param offset 起始地址，按 modbus4j 约定从 0 开始
     * @param quantity 读取位数量
     * @return modbus4j 读取请求对象
     * @throws ModbusTransportException 请求参数不合法时抛出
     */
    public ModbusRequest createReadRequest(int slaveId, int offset, int quantity)
            throws ModbusTransportException {
        return switch (this) {
            case COIL -> new ReadCoilsRequest(slaveId, offset, quantity);
            case DISCRETE_INPUT -> new ReadDiscreteInputsRequest(slaveId, offset, quantity);
        };
    }

    /**
     * 把功能码转换为位数据区枚举。
     *
     * @param functionCode 功能码，只支持 1 或 2
     * @return 对应的数据区
     */
    public static BitArea fromFunctionCode(int functionCode) {
        for (BitArea area : values()) {
            if (area.functionCode == functionCode) {
                return area;
            }
        }
        throw new IllegalArgumentException("functionCode 只支持 1(coil) 或 2(discrete input)");
    }
}
