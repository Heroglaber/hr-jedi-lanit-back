package ru.lanit.bpm.jedu.hrjedi.app.impl.businesstrip;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import ru.lanit.bpm.jedu.hrjedi.adapter.hibernate.businessTrip.BusinessTripRepository;
import ru.lanit.bpm.jedu.hrjedi.app.api.businessTrip.BusinessTripService;
import ru.lanit.bpm.jedu.hrjedi.domain.BusinessTrip;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BusinessTripServiceImpl implements BusinessTripService {
    private static final String BUSINESS_TRIP_PROCESS_DEFINITION_KEY = "business-trip-approval";

    private BusinessTripRepository businessTripRepository;
    private RuntimeService runtimeService;
    private RepositoryService repositoryService;

    public BusinessTripServiceImpl(BusinessTripRepository businessTripRepository, RuntimeService runtimeService,
        RepositoryService repositoryService) {
        this.businessTripRepository = businessTripRepository;
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
    }

    @Override
    public Set<String> findBusinessTripToApprove(String approverLogin) {
        ProcessDefinition businessTripProcessDefinition =
            repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(BUSINESS_TRIP_PROCESS_DEFINITION_KEY)
                .latestVersion()
                .singleResult();

        List<ProcessInstance> processInstances =
            runtimeService.createProcessInstanceQuery()
                .processDefinitionId(businessTripProcessDefinition.getId())
                .variableValueEquals("approverLogin", approverLogin)
                .active()
                .list();
        return processInstances.stream().map(ProcessInstance::getId).collect(Collectors.toSet());
    }

    @Override
    public void saveBusinessTrip(BusinessTrip businessTrip) {
        businessTripRepository.save(businessTrip);
    }
}
