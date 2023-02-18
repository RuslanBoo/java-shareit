CREATE TABLE IF NOT EXISTS users
(
    user_id    BIGINT GENERATED ALWAYS AS IDENTITY,
    name  VARCHAR(100),
    email VARCHAR(320),
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id           BIGINT GENERATED ALWAYS AS IDENTITY,
    description  VARCHAR(1000),
    requestor_id BIGINT,
    date_create             TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_request PRIMARY KEY (request_id),
    CONSTRAINT fk_requests_to_users FOREIGN KEY (requestor_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS items
(
    item_id           BIGINT GENERATED ALWAYS AS IDENTITY,
    name         VARCHAR(100),
    description  VARCHAR(1000),
    is_available BOOLEAN,
    owner_id     BIGINT,
    request_id   BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (item_id),
    CONSTRAINT fk_items_to_users FOREIGN KEY (owner_id) REFERENCES users (user_id),
    CONSTRAINT fk_items_to_requests FOREIGN KEY (request_id) REFERENCES requests (request_id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id         BIGINT GENERATED ALWAYS AS IDENTITY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(20),
    CONSTRAINT pk_booking PRIMARY KEY (booking_id),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) REFERENCES items (item_id),
    CONSTRAINT fk_bookings_to_users FOREIGN KEY (booker_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id          BIGINT GENERATED ALWAYS AS IDENTITY,
    author_text VARCHAR(100),
    item_id     BIGINT,
    author_id   BIGINT,
    CONSTRAINT pk_comment PRIMARY KEY (comment_id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) REFERENCES users (user_id),
    CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (item_id)
);
