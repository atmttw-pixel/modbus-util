package com.example.modbus;

import com.serotonin.modbus4j.BasicProcessImage;

public class Register {

    public static BasicProcessImage getModscanProcessImage(int slaveId) {
        // 初始化过程影像区
        BasicProcessImage processImage = new BasicProcessImage(slaveId);

        // 初始化 Holding Registers (03保持寄存器，模拟量)
        for (int i = 0; i < 20; i++) {
            processImage.setHoldingRegister(i, (short) 0); // 初始化为 0, 10, 20, ...
        }

        // 初始化 Input Registers (04输入寄存器，模拟量)
        for (int i = 0; i < 20; i++) {
            processImage.setInputRegister(i, (short) 0); // 初始化为 100, 101, 102, ...
        }

        // 初始化 Coil Status (01开关量，布尔)
        for (int i = 0; i < 20; i++) {
            processImage.setCoil(i, false); // 偶数索引为 true, 奇数索引为 false
        }

        // 初始化 Input Status (02输入位，布尔)
        for (int i = 0; i < 20; i++) {
            processImage.setInput(i, false); // 每隔 3 个为 true，其余为 false
        }

        // 为影像区添加监听
        processImage.addListener(new BasicProcessImageListener());
        return processImage;
    }
}
