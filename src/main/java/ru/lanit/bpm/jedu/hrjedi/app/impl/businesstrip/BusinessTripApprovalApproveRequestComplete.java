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
import ru.lanit.bpm.jedu.hrjedi.domain.Employee;
import ru.lanit.bpm.jedu.hrjedi.domain.Hotel;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Component("businessTripApprovalApproveRequestComplete")
public class BusinessTripApprovalApproveRequestComplete extends BusinessTripApprovalCommonTaskComplete {
    private BusinessTripService businessTripService;

    public BusinessTripApprovalApproveRequestComplete(EmailNotificationController notificationController,
        BusinessTripService businessTripService) {
        this.businessTripService = businessTripService;
    }

    @Override
    public void notify(DelegateTask task) {
        super.notify(task);

        Hotel hotel = getTaskVariable(task, "hotel");
        BusinessTrip businessTrip = getTaskVariable(task, "businessTrip");
        long nigthsAmount = nightsAmount(businessTrip.getStart(), businessTrip.getEnd());
        long hotelReservationCost = ÑalculateHotelReservationCost(hotel.getPrice(), nigthsAmount);
        if(hotelReservationCost > businessTrip.getBudget()) {
            setTaskVariable(task, "action", "reject");
            setProcessVariable(task, "lastAction", "reject");
            setProcessVariable(task, "processName", getProcessName(businessTrip.getEmployee()));
        }

        if ("approve".equals(getTaskVariable(task, "action"))) {
            businessTripService.saveBusinessTrip(businessTrip);
        }
    }

    private long nightsAmount(LocalDate dateBefore, LocalDate dateAfter) {
        return DAYS.between(dateBefore, dateAfter);
    }

    private long ÑalculateHotelReservationCost(long costPerNight, long nightsAmount) {
        return costPerNight * nightsAmount;
    }

    private String getProcessName(Employee approver) {
        return "ÐÐ¾Ð²ÑÐ¾ÑÐ½Ð¾Ðµ ÑÐ¾Ð³Ð»Ð°ÑÐ¾Ð²Ð°Ð½Ð¸Ðµ ÐºÐ¾Ð¼Ð°Ð½Ð´Ð¸ÑÐ¾Ð²ÐºÐ¸: " + approver.getFullName();
    }
}
