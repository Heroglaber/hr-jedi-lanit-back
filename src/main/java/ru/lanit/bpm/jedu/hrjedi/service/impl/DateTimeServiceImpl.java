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

import org.springframework.stereotype.Service;
import ru.lanit.bpm.jedu.hrjedi.service.DateTimeService;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class DateTimeServiceImpl implements DateTimeService {
    @Override
    public YearMonth getCurrentMonth() {
        return YearMonth.now();
    }

    @Override
    public LocalDate getCurrentDate(){
        return LocalDate.now();
    }

}
