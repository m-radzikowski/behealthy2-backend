import subprocess
from flask import Flask, request, send_file, make_response
from flask_restful import reqparse, abort, Api, Resource
import json
import sys
import logging
import io
import py_eureka_client.eureka_client as eureka_client
import base64
from utils.validation import Validator
import array
import pickle
from time import sleep
import time

your_rest_server_port = 5001
# The flowing code will register your server to eureka server and also start to send heartbeat every 30 seconds
eureka_client.init(eureka_server="http://localhost:8761/eureka",
                   app_name="codec-service",
                   instance_port=your_rest_server_port)
app = Flask(__name__)
api = Api(app)
iterator = 0

@app.route('/')
def main():
    return '''
        your endpoint for sending messages
        is http://api_ip_address/convert
        '''
class CodecHandler(Resource):
    def post(self):
        req = request.get_json(force=True)
        if Validator.validate_request(req) is not None:
            data = req['audio']
            decoded = base64.b64decode(data.split(',', 1)[-1])
            with open('audio.webm', 'wb') as w:
                w.write(decoded)
            ts = time.time()
            file_name = "audio" + str(ts) + ".wav"
            command = "ffmpeg -i audio.webm -ab 160k -ac 1 -ar 44100 -vn " + file_name
            subprocess.call(command, shell=True)

            #audio_file = open(file_name, "rb")
            #enc = base64.b64encode(audio_file.read())
            #audio_file.close()

            path = "/home/gabrys/repozytoria/behealthy2-backend/service-codec/" + file_name
            return send_file(
                             path,
                             mimetype="audio/wav",
                             as_attachment=True,
                             attachment_filename=file_name)
        return 'Internal api server issue', 500

api.add_resource(CodecHandler, '/convert')

if __name__ == '__main__':
    if len(sys.argv) > 1:
        app.run(debug=True, host='0.0.0.0', port=5001)
    else:
        print('NPL api name hasn\'t been provided')


#f = open("audio-base64.txt", "r")
#contents = f.read()


