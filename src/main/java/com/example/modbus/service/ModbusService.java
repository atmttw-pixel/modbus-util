package com.example.modbus.service;

import com.example.modbus.client.BitArea;
import com.example.modbus.client.ModbusClient;
import com.example.modbus.client.RegisterArea;
import com.example.modbus.codec.ModbusPayloadCodec;
import com.example.modbus.codec.RegisterByteOrder;
import com.example.modbus.dto.RawBitsResponse;
import com.example.modbus.dto.RawRegisterResponse;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import org.springframework.stereotype.Service;

/**
 * Modbus 采集业务服务。
 *
 * <p>这一层负责组织连接生命周期、调用底层 client，并把原始数据整理成接口返回对象。</p>
 */
@Service
public class ModbusService {

    /** 底层 Modbus 客户端。 */
    private final ModbusClient modbusClient;

    public ModbusService(ModbusClient modbusClient) {
        this.modbusClient = modbusClient;
    }

    /**
     * 读取连续寄存器并转换成多种原始数据展示格式。
     *
     * @param ip 目标设备 IP
     * @param port 目标设备端口
     * @param slaveId 从站 ID
     * @param functionCode 功能码，3 或 4
     * @param offset 起始地址，从 0 开始
     * @param quantity 读取寄存器数量
     * @param byteOrder 单个寄存器内部字节序
     * @return 原始寄存器采集结果
     */
    public RawRegisterResponse readRawRegisters(String ip,
                                                int port,
                                                int slaveId,
                                                int functionCode,
                                                int offset,
                                                int quantity,
                                                RegisterByteOrder byteOrder)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        RegisterArea area = RegisterArea.fromFunctionCode(functionCode);
        short[] registers = withMaster(ip, port, master ->
                modbusClient.readRegisters(slaveId, offset, quantity, area, master)
        );
        byte[] bytes = ModbusPayloadCodec.registersToBytes(registers, byteOrder);

        return new RawRegisterResponse(
                ip,
                port,
                slaveId,
                area.getFunctionCode(),
                offset,
                quantity,
                byteOrder.name(),
                ModbusPayloadCodec.toUnsignedRegisters(registers),
                ModbusPayloadCodec.toUnsignedBytes(bytes),
                ModbusPayloadCodec.toHex(bytes),
                ModbusPayloadCodec.toBase64(bytes),
                ModbusPayloadCodec.toAsciiPreview(bytes),
                ModbusPayloadCodec.toBinaryString(bytes)
        );
    }

    /**
     * 读取连续 bit 并转换成 boolean 列表和 0/1 字符串。
     */
    public RawBitsResponse readRawBits(String ip,
                                       int port,
                                       int slaveId,
                                       int functionCode,
                                       int offset,
                                       int quantity)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        BitArea area = BitArea.fromFunctionCode(functionCode);
        boolean[] values = withMaster(ip, port, master ->
                modbusClient.readBits(slaveId, offset, quantity, area, master)
        );

        return new RawBitsResponse(
                ip,
                port,
                slaveId,
                area.getFunctionCode(),
                offset,
                quantity,
                ModbusPayloadCodec.toBooleanList(values),
                ModbusPayloadCodec.bitsToBinaryString(values)
        );
    }

    /**
     * 读取单个线圈状态。
     */
    public Boolean readCoilStatus(String ip, int port, int slaveId, int offset)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        return withMaster(ip, port, master -> modbusClient.readCoilStatus(slaveId, offset, master));
    }

    /**
     * 写入单个线圈状态。
     */
    public void writeCoilStatus(String ip, int port, int slaveId, int offset, boolean value)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        withMaster(ip, port, master -> {
            modbusClient.writeCoilStatus(slaveId, offset, value, master);
            return null;
        });
    }

    /**
     * 读取单个离散输入状态。
     */
    public Boolean readInputStatus(String ip, int port, int slaveId, int offset)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        return withMaster(ip, port, master -> modbusClient.readInputStatus(slaveId, offset, master));
    }

    /**
     * 按 dataType 读取单个保持寄存器值。
     */
    public Number readHoldingRegister(String ip, int port, int slaveId, int offset, int dataType)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        return withMaster(ip, port, master ->
                modbusClient.readHoldingRegister(slaveId, offset, dataType, master)
        );
    }

    /**
     * 按 dataType 写入保持寄存器值。
     */
    public void writeHoldingRegister(String ip, int port, int slaveId, int offset, int dataType, Number value)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        withMaster(ip, port, master -> {
            modbusClient.writeHoldingRegister(slaveId, offset, dataType, value, master);
            return null;
        });
    }

    /**
     * 按 dataType 读取单个输入寄存器值。
     */
    public Number readInputRegister(String ip, int port, int slaveId, int offset, int dataType)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        return withMaster(ip, port, master ->
                modbusClient.readInputRegister(slaveId, offset, dataType, master)
        );
    }

    /**
     * 统一管理 ModbusMaster 生命周期，避免连接未释放。
     */
    private <T> T withMaster(String ip, int port, MasterCallback<T> callback)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        ModbusMaster master = null;
        try {
            master = modbusClient.createTcpMaster(ip, port);
            return callback.execute(master);
        } finally {
            if (master != null) {
                master.destroy();
            }
        }
    }

    /**
     * 在已创建的 Master 上执行一次 Modbus 操作。
     *
     * @param <T> 操作返回值类型
     */
    @FunctionalInterface
    private interface MasterCallback<T> {
        T execute(ModbusMaster master)
                throws ModbusTransportException, ErrorResponseException;
    }
}
