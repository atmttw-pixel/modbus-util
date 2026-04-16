package com.example.modbus.dto;

import java.util.List;

/**
 * 连续寄存器原始采集结果。
 *
 * @param ip 目标设备 IP
 * @param port 目标设备端口
 * @param slaveId 从站 ID
 * @param functionCode 功能码，3 表示保持寄存器，4 表示输入寄存器
 * @param offset 起始地址，从 0 开始
 * @param quantity 读取寄存器数量
 * @param byteOrder 单个寄存器内部的字节顺序
 * @param registers 无符号寄存器值，范围 0-65535
 * @param bytes 按字节序展开后的无符号字节值，范围 0-255
 * @param hex 空格分隔的十六进制字符串
 * @param base64 原始字节的 Base64 表示
 * @param asciiPreview ASCII 预览，不可见字符用点号代替
 * @param binary 按字节分组的二进制字符串
 */
public record RawRegisterResponse(
        String ip,
        int port,
        int slaveId,
        int functionCode,
        int offset,
        int quantity,
        String byteOrder,
        List<Integer> registers,
        List<Integer> bytes,
        String hex,
        String base64,
        String asciiPreview,
        String binary
) {
}
