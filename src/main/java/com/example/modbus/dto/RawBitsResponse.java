package com.example.modbus.dto;

import java.util.List;

/**
 * 连续 bit 原始采集结果。
 *
 * @param ip 目标设备 IP
 * @param port 目标设备端口
 * @param slaveId 从站 ID
 * @param functionCode 功能码，1 表示线圈，2 表示离散输入
 * @param offset 起始地址，从 0 开始
 * @param quantity 读取 bit 数量
 * @param values boolean 形式的 bit 值
 * @param binary 连续 0/1 字符串，便于人工查看
 */
public record RawBitsResponse(
        String ip,
        int port,
        int slaveId,
        int functionCode,
        int offset,
        int quantity,
        List<Boolean> values,
        String binary
) {
}
