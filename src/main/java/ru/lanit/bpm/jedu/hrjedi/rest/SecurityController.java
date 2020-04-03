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
import ru.lanit.bpm.jedu.hrjedi.model.Employee;
import ru.lanit.bpm.jedu.hrjedi.rest.form.LoginForm;
import ru.lanit.bpm.jedu.hrjedi.rest.form.SignUpForm;
import ru.lanit.bpm.jedu.hrjedi.security.jwt.JwtResponse;
import ru.lanit.bpm.jedu.hrjedi.service.SecurityService;
import ru.lanit.bpm.jedu.hrjedi.service.exception.EmployeeRegistrationException;

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
    public ResponseEntity<JwtResponse> authenticateEmployee(@RequestBody LoginForm loginForm) {
        JwtResponse jwtResponse = securityService.authenticateEmployee(loginForm.getLogin(), loginForm.getPassword());
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerEmployee(@RequestBody SignUpForm signUpRequest) {
        try {
             securityService.registerEmployee(signUpRequest.getLogin(), signUpRequest.getPassword(), signUpRequest.getEmail(), signUpRequest.getRoles());
        } catch (EmployeeRegistrationException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().body("Employee registered successfully!");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('OMNI') or hasRole('ADMIN')")
    public List<Employee> getAllUsers(){
        return securityService.getAllUsers();
    }
}
