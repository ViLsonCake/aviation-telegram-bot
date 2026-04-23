import logging

from FlightRadar24 import FlightRadar24API
from FlightRadar24.errors import AirportNotFoundError
from utils.flight_utils import filter_flights_by_aircraft_code
from models.scheduled_flight import ScheduledFlight

flightradar_api: FlightRadar24API = FlightRadar24API()
logger = logging.getLogger(__name__)

def lambda_handler(event, context):
    airports: dict = event.get('airports', {})
    response: dict = dict()

    for airport in airports:
        try:
            airport_code: str = airport.get('airport_code')
            aircraft_filter_codes = airport.get('aircraft_filter_codes', [])
            filtered_flights_for_airport = get_filtered_flights_for_airport(airport_code, aircraft_filter_codes)
            response[airport] = filtered_flights_for_airport
        except Exception as e:
            logger.error(e)

    return response

def get_filtered_flights_for_airport(code: str, aircraft_filter_codes: list[str]) -> dict:
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

    arrivals_count: int = len(arrivals)
    filtered_arrivals_count: int = len(row_filtered_arrivals)

    return {
        'arrivals_count': arrivals_count,
        'filtered_arrivals_count': filtered_arrivals_count,
        'flights': [vars(ScheduledFlight(flight)) for flight in row_filtered_arrivals]
    }