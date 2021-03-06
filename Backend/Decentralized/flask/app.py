import json
from web3 import Web3
# from compile_solidity_utils import w3
from flask import Flask, Response, request, jsonify
from marshmallow import Schema, fields, ValidationError


app = Flask(__name__)

contract_address = "0xAf5F788A32D09F6AAa8d54508bf53957bB6Bd6B7"

abi = json.load(open("VideoHash.json"))["abi"]


# web3.py instance
w3 = Web3(Web3.HTTPProvider("http://127.0.0.1:8545"))

class VideoSchema(Schema):
    url = fields.String(required=True)
    hash = fields.String(required=True)


@app.route("/api/video_hash", methods=['POST'])
def transaction():
    w3.eth.defaultAccount = w3.eth.accounts[1]
    
    video = w3.eth.contract(
        address=contract_address, abi=abi
    )
    # print(request.data)
    body = request.get_json(force=True)

    # print(body)

    url = body['url']
    hash = body['hash']
    # print(url)
    # print(hash)

    result, error = VideoSchema().load(body)
    # print(error)
    if error:
        return jsonify(error), 422

    # print(result)
    
    tx_hash = video.functions.setHash(
        result['url'], result['hash']
    )
    # tx_hash = video.functions.setHash(
    #     url, hash
    # )
    tx_hash = tx_hash.transact()

    w3.eth.waitForTransactionReceipt(tx_hash)
    video_data = video.functions.getHash().call()

    return jsonify({"data": video_data}), 200

@app.route("/test", methods=['GET'])
def testing():
    return jsonify({"data": "testing.."}), 200