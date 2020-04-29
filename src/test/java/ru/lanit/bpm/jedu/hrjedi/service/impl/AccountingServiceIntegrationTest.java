package ru.lanit.bpm.jedu.hrjedi.service.impl;

import com.ibm.mq.spring.boot.MQAutoConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import ru.lanit.bpm.jedu.hrjedi.model.Employee;
import ru.lanit.bpm.jedu.hrjedi.model.Vacation;
import ru.lanit.bpm.jedu.hrjedi.service.AccountingService;
import ru.lanit.bpm.jedu.hrjedi.service.exception.AccountingException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.containsStringIgnoringCase;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MQAutoConfiguration.class, JmsAutoConfiguration.class})
public class AccountingServiceIntegrationTest {
    private static final Employee EMPLOYEE_IVANOV = new Employee("ivanov", "", "", "", "", "");
    private static final Employee EMPLOYEE_PETROV = new Employee("petrov", "", "", "", "", "");
    private static final Employee EMPLOYEE_SERGEEV = new Employee("sergeev", "", "", "", "", "");

    private static final LocalDate DATE_START = LocalDate.now();
    private static final LocalDate DATE_END = LocalDate.now().plus(7, ChronoUnit.DAYS);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    JmsTemplate jmsTemplate;

    private AccountingService accountingService;

    @Before
    public void setUp() {
        accountingService = new AccountingServiceImpl(jmsTemplate);
    }

    @Test
    public void success() throws Exception {
        Vacation vacation = new Vacation(EMPLOYEE_IVANOV, DATE_START, DATE_END);

        accountingService.createVacationDocuments(vacation);
    }

    @Test
    public void employeeNotFound() throws Exception {
        Vacation vacation = new Vacation(EMPLOYEE_PETROV, DATE_START, DATE_END);
        expectedException.expect(AccountingException.class);
        expectedException.expectMessage(containsStringIgnoringCase("Не найден сотрудник с идентификатором"));

        accountingService.createVacationDocuments(vacation);
    }

    @Test
    public void responseTimeout() throws Exception {
        Vacation vacation = new Vacation(EMPLOYEE_SERGEEV, DATE_START, DATE_END);
        expectedException.expect(AccountingException.class);
        expectedException.expectMessage(containsStringIgnoringCase("Не получен ответ"));

        accountingService.createVacationDocuments(vacation);
    }
}
