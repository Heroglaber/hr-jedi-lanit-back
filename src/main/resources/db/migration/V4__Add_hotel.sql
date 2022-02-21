CREATE TABLE HOTEL
(
    id NUMBER PRIMARY KEY,
    name VARCHAR2(255) NOT NULL UNIQUE,
    price INTEGER NOT NULL,
    CONSTRAINT positive_price CHECK (price > 0)
);

CREATE SEQUENCE sq_hotel_id START WITH 1 INCREMENT BY 1;


INSERT INTO HOTEL (id, name, price)
VALUES
(next value for sq_hotel_id, 'Хостел "Лачуга"', 300)
     , (next value for sq_hotel_id, 'Отель "Приличное место"', 700)
     , (next value for sq_hotel_id, 'Отель "Отдых джедая"', 1200)
     , (next value for sq_hotel_id, 'Отель "Лакшери"', 2000)
     , (next value for sq_hotel_id, 'Отель "Императорский"', 5000);

CREATE TABLE BUSINESS_TRIP
(
    id NUMBER PRIMARY KEY,
    employee_id NUMBER NOT NULL,
    start DATE,
    end DATE,
    budget INTEGER
);

CREATE SEQUENCE sq_business_trip_id START WITH 1 INCREMENT BY 1;