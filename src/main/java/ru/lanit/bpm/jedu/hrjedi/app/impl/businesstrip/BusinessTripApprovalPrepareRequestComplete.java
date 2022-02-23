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
import ru.lanit.bpm.jedu.hrjedi.domain.BusinessTrip;
import ru.lanit.bpm.jedu.hrjedi.domain.Employee;

@Component("businessTripApprovalPrepareRequestComplete")
public class BusinessTripApprovalPrepareRequestComplete extends BusinessTripApprovalCommonTaskComplete {

    @Override
    public void notify(DelegateTask task) {
        super.notify(task);

        BusinessTrip businessTrip = getTaskVariable(task, "businessTrip");
        Employee employee = businessTrip.getEmployee();
        setProcessVariable(task, "approverLogin", employee.getLogin());

        if ("submit".equals(getTaskVariable(task, "action"))) {
            mapFromTaskToProcess(task, "businessTrip", "businessTrip");
            mapFromTaskToProcess(task, "hotel", "hotel");
        }
    }
}
