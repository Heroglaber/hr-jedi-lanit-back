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
package ru.lanit.bpm.jedu.hrjedi.app.impl.vacation;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.vacation.VacationRepository;
import ru.lanit.bpm.jedu.hrjedi.app.api.vacation.VacationService;
import ru.lanit.bpm.jedu.hrjedi.domain.Vacation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VacationServiceImpl implements VacationService {
    private static final String VACATION_PROCESS_DEFINITION_KEY = "vacation-approval";

    private VacationRepository vacationRepository;
    private RuntimeService runtimeService;
    private RepositoryService repositoryService;

    public VacationServiceImpl(VacationRepository vacationRepository, RuntimeService runtimeService, RepositoryService repositoryService) {
        this.vacationRepository = vacationRepository;
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
    }

    @Override
    public void saveVacation(Vacation vacation) {
        vacationRepository.save(vacation);
    }

    @Override
    public Set<String> findVacationsToApprove(String approverLogin) {
        ProcessDefinition vacationProcessDefinition =
            repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(VACATION_PROCESS_DEFINITION_KEY)
                .latestVersion()
                .singleResult();

        List<ProcessInstance> processInstances =
            runtimeService.createProcessInstanceQuery()
                .processDefinitionId(vacationProcessDefinition.getId())
                .variableValueEquals("approverLogin", approverLogin)
                .active()
                .list();
        return processInstances.stream().map(ProcessInstance::getId).collect(Collectors.toSet());
    }
}
