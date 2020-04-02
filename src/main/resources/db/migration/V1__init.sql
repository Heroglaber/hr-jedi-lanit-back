CREATE TABLE ROLE
(
    id   NUMBER PRIMARY KEY,
    name varchar2(50) NOT NULL UNIQUE
);
CREATE SEQUENCE sq_role_id START WITH 1 INCREMENT BY 1;

CREATE TABLE EMPLOYEE
(
    id            NUMBER PRIMARY KEY,
    login         VARCHAR2(255) NOT NULL UNIQUE,
    hash_password VARCHAR2(255) NOT NULL,
    email         VARCHAR(255)  NOT NULL UNIQUE,
    state         VARCHAR2(10)  NOT NULL
);
CREATE SEQUENCE sq_employee_id START WITH 1 INCREMENT BY 1;

CREATE TABLE EMPLOYEE_ROLE
(
    employee_id NUMBER NOT NULL,
    role_id NUMBER NOT NULL
);

INSERT INTO ROLE VALUES (next value for sq_role_id, 'ROLE_ADMIN');
INSERT INTO ROLE VALUES (next value for sq_role_id, 'ROLE_OMNI');
INSERT INTO ROLE VALUES (next value for sq_role_id, 'ROLE_USER');
INSERT INTO ROLE VALUES (next value for sq_role_id, 'ROLE_HR');

-- LOGIN:PASSWORD:
-- admin:admin
-- omni:omni
-- user:user
-- hr:hrhr
INSERT INTO EMPLOYEE (id, login, hash_password, email, state)
VALUES (next value for sq_employee_id, 'admin', '$2a$04$l6jf/IelD8EcKEx0z5LJFur01DtdBcTLUxfiq79X1GF2hgJdmIeEW', 'admin@hrjedu.com', 'ACTIVE'),
       (next value for sq_employee_id, 'omni', '$2a$04$y1oDipZIlwPEGBPtTrXT4.9enhB4zuQoQKstVueSSmK9qqdY.yU6y', 'omni@hrjedu.com', 'ACTIVE'),
       (next value for sq_employee_id, 'user', '$2a$04$uUTMuVyvusd6gIkxLdrF5ufDQ0K359C0Pjq6yBtbctOo3y6mhpwiy', 'user@hrjedu.com', 'ACTIVE'),
       (next value for sq_employee_id, 'hr', '$2a$10$XSQvfCA/R0pKCL01GUA9T.tQCUKRFfRt3uyOLEEYgCJQOaTN/F8Aa', 'hr@hrjedu.com', 'ACTIVE');

INSERT INTO EMPLOYEE_ROLE
VALUES (1, 1), (2, 2), (3, 3), (4, 4);
