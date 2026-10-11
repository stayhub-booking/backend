CREATE TABLE cities
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_code CHAR(2)      NOT NULL,
    name         VARCHAR(100) NOT NULL,
    slug         VARCHAR(150) NOT NULL,

    CONSTRAINT uq_cities_slug UNIQUE (slug)
);

CREATE TABLE amenities
(
    id       UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    code     VARCHAR(50)  NOT NULL,
    name     VARCHAR(100) NOT NULL,
    category VARCHAR(32)  NOT NULL DEFAULT 'ROOM',

    CONSTRAINT uq_amenities_code UNIQUE (code),
    CONSTRAINT ck_amenities_category
        CHECK (category IN ('HOTEL', 'ROOM'))
);

CREATE TABLE hotels
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    owner_user_id  UUID         NOT NULL,
    city_id        UUID         NOT NULL,
    name           VARCHAR(255) NOT NULL,
    slug           VARCHAR(255) NOT NULL,
    description    TEXT,
    address        VARCHAR(255) NOT NULL,
    latitude       DECIMAL(9, 6),
    longitude      DECIMAL(9, 6),
    star_rating    SMALLINT,
    check_in_time  TIME         NOT NULL DEFAULT '14:00:00',
    check_out_time TIME         NOT NULL DEFAULT '12:00:00',
    status         VARCHAR(32)  NOT NULL DEFAULT 'DRAFT',
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_hotels_slug UNIQUE (slug),

    CONSTRAINT fk_hotels_owner
        FOREIGN KEY (owner_user_id) REFERENCES users (id),

    CONSTRAINT fk_hotels_city
        FOREIGN KEY (city_id) REFERENCES cities (id),

    CONSTRAINT ck_hotels_status
        CHECK (status IN (
                          'DRAFT',
                          'PENDING_APPROVAL',
                          'ACTIVE',
                          'REJECTED',
                          'SUSPENDED'
            )),

    CONSTRAINT ck_hotels_star_rating
        CHECK (star_rating BETWEEN 1 AND 5),

    CONSTRAINT ck_hotels_latitude
        CHECK (latitude BETWEEN -90 AND 90),

    CONSTRAINT ck_hotels_longitude
        CHECK (longitude BETWEEN -180 AND 180)
);

CREATE TABLE room_types
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    hotel_id     UUID         NOT NULL,
    name         VARCHAR(100) NOT NULL,
    description  TEXT,
    max_adults   SMALLINT     NOT NULL DEFAULT 2,
    max_children SMALLINT     NOT NULL DEFAULT 0,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_room_types_hotel
        FOREIGN KEY (hotel_id) REFERENCES hotels (id),

    CONSTRAINT ck_room_types_max_adults
        CHECK (max_adults >= 1),

    CONSTRAINT ck_room_types_max_children
        CHECK (max_children >= 0)
);

CREATE TABLE hotel_amenities
(
    hotel_id   UUID NOT NULL,
    amenity_id UUID NOT NULL,

    CONSTRAINT pk_hotel_amenities
        PRIMARY KEY (hotel_id, amenity_id),

    CONSTRAINT fk_hotel_amenities_hotel
        FOREIGN KEY (hotel_id)
            REFERENCES hotels (id) ON DELETE CASCADE,

    CONSTRAINT fk_hotel_amenities_amenity
        FOREIGN KEY (amenity_id)
            REFERENCES amenities (id)
);


CREATE TABLE room_type_amenities
(
    room_type_id UUID NOT NULL,
    amenity_id   UUID NOT NULL,

    CONSTRAINT pk_room_type_amenities
        PRIMARY KEY (room_type_id, amenity_id),

    CONSTRAINT fk_room_type_amenities_room_type
        FOREIGN KEY (room_type_id)
            REFERENCES room_types (id) ON DELETE CASCADE,

    CONSTRAINT fk_room_type_amenities_amenity
        FOREIGN KEY (amenity_id)
            REFERENCES amenities (id)
);
