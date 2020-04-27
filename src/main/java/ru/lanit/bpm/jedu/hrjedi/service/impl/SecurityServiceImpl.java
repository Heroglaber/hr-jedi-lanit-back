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

import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lanit.bpm.jedu.hrjedi.model.Employee;
import ru.lanit.bpm.jedu.hrjedi.security.jwt.JwtProvider;
import ru.lanit.bpm.jedu.hrjedi.security.jwt.JwtResponse;
import ru.lanit.bpm.jedu.hrjedi.security.service.UserPrinciple;
import ru.lanit.bpm.jedu.hrjedi.service.EmployeeService;
import ru.lanit.bpm.jedu.hrjedi.service.SecurityService;

import java.lang.reflect.InvocationTargetException;

import static java.util.Arrays.asList;

@Service
public class SecurityServiceImpl implements SecurityService {
    final AuthenticationManager authenticationManager;
    final JwtProvider jwtProvider;
    final EmployeeService employeeService;

    public SecurityServiceImpl(AuthenticationManager authenticationManager, JwtProvider jwtProvider,
        EmployeeService employeeService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.employeeService = employeeService;
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
    public Employee getCurrentEmployee() {
        UserPrinciple user = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeService.findByLogin(user.getUsername());
    }

    /**
     * Legacy code used to load classes by reflection
     *
     * @return secure password
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public String generateSecurePassword() {
        try {
            Class digitsRuleClass = getClass().getClassLoader().loadClass("org.passay.DigitCharacterRule");
            Class loweCharsRuleClass = getClass().getClassLoader().loadClass("org.passay.LowercaseCharacterRule");
            Class upperCharsRuleClass = getClass().getClassLoader().loadClass("org.passay.UppercaseCharacterRule");
            Class passwordGeneratorClass = getClass().getClassLoader().loadClass("org.passay.PasswordGenerator");
            Object digits = digitsRuleClass.getConstructor(int.class).newInstance(2);
            Object lowerChars = loweCharsRuleClass.getConstructor(int.class).newInstance(4);
            Object upperChars = upperCharsRuleClass.getConstructor(int.class).newInstance(2);
            Object passwordGenerator = passwordGeneratorClass.newInstance();
            return (String) MethodUtils.invokeMethod(passwordGenerator, "generatePassword", 8, asList(digits, lowerChars, upperChars));
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to load library", e);
        }
    }
}
