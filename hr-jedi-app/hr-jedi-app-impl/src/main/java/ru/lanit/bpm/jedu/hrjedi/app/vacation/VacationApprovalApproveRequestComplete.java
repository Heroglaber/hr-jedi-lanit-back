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

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.stereotype.Component;
import ru.lanit.bpm.jedu.hrjedi.adapter.email.EmailNotificationController;
import ru.lanit.bpm.jedu.hrjedi.domain.Vacation;

@Component("vacationApprovalApproveRequestComplete")
public class VacationApprovalApproveRequestComplete extends VacationApprovalCommonTaskComplete {
    private EmailNotificationController notificationController;
    private VacationService vacationService;

    public VacationApprovalApproveRequestComplete(EmailNotificationController notificationController, VacationService vacationService) {
        this.notificationController = notificationController;
        this.vacationService = vacationService;
    }

    @Override
    public void notify(DelegateTask task) {
        super.notify(task);

        if ("approve".equals(getTaskVariable(task, "action"))) {
            Vacation approvedVacation = getTaskVariable(task, "vacation");
            vacationService.saveVacation(approvedVacation);
            notificationController.notifyOnVacationApproval(approvedVacation);
        }
    }
}
