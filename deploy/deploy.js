const { Web3 } = require("web3");

// 连接到本地节点
const localhost = "http://127.0.0.1:7545";
const web3 = new Web3(new Web3.providers.HttpProvider(localhost));

// 部署账户
const account_1 = "0x441d604Cd691cF3D8f4FB0a4Ad2ED0c913500e7E";

// ProductTracing 合约的 ABI
const contractABI = [
    {
        "anonymous": false,
        "inputs": [
            {
                "indexed": true,
                "internalType": "bytes32",
                "name": "productId",
                "type": "bytes32"
            },
            {
                "indexed": false,
                "internalType": "bytes32",
                "name": "name",
                "type": "bytes32"
            },
            {
                "indexed": false,
                "internalType": "bytes32",
                "name": "manufacturer",
                "type": "bytes32"
            }
        ],
        "name": "ProductCreated",
        "type": "event"
    },
    {
        "anonymous": false,
        "inputs": [
            {
                "indexed": true,
                "internalType": "bytes32",
                "name": "productId",
                "type": "bytes32"
            },
            {
                "indexed": false,
                "internalType": "bytes32",
                "name": "location",
                "type": "bytes32"
            },
            {
                "indexed": false,
                "internalType": "bytes32",
                "name": "handler",
                "type": "bytes32"
            }
        ],
        "name": "SupplyChainRecordAdded",
        "type": "event"
    },
    {
        "anonymous": false,
        "inputs": [
            {
                "indexed": true,
                "internalType": "bytes32",
                "name": "productId",
                "type": "bytes32"
            },
            {
                "indexed": false,
                "internalType": "address",
                "name": "from",
                "type": "address"
            },
            {
                "indexed": false,
                "internalType": "address",
                "name": "to",
                "type": "address"
            }
        ],
        "name": "ProductTransferred",
        "type": "event"
    },
    {
        "inputs": [
            {
                "internalType": "bytes32",
                "name": "_productId",
                "type": "bytes32"
            },
            {
                "internalType": "bytes32",
                "name": "_name",
                "type": "bytes32"
            },
            {
                "internalType": "bytes32",
                "name": "_manufacturer",
                "type": "bytes32"
            },
            {
                "internalType": "bytes32",
                "name": "_batchNumber",
                "type": "bytes32"
            },
            {
                "internalType": "bytes32",
                "name": "_origin",
                "type": "bytes32"
            }
        ],
        "name": "createProduct",
        "outputs": [],
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "inputs": [
            {
                "internalType": "bytes32",
                "name": "_productId",
                "type": "bytes32"
            },
            {
                "internalType": "bytes32",
                "name": "_location",
                "type": "bytes32"
            },
            {
                "internalType": "bytes32",
                "name": "_handler",
                "type": "bytes32"
            },
            {
                "internalType": "string",
                "name": "_description",
                "type": "string"
            }
        ],
        "name": "addSupplyChainRecord",
        "outputs": [],
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "inputs": [
            {
                "internalType": "bytes32",
                "name": "_productId",
                "type": "bytes32"
            },
            {
                "internalType": "address",
                "name": "_to",
                "type": "address"
            }
        ],
        "name": "transferProduct",
        "outputs": [],
        "stateMutability": "nonpayable",
        "type": "function"
    },
    {
        "inputs": [
            {
                "internalType": "bytes32",
                "name": "_productId",
                "type": "bytes32"
            }
        ],
        "name": "getProduct",
        "outputs": [
            {
                "internalType": "bytes32",
                "name": "name",
                "type": "bytes32"
            },
            {
                "internalType": "bytes32",
                "name": "manufacturer",
                "type": "bytes32"
            },
            {
                "internalType": "bytes32",
                "name": "batchNumber",
                "type": "bytes32"
            },
            {
                "internalType": "uint32",
                "name": "productionDate",
                "type": "uint32"
            },
            {
                "internalType": "bytes32",
                "name": "origin",
                "type": "bytes32"
            },
            {
                "internalType": "bool",
                "name": "isValid",
                "type": "bool"
            }
        ],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [
            {
                "internalType": "bytes32",
                "name": "_productId",
                "type": "bytes32"
            }
        ],
        "name": "getSupplyChainRecordCount",
        "outputs": [
            {
                "internalType": "uint256",
                "name": "",
                "type": "uint256"
            }
        ],
        "stateMutability": "view",
        "type": "function"
    },
    {
        "inputs": [
            {
                "internalType": "bytes32",
                "name": "_productId",
                "type": "bytes32"
            },
            {
                "internalType": "uint256",
                "name": "_index",
                "type": "uint256"
            }
        ],
        "name": "getSupplyChainRecord",
        "outputs": [
            {
                "internalType": "bytes32",
                "name": "location",
                "type": "bytes32"
            },
            {
                "internalType": "bytes32",
                "name": "handler",
                "type": "bytes32"
            },
            {
                "internalType": "uint32",
                "name": "timestamp",
                "type": "uint32"
            },
            {
                "internalType": "string",
                "name": "description",
                "type": "string"
            }
        ],
        "stateMutability": "view",
        "type": "function"
    }
];

