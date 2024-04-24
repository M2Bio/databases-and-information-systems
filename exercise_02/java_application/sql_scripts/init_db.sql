-- SQL Script for creating real estate database tables with PostgreSQL

-- Creating 'estate_agent' table
CREATE TABLE estate_agent
(
    agent_id SERIAL PRIMARY KEY,
    name     VARCHAR(255)        NOT NULL,
    address  VARCHAR(255),
    login    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL
);

-- Creating 'estate' table
CREATE TABLE estate
(
    estate_id     SERIAL PRIMARY KEY,
    city          VARCHAR(255) NOT NULL,
    postal_code   VARCHAR(10)  NOT NULL,
    street        VARCHAR(255) NOT NULL,
    street_number VARCHAR(10)  NOT NULL,
    square_area   NUMERIC      NOT NULL,
    agent_id      INT          NOT NULL,
    FOREIGN KEY (agent_id) REFERENCES estate_agent (agent_id)
);

-- Creating 'person' table
CREATE TABLE person
(
    person_id  SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    address    VARCHAR(255) NOT NULL
);

-- Creating 'house' table (inherits 'estate')
CREATE TABLE house
(
    estate_id INT PRIMARY KEY REFERENCES estate (estate_id),
    floors    INT     NOT NULL,
    price     NUMERIC NOT NULL,
    garden    BOOLEAN NOT NULL,
    owner_id  INT     NULL,
    FOREIGN KEY (owner_id) REFERENCES person (person_id)
);

-- Creating 'apartment' table (inherits 'estate')
CREATE TABLE apartment
(
    estate_id        INT PRIMARY KEY REFERENCES estate (estate_id),
    floor            INT     NOT NULL,
    rent             NUMERIC NOT NULL,
    rooms            INT     NOT NULL,
    balcony          BOOLEAN NOT NULL,
    built_in_kitchen BOOLEAN NOT NULL,
    renter_id        INT     NULL,
    FOREIGN KEY (renter_id) REFERENCES person (person_id)
);

-- Creating 'contract' table
CREATE TABLE contract
(
    contract_id SERIAL PRIMARY KEY,
    contract_no VARCHAR(50) UNIQUE NOT NULL,
    date        DATE               NOT NULL,
    place       VARCHAR(255)       NOT NULL
);

-- Creating 'tenancy_contract' table (inherits 'contract')
CREATE TABLE tenancy_contract
(
    contract_id      INT PRIMARY KEY REFERENCES contract (contract_id),
    start_date       DATE       NOT NULL,
    duration         INT        NOT NULL, -- Duration in months
    additional_costs NUMERIC    NOT NULL,
    apartment_id     INT UNIQUE NOT NULL,
    renter_id        INT        NOT NULL,
    FOREIGN KEY (apartment_id) REFERENCES apartment (estate_id),
    FOREIGN KEY (renter_id) REFERENCES person (person_id)
);

-- Creating 'purchase_contract' table (inherits 'contract')
CREATE TABLE purchase_contract
(
    contract_id        INT PRIMARY KEY REFERENCES contract (contract_id),
    no_of_installments INT        NOT NULL,
    interest_rate      NUMERIC    NOT NULL,
    house_id           INT UNIQUE NOT NULL,
    buyer_id           INT        NOT NULL,
    FOREIGN KEY (house_id) REFERENCES house (estate_id),
    FOREIGN KEY (buyer_id) REFERENCES person (person_id)
);

-- Enable foreign key constraints
SET CONSTRAINTS ALL IMMEDIATE;

-- Inserting sample data for an estate agent
INSERT INTO estate_agent (name, address, login, password)
VALUES ('John Doe', '1234 Boulevard Ave', 'johndoe', 'securepassword123');

-- Inserting sample data for a house
INSERT INTO estate (city, postal_code, street, street_number, square_area, agent_id)
VALUES ('New York', '10001', 'Broadway', '123', 200, 1);

-- Inserting sample data for a person
INSERT INTO person (first_name, last_name, address)
VALUES ('Jane', 'Doe', '5678 Main St');

-- Inserting sample data for a house
INSERT INTO house (estate_id, floors, price, garden, owner_id)
VALUES (1, 2, 500000, TRUE, 1);

-- Inserting sample data for a contract
INSERT INTO contract (contract_no, date, place)
VALUES ('12345', '2021-01-01', 'New York');

-- Inserting sample data for a purchase contract
INSERT INTO purchase_contract (contract_id, no_of_installments, interest_rate, house_id, buyer_id)
VALUES (1, 12, 3.5, 1, 1);

-- Inserting sample data for an apartment
INSERT INTO estate (city, postal_code, street, street_number, square_area, agent_id)
VALUES ('Los Angeles', '90001', 'Sunset Blvd', '456', 100, 1);

-- Inserting sample data for an apartment
INSERT INTO apartment (estate_id, floor, rent, rooms, balcony, built_in_kitchen, renter_id)
VALUES (2, 3, 2000, 3, TRUE, TRUE, 1);

-- Inserting sample data for a contract
INSERT INTO contract (contract_no, date, place)
VALUES ('54321', '2021-02-01', 'Los Angeles');

-- Inserting sample data for a tenancy contract
INSERT INTO tenancy_contract (contract_id, start_date, duration, additional_costs, apartment_id, renter_id)
VALUES (2, '2021-03-01', 24, 100, 2, 1);