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

import org.apache.commons.collections4.SetUtils;
import org.easymock.EasyMock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import ru.lanit.bpm.jedu.hrjedi.repository.AttendanceRepository;
import ru.lanit.bpm.jedu.hrjedi.service.DateTimeService;

import java.time.Month;
import java.time.YearMonth;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class AttendanceServiceimplTest {
    private static final int YEAR_2020 = 2020;
    private static final YearMonth JANUARY_2020 = YearMonth.of(YEAR_2020, Month.JANUARY);
    private static final YearMonth FEBRUARY_2020 = YearMonth.of(YEAR_2020, Month.FEBRUARY);
    private static final YearMonth APRIL_2020 = YearMonth.of(YEAR_2020, Month.APRIL);
    private static final YearMonth JUNE_2020 = YearMonth.of(YEAR_2020, Month.JUNE);
    private static final YearMonth OCTOBER_2020 = YearMonth.of(YEAR_2020, Month.OCTOBER);
    private static final YearMonth NOVEMBER_2020 = YearMonth.of(YEAR_2020, Month.NOVEMBER);
    private static final YearMonth DECEMBER_2020 = YearMonth.of(YEAR_2020, Month.DECEMBER);
    private static final int YEAR_2021 = 2021;
    private static final YearMonth JANUARY_2021 = YearMonth.of(YEAR_2021, Month.JANUARY);

    @TestSubject
    AttendanceServiceimpl attendanceService = new AttendanceServiceimpl();

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
        EasyMock.expect(dateTimeService.getCurrentMonth()).andReturn(NOVEMBER_2020);
        EasyMock.expect(attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(YEAR_2020))
                .andReturn(SetUtils.hashSet(1, 3, 5, 7, 8, 9));
        EasyMockUnitils.replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(YEAR_2020);

        assertEquals(asList(FEBRUARY_2020, APRIL_2020, JUNE_2020, OCTOBER_2020), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_currentYear_allRequiredMonthWithAttendanceInfo() {
        EasyMock.expect(dateTimeService.getCurrentMonth()).andReturn(NOVEMBER_2020);
        EasyMock.expect(attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(YEAR_2020))
                        .andReturn(SetUtils.hashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        EasyMockUnitils.replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(YEAR_2020);

        assertEquals(emptyList(), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_futureYear() {
        EasyMock.expect(dateTimeService.getCurrentMonth()).andReturn(NOVEMBER_2020);
        EasyMockUnitils.replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(YEAR_2021);

        assertEquals(emptyList(), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_futureCurrentJanuary() {
        EasyMock.expect(dateTimeService.getCurrentMonth()).andReturn(JANUARY_2020);
        EasyMockUnitils.replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(YEAR_2020);

        assertEquals(emptyList(), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_forPastYear() {
        EasyMock.expect(dateTimeService.getCurrentMonth()).andReturn(JANUARY_2021);
        EasyMock.expect(attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(YEAR_2020))
                        .andReturn(SetUtils.hashSet(1, 3, 5, 7, 8, 9, 11));
        EasyMockUnitils.replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(YEAR_2020);

        assertEquals(asList(FEBRUARY_2020, APRIL_2020, JUNE_2020, OCTOBER_2020, DECEMBER_2020), monthsWithoutAttendanceInfo);
    }
}
