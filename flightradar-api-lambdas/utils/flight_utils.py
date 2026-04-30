def filter_flights_by_aircraft_code(raw_flights: list[dict], filter_codes: list[str]) -> list[dict]:
    filtered_planes_code: list = []

    for raw_flight in raw_flights:
        try:
            code: str = raw_flight['flight']['aircraft']['model']['code']
        except TypeError:
            code = 'Unknown'

        if code in filter_codes:
            filtered_planes_code.append(raw_flight)

    return filtered_planes_code

def convert_images_to_dict(raw_aircraft_images: list[dict]) -> dict[str, list]:
    if raw_aircraft_images is None:
        return {}

    converted_images_result: dict = {}

    for raw_image in raw_aircraft_images:
        registration: str = raw_image['registration']
        images: dict = raw_image['images']

        if images is None:
            continue

        large_size_images: list = images.get('large', [])

        converted_images: list[dict] = list()

        for image in large_size_images:
            converted_image: dict = {
                'src': image['src'],
                'link': image['link'],
                'copyright': image['copyright'],
                'source': image['source']
            }
            converted_images.append(converted_image)

        converted_images_result[registration] = converted_images

    return converted_images_result