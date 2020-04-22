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

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import ru.lanit.bpm.jedu.hrjedi.model.Employee;
import ru.lanit.bpm.jedu.hrjedi.model.Vacation;
import ru.lanit.bpm.jedu.hrjedi.service.EmployeeService;

/**
 * Vacation Approval process start handler
 */
@Component("vacationApprovalStarted")
public class VacationApprovalStarted implements JavaDelegate {

    private IdentityService camundaIdentityService;

    private EmployeeService employeeService;

    public VacationApprovalStarted(IdentityService camundaIdentityService, EmployeeService employeeService) {
        this.camundaIdentityService = camundaIdentityService;
        this.employeeService = employeeService;
    }

    @Override
    public void execute(DelegateExecution process) {
        String initiatorLogin = getInitiatorLogin();
        Employee employee = employeeService.findByLogin(initiatorLogin);
        Vacation vacation = new Vacation();
        vacation.setEmployee(employee);
        Employee approver = employeeService.findWellKnownEmployeeHeadOfHr();

        process.setProcessBusinessKey(getBusinessKey(process));
        process.setVariable("initiatorLogin", initiatorLogin);
        process.setVariable("approverLogin", approver.getLogin());
        process.setVariable("processName", getProcessName(employee));
        process.setVariable("vacation", vacation);
    }

    private String getInitiatorLogin() {
        return camundaIdentityService.getCurrentAuthentication().getUserId();
    }

    private String getBusinessKey(DelegateExecution process) {
        return "ОТПУСК-" + process.getId();
    }

    private String getProcessName(Employee initiator) {
        return "Согласование отпуска: " + initiator.getFullName();
    }
}
