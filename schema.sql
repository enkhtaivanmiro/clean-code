-- Drop tables if they exist (for clean seed)
DROP TABLE IF EXISTS inventory CASCADE;
DROP TABLE IF EXISTS sale_item CASCADE;
DROP TABLE IF EXISTS sales CASCADE;
DROP TABLE IF EXISTS discount_rule CASCADE;
DROP TABLE IF EXISTS payment_type CASCADE;
DROP TABLE IF EXISTS pos_terminal CASCADE;
DROP TABLE IF EXISTS branch CASCADE;
DROP TABLE IF EXISTS product_price CASCADE;
DROP TABLE IF EXISTS barcode CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS category CASCADE;

-- 1. Category
CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 2. Product
CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_id INTEGER NOT NULL REFERENCES category(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 3. Barcode
CREATE TABLE barcode (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES product(id),
    code VARCHAR(50) NOT NULL UNIQUE
);

-- 4. Product Price
CREATE TABLE product_price (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES product(id),
    price NUMERIC(12,2) NOT NULL CHECK(price >= 0),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(product_id, updated_at)
);

-- 5. Branch
CREATE TABLE branch (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    address VARCHAR(300) NOT NULL
);

-- 6. POS Terminal
CREATE TABLE pos_terminal (
    id SERIAL PRIMARY KEY,
    branch_id INTEGER NOT NULL REFERENCES branch(id),
    name VARCHAR(50) NOT NULL
);

-- 7. Payment Type
CREATE TABLE payment_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 8. Discount Rule
CREATE TABLE discount_rule (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK(type IN ('PERCENT','FIXED')),
    value NUMERIC(12,2) NOT NULL CHECK(value >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 9. Sales
CREATE TABLE sales (
    id UUID PRIMARY KEY,
    branch_id INTEGER NOT NULL REFERENCES branch(id),
    pos_id INTEGER NOT NULL REFERENCES pos_terminal(id),
    payment_type_id INTEGER NOT NULL REFERENCES payment_type(id),
    total_amount NUMERIC(14,2) NOT NULL CHECK(total_amount >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    synced BOOLEAN NOT NULL DEFAULT FALSE
);

-- 10. Sale Item
CREATE TABLE sale_item (
    id SERIAL PRIMARY KEY,
    sale_id UUID NOT NULL REFERENCES sales(id),
    product_id INTEGER NOT NULL REFERENCES product(id),
    discount_rule_id INTEGER REFERENCES discount_rule(id),
    quantity INTEGER NOT NULL CHECK(quantity > 0),
    price NUMERIC(12,2) NOT NULL CHECK(price >= 0),
    discount_amount NUMERIC(12,2) DEFAULT 0
);

-- 11. Inventory
CREATE TABLE inventory (
    id SERIAL PRIMARY KEY,
    branch_id INTEGER REFERENCES branch(id),
    product_id INTEGER REFERENCES product(id),
    quantity INTEGER NOT NULL CHECK(quantity >= 0)
);

INSERT INTO category (name) VALUES ('Grocery'), ('Electronics');

INSERT INTO product (name, category_id) VALUES ('Milk', 1), ('Bread', 1), ('Phone', 2), ('Laptop', 2), ('Apple', 1);

INSERT INTO barcode (product_id, code) VALUES (1, '1001'), (2, '1002'), (3, '2001'), (4, '2002'), (5, '1003');

INSERT INTO product_price (product_id, price) VALUES (1, 3500.00), (2, 1500.00), (3, 1200000.00), (4, 2500000.00), (5, 500.00);

INSERT INTO branch (name, address) VALUES ('Zaisan Branch', 'Khan-Uul District, Ulaanbaatar');

INSERT INTO pos_terminal (branch_id, name) VALUES (1, 'POS-01'), (1, 'POS-02');

INSERT INTO payment_type (name) VALUES ('CASH'), ('CARD'), ('QR');

INSERT INTO inventory (branch_id, product_id, quantity) VALUES (1, 1, 100), (1, 2, 50), (1, 3, 10), (1, 4, 5), (1, 5, 200);
