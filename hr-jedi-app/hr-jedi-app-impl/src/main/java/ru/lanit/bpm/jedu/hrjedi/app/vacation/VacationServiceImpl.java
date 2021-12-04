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

import org.springframework.stereotype.Service;
import ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.vacation.VacationRepository;
import ru.lanit.bpm.jedu.hrjedi.domain.Vacation;

import java.util.Collections;
import java.util.Set;

@Service
public class VacationServiceImpl implements VacationService {
    private VacationRepository vacationRepository;

    public VacationServiceImpl(VacationRepository vacationRepository) {
        this.vacationRepository = vacationRepository;
    }

    @Override
    public void saveVacation(Vacation vacation) {
        vacationRepository.save(vacation);
    }

    @Override
    public Set<String> findVacationsToApprove(String approverLogin) {
        return Collections.emptySet();
    }
}
