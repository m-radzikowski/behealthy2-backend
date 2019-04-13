from utils.validation import Validator


class ResponseComposer:

    __location_response_logic_list = None
    __fields = ['confidence', 'value']

    def __init__(self, location_response_logic_list):
        if type(location_response_logic_list) is list:
            for single_logic in location_response_logic_list:
                if type(single_logic) is dict and Validator.check_if_all_keys_in(
                        single_logic, 'context', 'action'):
                    self.__location_response_logic_list = location_response_logic_list

    def get_with_highest_confidance(self, table_to_check):
        if all (Validator.check_if_all_keys_in(item, *self.__fields) for item in table_to_check):
            best_predicted = max(table_to_check, key=lambda t: t[self.__fields[0]])
            return best_predicted
        return None


