package com.example.modbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Spring Boot 启动入口。
 *
 * <p>{@link ConfigurationPropertiesScan} 用来扫描 {@code modbus.*} 配置，
 * 让 {@code ModbusProperties} 能自动绑定 application.yml。</p>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ModbusApplication {

    /**
     * 应用启动方法。
     *
     * @param args 命令行启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ModbusApplication.class, args);
    }

}
