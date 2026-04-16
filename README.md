# modbus-util

Modbus TCP 采集和本地模拟工具，默认 HTTP 端口是 `7770`。

## 目录结构

```text
src/main/java/com/example/modbus
├── client      # Modbus TCP 连接、单点读写、原始块读取
├── codec       # 寄存器 short[] 与字节、HEX、Base64、bit 字符串转换
├── config      # application.yml 配置绑定
├── controller  # HTTP API
├── dto         # API 返回对象
├── server      # 本地 Modbus 从站模拟器
└── service     # 采集业务编排
```

## 生产二进制数据怎么采

Modbus 本身没有“二进制流”这个功能码。生产现场所谓二进制数据，一般是设备把一段 payload 拆到连续的寄存器里。采集时不要先猜具体数值类型，应该先按点表读取原始寄存器块，再根据厂家协议解析字节。

常用规则：

- `functionCode=3`：读 Holding Register，最常见的可读写寄存器区。
- `functionCode=4`：读 Input Register，只读寄存器区。
- 一个寄存器是 16 bit，也就是 2 字节。
- `offset` 通常从 0 开始；如果厂家点表写的是 40001/30001，需要换算成 `offset=0`。
- 默认字节序是 `BIG_ENDIAN`，也就是一个寄存器高字节在前；如果设备协议要求低字节在前，传 `byteOrder=LITTLE_ENDIAN`。

读取原始寄存器块：

```bash
curl "http://localhost:7770/modbus/raw/registers?ip=192.168.0.61&port=502&slaveId=1&functionCode=3&offset=0&quantity=8&byteOrder=BIG_ENDIAN"
```

返回里重点看：

- `registers`：无符号寄存器值。
- `bytes`：按字节序展开后的原始字节。
- `hex`：适合和设备报文/厂家协议对照。
- `base64`：适合把二进制安全传给其他系统。
- `asciiPreview`：如果 payload 里是可见字符，这里能快速看出来。
- `binary`：每个字节的 8 位二进制字符串。

读取线圈/离散输入位：

```bash
curl "http://localhost:7770/modbus/raw/bits?ip=192.168.0.61&port=502&slaveId=1&functionCode=1&offset=0&quantity=16"
```

## 兼容旧接口

原来的接口仍保留：

- `GET /modbus/readHoldingRegister`
- `GET /modbus/writeHoldingRegister`
- `GET /modbus/readCoilStatus`
- `GET /modbus/writeCoilStatus`
- `GET /modbus/readInputStatus`
- `GET /modbus/readInputRegisters`

## 本地模拟从站

默认不启动模拟从站，避免生产环境占用端口。需要本地调试时在 `application.yml` 开启：

```yaml
modbus:
  simulator:
    enabled: true
    port: 5050
    slave-id: 1
    register-count: 20
```

## 运行

```bash
mvn test
mvn spring-boot:run
```
