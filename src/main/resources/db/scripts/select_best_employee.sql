select EMPLOYEE.LOGIN,
       OFFICE.NAME,
       SUM(CASE
               WHEN ROUND(DATEDIFF(second, ATTENDANCE.ENTRANCE_TIME, ATTENDANCE.EXIT_TIME) / 3600.00, 2) < 9
                   THEN -1
               WHEN ROUND(DATEDIFF(second, ATTENDANCE.ENTRANCE_TIME, ATTENDANCE.EXIT_TIME) / 3600.00, 2) < 10
                   THEN 0
               WHEN ROUND(DATEDIFF(second, ATTENDANCE.ENTRANCE_TIME, ATTENDANCE.EXIT_TIME) / 3600.00, 2) IS NULL
                   THEN 0
               ELSE 1
           END) AS score --насколько сотрудник молодец
from employee
         left join ATTENDANCE on EMPLOYEE.ID = ATTENDANCE.EMPLOYEE_ID
         left join OFFICE on ATTENDANCE.OFFICE_ID = OFFICE.ID
         join VACATION on VACATION.EMPLOYEE_ID = EMPLOYEE.ID
where ATTENDANCE.ENTRANCE_TIME BETWEEN PARSEDATETIME('14.01.2020 08:59:00', 'dd.MM.yyyy hh:mm:ss')
    and PARSEDATETIME('15.03.2020 08:59', 'dd.MM.yyyy hh:mm:ss')
  and score >= 0
group by employee.login
;