package com.example.modbus;

import com.serotonin.modbus4j.BasicProcessImage;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.ip.tcp.TcpSlave;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ModbusServer implements ApplicationRunner{


    /**
     * @description 创建ModbusTCP服务器
     */
    private static void createSalve() {
        //创建modbus工厂
        ModbusFactory modbusFactory = new ModbusFactory();
        //创建TCP服务端
        // final ModbusSlaveSet salve = modbusFactory.createTcpSlave(false);
        final ModbusSlaveSet salve = new TcpSlave(5050, false);

        //向过程影像区添加数据
        salve.addProcessImage(Register.getModscanProcessImage(1));
        //获取寄存器
        salve.getProcessImage(1);
        try {
            System.out.println("salve启动成功");
            salve.start();
//            System.out.println("salve.start();");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /* createSlave方法执行完成后，会进行监听，影响之后的业务运行，需要以线程的方式进行启动 */
        new Thread(() -> {
            createSalve();
        }).start();
        /* 为保证在进行点表数据初始化的时候slave已经启动完成，在此处进行了1s的休眠 */
        Thread.sleep(1000);
        /* 开始进行点表的初始化数据写入 */
        // InitModbusSlave();


}
}
