/*
 * Copyright (c) 2008-2020
 * LANIT
 * All rights reserved.
 *
 * This product and related documentation are protected by copyright and
 * distributed under licenses restricting its use, copying, distribution, and
 * decompilation. No part of this product or related documentation may be
 * reproduced in any form by any means without prior written authorization of
 * LANIT and its licensors, if any.
 *
 * $
 */
package ru.lanit.bpm.jedu.hrjedi.app.api.employee;

import ru.lanit.bpm.jedu.hrjedi.domain.Employee;

import java.util.List;
import java.util.Set;

public interface EmployeeService {
    Employee findByLogin(String login);

    Employee findWellKnownEmployeeHeadOfHr();

    String getEmployeeFullNameByLogin(String login);

    List<Employee> getAll();

    long getNumberOfAdmins();

    Employee save(Employee employee);

    void createEmployee(String login, String firstName, String patronymic, String lastName, String password, String email, Set<String> rolesStrings);

    String generateSecurePassword();
}
