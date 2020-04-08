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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lanit.bpm.jedu.hrjedi.model.Attendance;
import ru.lanit.bpm.jedu.hrjedi.repository.AttendanceRepository;
import ru.lanit.bpm.jedu.hrjedi.service.AttendanceService;
import ru.lanit.bpm.jedu.hrjedi.service.DateTimeService;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AttendanceServiceimpl implements AttendanceService {
    private AttendanceRepository attendanceRepository;
    private DateTimeService dateTimeService;

    @Autowired
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    @Autowired
    public void setAttendanceRepository(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional(readOnly=true)
    @Override
    public List<Attendance> findAllByEmployeeId(Long employeeId) {
        return attendanceRepository.findAllByEmployeeId(employeeId);
    }

    @Transactional(readOnly=true)
    @Override
    public List<YearMonth> getMonthsWithoutAttendanceInfoByYear(int year) {
        Set<Integer> monthsValuesWithAttendanceInfo = attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(year);

        return IntStream.range(1, dateTimeService.getCurrentMonth().getMonthValue())
                .filter(requiredMonth -> !monthsValuesWithAttendanceInfo.contains(requiredMonth))
                .mapToObj(requiredMonthWithoutInfo -> YearMonth.of(year, requiredMonthWithoutInfo))
                .collect(Collectors.toList());
    }
}