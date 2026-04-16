from utils.flight_utils import AircraftUtils

aircraft_utils: AircraftUtils = AircraftUtils()

class ScheduledFlight:
    id: str
    code: str
    aircraft: str
    airline_name: str
    airport: str
    iata: str
    icao: str
    callsign: str
    registration: str
    live: bool
    text: str

    def __init__(self, row_flight: dict):
        self.id: str = row_flight['flight']['identification']['id']

        if self.id is None:
            self.id = str(row_flight['flight']['identification']['row'])

        self.airport: str = row_flight['flight']['airport']['origin']['name']
        self.iata: str = row_flight['flight']['airport']['origin']['code']['iata']
        self.icao: str = row_flight['flight']['airport']['origin']['code']['icao']
        self.callsign: str = row_flight['flight']['identification']['callsign']
        self.registration: str = row_flight['flight']['aircraft']['registration']
        self.live: bool = row_flight['flight']['status']['live']
        self.text: str = row_flight['flight']['status']['text']

        try:
            self.code: str = row_flight['flight']['aircraft']['model']['code']
            self.aircraft = aircraft_utils.get_aircraft_name_by_code(self.code)
        except TypeError:
            self.code = 'Unknown'
            self.aircraft = 'Unknown'

        try:
            self.airline_name: str = row_flight['flight']['airline']['name']
        except TypeError:
            self.airline_name = 'Unknown'
