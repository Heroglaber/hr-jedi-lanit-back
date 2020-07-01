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
package ru.lanit.bpm.jedu.hrjedi.app.datetime;

import java.time.LocalDate;
import java.time.YearMonth;

public interface DateTimeService {
    /**
     * Возвращает текущий месяц. Необходим для тестирования, чтобы мокировать получение текущего месяца.
     *
     * @return текущий месяц
     */
    YearMonth getCurrentMonth();

    /**
     * Возвращает текущий день. Необходим для тестирования, чтобы мокировать получение текущего дня.
     *
     * @return текущий день
     */
    LocalDate getCurrentDate();
}
