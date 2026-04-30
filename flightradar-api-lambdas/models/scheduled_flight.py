from dataclasses import dataclass, field


@dataclass
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
    images: list = field(default_factory=list)

    @classmethod
    def create_from_raw_flight(cls, raw_flight: dict, converted_aircraft_images: dict) -> "ScheduledFlight":
        flight = raw_flight.get('flight', {})
        identification = flight.get('identification', {})
        airport = flight.get('airport', {})
        origin = airport.get('origin', {})
        origin_code = origin.get('code', {})
        aircraft = flight.get('aircraft', {})
        flight_status = flight.get('status', {})
        generic = flight_status.get('generic', {})
        time = flight.get('time', {})
        scheduled = time.get('scheduled', {})

        registration = aircraft.get('registration', '')

        return cls(
            id=str(identification.get('id', '')),
            row_id=str(identification.get('row', '')),
            origin_airport_name=origin.get('name', ''),
            origin_airport_iata=origin_code.get('iata', ''),
            origin_airport_icao=origin_code.get('icao', ''),
            callsign=identification.get('callsign', ''),
            registration=registration,
            live=flight_status.get('live', False),
            status=flight_status.get('text', ''),
            diverted=generic.get('status', {}).get('diverted', ''),
            scheduled_departure_time=scheduled.get('departure', 0),
            scheduled_arrival_time=scheduled.get('arrival', 0),
            estimated_arrival_time=generic.get('eventTime', {}).get('utc', 0),
            aircraft_code=aircraft.get('model', {}).get('code', 'Unknown'),
            aircraft_name=aircraft.get('model', {}).get('text', 'Unknown'),
            airline_name=flight.get('airline', {}).get('name', 'Unknown'),
            images=converted_aircraft_images.get(registration, []),
        )