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

## 如果要加零知识证明（ZKP），详细实施步骤（建议方案）

下面给你一个可以直接落地的方案：先做 **链下证明 + 链上存证**，再升级到 **链上验证器**。

---

### 第 0 步：先定义“你要证明什么”（业务规则建模）

先不要急着写代码，先把证明目标写成可计算规则（电路约束）。

建议先从 1~2 个高价值场景入手：

1. **产地合规证明**：证明产品来自允许产区，但不公开具体供应商明细。
2. **质检合规证明**：证明检测值在阈值范围内，但不公开原始检测数据。
3. **流程完整性证明**：证明某商品确实经过“生产 -> 运输 -> 仓储 -> 销售”关键节点。

产出物建议：

- `proofType` 列表（如 `origin-proof`、`qc-proof`）
- 每种 `proofType` 的公开输入（public inputs）定义
- 每种 `proofType` 的私密输入（private witness）定义

---

### 第 1 步：数据库新增证明表（先落地元数据）

**位置建议**：`src/main/java/org/Tracing/entity`、`repository`，配套 migration SQL。

新增 `ProofRecord`（建议字段）：

- `id`：主键
- `productId`：产品 ID（索引）
- `proofType`：证明类型（索引）
- `publicInputsHash`：公开输入哈希（建议 `SHA-256`）
- `proofHash`：证明文件哈希（建议 `SHA-256`）
- `proofData`：可选，存压缩后的 proof JSON（大字段）
- `verifyStatus`：`PENDING` / `VERIFIED` / `FAILED`
- `verifierMode`：`OFFCHAIN` / `ONCHAIN`
- `txHash`：若上链，记录交易哈希
- `errorMessage`：失败原因
- `createdAt`、`updatedAt`

为什么先建表：

- 你可以先把证明流程跑通，即使暂时还没上链验证器。
- 支持一个产品多次证明（历史可追溯、可审计）。

---

### 第 2 步：后端新增 ZKP 服务层（核心）

**位置建议**：`src/main/java/org/Tracing/service/ZkpService.java`

`ZkpService` 最少做 4 件事：

1. `buildWitness(productId, proofType)`
   - 从 `Product` + `TraceRecord` 聚合电路需要的数据。
   - 做字段标准化（时间格式、字符串编码、数值边界）。
2. `generateProof(witness)`
   - 调用链下 prover（比如独立 Node 服务：`snarkjs` / `halo2`）。
   - 返回 `proof` + `publicSignals`。
3. `verifyProofOffchain(proof, publicSignals)`
   - 在后端先验证正确性，避免错误 proof 上链浪费 gas。
4. `persistAndAnchor(...)`
   - 保存 `ProofRecord`。
   - 调 `ChainController` 把 `proofHash/publicInputsHash` 上链存证。

建议加异步：

- 证明生成通常耗时，建议改成异步任务（队列或线程池）。
- API 先返回任务 ID，前端轮询状态。

---

### 第 3 步：Controller 新增证明接口

**位置建议**：`ProductController` 或 `TraceabilityController`

建议新增接口：

- `POST /api/products/{productId}/proofs`
  - 入参：`proofType`
  - 行为：创建证明任务，返回 `taskId/proofId`
- `GET /api/products/{productId}/proofs`
  - 查询该产品所有证明记录
- `GET /api/products/{productId}/proofs/latest?proofType=...`
  - 查询最新证明状态
- `POST /api/products/{productId}/proofs/{proofId}/verify`
  - 手动重试验证（可选）

返回结构建议统一：

- `success`
- `proofId`
- `verifyStatus`
- `txHash`
- `message`

---

### 第 4 步：合约层新增“证明存证”接口（先做这个）

**位置建议**：`contracts/Tracing.sol`，并同步更新 Java 封装类 `ProductTracing` 与 `ChainController`。

第一阶段（推荐）先做轻量存证：

- `recordProofHash(productId, proofType, proofHash, publicInputsHash)`
- 事件 `ProofAnchored(productId, proofType, proofHash, publicInputsHash, timestamp)`

这样做的好处：

- gas 成本低
- 部署简单
- 先满足“防篡改审计”

第二阶段再上链验证器：

- 集成 verifier 合约（Groth16/Plonk）
- 新增 `verifyAndRecordProof(...)`，验证成功才入链

---

### 第 5 步：ChainController 增加 proof 上链方法

**位置建议**：`src/main/java/org/Tracing/controller/ChainController.java`

新增方法建议：

- `recordProofHash(String productId, String proofType, String proofHash, String publicInputsHash)`
- （二期）`verifyAndRecordProof(...proofBytes/publicInputs...)`

要点：

- 参数非空校验、长度校验
- 交易回执状态校验
- 异常里保留可读错误（链上 revert reason）

---

### 第 6 步：前端产品详情页增加“证明状态卡片”

**位置建议**：`src/main/resources/static/html/product-detail.html` + `static/js/product-detail.js`

建议增加：

- 下拉框选择 `proofType`
- “生成证明”按钮
- 状态展示：`PENDING / VERIFIED / FAILED`
- `txHash` 展示与复制
- 最近一次证明时间

交互建议：

- 生成后进入轮询（每 2~3 秒）直到状态终态
- 失败时显示 `errorMessage`

---

### 第 7 步：配置与安全（必须做）

新增配置（建议放 `application.yml` + 环境变量）：

- `zkp.prover.url`：链下 prover 服务地址
- `zkp.timeout.seconds`
- `zkp.enabledProofTypes`
- `zkp.anchorOnChain=true/false`

安全建议：

- 不在代码里硬编码 proving key / 私钥
- proof 原文可不落库，只存 hash + 对象存储 URL
- 对生成证明接口加权限（至少管理员）

---

### 第 8 步：分阶段上线计划（你可以照这个排期）

**Phase A（1~2 周）**

- ProofRecord 表
- ZkpService（链下生成+验证）
- API + 前端状态展示
- 链上仅 `recordProofHash`

**Phase B（1~2 周）**

- 增加失败重试、异步任务、监控告警
- 增加 proofType（从 1 个扩到 2~3 个）

**Phase C（2~4 周）**

- 接入链上 verifier
- 高价值 proofType 切换到“链上验证 + 存证”

---

### 最小可用改造清单（MVP Checklist）

1. 新增 `ProofRecord` 实体 + repository + migration。
2. 新增 `ZkpService`，跑通 `build -> prove -> verify -> persist`。
3. 新增 `POST /api/products/{productId}/proofs` 与查询接口。
4. 合约新增 `recordProofHash` 与事件。
5. `ChainController` 封装 proof 存证交易。
6. 产品详情页增加证明状态卡片。

> 一句话建议：**先用链下验证快速跑通业务闭环，再把关键证明逐步升级为链上验证。**

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
