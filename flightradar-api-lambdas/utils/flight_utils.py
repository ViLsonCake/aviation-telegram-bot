def filter_flights_by_aircraft_code(row_flights: list[dict], filter_codes: list[str]) -> list[dict]:
    filtered_planes_code: list = []

    for row_flight in row_flights:
        try:
            code: str = row_flight['flight']['aircraft']['model']['code']
        except TypeError:
            code = 'Unknown'

        if code in filter_codes:
            filtered_planes_code.append(row_flight)

    return filtered_planes_code