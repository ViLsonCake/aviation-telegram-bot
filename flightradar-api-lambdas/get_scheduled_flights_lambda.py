import logging

from FlightRadar24 import FlightRadar24API
from FlightRadar24.errors import AirportNotFoundError
from utils.flight_utils import filter_flights_by_aircraft_code
from models.scheduled_flight import ScheduledFlight

flightradar_api: FlightRadar24API = FlightRadar24API()
logger = logging.getLogger(__name__)

def lambda_handler(event, context):
    airports = event.get('airports', [])
    aircraft_filter_codes = event.get('aircraft_filter_codes', [])

    response: dict = dict()

    for airport in airports:
        try:
            wide_body_aircraft_for_aircraft = get_wide_body_aircraft_planes_for_airport(airport, aircraft_filter_codes)
            response[airport] = wide_body_aircraft_for_aircraft
        except Exception as e:
            logger.error(e)

    return response

def get_wide_body_aircraft_planes_for_airport(code: str, aircraft_filter_codes: list[str]):
    try:
        details = flightradar_api.get_airport_details(code)
    except AirportNotFoundError:
        logger.warning(f'Airport with code {code} not found')
        return {'flights': []}
    except ValueError:
        logger.warning(f'The code {code} is not valid. It must be the IATA or ICAO of the airport')
        return {'flights': []}

    arrivals: list = details['airport']['pluginData']['schedule']['arrivals']['data']
    row_filtered_arrivals: list = filter_flights_by_aircraft_code(arrivals, aircraft_filter_codes)

    return {'flights': [vars(ScheduledFlight(flight)) for flight in row_filtered_arrivals]}