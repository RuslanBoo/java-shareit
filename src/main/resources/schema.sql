CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY,
    name  VARCHAR(100),
    email VARCHAR(320),
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    description  VARCHAR(1000),
    requestor_id BIGINT,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_requests_to_users FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    name         VARCHAR(100),
    description  VARCHAR(1000),
    is_available BOOLEAN,
    owner_id     BIGINT,
    request_id   BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_items_to_users FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT fk_items_to_requests FOREIGN KEY (request_id) REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(100),
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_bookings_to_users FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY,
    author_text VARCHAR(100),
    item_id     BIGINT,
    author_id   BIGINT,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT fk_comments_to_items FOREIGN KEY (item_id) REFERENCES items (id)
);