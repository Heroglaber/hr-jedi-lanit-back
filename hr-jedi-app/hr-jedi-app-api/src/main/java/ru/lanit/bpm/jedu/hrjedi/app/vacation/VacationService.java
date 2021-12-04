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
package ru.lanit.bpm.jedu.hrjedi.app.vacation;

import ru.lanit.bpm.jedu.hrjedi.domain.Vacation;

import java.util.Set;

public interface VacationService {
    Set<String> findVacationsToApprove(String approverLogin);

    void saveVacation(Vacation vacation);
}
