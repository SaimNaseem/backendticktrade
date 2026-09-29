CREATE TABLE product
(
    id           UUID           NOT NULL,
    name         VARCHAR(50)    NOT NULL,
    description  VARCHAR(500),
    price        DECIMAL(10, 2) NOT NULL,
    image_url    VARCHAR(200),
    stock_level  INTEGER        NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    deleted_at   TIMESTAMP WITHOUT TIME ZONE,
    is_published BOOLEAN,
    CONSTRAINT pk_product PRIMARY KEY (id)
);