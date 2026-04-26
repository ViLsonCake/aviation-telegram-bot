class ScheduledFlight:
    id: str
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

    def __init__(self, row_flight: dict):
        self.row_id: str = str(row_flight['flight']['identification']['row'])

        self.origin_airport_name: str = row_flight['flight']['airport']['origin']['name']
        self.origin_airport_iata: str = row_flight['flight']['airport']['origin']['code']['iata']
        self.origin_airport_icao: str = row_flight['flight']['airport']['origin']['code']['icao']
        self.callsign: str = row_flight['flight']['identification']['callsign']
        self.registration: str = row_flight['flight']['aircraft']['registration']
        self.live: bool = row_flight['flight']['status']['live']
        self.status: str = row_flight['flight']['status']['text']
        self.diverted: str = row_flight['flight']['status']['generic']['status']['diverted']

        # Scheduled times (Unix timestamps in UTC)
        self.scheduled_departure_time: int = row_flight['flight']['time']['scheduled']['departure']
        self.scheduled_arrival_time: int = row_flight['flight']['time']['scheduled']['arrival']

        # Estimated time UTC (can be None if not yet happened)
        self.estimated_arrival_time: int = row_flight['flight']['status']['generic']['eventTime']['utc']

        try:
            self.aircraft_code: str = row_flight['flight']['aircraft']['model']['code']
            self.aircraft_name: str = row_flight['flight']['aircraft']['model']['text']
        except TypeError:
            self.aircraft_code = 'Unknown'
            self.aircraft_name = 'Unknown'

        try:
            self.airline_name: str = row_flight['flight']['airline']['name']
        except TypeError:
            self.airline_name = 'Unknown'
