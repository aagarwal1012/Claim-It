import os
from flask import Flask, render_template, request, redirect, url_for, send_from_directory, jsonify
from werkzeug import secure_filename

import requests

app = Flask(__name__)

app.config['UPLOAD_FOLDER'] = 'media/'
app.config['ALLOWED_EXTENSIONS'] = set(['mp4', 'csv'])

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in app.config['ALLOWED_EXTENSIONS']


@app.route('/api/video/upload', methods=['POST'])
def upload():
    uploaded_files = request.files.getlist("file[]")
    filenames = []
    for file in uploaded_files:
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            filenames.append(filename)
            checkSum = hashlib.md5(file.read()).hexdigest()

            url = 'http://127.0.0.1/api/video/upload/'+filename

            params = {
                'url': url,
                'hash': checkSum
            }

            r = requests.post(
                'http://127.0.0.1/api/video_hash',
                data=params
            )
            print(checkSum)
    return jsonify(sucess=True)

@app.route('/api/video/<filename>')
def uploaded_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'],
                               filename)

@app.route('/test')
def testing():
    return jsonify({"Testing": "ok done!"})

if __name__ == "__main__":
    app.run(
        host="127.0.0.1",
        port=int("80"),
        debug=True
    )