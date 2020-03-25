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
import ru.lanit.bpm.jedu.hrjedi.model.security.Role;
import ru.lanit.bpm.jedu.hrjedi.model.security.RoleName;
import ru.lanit.bpm.jedu.hrjedi.model.security.User;
import ru.lanit.bpm.jedu.hrjedi.repository.RoleRepository;
import ru.lanit.bpm.jedu.hrjedi.repository.UserRepository;
import ru.lanit.bpm.jedu.hrjedi.security.jwt.JwtProvider;
import ru.lanit.bpm.jedu.hrjedi.security.jwt.JwtResponse;
import ru.lanit.bpm.jedu.hrjedi.service.SecurityService;
import ru.lanit.bpm.jedu.hrjedi.service.exception.UserRegistrationException;

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

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void registerUser(String login, String password, String email, Set<String> rolesStrings) {
        String trimmedLoginInLowerCase = login.trim().toLowerCase();

        validateRegisteredLogin(trimmedLoginInLowerCase);
        validateRegisteredEmail(email);

        User user = new User(trimmedLoginInLowerCase, passwordEncoder.encode(password), email);
        user.setRoles(validateAndGetRegisteredRoles(rolesStrings));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public JwtResponse authenticateUser(String login, String password) {
        String trimmedLoginInLowerCase = login.trim().toLowerCase();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(trimmedLoginInLowerCase, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtProvider.generateJwtToken(authentication);

        return new JwtResponse(jwtToken);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public long getNumberOfAdmins() {
        return userRepository.findAll()
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
        if (userRepository.existsByLogin(login)) {
            throw new UserRegistrationException("User with this login already exists!");
        }
    }

    private void validateRegisteredEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserRegistrationException("User with this login already exists!");
        }
    }

    private Set<Role> validateAndGetRegisteredRoles(Set<String> rolesStrings) {
        Set<Role> roles = new HashSet<>();

        rolesStrings.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new UserRegistrationException("Invalid role was given to registration"));
                    roles.add(adminRole);

                    break;
                case "omni":
                    Role omniRole = roleRepository.findByName(RoleName.ROLE_OMNI)
                            .orElseThrow(() -> new UserRegistrationException("Invalid role was given to registration"));
                    roles.add(omniRole);

                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new UserRegistrationException("Invalid role was given to registration"));
                    roles.add(userRole);
            }
        });

        return roles;
    }
}
