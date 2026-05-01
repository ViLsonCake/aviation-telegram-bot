import logging

from concurrent.futures import ThreadPoolExecutor, as_completed
from FlightRadar24 import FlightRadar24API
from FlightRadar24.errors import AirportNotFoundError
from FlightRadar24.core import Core
from utils.flight_utils import filter_flights_by_aircraft_code, convert_images_to_dict
from models.scheduled_flight import ScheduledFlight

# TODO remove the library patching once it's updated to resolve 403 Forbidden response
Core.headers = {
    "accept-encoding": "gzip, br",
    "accept-language": "en-US,en;q=0.9",
    "cache-control": "max-age=0",
    "user-agent": "Flightradar24/10.0.0 (com.flightradar24.iphone; build:10.0.0.1; iOS 17.4.1) Alamofire/5.9.1"
}
Core.json_headers = Core.headers.copy()
flightradar_api: FlightRadar24API = FlightRadar24API()

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

def lambda_handler(event, context):
    logger.info(f'Event received: {event}')

    airports: list = event.get('airports', [])
    response: dict = dict()

    for airport in airports:
        try:
            airport_code: str = airport.get('airport_code')

            if airport_code is None or airport_code == '':
                continue

            aircraft_filter_codes = airport.get('aircraft_filter_codes', [])
            filtered_flights_for_airport = get_filtered_flights_for_airport(airport_code, aircraft_filter_codes)
            response[airport_code] = filtered_flights_for_airport
        except Exception as e:
            logger.error(e)

    logger.info('Event successfully processed.')

    return response

def fetch_page(code: str, page: int) -> dict:
    return flightradar_api.get_airport_details(code=code, page=page)


def get_filtered_flights_for_airport(code: str, aircraft_filter_codes: list[str], pages_to_retrieve: int = 3) -> dict:
    total_arrivals_count: int = 0
    total_filtered_arrivals_count: int = 0
    total_flights: list = []

    with ThreadPoolExecutor(max_workers=pages_to_retrieve) as executor:
        futures = {executor.submit(fetch_page, code, page): page for page in range(pages_to_retrieve)}

        for future in as_completed(futures):
            try:
                response = future.result()
            except AirportNotFoundError:
                logger.warning(f'The airport with the code {code} was not found')
                return {'flights': []}
            except ValueError:
                logger.warning(f'The code {code} is not valid. It must be the IATA or ICAO of the airport')
                return {'flights': []}

            arrivals: list = response.get('airport', {}).get('pluginData', {}).get('schedule', {}).get('arrivals', {}).get('data', [])
            aircraft_images: list = response.get('aircraftImages', [])
            raw_filtered_arrivals: list = filter_flights_by_aircraft_code(arrivals, aircraft_filter_codes)
            converted_aircraft_images: dict = convert_images_to_dict(aircraft_images)

            total_arrivals_count += len(arrivals)
            total_filtered_arrivals_count += len(raw_filtered_arrivals)
            total_flights.extend([vars(ScheduledFlight.create_from_raw_flight(flight, converted_aircraft_images)) for flight in raw_filtered_arrivals])

    return {
        'arrivals_count': total_arrivals_count,
        'filtered_arrivals_count': total_filtered_arrivals_count,
        'flights': total_flights
    }