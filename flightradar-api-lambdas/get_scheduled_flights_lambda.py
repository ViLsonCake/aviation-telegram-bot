import logging
import cloudscraper

from concurrent.futures import ThreadPoolExecutor, as_completed
from requests.exceptions import HTTPError, ConnectionError, Timeout
from utils.flight_utils import filter_flights_by_aircraft_code, convert_images_to_dict
from models.scheduled_flight import ScheduledFlight

scraper = cloudscraper.create_scraper()

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
            logger.info('Event successfully processed.')
        except Exception as e:
            logger.exception(e)

    return response

def fetch_page(code: str, page: int) -> dict:
    return scraper.get(
        'https://api.flightradar24.com/common/v1/airport.json',
        params={"format": "json", "code": code, "limit": 100, "page": page}
    )

def get_filtered_flights_for_airport(code: str, aircraft_filter_codes: list[str], pages_to_retrieve: int = 3) -> dict:
    total_arrivals_count: int = 0
    total_filtered_arrivals_count: int = 0
    total_flights: list = []

    with ThreadPoolExecutor(max_workers=pages_to_retrieve) as executor:
        futures = {executor.submit(fetch_page, code, page): page for page in range(pages_to_retrieve)}

        for future in as_completed(futures):
            try:
                response = future.result()
                response_json = response.json()
            except HTTPError as e:
                logger.warning(f"{e.response.status_code} Client Error")
                return {'flights': []}
            except ConnectionError:
                logger.warning("No connection")
                return {'flights': []}
            except Timeout:
                logger.warning("Request timed out")
                return {'flights': []}
            except ValueError:
                logger.warning(f'The code {code} is not valid. It must be the IATA or ICAO of the airport')
                return {'flights': []}

            result: dict = response_json.get('result', {}).get('response', {})
            arrivals: list = result.get('airport', {}).get('pluginData', {}).get('schedule', {}).get('arrivals', {}).get('data', [])
            aircraft_images: list = result.get('aircraftImages', [])
            raw_filtered_arrivals: list = filter_flights_by_aircraft_code(arrivals, aircraft_filter_codes)
            converted_aircraft_images: dict = convert_images_to_dict(aircraft_images)

            total_arrivals_count += len(arrivals)
            total_filtered_arrivals_count += len(raw_filtered_arrivals)
            total_flights.extend([vars(ScheduledFlight.create_from_raw_flight(flight, converted_aircraft_images)) for flight in raw_filtered_arrivals])

    seen = set()
    unique_flights = []
    for flight in total_flights:
        key = (flight['row_id'], flight['scheduled_departure_time'])
        if key not in seen:
            seen.add(key)
            unique_flights.append(flight)

    return {
        'arrivals_count': total_arrivals_count,
        'filtered_arrivals_count': len(unique_flights),
        'flights': unique_flights
    }