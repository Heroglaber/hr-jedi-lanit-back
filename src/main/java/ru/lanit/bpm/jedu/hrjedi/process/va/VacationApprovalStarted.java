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
package ru.lanit.bpm.jedu.hrjedi.process.va;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.lanit.bpm.jedu.hrjedi.model.Employee;
import ru.lanit.bpm.jedu.hrjedi.model.Vacation;
import ru.lanit.bpm.jedu.hrjedi.repository.EmployeeRepository;
import ru.lanit.bpm.jedu.hrjedi.repository.VacationRepository;

/**
 * Vacation Approval process start handler
 */
@Component("vacationApprovalStarted")
public class VacationApprovalStarted implements JavaDelegate {

    private IdentityService camundaIdentityService;

    private VacationRepository vacationRepository;

    private EmployeeRepository employeeRepository;

    public VacationApprovalStarted(IdentityService camundaIdentityService, VacationRepository vacationRepository, EmployeeRepository employeeRepository) {
        this.camundaIdentityService = camundaIdentityService;
        this.vacationRepository = vacationRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void execute(DelegateExecution process) {
        String initiatorLogin = getInitiatorLogin();
        Vacation vacation = new Vacation();
        Employee employee = employeeRepository.findByLogin(initiatorLogin)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with -> username: " + initiatorLogin));
        vacation.setEmployee(employee);
        vacation = vacationRepository.save(vacation);
        String businessKey = getBusinessKey(vacation);
        process.setProcessBusinessKey(businessKey);
        process.setVariable("businessKey", businessKey);
        process.setVariable("initiatorLogin", initiatorLogin);
        process.setVariable("name", getProcessName(employee));
    }

    private String getInitiatorLogin() {
        return camundaIdentityService.getCurrentAuthentication().getUserId();
    }

    private String getBusinessKey(Vacation vacation) {
        return "ОТПУСК-" + vacation.getId();
    }

    private String getProcessName(Employee initiator) {
        return "Согласование отпуска: " + initiator.getFullName();
    }
}
