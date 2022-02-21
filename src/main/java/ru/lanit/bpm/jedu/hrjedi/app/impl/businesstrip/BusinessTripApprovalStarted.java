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

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import ru.lanit.bpm.jedu.hrjedi.app.api.datetime.DateTimeService;
import ru.lanit.bpm.jedu.hrjedi.app.api.employee.EmployeeService;
import ru.lanit.bpm.jedu.hrjedi.domain.BusinessTrip;
import ru.lanit.bpm.jedu.hrjedi.domain.Employee;

import java.time.LocalDate;

/**
 * Business Trip process start handler
 */
@Component("businessTripApprovalStarted")
public class BusinessTripApprovalStarted implements JavaDelegate {
    private IdentityService camundaIdentityService;
    private EmployeeService employeeService;
    private DateTimeService dateTimeService;

    public BusinessTripApprovalStarted(IdentityService camundaIdentityService, EmployeeService employeeService, DateTimeService dateTimeService) {
        this.camundaIdentityService = camundaIdentityService;
        this.employeeService = employeeService;
        this.dateTimeService = dateTimeService;
    }

    @Override
    public void execute(DelegateExecution process) {
        String initiatorLogin = getInitiatorLogin();
        Employee employee = employeeService.findByLogin(initiatorLogin);
        BusinessTrip businessTrip = createDefaultBusinessTripForEmployee(employee);
        Employee approver = employeeService.findWellKnownEmployeeHeadOfHr();
        String businessKey = getBusinessKey(process);

        process.setProcessBusinessKey(businessKey);
        process.setVariable("initiatorLogin", initiatorLogin);
        process.setVariable("approverLogin", approver.getLogin());
        process.setVariable("processName", getProcessName(employee));
        process.setVariable("processBusinessKey", businessKey);
        process.setVariable("businessTrip", businessTrip);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private String getInitiatorLogin() {
        return camundaIdentityService.getCurrentAuthentication().getUserId();
    }

    private BusinessTrip createDefaultBusinessTripForEmployee(Employee employee) {
        BusinessTrip businessTrip = new BusinessTrip();
        businessTrip.setEmployee(employee);

        LocalDate currentDate = dateTimeService.getCurrentDate();
        businessTrip.setStart(currentDate.plusDays(1));
        businessTrip.setEnd(currentDate.plusWeeks(1).plusDays(1));
        businessTrip.setBudget(0);

        return businessTrip;
    }

    private String getBusinessKey(DelegateExecution process) {
        return "КОМАНДИРОВКА-" + process.getId();
    }

    private String getProcessName(Employee initiator) {
        return "Заявка на командировку: " + initiator.getFullName();
    }
}
