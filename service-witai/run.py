from flask import Flask, request
from flask_restful import reqparse, abort, Api, Resource
import json
import sys
import nltk.data
import nltk.tokenize.punkt
import logging
import py_eureka_client.eureka_client as eureka_client

import os
from wit_ai_service.wit_ai_main import WitService
from utils.validation import Validator
from utils.logger import Logger

request_to_send = dict(
    id = 'mojId',
    message = ""
)
your_rest_server_port = 5000
# The flowing code will register your server to eureka server and also start to send heartbeat every 30 seconds
eureka_client.init(eureka_server="http://localhost:8761/eureka",
                   app_name="witai-service",
                   instance_port=your_rest_server_port)
app = Flask(__name__)
api = Api(app)
configuration = None
location_response_logic_list = None
wit_service = None
watson_service = None
nltk.download('punkt')
print(os.getcwd())

def read_configuation():
    global configuration
    global location_response_logic_list
    try:
        configuration = json.loads(open('/home/gabrys/repozytoria/behealthy2-backend/service-witai/config.json', 'r').read())
    except:
        Exception('config file does not exists.')
    try:
        location_response_logic_list = json.loads(open('bot_logic/location_logic.json', 'r').read())
    except:
        Exception('bot location logic file does not exists.')
    if configuration is not None:
        print(' ## Configuration has been read.')
    else:
        raise Exception(' ## Configuration is not set or unaccesable, check you config.json file.')

def set_witai_dependencies():
    global configuration
    global location_response_logic_list
    global wit_service
    if 'wit_ai' in configuration.keys() and 'access_token' in configuration['wit_ai'].keys():
        access_token = configuration['wit_ai']['access_token']
        wit_service = WitService(access_token, location_response_logic_list)
        print(' ## Connected to wit.ai')
    else:
        raise Exception(' ## Configuretion file has wrong format or structure. Should be json file.')

@app.route('/')
def main():
    return '''
        Hello from Sentiment Recogintion API,
        your endpoint for sending messages for validation
        is http://api_ip_address/message. JSON format is: {"id": string, "message": string}
        '''


class SentimentHandler(Resource):
    global wit_service
    def post(self):
        # TODO: start session for each client
        client_ip_address = request.remote_addr
        req = request.get_json(force=True)
        Logger.client_ip(client_ip_address)
        responses = []
        if wit_service and Validator.validate_request(req) is not None:
            msg_text = req['message']
            to_send = []
            if len(msg_text) >250:
                tokenizer = nltk.data.load('tokenizers/punkt/polish.pickle')
                res = tokenizer.tokenize(msg_text)
                for sent in res:
                    if len(sent) > 249:
                        splitat = 248
                        l, r = sent[:splitat], sent[splitat:]
                        to_send.append(l)
                        to_send.append(r)
                        continue
                    to_send.append(sent)
            else:
                request_to_send['message'] = req['message']
                to_send.append(request_to_send)
            for sent in to_send:
                request_to_send['message'] = sent
                wit_response = wit_service.write_to(request_to_send)
                if wit_response is not None:
                    responses.append(wit_response)
            value = 0
            for response in responses:
                value = value + response
            return value, 200
        return 'Internal api server issue', 500

api.add_resource(SentimentHandler, '/sentiment')

if __name__ == '__main__':
    if len(sys.argv) > 1:
        read_configuation()
        if sys.argv[1] == 'witai':
            set_witai_dependencies()
        else:
            print('Wrong name.')
        app.run(debug=True, host='0.0.0.0')
    else:
        print('NPL api name hasn\'t been provided')
    
