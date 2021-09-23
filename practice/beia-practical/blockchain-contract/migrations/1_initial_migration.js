const DataSet = artifacts.require("DataSet");

module.exports = function (deployer) {
  deployer.deploy(DataSet);
};
