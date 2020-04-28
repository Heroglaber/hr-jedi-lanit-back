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
package ru.lanit.bpm.jedu.hrjedi.process.vacationapprv;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.stereotype.Component;
import ru.lanit.bpm.jedu.hrjedi.model.Vacation;
import ru.lanit.bpm.jedu.hrjedi.service.NotificationService;
import ru.lanit.bpm.jedu.hrjedi.service.VacationService;

@Component("vacationApprovalApproveRequestComplete")
public class VacationApprovalApproveRequestComplete extends VacationApprovalCommonTaskComplete {
    private NotificationService notificationService;
    private VacationService vacationService;

    public VacationApprovalApproveRequestComplete(NotificationService notificationService, VacationService vacationService) {
        this.notificationService = notificationService;
        this.vacationService = vacationService;
    }

    @Override
    public void notify(DelegateTask task) {
        super.notify(task);

        if ("approve".equals(getTaskVariable(task, "action"))) {
            Vacation approvedVacation = getTaskVariable(task, "vacation");
            vacationService.saveVacation(approvedVacation);
            notificationService.notifyOnVacationApproval(approvedVacation);
        }
    }
}
