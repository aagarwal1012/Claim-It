import json
from flask import Flask, Response, request, jsonify
from marshmallow import Schema, fields, ValidationError
from web3 import Web3

w3 = Web3(Web3.HTTPProvider("http://127.0.0.1:8545"))
w3.eth.defaultAccount = w3.eth.accounts[1]

with open("data.json", 'r') as f:
    datastore = json.load(f)
    abi = datastore["abi"]
    contract_address = datastore["contract_address"]


class VideoSchema(Schema):
    url = fields.String(required=True)
    hash = fields.Integer(required=True)

app = Flask(__name__)

@app.route("/api/video", methods=['POST'])
def video():
    video = w3.eth.contract(address=contract_address, abi=abi)
    body = request.get_json()
    result, error = VideoSchema().load(body)
    if error:
        return jsonify(error), 422
    tx_hash = video.functions.setHash(
        result['url'],result['hash']
    ).transact()

    receipt = w3.eth.waitForTransactionReceipt(tx_hash)
    video_data = video.functions.getHash().call()
    return jsonify({"data": video_data}), 200