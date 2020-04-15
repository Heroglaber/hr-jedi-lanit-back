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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lanit.bpm.jedu.hrjedi.model.Employee;
import ru.lanit.bpm.jedu.hrjedi.model.security.Role;
import ru.lanit.bpm.jedu.hrjedi.model.security.RoleName;
import ru.lanit.bpm.jedu.hrjedi.model.security.State;
import ru.lanit.bpm.jedu.hrjedi.repository.EmployeeRepository;
import ru.lanit.bpm.jedu.hrjedi.repository.RoleRepository;
import ru.lanit.bpm.jedu.hrjedi.security.jwt.JwtProvider;
import ru.lanit.bpm.jedu.hrjedi.security.jwt.JwtResponse;
import ru.lanit.bpm.jedu.hrjedi.service.SecurityService;
import ru.lanit.bpm.jedu.hrjedi.service.exception.EmployeeRegistrationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.lanit.bpm.jedu.hrjedi.model.security.RoleName.ROLE_ADMIN;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    private EmployeeRepository employeeRepository;

    @Autowired
    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    @Override
    public void registerEmployee(String login, String firstName, String secondName, String lastName, String password, String email, Set<String> rolesStrings) {
        String trimmedLoginInLowerCase = login.trim().toLowerCase();

        validateRegisteredLogin(trimmedLoginInLowerCase);
        validateRegisteredEmail(email);

        Employee user = new Employee(trimmedLoginInLowerCase, firstName, secondName, lastName, passwordEncoder.encode(password), email);
        user.setRoles(validateAndGetRegisteredRoles(rolesStrings));
        user.setState(State.ACTIVE); // FIXME: Уточнить значение по-умолчанию
        employeeRepository.save(user);
    }

    @Transactional
    @Override
    public JwtResponse authenticateEmployee(String login, String password) {
        String trimmedLoginInLowerCase = login.trim().toLowerCase();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(trimmedLoginInLowerCase, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtProvider.generateJwtToken(authentication);

        return new JwtResponse(jwtToken);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Employee> getAllUsers() {
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
            throw new EmployeeRegistrationException("Employee with this login already exists!");
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
                return  RoleName.ROLE_HR;
            case "user":
                return  RoleName.ROLE_USER;
            default:
                throw new EmployeeRegistrationException("Invalid role was given for registration");
        }
    }
}
