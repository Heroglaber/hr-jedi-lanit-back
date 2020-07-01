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

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import ru.lanit.bpm.jedu.hrjedi.app.datetime.DateTimeService;
import ru.lanit.bpm.jedu.hrjedi.app.employee.EmployeeService;
import ru.lanit.bpm.jedu.hrjedi.domain.Employee;
import ru.lanit.bpm.jedu.hrjedi.domain.Vacation;

import java.time.LocalDate;

/**
 * Vacation Approval process start handler
 */
@Component("vacationApprovalStarted")
public class VacationApprovalStarted implements JavaDelegate {

    private IdentityService camundaIdentityService;

    private EmployeeService employeeService;

    private DateTimeService dateTimeService;

    public VacationApprovalStarted(IdentityService camundaIdentityService, EmployeeService employeeService, DateTimeService dateTimeService) {
        this.camundaIdentityService = camundaIdentityService;
        this.employeeService = employeeService;
        this.dateTimeService = dateTimeService;
    }

    @Override
    public void execute(DelegateExecution process) {
        String initiatorLogin = getInitiatorLogin();
        Employee employee = employeeService.findByLogin(initiatorLogin);
        Vacation vacation = createDefaultVacationForEmployee(employee);
        Employee approver = employeeService.findWellKnownEmployeeHeadOfHr();
        String businessKey = getBusinessKey(process);

        process.setProcessBusinessKey(businessKey);
        process.setVariable("initiatorLogin", initiatorLogin);
        process.setVariable("approverLogin", approver.getLogin());
        process.setVariable("processName", getProcessName(employee));
        process.setVariable("processBusinessKey", businessKey);
        process.setVariable("vacation", vacation);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private String getInitiatorLogin() {
        return camundaIdentityService.getCurrentAuthentication().getUserId();
    }

    private Vacation createDefaultVacationForEmployee(Employee employee) {
        Vacation vacation = new Vacation();
        vacation.setEmployee(employee);

        LocalDate currentDate = dateTimeService.getCurrentDate();
        vacation.setStart(currentDate.plusWeeks(2));
        vacation.setEnd(currentDate.plusWeeks(2).plusDays(7));

        return vacation;
    }

    private String getBusinessKey(DelegateExecution process) {
        return "ОТПУСК-" + process.getId();
    }

    private String getProcessName(Employee initiator) {
        return "Согласование отпуска: " + initiator.getFullName();
    }
}
