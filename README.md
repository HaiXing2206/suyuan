# 溯源系统（suyuan）

一个基于 **Spring Boot + MySQL + Ethereum/Web3j** 的产品溯源系统，支持产品上链、流转记录、追溯查询、用户管理和统计分析。

## 项目简介

该项目包含三部分能力：

- **后端服务（Java）**：提供产品、追溯、用户、系统设置、访问统计等 REST API。
- **前端页面（静态资源）**：位于 `src/main/resources/static`，包含登录、产品管理、追溯查询、分析看板等页面。
- **智能合约（Solidity）**：`ProductTracing` 合约用于存储产品信息及供应链流转记录。

## 技术栈

- Java 17
- Spring Boot 2.7.x（Web / JPA / Thymeleaf）
- MySQL 8
- Web3j 4.x
- Solidity 0.8.0 + Truffle/Ganache
- 前端：原生 HTML/CSS/JavaScript

## 目录结构

```text
.
├── src/main/java/org/Tracing/
│   ├── controller/              # 业务接口（产品、追溯、用户、分析、设置、访问统计）
│   ├── controller/logcontroller # 登录/注册接口
│   ├── entity/                  # JPA 实体
│   ├── repository/              # 数据访问层
│   ├── service/                 # 业务服务层
│   ├── contract/                # Web3j 生成的合约封装类
│   └── config/
├── src/main/resources/
│   ├── application.yml          # Spring 配置（DB/JPA/端口/JWT）
│   ├── db/migration/            # 数据库迁移脚本
│   └── static/                  # 前端静态资源
├── contracts/Tracing.sol        # Solidity 智能合约
├── migrations/                  # Truffle 迁移脚本
├── truffle-config.js            # Truffle 网络配置
├── pom.xml                      # Maven 配置
└── suyuanchain.sql              # 数据库初始化脚本
```

## 主要功能

- 产品创建：创建产品并写入链上，同时同步数据库。
- 追溯记录：按产品追加流转记录（如生产、运输、仓储、销售）。
- 追溯查询：查询产品详情和完整流转链路。
- 用户能力：注册、登录、信息管理、用户列表与状态管理。
- 分析看板：供应链分析、产品类型占比、月度扫码趋势、热门产品。
- 页面访问统计：记录页面访问次数并做趋势统计。
- 系统设置：系统名、通知策略、安全策略等配置读写。

## 运行前准备

1. 安装 JDK 17、Maven 3.8+
2. 安装并启动 MySQL 8
3. 安装并启动 Ganache（默认 `127.0.0.1:7545`）
4. 可选：安装 Truffle（如果需要重新编译/部署合约）

## 配置说明

### 1) 数据库配置

在 `src/main/resources/application.yml` 中按需修改：

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

默认配置库名为 `suyuanchain`，端口 `3306`。

### 2) 区块链配置

`ChainController` 中内置了链节点地址、合约地址和测试私钥，启动前需与你本地 Ganache 账户/已部署合约保持一致：

- `CHAIN_IP`
- `CONTRACT_ADDRESS`
- `PRIVATE_KEY`
- `ACCOUNT_ADDRESS`

> 建议改造成环境变量注入，避免将私钥硬编码在代码中。

## 本地启动

### 方式 A：仅启动后端（默认前端静态资源由后端托管）

```bash
mvn spring-boot:run
```

启动后访问：

- 应用首页：`http://localhost:8080/html/index.html`
- 登录页：`http://localhost:8080/html/login.html`

### 方式 B：先构建再运行

```bash
mvn clean package
java -jar target/suyuan-1.0-SNAPSHOT.jar
```

## 智能合约（可选）

如果你需要重新部署合约：

```bash
truffle compile
truffle migrate --network development
```

部署完成后，把新合约地址更新到 `ChainController`。

## 常用 API（示例）

> 以下仅列出核心接口，完整接口请查看 `controller` 目录。

- 认证
  - `POST /api/auth/register`
  - `POST /api/login`
- 产品
  - `POST /api/products`
  - `GET /api/products`
  - `GET /api/products/{productId}`
  - `POST /api/products/{productId}/trace`
  - `GET /api/products/{productId}/trace`
- 追溯
  - `GET /api/trace/{productId}`
  - `POST /api/trace/record`
- 分析
  - `GET /api/analysis/supply-chain`
  - `GET /api/analysis/product-types`
  - `GET /api/analysis/monthly-scans`
  - `GET /api/analysis/top-products`
- 用户与系统
  - `GET /api/users/list`
  - `PUT /api/users/{id}/status`
  - `GET /api/settings`
  - `PUT /api/settings`

## 测试

```bash
mvn test
```

## 已知注意事项

- `ChainController` 当前使用硬编码测试私钥/合约地址，仅适合本地开发。
- 生产环境务必将密钥、JWT 密钥、数据库凭据移入安全配置中心或环境变量。
- 若你重新部署合约后未同步地址，产品创建/追溯上链会失败。

## License

当前仓库未声明明确开源许可证；如需开源，请补充 `LICENSE` 文件。
