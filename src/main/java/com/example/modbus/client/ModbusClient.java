package com.example.modbus.client;

import com.example.modbus.config.ModbusProperties;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ModbusResponse;
import com.serotonin.modbus4j.msg.ReadResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * Modbus TCP 客户端封装。
 *
 * <p>这一层只负责连接设备、发送请求和做基础参数校验，不处理业务字段解析。</p>
 */
@Component
public class ModbusClient {

    /** modbus4j 工厂对象，用来创建 TCP Master。 */
    private final ModbusFactory modbusFactory = new ModbusFactory();
    /** 客户端超时、重试等配置。 */
    private final ModbusProperties properties;

    public ModbusClient(ModbusProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建并初始化一个 Modbus TCP Master。
     *
     * @param ip 目标设备 IP
     * @param port 目标设备端口
     * @return 已初始化的 Master，调用方必须在 finally 中 destroy
     * @throws ModbusInitException 连接初始化失败时抛出
     */
    public ModbusMaster createTcpMaster(String ip, int port) throws ModbusInitException {
        validateTarget(ip, port);

        IpParameters params = new IpParameters();
        params.setHost(ip);
        params.setPort(port);

        ModbusMaster master = modbusFactory.createTcpMaster(params, false);
        master.setTimeout(properties.getClient().getTimeout());
        master.setRetries(properties.getClient().getRetries());
        master.init();
        return master;
    }

    /**
     * 读取单个线圈状态，功能码 01。
     */
    public Boolean readCoilStatus(int slaveId, int offset, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        validateSlaveAndOffset(slaveId, offset);
        BaseLocator<Boolean> locator = BaseLocator.coilStatus(slaveId, offset);
        return master.getValue(locator);
    }

    /**
     * 写入单个线圈状态，功能码 05。
     */
    public void writeCoilStatus(int slaveId, int offset, boolean value, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        validateSlaveAndOffset(slaveId, offset);
        BaseLocator<Boolean> locator = BaseLocator.coilStatus(slaveId, offset);
        master.setValue(locator, value);
    }

    /**
     * 读取单个离散输入状态，功能码 02。
     */
    public Boolean readInputStatus(int slaveId, int offset, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        validateSlaveAndOffset(slaveId, offset);
        BaseLocator<Boolean> locator = BaseLocator.inputStatus(slaveId, offset);
        return master.getValue(locator);
    }

    /**
     * 按指定 dataType 读取保持寄存器，功能码 03。
     */
    public Number readHoldingRegister(int slaveId, int offset, int dataType, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        validateSlaveAndOffset(slaveId, offset);
        validateDataType(dataType);
        BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, offset, dataType);
        return master.getValue(locator);
    }

    /**
     * 按指定 dataType 写入保持寄存器，功能码 06/16 由 modbus4j 根据数据长度决定。
     */
    public void writeHoldingRegister(int slaveId, int offset, int dataType, Number value, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        validateSlaveAndOffset(slaveId, offset);
        validateDataType(dataType);
        BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, offset, dataType);
        master.setValue(locator, value);
    }

    /**
     * 按指定 dataType 读取输入寄存器，功能码 04。
     */
    public Number readInputRegister(int slaveId, int offset, int dataType, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        validateSlaveAndOffset(slaveId, offset);
        validateDataType(dataType);
        BaseLocator<Number> locator = BaseLocator.inputRegister(slaveId, offset, dataType);
        return master.getValue(locator);
    }

    /**
     * 读取连续寄存器原始块。
     *
     * <p>生产上的二进制数据优先用这个方法采集，先拿原始 short[]，
     * 再交给 codec 层按字节序展开。</p>
     */
    public short[] readRegisters(int slaveId, int offset, int quantity, RegisterArea area, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        validateSlaveAndOffset(slaveId, offset);
        validateQuantity(quantity, 125, "寄存器");

        ModbusRequest request = area.createReadRequest(slaveId, offset, quantity);
        ReadResponse response = sendReadRequest(master, request);
        return response.getShortData();
    }

    /**
     * 读取连续 bit 原始块。
     *
     * <p>适用于功能码 01/02 批量读取开关量。</p>
     */
    public boolean[] readBits(int slaveId, int offset, int quantity, BitArea area, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        validateSlaveAndOffset(slaveId, offset);
        validateQuantity(quantity, 2000, "位");

        ModbusRequest request = area.createReadRequest(slaveId, offset, quantity);
        ReadResponse response = sendReadRequest(master, request);
        boolean[] values = response.getBooleanData();
        return values.length == quantity ? values : Arrays.copyOf(values, quantity);
    }

    /**
     * 发送读取请求并统一处理 Modbus 异常响应。
     */
    private ReadResponse sendReadRequest(ModbusMaster master, ModbusRequest request)
            throws ModbusTransportException, ErrorResponseException {
        ModbusResponse response = master.send(request);
        if (response.isException()) {
            throw new ErrorResponseException(request, response);
        }
        return (ReadResponse) response;
    }

    /**
     * 校验目标地址，避免空 IP 或非法端口直接传给底层库。
     */
    private void validateTarget(String ip, int port) {
        if (!StringUtils.hasText(ip)) {
            throw new IllegalArgumentException("ip 不能为空");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("port 必须在 1-65535 之间");
        }
    }

    /**
     * 校验从站和地址范围。
     */
    private void validateSlaveAndOffset(int slaveId, int offset) {
        if (slaveId < 1 || slaveId > 247) {
            throw new IllegalArgumentException("slaveId 必须在 1-247 之间");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset 必须大于等于 0");
        }
    }

    /**
     * 校验读取数量，寄存器最多 125 个，bit 最多 2000 个。
     */
    private void validateQuantity(int quantity, int max, String name) {
        if (quantity < 1 || quantity > max) {
            throw new IllegalArgumentException(name + " quantity 必须在 1-" + max + " 之间");
        }
    }

    /**
     * 校验 modbus4j 的 dataType 是否可用。
     */
    private void validateDataType(int dataType) {
        try {
            DataType.getRegisterCount(dataType);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("不支持的 dataType: " + dataType, ex);
        }
    }
}
