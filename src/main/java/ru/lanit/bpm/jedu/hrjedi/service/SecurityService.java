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
package ru.lanit.bpm.jedu.hrjedi.service;

import ru.lanit.bpm.jedu.hrjedi.model.Employee;
import ru.lanit.bpm.jedu.hrjedi.security.jwt.JwtResponse;

import java.util.List;
import java.util.Set;

public interface SecurityService {

    void registerEmployee(String login, String firstName, String secondName, String lastName, String password, String email, Set<String> rolesStrings);

    JwtResponse authenticateEmployee(String login, String password);

    List<Employee> getAllUsers();

    long getNumberOfAdmins();
}
