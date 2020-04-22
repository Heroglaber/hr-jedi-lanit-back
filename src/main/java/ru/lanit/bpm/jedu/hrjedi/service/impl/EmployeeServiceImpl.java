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
package ru.lanit.bpm.jedu.hrjedi.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.lanit.bpm.jedu.hrjedi.model.Employee;
import ru.lanit.bpm.jedu.hrjedi.repository.EmployeeRepository;
import ru.lanit.bpm.jedu.hrjedi.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;

    private String headOfHrLogin;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, @Value("${ru.lanit.bpm.jedu.hrjedi.headOfHrLogin}") String headOfHrLogin) {
        this.employeeRepository = employeeRepository;
        this.headOfHrLogin = headOfHrLogin;
    }

    @Override
    public Employee findByLogin(String login) {
        return employeeRepository.findByLoginIgnoreCase(login.trim())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with -> username: " + login));
    }

    @Override
    public Employee findWellKnownEmployeeHeadOfHr() {
        return findByLogin(headOfHrLogin);
    }
}
