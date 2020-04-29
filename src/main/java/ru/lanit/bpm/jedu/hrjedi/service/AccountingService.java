package ru.lanit.bpm.jedu.hrjedi.service;

import ru.lanit.bpm.jedu.hrjedi.model.Vacation;
import ru.lanit.bpm.jedu.hrjedi.service.exception.AccountingException;

public interface AccountingService {

    void createVacationDocuments(Vacation vacation) throws AccountingException;

}
