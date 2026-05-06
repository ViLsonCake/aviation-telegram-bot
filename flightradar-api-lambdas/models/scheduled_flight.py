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
        flight = raw_flight.get('flight') or {}
        identification = flight.get('identification') or {}
        airport = flight.get('airport') or {}
        origin = airport.get('origin') or {}
        origin_code = origin.get('code') or {}
        aircraft = flight.get('aircraft') or {}
        flight_status = flight.get('status') or {}
        generic = flight_status.get('generic') or {}
        time = flight.get('time') or {}
        scheduled = time.get('scheduled') or {}
        airline = flight.get('airline') or {}
        model = aircraft.get('model') or {}
        generic_status = generic.get('status') or {}
        event_time = generic.get('eventTime') or {}

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
            diverted=generic_status.get('diverted', ''),
            scheduled_departure_time=scheduled.get('departure', 0),
            scheduled_arrival_time=scheduled.get('arrival', 0),
            estimated_arrival_time=event_time.get('utc', 0),
            aircraft_code=model.get('code', 'Unknown'),
            aircraft_name=model.get('text', 'Unknown'),
            airline_name=airline.get('name', 'Unknown'),
            images=converted_aircraft_images.get(registration, []),
        )