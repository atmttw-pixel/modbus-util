package com.example.modbus.server;

import com.serotonin.modbus4j.BasicProcessImage;

/**
 * 本地模拟从站过程影像工厂。
 *
 * <p>过程影像就是模拟设备内部的线圈、输入位和寄存器数据区。</p>
 */
public final class SimulatorProcessImageFactory {

    private SimulatorProcessImageFactory() {
    }

    /**
     * 创建并初始化过程影像。
     *
     * @param slaveId 从站 ID
     * @param registerCount 初始化数量
     * @return 初始化后的过程影像
     */
    public static BasicProcessImage create(int slaveId, int registerCount) {
        BasicProcessImage processImage = new BasicProcessImage(slaveId);

        for (int i = 0; i < registerCount; i++) {
            processImage.setHoldingRegister(i, (short) 0);
            processImage.setInputRegister(i, (short) 0);
            processImage.setCoil(i, false);
            processImage.setInput(i, false);
        }

        processImage.addListener(new BasicProcessImageChangeListener());
        return processImage;
    }
}
