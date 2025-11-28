package com.example.modbus;

import com.example.modbus.dto.ResponseDto;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.modbus.ModbusClient.getMaster;

@RestController
@RequestMapping("/modbus")
public class ModbusController {

    // 读取 Holding Register (03功能码)
    @GetMapping("/readHoldingRegister")
    public String readHoldingRegister(@RequestParam Integer slaveId,
                                      @RequestParam Integer offset,
                                      @RequestParam String ip,
                                      @RequestParam Integer port,
                                      @RequestParam Integer type) {
        ModbusMaster master = null;
        try {
            // 获取 ModbusMaster 实例
            master = getMaster(ip, port);

            // 执行读取操作
//            int i = offset  -1;
            Number holdingValue = ModbusClient.readHoldingRegister(slaveId, offset, type, master);
            return  holdingValue.toString();

        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException e) {
            return String.format("Xduqushibai: %s", e.getMessage());
        } finally {
            // 确保资源释放
            if (master != null) {
                master.destroy();
            }
        }
    }

    // 写入 Holding Register (03功能码)
    @GetMapping("/writeHoldingRegister")
    public ResponseDto writeHoldingRegister(@RequestParam Integer slaveId,
                                            @RequestParam Integer offset,
                                            @RequestParam Integer value,
                                            @RequestParam String ip,
                                            @RequestParam Integer port,
                                            @RequestParam Integer type) {
        ModbusMaster master = null;
        try {
            // 获取 ModbusMaster 实例
            master = getMaster(ip, port);
            // 执行写入操作
            ModbusClient.writeHoldingRegister(slaveId, offset, type, value, master);
            return new ResponseDto(200,String.format("xchenggong Holding Register: SlaveId=%d, Offset=%d, Value=%d", slaveId, offset, value));

        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException e) {
            return new ResponseDto(500,String.format("Xcuowu: %s", e.getMessage()));
        } finally {
            // 确保资源释放
            if (master != null) {
                master.destroy();
            }
        }
    }

    // 读取 Coil Status (01功能码)
    @GetMapping("/readCoilStatus")
    public Boolean readCoilStatus(@RequestParam Integer slaveId,
                                 @RequestParam Integer offset,
                                 @RequestParam String ip,
                                 @RequestParam Integer port) {
        ModbusMaster master = null;
        try {
            // 获取 ModbusMaster 实例
            master = getMaster(ip, port);

            // 执行读取操作
            Boolean coilValue = ModbusClient.readCoilStatus(slaveId, offset, master);
            return  coilValue;

        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException e) {
            return null;
        } finally {
            // 确保资源释放
            if (master != null) {
                master.destroy();
            }
        }
    }

    // 写入 Coil Status (01功能码)
    @GetMapping("/writeCoilStatus")
    public ResponseDto writeCoilStatus(@RequestParam Integer slaveId,
                                  @RequestParam Integer offset,
                                  @RequestParam Boolean value,
                                  @RequestParam String ip,
                                  @RequestParam Integer port) {
        ModbusMaster master = null;
        try {
            // 获取 ModbusMaster 实例
            master = getMaster(ip, port);

            // 执行写入操作
            ModbusClient.writeCoilStatus(slaveId, offset, value, master);
            return new ResponseDto(200,String.format("成功写入 Coil Status: SlaveId=%d, Offset=%d, Value=%b", slaveId, offset, value));

        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException e) {
            return new ResponseDto(500,String.format("写入失败: %s", e.getMessage()));
        } finally {
            // 确保资源释放
            if (master != null) {
                master.destroy();
            }
        }
    }

    // 读取 Input Status (02功能码)
    @GetMapping("/readInputStatus")
    public Boolean readInputStatus(@RequestParam Integer slaveId,
                                  @RequestParam Integer offset,
                                  @RequestParam String ip,
                                  @RequestParam Integer port) {
        ModbusMaster master = null;
        try {
            // 获取 ModbusMaster 实例
            master = getMaster(ip, port);

            // 执行读取操作
            Boolean inputStatus = ModbusClient.readInputStatus(slaveId, offset, master);
            return  inputStatus;

        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException e) {
            return null;
        } finally {
            // 确保资源释放
            if (master != null) {
                master.destroy();
            }
        }
    }

    // 读取 Input Registers (04功能码)
    @GetMapping("/readInputRegisters")
    public String readInputRegisters(@RequestParam Integer slaveId,
                                     @RequestParam Integer offset,
                                     @RequestParam String ip,
                                     @RequestParam Integer port,
                                     @RequestParam Integer type) {
        ModbusMaster master = null;
        try {
            // 获取 ModbusMaster 实例
            master = getMaster(ip, port);

            // 执行读取操作
            Number inputRegister = ModbusClient.readInputRegisters(slaveId, offset, type, master);
            return  inputRegister.toString();

        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException e) {
            return String.format("读取失败: %s", e.getMessage());
        } finally {
            // 确保资源释放
            if (master != null) {
                master.destroy();
            }
        }
    }
}
