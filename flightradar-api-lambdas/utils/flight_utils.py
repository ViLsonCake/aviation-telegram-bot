import os
import json

class Singleton(type):
    _instances = {}
    def __call__(cls, *args, **kwargs):
        if cls not in cls._instances:
            cls._instances[cls] = super(Singleton, cls).__call__(*args, **kwargs)
        return cls._instances[cls]

class AircraftUtils(metaclass=Singleton):

    __data: dict[str, str]

    def __init__(self):
        self.__data = self.__load_json_widebody_aircraft_data()

    def __load_json_widebody_aircraft_data(self) -> dict[str, str]:
        with open(os.getcwd() + '/static/wide-body-aircraft.json', 'r') as widebody_aircraft_file:
            string_json = widebody_aircraft_file.read()

        return json.loads(string_json)

    def get_data(self):
        return self.__data

    def get_aircraft_name_by_code(self, code: str) -> str:
        return self.get_data()[code]

    def get_white_list_plane_codes(self, row_flights: list) -> list:
        white_list_codes_with_names: dict[str, str] = self.get_data()
        white_list_planes: list = []

        for row_flight in row_flights:
            try:
                code: str = row_flight['flight']['aircraft']['model']['code']
            except TypeError:
                code = 'Unknown'

            if code in white_list_codes_with_names.keys():
                white_list_planes.append(row_flight)

        return white_list_planes