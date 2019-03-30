from compile_solidity_utils import deploy_n_transact
from solc import link_code
import json
    

contract_address, abi = deploy_n_transact(['video_hash.sol'])

with open('data.json', 'w') as outfile:
    data = {
        "abi": abi,
        "contract_address": contract_address
    }
    json.dump(data, outfile, indent=4, sort_keys=True)