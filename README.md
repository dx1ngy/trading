# 交易系统

高速、可靠、可扩展的交易系统，专为股票、期货、加密货币、高频撮合等场景设计，提供微秒级延迟的交易处理能力。

![trading](https://github.com/user-attachments/assets/094aa3ec-d790-4250-8b0c-c1e93c2cea01)

## 技术栈

- **核心语言**: Java 17
- **构建工具**: Maven
- **基础框架**: Spring Boot 3.x
- **数据库**: MySQL
- **消息队列**: Kafka
- **权限认证**: Sa-Token
- **序列化框架**: Kryo
- **内存队列**: Disruptor
- **日志框架**: Logbook

## 功能

- 实现多空交易、保证金、手续费、K线行情等功能

## 快速开始

### 环境要求

- JDK 17
- Maven
- Docker

### 步骤

1. 克隆仓库

   ```
   git clone https://github.com/dx1ngy/trading.git
   ```

2. 修改docker-compose.yml（trading/docker目录下）配置，注意需要将下面的配置的ip改为docker宿主机的ip，其他配置可改可不改。

    ```
    KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://192.168.1.25:9192
    KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://192.168.1.25:9292
    KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://192.168.1.25:9392
    ```

3. 执行命令启动docker

    ```
    docker compose up -d
    ```

4. 创建数据库和表  
   等待docker启动成功后，连接上mysql创建数据库名为trading，并执行trading.sql（在trading/sql目录下）初始化表。


5. 修改配置文件  
   将trading-api、trading-engine、trading-quotation的配置文件和docker-compose.yml文件中的配置对应，并且将trading-engine配置文件中snapshot-path改为本地某个路径。


6. 构建

    ```
    mvn clean package
    ```

7. 运行应用

    ```
    java -java trading-api-1.0.jar
    java -java trading-engine-1.0.jar
    java -java trading-quotation-1.0.jar
    ```

8. 启动客户端页面  
   使用浏览器访问index.html（trading/html目录下）。

### API文档

启动项目后访问Swagger UI：http://localhost:9000/swagger-ui/index.html

### 性能测试

基于32核CPU/128G内存环境，每秒可以处理15万+订单




