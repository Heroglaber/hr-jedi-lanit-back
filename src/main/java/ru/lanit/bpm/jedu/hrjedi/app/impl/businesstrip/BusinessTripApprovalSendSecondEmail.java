package ru.lanit.bpm.jedu.hrjedi.app.impl.businesstrip;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import ru.lanit.bpm.jedu.hrjedi.adapter.email.EmailNotificationController;
import ru.lanit.bpm.jedu.hrjedi.domain.BusinessTrip;

@Component("businessTripApprovalSendSecondEmail")
public class BusinessTripApprovalSendSecondEmail implements JavaDelegate {
    private EmailNotificationController notificationController;

    public BusinessTripApprovalSendSecondEmail(EmailNotificationController notificationController) {
        this.notificationController = notificationController;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("========================THERE====================================");
        BusinessTrip businessTrip = (BusinessTrip) delegateExecution.getVariable("businessTrip");
        notificationController.notifyOnBusinessTripApproval(businessTrip);
    }
}
