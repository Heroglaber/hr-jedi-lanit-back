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
package ru.lanit.bpm.jedu.hrjedi.adapter.restservice.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.lanit.bpm.jedu.hrjedi.adapter.restservice.dto.SignUpFormDto;
import ru.lanit.bpm.jedu.hrjedi.app.api.employee.EmployeeRegistrationException;
import ru.lanit.bpm.jedu.hrjedi.app.api.employee.EmployeeService;
import ru.lanit.bpm.jedu.hrjedi.domain.Employee;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/hr-rest/employees")
public class EmployeeController {
    private EmployeeService employeeService;
    private ServletContext servletContext;

    public EmployeeController(EmployeeService employeeService, ServletContext servletContext) {
        this.employeeService = employeeService;
        this.servletContext = servletContext;
    }

    @PostMapping("/current/update-email")
    public ResponseEntity<String> updateEmail(@RequestAttribute String currentUser, @RequestBody String email) {
        if (StringUtils.isEmpty(email)) {
            return ResponseEntity.badRequest().body("Email is empty");
        } else if (!email.matches("^[a-zA-Z0-9А-яЁё.-]+@[a-zA-Z0-9А-яЁё.-]+$")){
            return ResponseEntity.badRequest().body("Incorrect email format");
        } else {
            Employee employee = employeeService.findByLogin(currentUser);
            employee.setEmail(email);
            employeeService.save(employee);
            return ResponseEntity.ok("Email changed!");
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('OMNI') or hasRole('ADMIN')")
    public ResponseEntity<String> createEmployee(@RequestBody SignUpFormDto signUpRequest) {
        try {
            employeeService.createEmployee(
                signUpRequest.getLogin(),
                signUpRequest.getFirstName(),
                signUpRequest.getPatronymic(),
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
    public ResponseEntity<byte[]> getAvatar(@RequestAttribute String currentUser) {
        Path avatarPath = Paths.get("target", "classes", "images", currentUser + ".png");
        try {
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(servletContext.getMimeType(avatarPath.toAbsolutePath().toString())))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + avatarPath.getFileName() + "\"")
                .body(Files.readAllBytes(avatarPath));
        } catch (IOException e) {
            return ResponseEntity.ok().body(null);
        }
    }

    @PostMapping(value = "/current/avatar", produces = {MediaType.IMAGE_PNG_VALUE, "application/json"})
    public ResponseEntity<String> uploadAvatar(@RequestAttribute String currentUser, @RequestParam("imageFile") MultipartFile file) {
        Path avatarPath = Paths.get("target", "classes", "images", currentUser + ".png");
        try {
            Files.write(avatarPath, file.getBytes());
            return ResponseEntity.ok("Avatar uploaded successfully.");
        } catch (IOException  ex) {
            return ResponseEntity.badRequest().body("Avatar is not uploaded");
        }
    }

    @GetMapping("/generate-pass")
    public String generateSecurePassword() {
        return employeeService.generateSecurePassword();
    }
}
