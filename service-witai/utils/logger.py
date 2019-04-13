import json


class Logger:

    @staticmethod
    def client_ip(ip):
        print('************************* NEW REQUEST *************************')
        print('## Requst from ip address: {}'.format(ip))
        print('***************************** END *****************************')


    @staticmethod
    def json_print(json_to_print):
        print('*************************** NEW JSON ***************************')
        print(json.dumps(json_to_print, indent=2, sort_keys=True))
        print('***************************** END *****************************')
