# modbus-util

Modbus TCP collection and local simulator utility.

## Raw binary collection

Modbus does not expose a standalone binary stream function. Binary payloads from devices are usually split across continuous registers. Read the raw register block first, then decode it according to the vendor point table or protocol.

```bash
curl "http://localhost:7770/modbus/raw/registers?ip=192.168.0.61&port=502&slaveId=1&functionCode=3&offset=0&quantity=8&byteOrder=BIG_ENDIAN"
```

The response includes unsigned register values, expanded bytes, HEX, Base64, ASCII preview, and binary strings.

## Local simulator

The simulator is disabled by default. Enable it in `application.yml` only for local testing:

```yaml
modbus:
  simulator:
    enabled: true
    port: 5050
    slave-id: 1
    register-count: 20
```

## Run

```bash
./mvnw test
./mvnw spring-boot:run
```
