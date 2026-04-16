package com.example.modbus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Modbus 相关配置绑定类。
 *
 * <p>对应 application.yml 中的 {@code modbus.*} 配置。</p>
 */
@ConfigurationProperties(prefix = "modbus")
public class ModbusProperties {

    /** Modbus 主站客户端配置。 */
    private final Client client = new Client();
    /** 本地模拟从站配置。 */
    private final Simulator simulator = new Simulator();

    public Client getClient() {
        return client;
    }

    public Simulator getSimulator() {
        return simulator;
    }

    /**
     * Modbus 客户端连接配置。
     */
    public static class Client {
        /** 请求超时时间，单位毫秒。 */
        private int timeout = 3000;
        /** 请求失败后的重试次数。 */
        private int retries = 1;

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getRetries() {
            return retries;
        }

        public void setRetries(int retries) {
            this.retries = retries;
        }
    }

    /**
     * 本地 Modbus TCP 模拟从站配置。
     */
    public static class Simulator {
        /** 是否启动本地模拟从站。 */
        private boolean enabled = false;
        /** 模拟从站监听端口。 */
        private int port = 5050;
        /** 模拟从站 ID。 */
        private int slaveId = 1;
        /** 初始化的寄存器、线圈和输入位数量。 */
        private int registerCount = 20;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getSlaveId() {
            return slaveId;
        }

        public void setSlaveId(int slaveId) {
            this.slaveId = slaveId;
        }

        public int getRegisterCount() {
            return registerCount;
        }

        public void setRegisterCount(int registerCount) {
            this.registerCount = registerCount;
        }
    }
}
