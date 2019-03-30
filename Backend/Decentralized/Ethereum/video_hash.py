import pickle
from web3 import Web3
from solc import compile_files, link_code



w3 = Web3(Web3.HTTPProvider("http://127.0.0.1:8545"))
    
def separate_main_n_link(file_path, contracts):
    main = {}
    link = {}

    all_keys = list(contracts.keys())
    for key in all_keys:
        if file_path[0] in key:
            main = contracts[key]
        else:
            link[key] = contracts[key]
    return main, link


def deploy_contract(contract_interface):
    contract = w3.eth.contract(
        abi=contract_interface['abi'], bytecode=contract_interface['bin'])

    tx_hash = contract.deploy(transaction={'from': w3.eth.accounts[1]})

    tx_receipt = w3.eth.getTransactionReceipt(tx_hash)
    return tx_receipt['contractAddress']    


def deploy_n_transact(file_path, mappings=[]):
    contracts = compile_files(file_path, import_remappings=mappings)
    link_add = {}
    contract_interface, links = separate_main_n_link(file_path, contracts)

    for link in links:
        link_add[link] = deploy_contract(links[link])    

    if link_add:
        contract_interface['bin'] = link_code(contract_interface['bin'], link_add)    

    return deploy_contract(contract_interface), contract_interface['abi']