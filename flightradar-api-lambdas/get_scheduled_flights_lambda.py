import logging
import cloudscraper

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
            aircraft_filter_codes = airport.get('aircraft_filter_codes', [])
            filtered_flights_for_airport = get_filtered_flights_for_airport(airport_code, aircraft_filter_codes)
            response[airport_code] = filtered_flights_for_airport
        except Exception as e:
            logger.error(e)

    logger.info('Event successfully processed.')

    return response

def get_filtered_flights_for_airport(code: str, aircraft_filter_codes: list[str]) -> dict:
    try:
        response = scraper.get(
            'https://api.flightradar24.com/common/v1/airport.json',
            params={"format": "json", "code": code, "limit": 100, "page": 1}
        )
        response_json = response.json()
    except HTTPError as e:
        logger.warning(f"HTTP error: {e.response.status_code}")
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

    result = response_json["result"]["response"]
    arrivals: list = result['airport']['pluginData']['schedule']['arrivals']['data']
    aircraft_images: list = result['aircraftImages']
    raw_filtered_arrivals: list = filter_flights_by_aircraft_code(arrivals, aircraft_filter_codes)
    converted_aircraft_images: dict = convert_images_to_dict(aircraft_images)

    arrivals_count: int = len(arrivals)
    filtered_arrivals_count: int = len(raw_filtered_arrivals)

    return {
        'arrivals_count': arrivals_count,
        'filtered_arrivals_count': filtered_arrivals_count,
        'flights': [vars(ScheduledFlight(flight, converted_aircraft_images)) for flight in raw_filtered_arrivals]
    }