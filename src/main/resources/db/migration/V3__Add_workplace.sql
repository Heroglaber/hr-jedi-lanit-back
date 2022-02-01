CREATE TABLE WORKPLACE
(
    id NUMBER PRIMARY KEY,
    office_id NUMBER NOT NULL REFERENCES OFFICE,
    employee_id NUMBER UNIQUE REFERENCES EMPLOYEE,
    floor INTEGER NOT NULL,
    room INTEGER NOT NULL,
	table_num INTEGER NOT NULL,
	CONSTRAINT positive_floor CHECK (floor > 0),
	CONSTRAINT positive_room CHECK (room > 0),
	CONSTRAINT positive_table_num  CHECK (table_num > 0),
	CONSTRAINT unique_workplace UNIQUE(office_id, floor, room, table_num)
);

CREATE SEQUENCE sq_workplace_id START WITH 1 INCREMENT BY 1;


INSERT INTO WORKPLACE (id, office_id, employee_id, floor, room, table_num)
SELECT val.id, o.id, e.id, val.floor, val.room, val.table_num
FROM (
   VALUES
      (next value for sq_workplace_id, 'Нижний Новгород', 'ivanov', 7, 711, 7)
	, (next value for sq_workplace_id, 'Нижний Новгород', NULL, 7, 711, 8)
	, (next value for sq_workplace_id, 'Нижний Новгород', NULL, 7, 711, 9)
	, (next value for sq_workplace_id, 'Нижний Новгород', NULL, 7, 711, 10)
	, (next value for sq_workplace_id, 'Нижний Новгород', NULL, 7, 711, 11)
    , (next value for sq_workplace_id, 'Москва', 'petrov', 3, 324, 1)
    , (next value for sq_workplace_id, 'Москва', 'sergeev', 3, 324, 2)
    , (next value for sq_workplace_id, 'Москва', NULL, 3, 324, 3)
	, (next value for sq_workplace_id, 'Москва', NULL, 3, 324, 4)
	, (next value for sq_workplace_id, 'Москва', NULL, 3, 324, 5)
   ) val (id, office_name, employee_login, floor, room, table_num)
LEFT JOIN OFFICE o ON (val.office_name = o.name)
LEFT JOIN EMPLOYEE e on (val.employee_login = e.login);