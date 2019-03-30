var HashStorage = artifacts.require("VideoHash.sol");

module.exports = function(deployer) {
  deployer.deploy(HashStorage)

    // Option 2) Console log the address:
    .then(() => console.log(HashStorage.address))

    // Option 3) Retrieve the contract instance and get the address from that:
    .then(() => HashStorage.deployed())
    .then(_instance => console.log(_instance.address));
};