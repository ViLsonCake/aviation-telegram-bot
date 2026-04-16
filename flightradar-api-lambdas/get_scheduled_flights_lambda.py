import logging

from FlightRadar24 import FlightRadar24API
from FlightRadar24.errors import AirportNotFoundError
from utils.flight_utils import AircraftUtils
from models.scheduled_flight import ScheduledFlight

aircraft_utils: AircraftUtils = AircraftUtils()
flightradar_api: FlightRadar24API = FlightRadar24API()
logger = logging.getLogger(__name__)

def lambda_handler(event, context):
    airports = event.get('airports', [])

    response: dict = dict()

    for airport in airports:
        try:
            wide_body_aircraft_for_aircraft = get_wide_body_aircraft_planes_for_airport(airport)
            response[airport] = wide_body_aircraft_for_aircraft
        except Exception as e:
            logger.error(e)

    return response

def get_wide_body_aircraft_planes_for_airport(code: str):
    try:
        details = flightradar_api.get_airport_details(code)
    except AirportNotFoundError:
        logger.warning(f'Airport with code {code} not found')
    except ValueError:
        logger.warning(f'The code {code} is not valid. It must be the IATA or ICAO of the airport')

    arrivals: list = details['airport']['pluginData']['schedule']['arrivals']['data']
    row_white_list_arrivals: list = aircraft_utils.get_white_list_plane_codes(arrivals)

    return {'flights': [ScheduledFlight(flight) for flight in row_white_list_arrivals]}