// 合约字节码
// 获取账户列表
web3.eth.getAccounts().then(accounts => {
    console.log("账户列表地址：");
    console.log(accounts);
});

// 创建合约实例
const productTracingContract = new web3.eth.Contract(contractABI);

// 部署合约
productTracingContract.deploy({
    data: '0x6080604052348015600e575f5ffd5b506116048061001c5f395ff3fe608060405234801561000f575f5ffd5b5060043610610091575f3560e01c80636aacc984116100645780636aacc98414610149578063790543911461017957806387ff3ed8146101af5780639163e47d146101e25780639beeb59e146101fe57610091565b806329858cdd14610095578063383d36b4146100c55780633a20e9df146100e1578063620f289c14610116575b5f5ffd5b6100af60048036038101906100aa9190610b0b565b61021a565b6040516100bc9190610b4e565b60405180910390f35b6100df60048036038101906100da9190610bc1565b610237565b005b6100fb60048036038101906100f69190610b0b565b610426565b60405161010d96959493929190610c46565b60405180910390f35b610130600480360381019061012b9190610ccf565b61048e565b6040516101409493929190610d7d565b60405180910390f35b610163600480360381019061015e9190610b0b565b6105d8565b6040516101709190610dd6565b60405180910390f35b610193600480360381019061018e9190610b0b565b610608565b6040516101a69796959493929190610def565b60405180910390f35b6101c960048036038101906101c49190610ccf565b610660565b6040516101d99493929190610d7d565b60405180910390f35b6101fc60048036038101906101f79190610e5c565b61073b565b005b61021860048036038101906102139190610fff565b6108e5565b005b5f60015f8381526020019081526020015f20805490509050919050565b5f5f1b5f5f8481526020019081526020015f205f01540361028d576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610284906110c9565b60405180910390fd5b3373ffffffffffffffffffffffffffffffffffffffff1660025f8481526020019081526020015f205f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff161461032b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161032290611131565b60405180910390fd5b5f73ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1603610399576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161039090611199565b60405180910390fd5b8060025f8481526020019081526020015f205f6101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550817f2aec68d7e21d2b2b3c0246f4539531f1497e912daff5ebc1e50c4a50cd9a771e338360405161041a9291906111b7565b60405180910390a25050565b5f5f5f5f5f5f5f5f5f8981526020019081526020015f209050806001015481600201548260030154836004015f9054906101000a900463ffffffff168460050154856006015f9054906101000a900460ff169650965096509650965096505091939550919395565b5f5f5f606060015f8781526020019081526020015f208054905085106104e9576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016104e090611228565b60405180910390fd5b5f60015f8881526020019081526020015f20868154811061050d5761050c611246565b5b905f5260205f2090600402019050805f01548160010154826002015f9054906101000a900463ffffffff1683600301808054610548906112a0565b80601f0160208091040260200160405190810160405280929190818152602001828054610574906112a0565b80156105bf5780601f10610596576101008083540402835291602001916105bf565b820191905f5260205f20905b8154815290600101906020018083116105a257829003601f168201915b5050505050905094509450945094505092959194509250565b6002602052805f5260405f205f915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b5f602052805f5260405f205f91509050805f015490806001015490806002015490806003015490806004015f9054906101000a900463ffffffff1690806005015490806006015f9054906101000a900460ff16905087565b6001602052815f5260405f208181548110610679575f80fd5b905f5260205f2090600402015f9150915050805f015490806001015490806002015f9054906101000a900463ffffffff16908060030180546106ba906112a0565b80601f01602080910402602001604051908101604052809291908181526020018280546106e6906112a0565b80156107315780601f1061070857610100808354040283529160200191610731565b820191905f5260205f20905b81548152906001019060200180831161071457829003601f168201915b5050505050905084565b5f5f1b5f5f8781526020019081526020015f205f015414610791576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016107889061131a565b60405180910390fd5b6040518060e001604052808681526020018581526020018481526020018381526020014263ffffffff168152602001828152602001600115158152505f5f8781526020019081526020015f205f820151815f01556020820151816001015560408201518160020155606082015181600301556080820151816004015f6101000a81548163ffffffff021916908363ffffffff16021790555060a0820151816005015560c0820151816006015f6101000a81548160ff0219169083151502179055509050503360025f8781526020019081526020015f205f6101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550847f3aa86bf208734cabd4313f9add74fdcd2880ab49f6599fd2a06155f6a852134685856040516108d6929190611338565b60405180910390a25050505050565b5f5f1b5f5f8681526020019081526020015f205f01540361093b576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610932906110c9565b60405180910390fd5b3373ffffffffffffffffffffffffffffffffffffffff1660025f8681526020019081526020015f205f9054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146109d9576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016109d090611131565b60405180910390fd5b60015f8581526020019081526020015f2060405180608001604052808581526020018481526020014263ffffffff16815260200183815250908060018154018082558091505060019003905f5260205f2090600402015f909190919091505f820151815f0155602082015181600101556040820151816002015f6101000a81548163ffffffff021916908363ffffffff1602179055506060820151816003019081610a8491906114ff565b505050837fd522387e012614ba22ae0e30856c0e9e2da88052c07c7a1900085e31b3fba0e58484604051610ab9929190611338565b60405180910390a250505050565b5f604051905090565b5f5ffd5b5f5ffd5b5f819050919050565b610aea81610ad856b55f5ffd5b50565b5f81359050610b0581610ae1565b92915050565b5f60208284031215610b2057610b1f610ad0565b5b5f610b2d84828501610af7565b91505092915050565b5f819050919050565b610b4881610b36565b82525050565b5f602082019050610b615f830184610b3f565b92915050565b5f73ffffffffffffffffffffffffffffffffffffffff82169050919050565b5f610b9082610b67565b9050919050565b610ba081610b86565b8114610baa575f5ffd5b50565b5f81359050610bbb81610b97565b92915050565b5f5f60408385031215610bd757610bd6610ad0565b5b5f610be485828601610af7565b9250506020610bf585828601610bad565b9150509250929050565b610c0881610ad8565b82525050565b5f63ffffffff82169050919050565b610c2681610c0e565b82525050565b5f8115159050919050565b610c4081610c2c565b82525050565b5f60c082019050610c595f830189610bff565b610c666020830188610bff565b610c736040830187610bff565b610c806060830186610c1d565b610c8d6080830185610bff565b610c9a60a0830184610c37565b979650505050505050565b610cae81610b36565b8114610cb8575f5ffd5b50565b5f81359050610cc981610ca5565b92915050565b5f5f60408385031215610ce557610ce4610ad0565b5b5f610cf285828601610af7565b9250506020610d0385828601610cbb565b9150509250929050565b5f81519050919050565b5f82825260208201905092915050565b8281835e5f83830152505050565b5f601f19601f8301169050919050565b5f610d4f82610d0d565b610d598185610d17565b9350610d69818560208601610d27565b610d7281610d3556b55f5ffd5b610d7d81610d3556b55f5ffd5b610d8882828561147656b55f5ffd5b5f60209050601f831160018114610faf575f8415610f9d578287015190505b610fa985826114e456b55f5ffd5b86555061100a565b601f198416610fb98661135f565b5f5b82811015610fdc57848901518255600182019150602085019450602081019050610fb7565b86831015610ffd5784890151610ff9601f8916826114c856b55f5ffd5b8355505b60016002880201885550505b50505050505056fea26469706673582212205ce174e0cbc732fabb859780f53bf23d52595acf7b8c06712a4e2c00588ce70664736f6c634300081e0033',
    arguments: []
}).send({
    from: account_1,
    gas: "16892230",
    gasPrice: web3.utils.toWei('2', 'gwei'),
    maxFeePerGas: web3.utils.toWei('2', 'gwei'),
    maxPriorityFeePerGas: web3.utils.toWei('1', 'gwei')
})
.on('transactionHash', function(hash) {
    console.log('交易 Hash:', hash);
})
.on('confirmation', function(confirmationNumber, receipt) {
    console.log('确认编号:', confirmationNumber);
    console.log('交易确认:', receipt);
})
.on('receipt', function(receipt) {
    console.log('合约部署成功:', receipt.contractAddress);
})
.on('error', function(error) {
    console.log('部署过程中发生错误:', error);
});