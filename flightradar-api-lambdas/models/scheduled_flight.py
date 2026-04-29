class ScheduledFlight:
    id: str
    row_id: str
    aircraft_code: str
    aircraft_name: str
    airline_name: str
    origin_airport_name: str
    origin_airport_iata: str
    origin_airport_icao: str
    callsign: str
    registration: str
    live: bool
    status: str
    diverted: str
    scheduled_departure_time: int
    scheduled_arrival_time: int
    estimated_arrival_time: int
    images: list

    def __init__(self, raw_flight: dict, converted_aircraft_images: dict):
        self.id: str = str(raw_flight['flight']['identification']['id'])
        self.row_id: str = str(raw_flight['flight']['identification']['row'])

        self.origin_airport_name: str = raw_flight['flight']['airport']['origin']['name']
        self.origin_airport_iata: str = raw_flight['flight']['airport']['origin']['code']['iata']
        self.origin_airport_icao: str = raw_flight['flight']['airport']['origin']['code']['icao']
        self.callsign: str = raw_flight['flight']['identification']['callsign']
        self.registration: str = raw_flight['flight']['aircraft']['registration']
        self.live: bool = raw_flight['flight']['status']['live']
        self.status: str = raw_flight['flight']['status']['text']
        self.diverted: str = raw_flight['flight']['status']['generic']['status']['diverted']

        # Scheduled times (Unix timestamps in UTC)
        self.scheduled_departure_time: int = raw_flight['flight']['time']['scheduled']['departure']
        self.scheduled_arrival_time: int = raw_flight['flight']['time']['scheduled']['arrival']

        # Estimated time UTC (can be None if not yet happened)
        self.estimated_arrival_time: int = raw_flight['flight']['status']['generic']['eventTime']['utc']

        self.images: list = converted_aircraft_images.get(self.registration, [])

        try:
            self.aircraft_code: str = raw_flight['flight']['aircraft']['model']['code']
            self.aircraft_name: str = raw_flight['flight']['aircraft']['model']['text']
        except TypeError:
            self.aircraft_code = 'Unknown'
            self.aircraft_name = 'Unknown'

        try:
            self.airline_name: str = raw_flight['flight']['airline']['name']
        except TypeError:
            self.airline_name = 'Unknown'
