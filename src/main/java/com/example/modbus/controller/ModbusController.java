package com.example.modbus.controller;

import com.example.modbus.codec.RegisterByteOrder;
import com.example.modbus.dto.ApiResponse;
import com.example.modbus.dto.RawBitsResponse;
import com.example.modbus.dto.RawRegisterResponse;
import com.example.modbus.service.ModbusService;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Modbus HTTP 接口入口。
 *
 * <p>新接口用于原始块采集，旧接口继续保留，避免已经在用的调用方式失效。</p>
 */
@RestController
@RequestMapping("/modbus")
public class ModbusController {

    /** 采集业务服务。 */
    private final ModbusService modbusService;

    public ModbusController(ModbusService modbusService) {
        this.modbusService = modbusService;
    }

    /**
     * 读取连续寄存器原始数据。
     *
     * <p>生产上的二进制 payload 推荐先用这个接口采下来，再根据厂家协议解析。</p>
     */
    @GetMapping({"/readRawRegisters", "/raw/registers"})
    public ApiResponse<RawRegisterResponse> readRawRegisters(@RequestParam String ip,
                                                             @RequestParam Integer port,
                                                             @RequestParam Integer slaveId,
                                                             @RequestParam Integer offset,
                                                             @RequestParam Integer quantity,
                                                             @RequestParam(defaultValue = "3") Integer functionCode,
                                                             @RequestParam(defaultValue = "BIG_ENDIAN") String byteOrder)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        RawRegisterResponse data = modbusService.readRawRegisters(
                ip,
                port,
                slaveId,
                functionCode,
                offset,
                quantity,
                RegisterByteOrder.from(byteOrder)
        );
        return ApiResponse.success(data);
    }

    /**
     * 读取连续 bit 原始数据。
     *
     * <p>适用于功能码 01 线圈或功能码 02 离散输入。</p>
     */
    @GetMapping({"/readRawBits", "/raw/bits"})
    public ApiResponse<RawBitsResponse> readRawBits(@RequestParam String ip,
                                                    @RequestParam Integer port,
                                                    @RequestParam Integer slaveId,
                                                    @RequestParam Integer offset,
                                                    @RequestParam Integer quantity,
                                                    @RequestParam(defaultValue = "1") Integer functionCode)
            throws ModbusInitException, ModbusTransportException, ErrorResponseException {
        RawBitsResponse data = modbusService.readRawBits(ip, port, slaveId, functionCode, offset, quantity);
        return ApiResponse.success(data);
    }

    /**
     * 兼容旧接口：读取单个保持寄存器。
     */
    @GetMapping("/readHoldingRegister")
    public String readHoldingRegister(@RequestParam Integer slaveId,
                                      @RequestParam Integer offset,
                                      @RequestParam String ip,
                                      @RequestParam Integer port,
                                      @RequestParam Integer type) {
        try {
            Number value = modbusService.readHoldingRegister(ip, port, slaveId, offset, type);
            return value.toString();
        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException | IllegalArgumentException e) {
            return String.format("读取失败: %s", e.getMessage());
        }
    }

    /**
     * 兼容旧接口：写入单个保持寄存器。
     */
    @GetMapping("/writeHoldingRegister")
    public ApiResponse<Void> writeHoldingRegister(@RequestParam Integer slaveId,
                                                  @RequestParam Integer offset,
                                                  @RequestParam Integer value,
                                                  @RequestParam String ip,
                                                  @RequestParam Integer port,
                                                  @RequestParam Integer type) {
        try {
            modbusService.writeHoldingRegister(ip, port, slaveId, offset, type, value);
            return ApiResponse.success(String.format(
                    "成功写入 Holding Register: SlaveId=%d, Offset=%d, Value=%d",
                    slaveId,
                    offset,
                    value
            ));
        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException | IllegalArgumentException e) {
            return ApiResponse.fail(500, String.format("写入失败: %s", e.getMessage()));
        }
    }

    /**
     * 兼容旧接口：读取单个线圈状态。
     */
    @GetMapping("/readCoilStatus")
    public Boolean readCoilStatus(@RequestParam Integer slaveId,
                                  @RequestParam Integer offset,
                                  @RequestParam String ip,
                                  @RequestParam Integer port) {
        try {
            return modbusService.readCoilStatus(ip, port, slaveId, offset);
        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 兼容旧接口：写入单个线圈状态。
     */
    @GetMapping("/writeCoilStatus")
    public ApiResponse<Void> writeCoilStatus(@RequestParam Integer slaveId,
                                             @RequestParam Integer offset,
                                             @RequestParam Boolean value,
                                             @RequestParam String ip,
                                             @RequestParam Integer port) {
        try {
            modbusService.writeCoilStatus(ip, port, slaveId, offset, value);
            return ApiResponse.success(String.format(
                    "成功写入 Coil Status: SlaveId=%d, Offset=%d, Value=%b",
                    slaveId,
                    offset,
                    value
            ));
        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException | IllegalArgumentException e) {
            return ApiResponse.fail(500, String.format("写入失败: %s", e.getMessage()));
        }
    }

    /**
     * 兼容旧接口：读取单个离散输入状态。
     */
    @GetMapping("/readInputStatus")
    public Boolean readInputStatus(@RequestParam Integer slaveId,
                                   @RequestParam Integer offset,
                                   @RequestParam String ip,
                                   @RequestParam Integer port) {
        try {
            return modbusService.readInputStatus(ip, port, slaveId, offset);
        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 兼容旧接口：读取单个输入寄存器。
     */
    @GetMapping("/readInputRegisters")
    public String readInputRegisters(@RequestParam Integer slaveId,
                                     @RequestParam Integer offset,
                                     @RequestParam String ip,
                                     @RequestParam Integer port,
                                     @RequestParam Integer type) {
        try {
            Number value = modbusService.readInputRegister(ip, port, slaveId, offset, type);
            return value.toString();
        } catch (ModbusInitException | ModbusTransportException | ErrorResponseException | IllegalArgumentException e) {
            return String.format("读取失败: %s", e.getMessage());
        }
    }
}
