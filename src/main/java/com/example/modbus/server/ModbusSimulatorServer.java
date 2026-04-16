package com.example.modbus.server;

import com.example.modbus.config.ModbusProperties;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.ip.tcp.TcpSlave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 本地 Modbus TCP 模拟从站。
 *
 * <p>只有 {@code modbus.simulator.enabled=true} 时才会启动，
 * 生产环境默认不会监听端口。</p>
 */
@Component
@ConditionalOnProperty(prefix = "modbus.simulator", name = "enabled", havingValue = "true")
public class ModbusSimulatorServer implements ApplicationRunner, DisposableBean {

    /** 模拟从站运行日志。 */
    private static final Logger log = LoggerFactory.getLogger(ModbusSimulatorServer.class);

    /** 模拟从站配置。 */
    private final ModbusProperties properties;
    /** 当前运行中的从站实例，应用关闭时需要 stop。 */
    private volatile ModbusSlaveSet slaveSet;

    public ModbusSimulatorServer(ModbusProperties properties) {
        this.properties = properties;
    }

    /**
     * Spring Boot 启动完成后异步启动模拟从站。
     */
    @Override
    public void run(ApplicationArguments args) {
        Thread thread = new Thread(this::startSimulator, "modbus-simulator-server");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 应用关闭时停止模拟从站，释放监听端口。
     */
    @Override
    public void destroy() {
        if (slaveSet != null) {
            slaveSet.stop();
            log.info("Modbus simulator stopped");
        }
    }

    /**
     * 创建并启动 TCP 从站。
     */
    private void startSimulator() {
        ModbusProperties.Simulator simulator = properties.getSimulator();
        ModbusSlaveSet server = new TcpSlave(simulator.getPort(), false);
        server.addProcessImage(SimulatorProcessImageFactory.create(
                simulator.getSlaveId(),
                simulator.getRegisterCount()
        ));

        try {
            server.start();
            slaveSet = server;
            log.info(
                    "Modbus simulator started: port={}, slaveId={}",
                    simulator.getPort(),
                    simulator.getSlaveId()
            );
        } catch (ModbusInitException ex) {
            log.error("Failed to start Modbus simulator", ex);
        }
    }
}
