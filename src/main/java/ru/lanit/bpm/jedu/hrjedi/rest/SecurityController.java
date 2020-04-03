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
package ru.lanit.bpm.jedu.hrjedi.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.lanit.bpm.jedu.hrjedi.model.security.User;
import ru.lanit.bpm.jedu.hrjedi.rest.form.LoginForm;
import ru.lanit.bpm.jedu.hrjedi.rest.form.SignUpForm;
import ru.lanit.bpm.jedu.hrjedi.security.jwt.JwtResponse;
import ru.lanit.bpm.jedu.hrjedi.service.SecurityService;
import ru.lanit.bpm.jedu.hrjedi.service.exception.UserRegistrationException;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/hr-rest/security")
public class SecurityController {
    private SecurityService securityService;

    @Autowired
    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody LoginForm loginForm) {
        JwtResponse jwtResponse = securityService.authenticateUser(loginForm.getLogin(), loginForm.getPassword());
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody SignUpForm signUpRequest) {
        try {
            securityService.registerUser(signUpRequest.getLogin(), signUpRequest.getPassword(), signUpRequest.getEmail(), signUpRequest.getRoles());
        } catch (UserRegistrationException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().body("User registered successfully!");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('OMNI') or hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return securityService.getAllUsers();
    }
}
