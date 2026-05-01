CREATE EXTENSION IF NOT EXISTS cube;
CREATE EXTENSION IF NOT EXISTS earthdistance;

-- Airports table
CREATE TABLE IF NOT EXISTS airports (
    icao      VARCHAR(4) PRIMARY KEY,
    iata      VARCHAR(3) NOT NULL UNIQUE,
    name      VARCHAR(255) NOT NULL,
    city      VARCHAR(255) NOT NULL,
    country   VARCHAR(50) NOT NULL,
    timezone  VARCHAR(50) NOT NULL,
    latitude  DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL
);

COMMENT ON TABLE airports IS 'Table for static airports storing';

-- Aircraft families table
CREATE TABLE IF NOT EXISTS aircraft_families (
    code         VARCHAR(50) PRIMARY KEY,
    manufacturer VARCHAR(50) NOT NULL
);

COMMENT ON TABLE aircraft_families IS 'Table for static storing aircraft families';

-- Wide-body aircraft table
CREATE TABLE IF NOT EXISTS wide_body_aircraft (
    code                 VARCHAR(50) PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    family               VARCHAR(50) NOT NULL,
    priority             VARCHAR(50) NOT NULL,

    FOREIGN KEY (family) REFERENCES aircraft_families(code)
);

COMMENT ON TABLE wide_body_aircraft IS 'Table for static storing wide-body aircraft';

-- Scheduled flights table
CREATE TABLE IF NOT EXISTS scheduled_flights (
    id                                     VARCHAR(50) PRIMARY KEY,
    row_id                                 VARCHAR(50) NOT NULL,
    aircraft_code                          VARCHAR(50) NOT NULL,
    airline_name                           VARCHAR(255) NOT NULL,
    origin_airport_icao                    VARCHAR(4) NOT NULL,
    destination_airport_icao               VARCHAR(4) NOT NULL,
    callsign                               VARCHAR(50) NOT NULL,
    registration                           VARCHAR(50) NOT NULL,
    live                                   BOOLEAN NOT NULL,
    status                                 VARCHAR(50) NOT NULL,
    scheduled_departure_time               TIMESTAMP WITH TIME ZONE NOT NULL,
    scheduled_arrival_time                 TIMESTAMP WITH TIME ZONE NOT NULL,
    estimated_arrival_time                 TIMESTAMP WITH TIME ZONE,

    FOREIGN KEY (origin_airport_icao)      REFERENCES airports(icao),
    FOREIGN KEY (destination_airport_icao) REFERENCES airports(icao),
    FOREIGN KEY (aircraft_code)            REFERENCES wide_body_aircraft(code)
);

COMMENT ON TABLE scheduled_flights IS 'Table for storing scheduled flights';

-- Specific aircraft flights table
CREATE TABLE IF NOT EXISTS specific_aircraft_flights (
    id                                     VARCHAR(50) PRIMARY KEY,
    aircraft_code                          VARCHAR(50) NOT NULL,
    airline_name                           VARCHAR(255) NOT NULL,
    callsign                               VARCHAR(50) NOT NULL,
    origin_airport_icao                    VARCHAR(4) NOT NULL,
    destination_airport_icao               VARCHAR(4) NOT NULL,
    altitude                               INTEGER NOT NULL,
    ground_speed                           INTEGER NOT NULL,
    vertical_speed                         INTEGER NOT NULL,
    heading                                INTEGER NOT NULL,
    latitude                               DOUBLE PRECISION NOT NULL,
    longitude                              DOUBLE PRECISION NOT NULL,

    FOREIGN KEY (origin_airport_icao)      REFERENCES airports(icao),
    FOREIGN KEY (destination_airport_icao) REFERENCES airports(icao),
    FOREIGN KEY (aircraft_code)            REFERENCES wide_body_aircraft(code)
);

COMMENT ON TABLE specific_aircraft_flights IS 'Table for storing specific aircraft flights';

-- Flights notification table
CREATE TABLE IF NOT EXISTS flights_notification (
    -- General table columns
    id                                        UUID PRIMARY KEY,
    notification_type                         VARCHAR(50) NOT NULL,
    user_id                                   UUID,
    notified_at                               TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Scheduled flights entity columns
    scheduled_flight_id                       VARCHAR(50),
    notified_delayed                          BOOLEAN DEFAULT FALSE,
    notified_cancelled                        BOOLEAN DEFAULT FALSE,
    notified_diverted                         BOOLEAN DEFAULT FALSE,
    notified_arriving_soon                    BOOLEAN DEFAULT FALSE,
    notified_live                             BOOLEAN DEFAULT FALSE,
    notified_landed                           BOOLEAN DEFAULT FALSE,
    last_notified_eta                         TIMESTAMP WITH TIME ZONE,

    -- Specific aircraft flights entity columns
    specific_aircraft_flight_id               VARCHAR(50),
    took_off                                  BOOLEAN DEFAULT FALSE,
    on_ground                                 BOOLEAN DEFAULT FALSE,
    landing                                   BOOLEAN DEFAULT FALSE,
    flying_near_airport                       BOOLEAN DEFAULT FALSE,
    at_user_airport                           BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (user_id)                     REFERENCES users(id),
    FOREIGN KEY (scheduled_flight_id)         REFERENCES scheduled_flights(id),
    FOREIGN KEY (specific_aircraft_flight_id) REFERENCES specific_aircraft_flights(id)
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id                         UUID PRIMARY KEY,
    username                   VARCHAR(50) NOT NULL UNIQUE,
    chat_id                    BIGINT NOT NULL,
    state                      VARCHAR(50) NOT NULL,
    bot_mode                   VARCHAR(50),
    airport_icao               VARCHAR(4),
    created_at                 TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                 TIMESTAMP WITH TIME ZONE NOT NULL,

    FOREIGN KEY (airport_icao) REFERENCES airports(icao)
);

COMMENT ON TABLE users IS 'Table for storing bot users';