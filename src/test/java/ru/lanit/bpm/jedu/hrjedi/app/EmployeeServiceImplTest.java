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
package ru.lanit.bpm.jedu.hrjedi.app;

import org.easymock.EasyMock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.employee.EmployeeRepository;
import ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.employee.RoleRepository;
import ru.lanit.bpm.jedu.hrjedi.app.api.employee.EmployeeService;
import ru.lanit.bpm.jedu.hrjedi.app.impl.employee.EmployeeServiceImpl;
import ru.lanit.bpm.jedu.hrjedi.domain.Employee;
import ru.lanit.bpm.jedu.hrjedi.domain.security.Role;
import ru.lanit.bpm.jedu.hrjedi.domain.security.RoleName;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static ru.lanit.bpm.jedu.hrjedi.domain.security.RoleName.*;

@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class EmployeeServiceImplTest {
    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    RoleRepository roleRepository;
    @TestSubject
    EmployeeService employeeService;

    @Before
    public void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, "HeadOfHrLogin", passwordEncoder, roleRepository);
    }

    @Test
    public void getNumberOfAdmins() {
        EasyMock.expect(employeeRepository.findAll())
            .andReturn(asList(
                user(singleton(role(ROLE_USER))),
                user(singleton(role(ROLE_OMNI))),
                user(asList(role(ROLE_USER), role(ROLE_OMNI))),
                user(asList(role(ROLE_USER), role(ROLE_ADMIN))),
                user(singleton(role(ROLE_ADMIN)))

            ));
        EasyMockUnitils.replay();

        long numberOfAdmins = employeeService.getNumberOfAdmins();

        assertEquals(2, numberOfAdmins);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private Employee user(Collection<Role> roles) {
        Employee user = new Employee();
        user.setRoles(new HashSet<>(roles));

        return user;
    }

    private Role role(RoleName name) {
        Role role = new Role();
        role.setName(name);

        return role;
    }
}
