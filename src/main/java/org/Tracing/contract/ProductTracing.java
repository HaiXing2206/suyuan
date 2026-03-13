package org.Tracing.contract;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.abi.datatypes.Type;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class ProductTracing extends Contract {
    public static final String BINARY = "";  // 合约的二进制代码，这里可以留空
    public static final String ABI = "[{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"string\",\"name\":\"productId\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"name\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"manufacturer\",\"type\":\"string\"}],\"name\":\"ProductCreated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"string\",\"name\":\"productId\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"action\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"location\",\"type\":\"string\"}],\"name\":\"SupplyChainRecordAdded\",\"type\":\"event\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_productId\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_manufacturer\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_batchNumber\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_origin\",\"type\":\"string\"}],\"name\":\"createProduct\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_productId\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_action\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_location\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_details\",\"type\":\"string\"}],\"name\":\"addSupplyChainRecord\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_productId\",\"type\":\"string\"}],\"name\":\"getProduct\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"manufacturer\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"batchNumber\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"productionDate\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"origin\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_productId\",\"type\":\"string\"}],\"name\":\"getSupplyChainRecordCount\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_productId\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"_index\",\"type\":\"uint256\"}],\"name\":\"getSupplyChainRecord\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"location\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"action\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"},{\"internalType\":\"address\",\"name\":\"operator\",\"type\":\"address\"},{\"internalType\":\"string\",\"name\":\"details\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]";

    public static final String FUNC_CREATEPRODUCT = "createProduct";
    public static final String FUNC_ADDSUPPLYCHAINSRECORD = "addSupplyChainRecord";
    public static final String FUNC_GETPRODUCT = "getProduct";
    public static final String FUNC_GETSUPPLYCHAINSRECORDCOUNT = "getSupplyChainRecordCount";
    public static final String FUNC_GETSUPPLYCHAINSRECORD = "getSupplyChainRecord";

    protected ProductTracing(String contractAddress, Web3j web3j, Credentials credentials,
                           ContractGasProvider gasProvider) {
        super(ABI, contractAddress, web3j, credentials, gasProvider);
    }

    protected ProductTracing(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                           ContractGasProvider gasProvider) {
        super(ABI, contractAddress, web3j, transactionManager, gasProvider);
    }

    public static ProductTracing load(String contractAddress, Web3j web3j, Credentials credentials,
                                    ContractGasProvider gasProvider) {
        return new ProductTracing(contractAddress, web3j, credentials, gasProvider);
    }

    public static ProductTracing load(String contractAddress, Web3j web3j, TransactionManager transactionManager,
                                    ContractGasProvider gasProvider) {
        return new ProductTracing(contractAddress, web3j, transactionManager, gasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> createProduct(String productId, String name,
            String manufacturer, String batchNumber, String origin) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CREATEPRODUCT,
                Arrays.asList(
                    new org.web3j.abi.datatypes.Utf8String(productId),
                    new org.web3j.abi.datatypes.Utf8String(name),
                    new org.web3j.abi.datatypes.Utf8String(manufacturer),
                    new org.web3j.abi.datatypes.Utf8String(batchNumber),
                    new org.web3j.abi.datatypes.Utf8String(origin)
                ),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addSupplyChainRecord(String productId, String action,
            String location, String details) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDSUPPLYCHAINSRECORD,
                Arrays.asList(
                    new org.web3j.abi.datatypes.Utf8String(productId),
                    new org.web3j.abi.datatypes.Utf8String(action),
                    new org.web3j.abi.datatypes.Utf8String(location),
                    new org.web3j.abi.datatypes.Utf8String(details)
                ),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple5<String, String, String, BigInteger, String>> getProduct(String productId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GETPRODUCT,
                Arrays.asList(new org.web3j.abi.datatypes.Utf8String(productId)),
                Arrays.asList(
                        new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Utf8String>() {},  // name
                        new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Utf8String>() {},  // manufacturer
                        new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Utf8String>() {},  // batchNumber
                        new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {},  // productionDate
                        new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Utf8String>() {}   // origin
                )
        );

        return new RemoteFunctionCall<>(function, new Callable<Tuple5<String, String, String, BigInteger, String>>() {
            @Override
            public Tuple5<String, String, String, BigInteger, String> call() throws Exception {
                List<Type> result = executeCallMultipleValueReturn(function);
                if (result == null || result.size() != 5) {
                    throw new RuntimeException("Unexpected number of return values");
                }

                String name = ((org.web3j.abi.datatypes.Utf8String) result.get(0)).getValue();
                String manufacturer = ((org.web3j.abi.datatypes.Utf8String) result.get(1)).getValue();
                String batchNumber = ((org.web3j.abi.datatypes.Utf8String) result.get(2)).getValue();
                BigInteger productionDate = ((org.web3j.abi.datatypes.generated.Uint256) result.get(3)).getValue();
                String origin = ((org.web3j.abi.datatypes.Utf8String) result.get(4)).getValue();

                return new Tuple5<>(name, manufacturer, batchNumber, productionDate, origin);
            }
        });
    }


    public RemoteFunctionCall<BigInteger> getSupplyChainRecordCount(String productId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GETSUPPLYCHAINSRECORDCOUNT,
                Arrays.asList(new org.web3j.abi.datatypes.Utf8String(productId)),
                Arrays.asList(new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple5<String, String, BigInteger, String, String>> getSupplyChainRecord(String productId, BigInteger index) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GETSUPPLYCHAINSRECORD,
                Arrays.asList(
                    new org.web3j.abi.datatypes.Utf8String(productId),
                    new org.web3j.abi.datatypes.generated.Uint256(index)
                ),
                Arrays.asList(
                    new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Utf8String>() {},  // location
                    new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Utf8String>() {},  // action
                    new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.generated.Uint256>() {},  // timestamp
                    new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Address>() {},  // operator
                    new org.web3j.abi.TypeReference<org.web3j.abi.datatypes.Utf8String>() {}  // details
                ));
        return new RemoteFunctionCall<>(function,
                new Callable<Tuple5<String, String, BigInteger, String, String>>() {
                    @Override
                    public Tuple5<String, String, BigInteger, String, String> call() throws Exception {
                        List<Type> result = executeCallMultipleValueReturn(function);
                        if (result == null || result.size() != 5) {
                            throw new RuntimeException("Unexpected number of return values");
                        }

                        try {
                            String location = ((org.web3j.abi.datatypes.Utf8String) result.get(0)).getValue();
                            String action = ((org.web3j.abi.datatypes.Utf8String) result.get(1)).getValue();
                            BigInteger timestamp = ((org.web3j.abi.datatypes.generated.Uint256) result.get(2)).getValue();
                            String operator = ((org.web3j.abi.datatypes.Address) result.get(3)).getValue();
                            String details = ((org.web3j.abi.datatypes.Utf8String) result.get(4)).getValue();
                            
                            return new Tuple5<>(location, action, timestamp, operator, details);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse supply chain record data: " + e.getMessage(), e);
                        }
                    }
                });
    }
} 