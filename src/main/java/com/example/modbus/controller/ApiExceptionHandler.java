package com.example.modbus.controller;

import com.example.modbus.dto.ApiResponse;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局接口异常处理。
 *
 * <p>新接口统一返回 {@link ApiResponse}，便于前端或调用方判断错误原因。</p>
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * 参数错误返回 400。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail(400, ex.getMessage()));
    }

    /**
     * Modbus 连接、传输或设备异常响应统一返回 502。
     */
    @ExceptionHandler({ModbusInitException.class, ModbusTransportException.class, ErrorResponseException.class})
    public ResponseEntity<ApiResponse<Void>> handleModbusError(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ApiResponse.fail(502, ex.getMessage()));
    }

    /**
     * 未预期异常返回 500。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(500, ex.getMessage()));
    }
}
