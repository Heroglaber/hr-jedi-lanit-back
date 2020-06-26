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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lanit.bpm.jedu.hrjedi.model.Employee;
import ru.lanit.bpm.jedu.hrjedi.model.security.Role;
import ru.lanit.bpm.jedu.hrjedi.model.security.RoleName;
import ru.lanit.bpm.jedu.hrjedi.model.security.State;
import ru.lanit.bpm.jedu.hrjedi.repository.EmployeeRepository;
import ru.lanit.bpm.jedu.hrjedi.repository.RoleRepository;
import ru.lanit.bpm.jedu.hrjedi.service.EmployeeService;
import ru.lanit.bpm.jedu.hrjedi.service.exception.EmployeeRegistrationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.lanit.bpm.jedu.hrjedi.model.security.RoleName.ROLE_ADMIN;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private PasswordEncoder passwordEncoder;
    private String headOfHrLogin;
    private RoleRepository roleRepository;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            @Value("${ru.lanit.bpm.jedu.hrjedi.headOfHrLogin}") String headOfHrLogin,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.headOfHrLogin = headOfHrLogin;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Employee findByLogin(String login) {
        return employeeRepository.findByLoginIgnoreCase(login.trim())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with -> username: " + login));
    }

    @Transactional(readOnly = true)
    @Override
    public Employee findWellKnownEmployeeHeadOfHr() {
        return findByLogin(headOfHrLogin);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public long getNumberOfAdmins() {
        return employeeRepository.findAll()
                .stream()
                .filter(user ->
                        user.getRoles()
                                .stream()
                                .anyMatch(role -> ROLE_ADMIN.equals(role.getName()))
                )
                .count();
    }

    @Transactional
    @Override
    public void createEmployee(String login, String firstName, String patronymic, String lastName, String password, String email, Set<String> rolesStrings) {
        String trimmedLoginInLowerCase = login.trim().toLowerCase();

        validateRegisteredLogin(trimmedLoginInLowerCase);
        validateRegisteredEmail(email);

        Employee user = new Employee(trimmedLoginInLowerCase, firstName, patronymic, lastName, passwordEncoder.encode(password), email);
        user.setRoles(validateAndGetRegisteredRoles(rolesStrings));
        user.setState(State.ACTIVE);
        employeeRepository.save(user);
    }

    @Transactional
    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    @Override
    public String getEmployeeFullNameByLogin(String login) {
        return findByLogin(login)
                .getFullName();
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void validateRegisteredLogin(String login) {
        if (employeeRepository.existsByLogin(login)) {
            throw new EmployeeRegistrationException("Employee with this login already exists!");
        }
    }

    private void validateRegisteredEmail(String email) {
        if (employeeRepository.existsByEmail(email)) {
            throw new EmployeeRegistrationException("Employee with this email already exists!");
        }
    }

    private Set<Role> validateAndGetRegisteredRoles(Set<String> rolesStrings) {
        Set<Role> registeredRoles = new HashSet<>();

        rolesStrings.forEach(roleString -> {
                    RoleName registeredRoleName = extractRoleNameFromRoleString(roleString);
                    Role registeredRole = roleRepository.findByName(registeredRoleName)
                            .orElseThrow(() -> new EmployeeRegistrationException("Could not find provided role by role name in the database"));
                    registeredRoles.add(registeredRole);
                }
        );

        return registeredRoles;
    }

    private RoleName extractRoleNameFromRoleString(String roleString) {
        switch (roleString.trim().toLowerCase()) {
            case "admin":
                return RoleName.ROLE_ADMIN;
            case "omni":
                return RoleName.ROLE_OMNI;
            case "hr":
                return RoleName.ROLE_HR;
            case "user":
                return RoleName.ROLE_USER;
            default:
                throw new EmployeeRegistrationException("Invalid role was given for registration");
        }
    }
}
