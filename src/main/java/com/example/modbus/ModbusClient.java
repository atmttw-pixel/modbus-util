package com.example.modbus;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.locator.BaseLocator;
import org.springframework.stereotype.Component;

@Component
public class ModbusClient {

    // 获取 Modbus Master
    public static ModbusMaster getMaster(String ip, Integer port) throws ModbusInitException {
        IpParameters params = new IpParameters();
        params.setHost(ip);
        params.setPort(port);

        ModbusMaster master = new ModbusFactory().createTcpMaster(params, false); // TCP 协议
        master.init();
        return master;
    }

    // 读取 01 Coil Status (开关量)
    public static Boolean readCoilStatus(int slaveId, int offset, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        BaseLocator<Boolean> loc = BaseLocator.coilStatus(slaveId, offset);
        return master.getValue(loc);
    }

    // 写入 01 Coil Status (开关量)
    public static void writeCoilStatus(int slaveId, int offset, boolean value, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        BaseLocator<Boolean> loc = BaseLocator.coilStatus(slaveId, offset);
        master.setValue(loc, value);
    }

    // 读取 02 Input Status (输入位，位状态，只读)
    public static Boolean readInputStatus(int slaveId, int offset, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        BaseLocator<Boolean> loc = BaseLocator.inputStatus(slaveId, offset);
        return master.getValue(loc);
    }

    // 读取 03 Holding Register (模拟量，保持寄存器)
    public static Number readHoldingRegister(int slaveId, int offset, int dataType, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        BaseLocator<Number> loc = BaseLocator.holdingRegister(slaveId, offset, dataType);
        return master.getValue(loc);
    }

    // 写入 03 Holding Register (模拟量，保持寄存器)
    public static void writeHoldingRegister(int slaveId, int offset, int dataType, Number value, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        BaseLocator<Number> loc = BaseLocator.holdingRegister(slaveId, offset, dataType);
        master.setValue(loc, value);
    }

    // 读取 04 Input Register (模拟量，输入寄存器，只读)
    public static Number readInputRegisters(int slaveId, int offset, int dataType, ModbusMaster master)
            throws ModbusTransportException, ErrorResponseException {
        BaseLocator<Number> loc = BaseLocator.inputRegister(slaveId, offset, dataType);
        return master.getValue(loc);
    }

    public static void testReadAndWrite() {
        try {
            String ip = "192.168.0.61"; // 目标设备 IP
            int port = 5050; // 目标设备端口
            int slaveId = 1; // 从站 ID
            ModbusMaster master = getMaster(ip, port);

            // 1. Coil Status (开关量) 读写
            writeCoilStatus(slaveId, 3, true, master);
            System.out.println("成功写入 Coil Status: Offset=3, Value=true");
            Boolean coilValue = readCoilStatus(slaveId, 3, master);
            System.out.println("读取 Coil Status 的值：" + coilValue);
//
//            // 2. Input Status (输入位，状态) 读取
//            Boolean inputStatus = readInputStatus(slaveId, 6, master);
//            System.out.println("读取 Input Status 的值：" + inputStatus);

            // 3. Holding Register (保持寄存器) 读写
            writeHoldingRegister(slaveId, 3, 2, 1, master);
//            System.out.println("成功写入 Holding Register: Offset=1, Value=123");
            Number holdingValue = readHoldingRegister(slaveId, 3, 2, master);
            System.out.println("读取 Holding Register 的值：" + holdingValue);

            // 4. Input Register (输入寄存器) 读取
//            Number inputValue = readInputRegisters(slaveId, 1, DataType.TWO_BYTE_INT_SIGNED, master);
//            System.out.println("读取 Input Register 的值：" + inputValue);

            master.destroy();
        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testReadAndWrite();
    }
}
