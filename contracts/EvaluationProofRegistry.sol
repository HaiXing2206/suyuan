// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/// @title EvaluationProofRegistry
/// @notice Stores off-chain zero-knowledge proof verification summaries for audit and traceability.
contract EvaluationProofRegistry {
    enum VerifyStatus {
        Pending,
        Verified,
        Rejected
    }

    struct ProofRecord {
        uint256 recordId;
        string taskId;
        string reportVersion;
        string proofType;
        bytes32 proofHash;
        bytes32 publicInputsHash;
        VerifyStatus status;
        string verifierComment;
        address submitter;
        address verifier;
        uint256 submittedAt;
        uint256 verifiedAt;
    }

    address public owner;
    uint256 public nextRecordId = 1;

    mapping(uint256 => ProofRecord) public records;
    mapping(string => uint256[]) private taskRecordIds;
    mapping(address => bool) public verifiers;

    event VerifierUpdated(address indexed verifier, bool enabled);
    event ProofSubmitted(
        uint256 indexed recordId,
        string indexed taskId,
        string reportVersion,
        string proofType,
        bytes32 proofHash,
        bytes32 publicInputsHash,
        address submitter
    );
    event ProofStatusUpdated(
        uint256 indexed recordId,
        VerifyStatus status,
        address indexed verifier,
        string verifierComment,
        uint256 verifiedAt
    );

    modifier onlyOwner() {
        require(msg.sender == owner, "Only owner");
        _;
    }

    modifier onlyVerifier() {
        require(verifiers[msg.sender], "Only verifier");
        _;
    }

    constructor() {
        owner = msg.sender;
        verifiers[msg.sender] = true;
        emit VerifierUpdated(msg.sender, true);
    }

    /// @notice Grant or revoke verifier permissions.
    function setVerifier(address _verifier, bool _enabled) external onlyOwner {
        require(_verifier != address(0), "Invalid verifier");
        verifiers[_verifier] = _enabled;
        emit VerifierUpdated(_verifier, _enabled);
    }

    /// @notice Save proof metadata after off-chain proof generation.
    function submitProof(
        string memory _taskId,
        string memory _reportVersion,
        string memory _proofType,
        bytes32 _proofHash,
        bytes32 _publicInputsHash
    ) external returns (uint256) {
        require(bytes(_taskId).length > 0, "taskId required");
        require(bytes(_reportVersion).length > 0, "reportVersion required");
        require(bytes(_proofType).length > 0, "proofType required");
        require(_proofHash != bytes32(0), "proofHash required");
        require(_publicInputsHash != bytes32(0), "publicInputsHash required");

        uint256 recordId = nextRecordId;
        nextRecordId += 1;

        records[recordId] = ProofRecord({
            recordId: recordId,
            taskId: _taskId,
            reportVersion: _reportVersion,
            proofType: _proofType,
            proofHash: _proofHash,
            publicInputsHash: _publicInputsHash,
            status: VerifyStatus.Pending,
            verifierComment: "",
            submitter: msg.sender,
            verifier: address(0),
            submittedAt: block.timestamp,
            verifiedAt: 0
        });

        taskRecordIds[_taskId].push(recordId);

        emit ProofSubmitted(
            recordId,
            _taskId,
            _reportVersion,
            _proofType,
            _proofHash,
            _publicInputsHash,
            msg.sender
        );

        return recordId;
    }

    /// @notice Update proof verification result after off-chain verifier execution.
    function updateProofStatus(
        uint256 _recordId,
        VerifyStatus _status,
        string memory _verifierComment
    ) external onlyVerifier {
        require(_recordId > 0 && _recordId < nextRecordId, "record not found");
        require(_status != VerifyStatus.Pending, "Invalid status");

        ProofRecord storage record = records[_recordId];
        require(record.status == VerifyStatus.Pending, "Already decided");

        record.status = _status;
        record.verifierComment = _verifierComment;
        record.verifier = msg.sender;
        record.verifiedAt = block.timestamp;

        emit ProofStatusUpdated(
            _recordId,
            _status,
            msg.sender,
            _verifierComment,
            record.verifiedAt
        );
    }

    function getTaskProofRecordIds(string memory _taskId) external view returns (uint256[] memory) {
        return taskRecordIds[_taskId];
    }

    function transferOwnership(address _newOwner) external onlyOwner {
        require(_newOwner != address(0), "Invalid owner");

        owner = _newOwner;
        if (!verifiers[_newOwner]) {
            verifiers[_newOwner] = true;
            emit VerifierUpdated(_newOwner, true);
        }
    }
}
