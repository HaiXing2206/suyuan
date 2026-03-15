const EvaluationProofRegistry = artifacts.require("EvaluationProofRegistry");

module.exports = function(deployer) {
  deployer.deploy(EvaluationProofRegistry);
};
