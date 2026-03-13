// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract ProductTracing {
    // Product struct
    struct Product {
        string productId;        // Unique product ID
        string name;            // Product name
        string manufacturer;    // Manufacturer
        string batchNumber;     // Batch number
        uint256 productionDate; // Production date
        string origin;          // Origin
    }

    // Supply chain record struct
    struct SupplyChainRecord {
        string location;        // Location
        string action;          // Action (e.g., production, transportation, sale)
        uint256 timestamp;      // Timestamp
        address operator;       // Operator address
        string details;         // Details
    }

    // Mapping from product ID to product information
    mapping(string => Product) public products;
    
    // Mapping from product ID to supply chain records
    mapping(string => SupplyChainRecord[]) public supplyChainRecords;

    // Events
    event ProductCreated(string productId, string name, string manufacturer);
    event SupplyChainRecordAdded(string productId, string action, string location);

    // Create new product
    function createProduct(
        string memory _productId,
        string memory _name,
        string memory _manufacturer,
        string memory _batchNumber,
        string memory _origin
    ) public {
        require(bytes(products[_productId].productId).length == 0, "Product already exists");
        
        products[_productId] = Product({
            productId: _productId,
            name: _name,
            manufacturer: _manufacturer,
            batchNumber: _batchNumber,
            productionDate: block.timestamp,
            origin: _origin
        });

        // Add initial supply chain record
        addSupplyChainRecord(
            _productId,
            "Production",
            _manufacturer,
            "Initial production completed"
        );

        emit ProductCreated(_productId, _name, _manufacturer);
    }

    // Add supply chain record
    function addSupplyChainRecord(
        string memory _productId,
        string memory _action,
        string memory _location,
        string memory _details
    ) public {
        require(bytes(products[_productId].productId).length > 0, "Product does not exist");

        SupplyChainRecord memory newRecord = SupplyChainRecord({
            location: _location,
            action: _action,
            timestamp: block.timestamp,
            operator: msg.sender,
            details: _details
        });

        supplyChainRecords[_productId].push(newRecord);

        emit SupplyChainRecordAdded(_productId, _action, _location);
    }

    // Get product information
    function getProduct(string memory _productId) public view returns (
        string memory name,
        string memory manufacturer,
        string memory batchNumber,
        uint256 productionDate,
        string memory origin
    ) {
        Product memory product = products[_productId];
        require(bytes(product.productId).length > 0, "Product does not exist");
        
        return (
            product.name,
            product.manufacturer,
            product.batchNumber,
            product.productionDate,
            product.origin
        );
    }

    // Get supply chain record count
    function getSupplyChainRecordCount(string memory _productId) public view returns (uint256) {
        return supplyChainRecords[_productId].length;
    }

    // Get specific supply chain record
    function getSupplyChainRecord(string memory _productId, uint256 _index) public view returns (
        string memory location,
        string memory action,
        uint256 timestamp,
        address operator,
        string memory details
    ) {
        require(_index < supplyChainRecords[_productId].length, "Record does not exist");
        
        SupplyChainRecord memory record = supplyChainRecords[_productId][_index];
        return (
            record.location,
            record.action,
            record.timestamp,
            record.operator,
            record.details
        );
    }
}
