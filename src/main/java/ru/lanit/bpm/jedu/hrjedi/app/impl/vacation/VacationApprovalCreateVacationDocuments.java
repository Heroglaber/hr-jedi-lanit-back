package ru.lanit.bpm.jedu.hrjedi.app.impl.vacation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import ru.lanit.bpm.jedu.hrjedi.adapter.accounting.AccountingController;
import ru.lanit.bpm.jedu.hrjedi.domain.Vacation;

@Component
public class VacationApprovalCreateVacationDocuments implements JavaDelegate {
    private AccountingController accountingController;

    public VacationApprovalCreateVacationDocuments(AccountingController accountingController) {
        this.accountingController = accountingController;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        Vacation vacation = (Vacation) delegateExecution.getVariable("vacation");
        accountingController.createVacationDocuments(vacation);
    }
}
