package org.Tracing.controller;

import org.Tracing.contract.ProductTracing;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.DefaultBlockParameterName;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ChainController {
    private static final String CHAIN_IP = envOrDefault("CHAIN_RPC_URL", "http://127.0.0.1:7545");
    private static final String CONTRACT_ADDRESS = envOrDefault("PRODUCT_TRACING_CONTRACT_ADDRESS", "");
    private static final String PRIVATE_KEY = envOrDefault("CHAIN_PRIVATE_KEY", "");
    private static final String ACCOUNT_ADDRESS = envOrDefault("CHAIN_ACCOUNT_ADDRESS", "");

    private static Web3j web3j;
    private static ProductTracing productTracing;
    private static ChainController instance;

    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private static String requireConfig(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(message);
        }
        return value.trim();
    }

    private ChainController() {
        try {
            initializeWeb3j();
            initializeContract();
        } catch (Exception e) {
            throw new RuntimeException("初始化ChainController失败: " + e.getMessage(), e);
        }
    }

    private void initializeWeb3j() throws Exception {
        HttpService httpService = new HttpService(requireConfig(CHAIN_IP, "缺少链节点配置: CHAIN_RPC_URL"));
        web3j = Web3j.build(httpService);
        
        // 验证连接
        BigInteger chainId = web3j.ethChainId().send().getChainId();
        System.out.println("已连接到链，ID: " + chainId);
        
        // 验证账户余额
        BigInteger balance = web3j.ethGetBalance(requireConfig(ACCOUNT_ADDRESS, "缺少账户地址配置: CHAIN_ACCOUNT_ADDRESS"), DefaultBlockParameterName.LATEST).send().getBalance();
        System.out.println("账户余额: " + balance + " wei");
        
        Credentials credentials = Credentials.create(requireConfig(PRIVATE_KEY, "缺少私钥配置: CHAIN_PRIVATE_KEY"));
        System.out.println("使用账户: " + credentials.getAddress());
        System.out.println("账户地址匹配: " + credentials.getAddress().equalsIgnoreCase(ACCOUNT_ADDRESS));
    }

    private void initializeContract() throws Exception {
        Credentials credentials = Credentials.create(requireConfig(PRIVATE_KEY, "缺少私钥配置: CHAIN_PRIVATE_KEY"));
        BigInteger chainId = web3j.ethChainId().send().getChainId();
        RawTransactionManager transactionManager = new RawTransactionManager(web3j, credentials, chainId.longValue());
        
        // 设置gas价格和限制
        BigInteger gasPrice = BigInteger.valueOf(20000000000L); // 20 Gwei
        BigInteger gasLimit = BigInteger.valueOf(3000000L);     // 3M gas limit
        StaticGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
        
        productTracing = ProductTracing.load(requireConfig(CONTRACT_ADDRESS, "缺少合约地址配置: PRODUCT_TRACING_CONTRACT_ADDRESS"), web3j, transactionManager, gasProvider);
        
        if (productTracing == null) {
            throw new RuntimeException("加载合约实例失败");
        }
        System.out.println("合约实例加载成功");
    }

    public static ChainController getInstance() {
        if (instance == null) {
            instance = new ChainController();
        }
        return instance;
    }

    // 创建新产品
    public String createProduct(String productId, String name, String manufacturer, 
                              String batchNumber, String origin) {
        try {
            validateProductParameters(productId, name, manufacturer, batchNumber, origin);

            TransactionReceipt receipt = productTracing.createProduct(
                productId,
                name,
                manufacturer,
                batchNumber,
                origin
            ).send();
            
            validateTransactionReceipt(receipt);
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new RuntimeException("创建产品失败: " + e.getMessage(), e);
        }
    }

    // 添加供应链记录
    public String addSupplyChainRecord(String productId, String action, 
                                     String location, String details) {
        try {
            System.out.println("正在添加供应链记录 - 商品ID: " + productId);
            validateSupplyChainParameters(productId, action, location, details);

            TransactionReceipt receipt = productTracing.addSupplyChainRecord(
                productId,
                action,
                location,
                details
            ).send();
            
            validateTransactionReceipt(receipt);
            System.out.println("添加供应链记录成功，交易哈希: " + receipt.getTransactionHash());
            return receipt.getTransactionHash();
        } catch (Exception e) {
            System.err.println("添加供应链记录失败: " + e.getMessage());
            throw new RuntimeException("添加供应链记录失败: " + e.getMessage(), e);
        }
    }

    // 获取产品信息
    public ProductInfo getProduct(String productId) {
        try {
            System.out.println("正在获取商品信息 - 商品ID: " + productId);
            validateProductId(productId);

            Tuple5<String, String, String, BigInteger, String> result = productTracing.getProduct(productId).send();
            System.out.println("成功获取产品数据");

            return new ProductInfo(
                productId,
                result.component1(),  // name
                result.component2(),  // manufacturer
                result.component3(),  // batchNumber
                result.component4(),  // productionDate
                result.component5()   // origin
            );
        } catch (Exception e) {
            System.err.println("获取商品信息失败: " + e.getMessage());
            throw new RuntimeException("获取商品信息失败: " + e.getMessage(), e);
        }
    }

    // 获取供应链记录数量
    public BigInteger getSupplyChainRecordCount(String productId) {
        try {
            System.out.println("正在获取商品 " + productId + " 的供应链记录数量");
            validateProductId(productId);
            
            BigInteger count = productTracing.getSupplyChainRecordCount(productId).send();
            System.out.println("供应链记录数量: " + count);
            return count;
        } catch (Exception e) {
            System.err.println("获取供应链记录数量失败: " + e.getMessage());
            throw new RuntimeException("获取供应链记录数量失败: " + e.getMessage(), e);
        }
    }

    // 获取特定供应链记录
    public SupplyChainRecord getSupplyChainRecord(String productId, BigInteger index) {
        try {
            validateProductId(productId);
            validateIndex(index);
            
            Tuple5<String, String, BigInteger, String, String> result = productTracing.getSupplyChainRecord(
                productId,
                index
            ).send();
            
            return new SupplyChainRecord(
                result.component1(),  // location
                result.component2(),  // action
                result.component3(),  // timestamp
                result.component4(),  // operator
                result.component5()   // details
            );
        } catch (Exception e) {
            throw new RuntimeException("获取供应链记录失败: " + e.getMessage(), e);
        }
    }



    // 验证方法
    private void validateProductParameters(String productId, String name, String manufacturer, 
                                         String batchNumber, String origin) {
        if (isBlank(productId) || isBlank(name) || isBlank(manufacturer) ||
            isBlank(batchNumber) || isBlank(origin)) {
            throw new IllegalArgumentException("所有参数不能为空");
        }
    }

    private void validateSupplyChainParameters(String productId, String action, 
                                             String location, String details) {
        if (isBlank(productId) || isBlank(action) || isBlank(location) || isBlank(details)) {
            throw new IllegalArgumentException("所有参数不能为空");
        }
    }

    private void validateProductId(String productId) {
        if (isBlank(productId)) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void validateIndex(BigInteger index) {
        if (index == null || index.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("索引无效");
        }
    }

    private void validateTransactionReceipt(TransactionReceipt receipt) {
        if (!receipt.isStatusOK()) {
            throw new RuntimeException("交易失败: " + receipt.getStatus());
        }
    }

    // 产品信息类
    public static class ProductInfo {
        private final String productId;
        private final String name;
        private final String manufacturer;
        private final String batchNumber;
        private final BigInteger productionDate;
        private final String origin;

        public ProductInfo(String productId, String name, String manufacturer, 
                          String batchNumber, BigInteger productionDate, String origin) {
            this.productId = productId;
            this.name = name;
            this.manufacturer = manufacturer;
            this.batchNumber = batchNumber;
            this.productionDate = productionDate;
            this.origin = origin;
        }

        // Getters
        public String getProductId() { return productId; }
        public String getName() { return name; }
        public String getManufacturer() { return manufacturer; }
        public String getBatchNumber() { return batchNumber; }
        public BigInteger getProductionDate() { return productionDate; }
        public String getOrigin() { return origin; }
    }

    // 供应链记录类
    public static class SupplyChainRecord {
        private final String location;
        private final String action;
        private final BigInteger timestamp;
        private final String operator;
        private final String details;

        public SupplyChainRecord(String location, String action, BigInteger timestamp, 
                                String operator, String details) {
            this.location = location;
            this.action = action;
            this.timestamp = timestamp;
            this.operator = operator;
            this.details = details;
        }

        // Getters
        public String getLocation() { return location; }
        public String getAction() { return action; }
        public BigInteger getTimestamp() { return timestamp; }
        public String getOperator() { return operator; }
        public String getDetails() { return details; }
    }
}

