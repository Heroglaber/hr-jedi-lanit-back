/*
 * Copyright (c) 2008-CURRENT_YEAR
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
package ru.lanit.bpm.jedu.hrjedi.app;

import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.easymock.annotation.Mock;
import ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.attendance.AttendanceRepository;
import ru.lanit.bpm.jedu.hrjedi.app.api.datetime.DateTimeService;
import ru.lanit.bpm.jedu.hrjedi.app.impl.attendance.AttendanceServiceImpl;

import java.time.Month;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.Month.*;
import static java.time.YearMonth.of;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.SetUtils.hashSet;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;

@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class AttendanceServiceImplTest {
    private static final int CURRENT_YEAR = 2020;
    private static final int NEXT_YEAR = 2021;
    private static final YearMonth CURRENT_NOVEMBER = of(CURRENT_YEAR, NOVEMBER);
    private static final YearMonth CURRENT_JANUARY = of(CURRENT_YEAR, JANUARY);
    private static final YearMonth NEXT_JANUARY = of(NEXT_YEAR, JANUARY);

    @TestSubject
    AttendanceServiceImpl attendanceService = new AttendanceServiceImpl();

    @Mock
    AttendanceRepository attendanceRepository;
    @Mock
    DateTimeService dateTimeService;

    @Before
    public void setUp() {
        attendanceService.setAttendanceRepository(attendanceRepository);
        attendanceService.setDateTimeService(dateTimeService);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_currentYear() {
        final Set<Integer> EXPECTED_MONTHS = getSetOfMonthNumbers(1, 3, 5, 7, 8, 9);

        expect(dateTimeService.getCurrentMonth()).andReturn(CURRENT_NOVEMBER);
        expect(attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(CURRENT_YEAR)).andReturn(EXPECTED_MONTHS);
        replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(CURRENT_YEAR);

        assertEquals(getYearMonths(CURRENT_YEAR, FEBRUARY, APRIL, JUNE, OCTOBER), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_currentYear_allRequiredMonthWithAttendanceInfo() {
        final Set<Integer> EXPECTED_MONTHS = getSetOfMonthNumbers(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        expect(dateTimeService.getCurrentMonth()).andReturn(CURRENT_NOVEMBER);
        expect(attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(CURRENT_YEAR)).andReturn(EXPECTED_MONTHS);
        replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(CURRENT_YEAR);

        assertEquals(emptyList(), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_futureYear() {
        expect(dateTimeService.getCurrentMonth()).andReturn(CURRENT_NOVEMBER);
        replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(NEXT_YEAR);

        assertEquals(emptyList(), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_futureCurrentJanuary() {
        expect(dateTimeService.getCurrentMonth()).andReturn(CURRENT_JANUARY);
        replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(CURRENT_YEAR);

        assertEquals(emptyList(), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_forPastYear() {
        final Set<Integer> EXPECTED_MONTHS = getSetOfMonthNumbers(1, 3, 5, 7, 8, 9, 11);

        expect(dateTimeService.getCurrentMonth()).andReturn(NEXT_JANUARY);
        expect(attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(CURRENT_YEAR)).andReturn(EXPECTED_MONTHS);
        replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(CURRENT_YEAR);

        assertEquals(getYearMonths(CURRENT_YEAR, FEBRUARY, APRIL, JUNE, OCTOBER, DECEMBER), monthsWithoutAttendanceInfo);
    }

    private List<YearMonth> getYearMonths(int year, Month... months) {
        return stream(months).map(month -> of(year, month)).collect(Collectors.toList());
    }

    private HashSet<Integer> getSetOfMonthNumbers(Integer... numbers) {
        return hashSet(numbers);
    }
}
