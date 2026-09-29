CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS product (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL CHECK (char_length(name) > 1),
    description VARCHAR(500) CHECK (char_length(description) >= 5),
    price NUMERIC(10, 2) NOT NULL CHECK (price > 0),
    image_url VARCHAR(200) CHECK (char_length(image_url) > 0),
    stock_level INT NOT NULL DEFAULT 1 CHECK (stock_level >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);