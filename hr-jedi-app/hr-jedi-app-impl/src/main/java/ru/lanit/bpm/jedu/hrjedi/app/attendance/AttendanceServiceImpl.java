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
package ru.lanit.bpm.jedu.hrjedi.app.attendance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.attendance.AttendanceRepository;
import ru.lanit.bpm.jedu.hrjedi.app.datetime.DateTimeService;
import ru.lanit.bpm.jedu.hrjedi.domain.Attendance;

import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.Month.JANUARY;
import static java.util.Collections.emptyList;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private static final int NUMBER_OF_MONTH_IN_YEAR = 12;

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

    @Transactional(readOnly = true)
    @Override
    public List<Attendance> findAllByEmployeeId(Long employeeId) {
        return attendanceRepository.findAllByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<YearMonth> getMonthsWithoutAttendanceInfoByYear(int year) {
        YearMonth currentMonth = dateTimeService.getCurrentMonth();
        int currentYear = currentMonth.getYear();

        if (year > currentYear || YearMonth.of(year, JANUARY).equals(currentMonth)) {
            return emptyList();
        }

        int numberOfMonthForWhichAttendanceInfoRequired = year == currentYear ? currentMonth.getMonthValue() - 1 : NUMBER_OF_MONTH_IN_YEAR;
        Set<Integer> monthsValuesWithAttendanceInfo = attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(year);

        return IntStream.rangeClosed(1, numberOfMonthForWhichAttendanceInfoRequired)
            .filter(requiredMonth -> !monthsValuesWithAttendanceInfo.contains(requiredMonth))
            .mapToObj(requiredMonthWithoutInfo -> YearMonth.of(year, requiredMonthWithoutInfo))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Attendance> findAllByMonth(YearMonth yearMonth) {
        return attendanceRepository.findAllByMonth(yearMonth.getYear(), yearMonth.getMonthValue());
    }
}
