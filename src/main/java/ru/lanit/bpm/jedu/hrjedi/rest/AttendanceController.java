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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lanit.bpm.jedu.hrjedi.model.Attendance;
import ru.lanit.bpm.jedu.hrjedi.service.AttendanceService;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/hr-rest/attendances")
public class AttendanceController {
    private AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/employees/{id}")
    @PreAuthorize("hasRole('OMNI') or hasRole('HR')")
    public List<Attendance> getAllAttendancesByEmployeeId(@PathVariable("id") Long employeeId) {
        Assert.notNull(employeeId, "Employee id cannot be null");
        return attendanceService.findAllByEmployeeId(employeeId);
    }

    @GetMapping("/monthsWithoutInfo/{year}")
    @PreAuthorize("hasRole('OMNI') or hasRole('HR')")
    public List<YearMonth> getMonthsWithoutAttendanceInfoByYear(@PathVariable("year") Integer year) {
        Assert.notNull(year, "Provided year cannot be null");
        return attendanceService.getMonthsWithoutAttendanceInfoByYear(year);
    }
}
