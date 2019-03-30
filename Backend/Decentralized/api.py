from flask import Flask, Response, request, jsonify
from marshmallow import Schema, fields, ValidationError

from Ethereum import video_hash

class VideoSchema(Schema):
    url = fields.String(required=True)
    hash = fields.Integer(required=True)

app = Flask(__name__)

@app.route("/api/video", methods=['POST'])
def transaction():
    
    w3.eth.defaultAccount = w3.eth.accounts[1]
    with open("data.json", 'r') as f:
        datastore = json.load(f)
    abi = datastore["abi"]
    contract_address = datastore["contract_address"]

    video = w3.eth.contract(
        address=contract_address, abi=abi,
    )
    body = request.get_json()
    result, error = VideoSchema().load(body)
    if error:        
        return jsonify(error), 422
    tx_hash = video.functions.setHash(
        result['name'], result['gender']
    )
    tx_hash = tx_hash.transact()
    w3.eth.waitForTransactionReceipt(tx_hash)
    video_data = video.functions.getHash().call()
    return jsonify({"data": video_data}), 200

@app.route("/api/", methods=['GET'])
def api():
    return jsonify({"data": "api"}), 200

if __name__ == "__main__":
    app.run()