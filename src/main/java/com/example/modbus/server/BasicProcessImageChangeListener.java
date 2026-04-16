package com.example.modbus.server;

import com.serotonin.modbus4j.ProcessImageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本地模拟从站过程影像监听器。
 *
 * <p>当外部客户端写线圈或保持寄存器时，这里记录变化日志，方便调试。</p>
 */
public class BasicProcessImageChangeListener implements ProcessImageListener {

    /** 模拟从站写入日志。 */
    private static final Logger log = LoggerFactory.getLogger(BasicProcessImageChangeListener.class);

    /**
     * 线圈被写入时触发。
     */
    @Override
    public void coilWrite(int offset, boolean oldValue, boolean newValue) {
        log.info("Simulator coil changed: offset={}, old={}, new={}", offset, oldValue, newValue);
    }

    /**
     * 保持寄存器被写入时触发。
     */
    @Override
    public void holdingRegisterWrite(int offset, short oldValue, short newValue) {
        log.info(
                "Simulator holding register changed: offset={}, old={}, new={}",
                offset,
                oldValue & 0xFFFF,
                newValue & 0xFFFF
        );
    }
}
