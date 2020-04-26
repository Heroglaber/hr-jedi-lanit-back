package ru.lanit.bpm.jedu.hrjedi.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.UUID;

import static org.junit.Assert.*;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ArtemisAutoConfiguration.class, JmsAutoConfiguration.class})
public class AccountingServiceStubTest {
    private static final String REQUEST_OK = "<CreateVacationDocumentsRq>\n" +
        "    <login>ivanov</login>\n" +
        "    <startDate>2020-01-01</startDate>\n" +
        "    <endDate>2020-01-14</endDate>\n" +
        "</CreateVacationDocumentsRq>";
    private static final String REQUEST_ERROR = "<CreateVacationDocumentsRq>\n" +
        "    <login>petrov</login>\n" +
        "    <startDate>2020-01-01</startDate>\n" +
        "    <endDate>2020-01-14</endDate>\n" +
        "</CreateVacationDocumentsRq>";
    private static final String REQUEST_TIMEOUT = "<CreateVacationDocumentsRq>\n" +
        "    <login>sergeev</login>\n" +
        "    <startDate>2020-01-01</startDate>\n" +
        "    <endDate>2020-01-14</endDate>\n" +
        "</CreateVacationDocumentsRq>";
    private static final String RESPONSE_OK = "<CreateVacationDocumentsRs>" +
        "    <status>OK</status>" +
        "</CreateVacationDocumentsRs>";
    private static final String RESPONSE_ERROR = "<CreateVacationDocumentsRs>" +
        "    <status>ERROR</status>" +
        "    <description>Не найден сотрудник с идентификатором 'petrov'</description>" +
        "</CreateVacationDocumentsRs>";

    @Value("ru.lanit.bpm.jedu.hrjedi.queues.2t-cv-requset")
    private String requestQueue;
    @Value("ru.lanit.bpm.jedu.hrjedi.queues.2t-cv-response")
    private String responseQueue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    public void response_success() throws Exception {
        String correlationId = randomCorrelationId();

        sendRequest(correlationId, REQUEST_OK);
        TextMessage message = receiveResponse(correlationId);

        assertNotNull(message);
        assertEquals(RESPONSE_OK, message.getText());
    }

    @Test
    public void response_error() throws Exception {
        String correlationId = randomCorrelationId();

        sendRequest(correlationId, REQUEST_ERROR);
        TextMessage message = receiveResponse(correlationId);

        assertNotNull(message);
        assertEquals(RESPONSE_ERROR, message.getText());
    }

    @Test
    public void response_timeout() {
        String correlationId = randomCorrelationId();

        sendRequest(correlationId, REQUEST_TIMEOUT);
        TextMessage message = receiveResponse(correlationId);

        assertNull(message);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void sendRequest(String correlationId, String messageContent) {
        jmsTemplate.send(requestQueue, (session) -> {
            Message message = session.createTextMessage(messageContent);
            message.setJMSCorrelationID(correlationId);
            return message;
        });
    }

    private TextMessage receiveResponse(String correlationId) {
        return (TextMessage) jmsTemplate.receiveSelected(responseQueue, "JMSCorrelationID='" + correlationId + "'");
    }

    private String randomCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
