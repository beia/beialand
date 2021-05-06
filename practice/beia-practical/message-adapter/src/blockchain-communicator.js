const Web3 = require("web3")

// Should change to a env-file / secrets. The ABI should be stored on a separate platform
const ABIJson = require('./DataSet.json');
const ABI = ABIJson["abi"]
const web3 = new Web3("http://host.docker.internal:7545")

const DAI_ADDRESS = process.env.DAI_ADDRESS

const daiToken = new web3.eth.Contract(ABI, DAI_ADDRESS)
const ownerPrivateKey = "eedb9d845fb422c2433692698efb4e4bd235403342eba49e39a38979a1e39bf8"

const account = web3.eth.accounts.privateKeyToAccount(ownerPrivateKey);
web3.eth.accounts.wallet.add(account);
web3.eth.defaultAccount = account.address;

async function storeData(data) {
    try {
        const gasPrice = await web3.eth.getGasPrice();
        const gasEstimate = await daiToken.methods.store(data).estimateGas(
                                    { from: account.address }
                                );
        
        daiToken.methods.store(data).send(
            {
                from: account.address, 
                gasPrice: gasPrice, 
                gas: gasEstimate
            }
        );
    } catch (error) {
        console.error(error)    
    }
}

module.exports = {
    storeData
}