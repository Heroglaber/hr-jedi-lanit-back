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

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.lanit.bpm.jedu.hrjedi.model.Employee;
import ru.lanit.bpm.jedu.hrjedi.rest.form.SignUpForm;
import ru.lanit.bpm.jedu.hrjedi.service.EmployeeService;
import ru.lanit.bpm.jedu.hrjedi.service.SecurityService;
import ru.lanit.bpm.jedu.hrjedi.service.exception.EmployeeRegistrationException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/hr-rest/employees")
public class EmployeeController {
    private SecurityService securityService;
    private EmployeeService employeeService;

    public EmployeeController(SecurityService securityService, EmployeeService employeeService) {
        this.securityService = securityService;
        this.employeeService = employeeService;
    }

    @PostMapping("/current/update-email")
    public ResponseEntity<String> updateEmail(@RequestBody String email) {
        if (StringUtils.isEmpty(email)) {
            return ResponseEntity.badRequest().body("Email is empty");
        } else {
            Employee employee = securityService.getCurrentEmployee();
            employee.setEmail(email);
            employeeService.save(employee);
            return ResponseEntity.ok("Email changed!");
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('OMNI') or hasRole('ADMIN')")
    public ResponseEntity<String> createEmployee(@RequestBody SignUpForm signUpRequest) {
        try {
            employeeService.createEmployee(
                signUpRequest.getLogin(),
                signUpRequest.getFirstName(),
                signUpRequest.getSecondName(),
                signUpRequest.getLastName(),
                signUpRequest.getPassword(),
                signUpRequest.getEmail(),
                signUpRequest.getRoles());
        } catch (EmployeeRegistrationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        return ResponseEntity.ok("Employee registered successfully!");
    }

    @GetMapping("/{login}")
    public Employee findEmployee(@PathVariable String login) {
        return employeeService.findByLogin(login);
    }

    @GetMapping()
    @PreAuthorize("hasRole('OMNI') or hasRole('ADMIN')")
    public List<Employee> getAll() {
        return employeeService.getAll();
    }

    @GetMapping("/{employeeLogin}/fullName")
    @PreAuthorize("hasRole('OMNI') or hasRole('ADMIN') or hasRole('HR') or hasRole('USER')")
    public String getEmployeeFullNameByLogin(@PathVariable("employeeLogin") String employeeLogin) {
        return employeeService.getEmployeeFullNameByLogin(employeeLogin);
    }

    @GetMapping("/current/avatar")
    public ResponseEntity<byte[]> getAvatar(HttpServletRequest request) {
        String login = securityService.getCurrentEmployee().getLogin();
        Path avatarPath = Paths.get("target", "classes", "images", login + ".png");
        try {
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(request.getServletContext().getMimeType(avatarPath.toAbsolutePath().toString())))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + avatarPath.getFileName() + "\"")
                .body(Files.readAllBytes(avatarPath));
        } catch (IOException e) {
            return ResponseEntity.ok().body(null);
        }
    }
}
