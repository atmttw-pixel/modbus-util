package com.example.modbus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring Boot 上下文启动测试。
 *
 * <p>用于确认配置绑定、Bean 扫描和 Controller/Service/Client 依赖关系都能正常创建。</p>
 */
@SpringBootTest
class ModbusApplicationTests {

    /**
     * 只要应用上下文能启动成功，这个测试就通过。
     */
    @Test
    void contextLoads() {
    }

}
