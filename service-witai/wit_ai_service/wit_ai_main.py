from wit import Wit
from wit_ai_service.response_composer import ResponseComposer
from utils.validation import Validator
from utils.logger import Logger


class WitService(ResponseComposer):
    
    @staticmethod
    def compose_response(message_from_wit, msg_id):
        if 'entities' in message_from_wit.keys():
                if'sentiment' in message_from_wit['entities'].keys():
                    for sugestion in message_from_wit['entities']['sentiment']:
                        if all (k in sugestion.keys() for k in ('confidence','value')):
                            return {
                            'confidence': sugestion['confidence'],
                            'value': sugestion['value'].upper(),
                            'id': msg_id
                            }
        return None
    
    def __init__(self, access_token, location_response_logic_list):
        super().__init__(location_response_logic_list)
        self.__client = Wit(access_token)

    def write_to(self, message):
        msg_text = message['message']
        msg_text_length = len(msg_text)
        print(' ## MESSAGE: {}'.format(msg_text))
        if msg_text_length in range(0, 250):
            wit_response = self.__client.message(msg_text)
            Logger.json_print(wit_response)
            return WitService.compose_response(wit_response, message['id'])
        return None

    def talk_to(self, message):
        msg_text = message['message']
        msg_text_length = len(msg_text)
        print(' ## MESSAGE: {}'.format(msg_text))
        if msg_text_length in range(0, 250):
            wit_response = self.__client.message(msg_text)
            Logger.json_print(wit_response)
            if 'entities' in wit_response.keys():
                return self.__create_response_to_msg(wit_response['entities'])
        return None

    def __create_response_to_msg(self, wit_response):
        entities = []
        # TODO: each step here inside isteed of returning response value will calculate tensor.
        # This tensor will give response type and entities that will be used for answare
        if Validator.check_if_any_keys_in(wit_response, 'exaggeration'):
            entities.append(self.get_with_highest_confidance(wit_response['exaggeration']))
        if Validator.check_if_any_keys_in(wit_response, 'sentiment'):
            entities.append(self.get_with_highest_confidance(wit_response['sentiment']))
        return entities


