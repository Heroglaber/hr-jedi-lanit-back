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
package ru.lanit.bpm.jedu.hrjedi.app;

import org.apache.commons.collections4.SetUtils;
import org.easymock.EasyMock;
import org.easymock.TestSubject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.attendance.AttendanceRepository;
import ru.lanit.bpm.jedu.hrjedi.app.api.datetime.DateTimeService;
import ru.lanit.bpm.jedu.hrjedi.app.impl.attendance.AttendanceServiceImpl;

import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class AttendanceServiceImplTest {
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
        EasyMock.expect(dateTimeService.getCurrentMonth()).andReturn(YearMonth.of(2020, Month.NOVEMBER));
        EasyMock.expect(attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(2020)).andReturn(SetUtils.hashSet(1, 3, 5, 7, 8, 9));
        EasyMockUnitils.replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(2020);

        Assert.assertEquals(Arrays.asList(
            YearMonth.of(2020, Month.FEBRUARY),
            YearMonth.of(2020, Month.APRIL),
            YearMonth.of(2020, Month.JUNE),
            YearMonth.of(2020, Month.OCTOBER)
        ), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_currentYear_allRequiredMonthWithAttendanceInfo() {
        EasyMock.expect(dateTimeService.getCurrentMonth()).andReturn(YearMonth.of(2020, Month.NOVEMBER));
        EasyMock.expect(attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(2020)).andReturn(SetUtils.hashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        EasyMockUnitils.replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(2020);

        Assert.assertEquals(Collections.emptyList(), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_futureYear() {
        EasyMock.expect(dateTimeService.getCurrentMonth()).andReturn(YearMonth.of(2020, Month.NOVEMBER));
        EasyMockUnitils.replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(2021);

        Assert.assertEquals(Collections.emptyList(), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_futureCurrentJanuary() {
        EasyMock.expect(dateTimeService.getCurrentMonth()).andReturn(YearMonth.of(2020, Month.JANUARY));
        EasyMockUnitils.replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(2020);

        Assert.assertEquals(Collections.emptyList(), monthsWithoutAttendanceInfo);
    }

    @Test
    public void getMonthsWithoutAttendanceInfoByYear_forPastYear() {
        EasyMock.expect(dateTimeService.getCurrentMonth()).andReturn(YearMonth.of(2021, Month.JANUARY));
        EasyMock.expect(attendanceRepository.findMonthsValuesWithAttendanceInfoByYear(2020)).andReturn(SetUtils.hashSet(1, 3, 5, 7, 8, 9, 11));
        EasyMockUnitils.replay();

        List<YearMonth> monthsWithoutAttendanceInfo = attendanceService.getMonthsWithoutAttendanceInfoByYear(2020);

        Assert.assertEquals(Arrays.asList(
            YearMonth.of(2020, Month.FEBRUARY),
            YearMonth.of(2020, Month.APRIL),
            YearMonth.of(2020, Month.JUNE),
            YearMonth.of(2020, Month.OCTOBER),
            YearMonth.of(2020, Month.DECEMBER)
        ), monthsWithoutAttendanceInfo);
    }
}
