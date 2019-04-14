class Validator:

    @staticmethod
    def validate_request(req_message):
        if Validator.check_if_all_keys_in(req_message, 'audio'):
            if type(req_message['audio']) is str:
                if len(req_message['audio']) > 0:
                    return True
            return False
    
    @staticmethod
    def check_if_any_keys_in(object_to_validate, *key_names):
        if any (k in object_to_validate.keys() for k in (key_names)):
            return True
        return False

    @staticmethod
    def check_if_all_keys_in(object_to_validate, *key_names):
        if all (k in object_to_validate.keys() for k in (key_names)):
            return True
        return False
