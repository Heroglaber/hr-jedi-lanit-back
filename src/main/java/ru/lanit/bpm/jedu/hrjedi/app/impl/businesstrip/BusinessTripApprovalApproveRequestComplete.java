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
package ru.lanit.bpm.jedu.hrjedi.app.impl.businesstrip;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.stereotype.Component;
import ru.lanit.bpm.jedu.hrjedi.adapter.email.EmailNotificationController;
import ru.lanit.bpm.jedu.hrjedi.app.api.businessTrip.BusinessTripService;
import ru.lanit.bpm.jedu.hrjedi.domain.BusinessTrip;

@Component("businessTripApprovalApproveRequestComplete")
public class BusinessTripApprovalApproveRequestComplete extends BusinessTripApprovalCommonTaskComplete {
    private EmailNotificationController notificationController;
    private BusinessTripService businessTripService;

    public BusinessTripApprovalApproveRequestComplete(EmailNotificationController notificationController,
        BusinessTripService businessTripService) {
        this.notificationController = notificationController;
        this.businessTripService = businessTripService;
    }

    @Override
    public void notify(DelegateTask task) {
        super.notify(task);

        if ("approve".equals(getTaskVariable(task, "action"))) {
            BusinessTrip approvedBusinessTrip = getTaskVariable(task, "businessTrip");
            businessTripService.saveBusinessTrip(approvedBusinessTrip);
            notificationController.notifyOnBusinessTripApproval(approvedBusinessTrip);
        }
    }
}
