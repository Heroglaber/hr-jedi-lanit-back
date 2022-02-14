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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.lanit.bpm.jedu.hrjedi.adapter.restservice.dto.EmployeeDto;
import ru.lanit.bpm.jedu.hrjedi.adapter.restservice.dto.SignUpFormDto;
import ru.lanit.bpm.jedu.hrjedi.app.api.employee.EmployeeRegistrationException;
import ru.lanit.bpm.jedu.hrjedi.app.api.employee.EmployeeService;
import ru.lanit.bpm.jedu.hrjedi.domain.Employee;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @PostMapping(value="/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('OMNI') or hasRole('ADMIN')")
    public ResponseEntity<String> uploadEmployees(@RequestPart("userFile") MultipartFile document) {
        List<EmployeeDto> employeeDtoList;
        try {
            employeeDtoList = mapJsonToEmployeeDtos(document);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error while parsing json file.");
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body("Json validation exception.");
        }
        if(checkDuplicateEmployeesDto(employeeDtoList)) {
            return ResponseEntity.badRequest().body("Json contains login duplicates.");
        }
        try {
            employeeService.createEmployees(employeeDtoList);
        } catch (EmployeeRegistrationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok("Employees uploaded successfully.");
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

    private static List<EmployeeDto> mapJsonToEmployeeDtos(MultipartFile document) throws IOException {
        String json = new String(document.getBytes(), StandardCharsets.UTF_8);
        validateJson(json);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = (ObjectNode) objectMapper.readTree(json);
        ArrayNode array = (ArrayNode) root.get("users");
        return objectMapper.readValue(array.toString(), new TypeReference<List<EmployeeDto>>() {});
    }

    private static boolean checkDuplicateEmployeesDto(List<EmployeeDto> inputList){
        Set inputSet = new HashSet(inputList);
        return inputSet.size() < inputList.size();
    }

    private static void validateJson(String json) throws IOException {
        final String schemaName = "employeeSchema.json";

        Path schemaPath = Paths.get("target", "classes", "documents", schemaName);
        InputStream schemaStream = Files.newInputStream(schemaPath);
        JSONObject jsonSchema = new JSONObject(new JSONTokener(schemaStream));
        JSONObject jsonSubject = new JSONObject(
            new JSONTokener(new ByteArrayInputStream(json.getBytes())));

        Schema schema = SchemaLoader.load(jsonSchema);
        schema.validate(jsonSubject);
    }
}
