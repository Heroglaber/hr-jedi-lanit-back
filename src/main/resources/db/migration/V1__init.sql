CREATE TABLE ROLE
(
    id   NUMBER PRIMARY KEY,
    name varchar2(50) NOT NULL UNIQUE
);

CREATE TABLE USER_ACCOUNT
(
    id            NUMBER PRIMARY KEY,
    login         VARCHAR2(255) NOT NULL UNIQUE,
    hash_password VARCHAR2(255) NOT NULL,
    email         VARCHAR(255)  NOT NULL UNIQUE,
    state         VARCHAR2(10)  NOT NULL
);

CREATE TABLE USER_ROLE
(
    user_id NUMBER NOT NULL,
    role_id NUMBER NOT NULL
);

INSERT INTO ROLE VALUES (1, 'ROLE_ADMIN');
INSERT INTO ROLE VALUES (2, 'ROLE_OMNI');
INSERT INTO ROLE VALUES (3, 'ROLE_USER');

-- LOGIN:PASSWORD:
-- admin:admin
-- omni:omni
-- user:user
INSERT INTO USER_ACCOUNT
VALUES (1, 'admin', '$2a$04$l6jf/IelD8EcKEx0z5LJFur01DtdBcTLUxfiq79X1GF2hgJdmIeEW', 'admin@hrjedu.com', 'ACTIVE'),
       (2, 'omni', '$2a$04$y1oDipZIlwPEGBPtTrXT4.9enhB4zuQoQKstVueSSmK9qqdY.yU6y', 'omni@hrjedu.com', 'ACTIVE'),
       (3, 'user', '$2a$04$uUTMuVyvusd6gIkxLdrF5ufDQ0K359C0Pjq6yBtbctOo3y6mhpwiy', 'user@hrjedu.com', 'ACTIVE');

INSERT INTO USER_ROLE
VALUES (1, 1), (2, 2), (3, 3);