package ru.lanit.bpm.jedu.hrjedi.service.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.lanit.bpm.jedu.hrjedi.model.Vacation;
import ru.lanit.bpm.jedu.hrjedi.service.AccountingService;
import ru.lanit.bpm.jedu.hrjedi.service.exception.AccountingException;

@Service
public class AccountingServiceImpl implements AccountingService {
    private final JmsTemplate jmsTemplate;

    public AccountingServiceImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void createVacationDocuments(Vacation vacation) throws AccountingException {
        // TODO: Добавить реализацию
        throw new AccountingException("Not implemented yet");
    }
}
