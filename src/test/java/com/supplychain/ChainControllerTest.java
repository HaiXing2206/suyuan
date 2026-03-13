package com.supplychain;

import org.Tracing.controller.ChainController;
import org.Tracing.controller.ChainController.ProductInfo;
import org.Tracing.controller.ChainController.SupplyChainRecord;
import java.math.BigInteger;
import java.util.UUID;

public class ChainControllerTest {
    private ChainController chainController;
    private String testProductId;

    public void setUp() {
        chainController = ChainController.getInstance();
        // 生成唯一的测试商品ID
        testProductId = "TEST-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void waitForTransactionConfirmation(String txHash) {
        try {
            // 等待3秒让交易被打包确认
            Thread.sleep(3000);
            System.out.println("等待交易确认完成: " + txHash);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("等待交易确认被中断", e);
        }
    }

    public void testProductLifecycle() {
        try {
            setUp();
            // 1. 创建商品
            String createTxHash = chainController.createProduct(
                testProductId,
                "测试商品",
                "测试制造商",
                "BATCH-001",
                "测试产地"
            );
            assert createTxHash != null : "创建商品交易哈希不应为空";
            System.out.println("创建商品成功，交易哈希: " + createTxHash);
            
            // 等待交易确认
            waitForTransactionConfirmation(createTxHash);

            // 2. 获取商品信息
            ProductInfo productInfo = chainController.getProduct(testProductId);
            assert productInfo != null : "商品信息不应为空";
            assert "测试商品".equals(productInfo.getName()) : "商品名称不匹配";
            assert "测试制造商".equals(productInfo.getManufacturer()) : "制造商不匹配";
            assert "BATCH-001".equals(productInfo.getBatchNumber()) : "批次号不匹配";
            assert "测试产地".equals(productInfo.getOrigin()) : "产地不匹配";
//            assert productInfo.getProductionDate() > 0 : "生产日期应该大于0";
            System.out.println("获取商品信息成功: " + productInfo.getName());
            System.out.println("获取商品信息成功: " + productInfo.getManufacturer());
            System.out.println("获取商品信息成功: " + productInfo.getBatchNumber());
            System.out.println("获取商品信息成功: " + productInfo.getOrigin());

            // 3. 验证初始供应链记录
            BigInteger recordCount = chainController.getSupplyChainRecordCount(testProductId);
            assert recordCount.compareTo(BigInteger.ONE) == 0 : "应该有一条初始供应链记录";
            
            SupplyChainRecord initialRecord = chainController.getSupplyChainRecord(testProductId, BigInteger.ZERO);
            assert initialRecord != null : "初始供应链记录不应为空";
            assert "Production".equals(initialRecord.getAction()) : "初始操作应该是Production";
            assert "测试制造商".equals(initialRecord.getLocation()) : "初始位置应该是制造商地址";
            assert "Initial production completed".equals(initialRecord.getDetails()) : "初始详情不匹配";
            System.out.println("验证初始供应链记录成功");

            // 4. 添加新的供应链记录
            String recordTxHash = chainController.addSupplyChainRecord(
                testProductId,
                "测试操作",
                "测试位置",
                "测试详情"
            );
            assert recordTxHash != null : "添加供应链记录交易哈希不应为空";
            System.out.println("添加供应链记录成功，交易哈希: " + recordTxHash);

            // 等待交易确认
            waitForTransactionConfirmation(recordTxHash);

            // 5. 验证新的供应链记录
            recordCount = chainController.getSupplyChainRecordCount(testProductId);
            assert recordCount.compareTo(BigInteger.valueOf(2)) == 0 : "应该有两条供应链记录";
            
            SupplyChainRecord newRecord = chainController.getSupplyChainRecord(testProductId, BigInteger.ONE);
            assert newRecord != null : "新的供应链记录不应为空";
            assert "测试位置".equals(newRecord.getLocation()) : "位置不匹配";
            assert "测试操作".equals(newRecord.getAction()) : "操作不匹配";
            assert "测试详情".equals(newRecord.getDetails()) : "详情不匹配";
            System.out.println("验证新的供应链记录成功");

        } catch (Exception e) {
            System.err.println("测试过程中发生异常: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void testInvalidProductId() {
        setUp();
        // 测试使用不存在的商品ID
        String invalidProductId = "INVALID-" + UUID.randomUUID().toString();
        
        try {
            chainController.getProduct(invalidProductId);
            assert false : "应该抛出获取商品信息失败异常";
        } catch (RuntimeException e) {
            assert e.getMessage().contains("获取商品信息失败") : "异常消息不匹配";
        }
    }

    public void testInvalidSupplyChainRecord() {
        setUp();
        // 测试使用不存在的商品ID添加供应链记录
        String invalidProductId = "INVALID-" + UUID.randomUUID().toString();
        
        try {
            chainController.addSupplyChainRecord(
                invalidProductId,
                "测试操作",
                "测试位置",
                "测试详情"
            );
            assert false : "应该抛出添加供应链记录失败异常";
        } catch (RuntimeException e) {
            assert e.getMessage().contains("添加供应链记录失败") : "异常消息不匹配";
        }
    }

    public static void main(String[] args) {
        ChainControllerTest test = new ChainControllerTest();
        test.testProductLifecycle();
        test.testInvalidProductId();
        test.testInvalidSupplyChainRecord();
        System.out.println("所有测试通过！");
    }
} 