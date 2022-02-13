package ru.lanit.bpm.jedu.hrjedi.adapter.accounting;

import com.ibm.mq.jms.MQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import ru.lanit.bpm.jedu.hrjedi.adapter.accounting.messages.CreateVacationDocumentsRq;
import ru.lanit.bpm.jedu.hrjedi.adapter.accounting.messages.CreateVacationDocumentsRs;
import ru.lanit.bpm.jedu.hrjedi.domain.Vacation;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class AccountingController {
    @Value("${ru.lanit.bpm.jedu.hrjedi.queues.2t-cv-request}")
    private String requestQueue;

    @Value("${ru.lanit.bpm.jedu.hrjedi.queues.2t-cv-response}")
    private String responseQueue;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void createVacationDocuments(Vacation vacation) throws AccountingException {

        String requestMessage;
        try {
            requestMessage = marshalToString(new CreateVacationDocumentsRq(vacation));
        } catch (JAXBException e) {
            throw new AccountingException("Error while marshalling request message.", e);
        }

        CreateVacationDocumentsRs responseMessage;

        try {
            String requestCorrelationId = sendMessage(requestMessage, requestQueue);
            responseMessage = getResponseMessage(requestCorrelationId, responseQueue);
        } catch (TimeoutException e) {
            throw new AccountingException("Не получен ответ", e);
        } catch (JMSException | JAXBException e) {
            throw new AccountingException("Error during CreateVacationDocuments service request.", e);
        }

        //Основная логика
        if("ERROR".equals(responseMessage.getStatus())) {
            throw new AccountingException("Не найден сотрудник с идентификатором");
        } else if(!"OK".equals(responseMessage.getStatus())) {
            throw new AccountingException("Not implemented yet");
        }
    }

    private CreateVacationDocumentsRs getResponseMessage(String requestCorrelationId, String queueName) throws JMSException, JAXBException, TimeoutException {
        final String selectorExpression = String.format("JMSCorrelationID='%s'", requestCorrelationId);
        //receiveSelected() ждёт ответ ${jms.template.receive-timeout} мс
        //В процессе тестирования обнаружил, что заглушка работает не совсем так, как описано:
        //Иногда в логе request_receipt_log ответные сообщения появляются через гораздо большее время, чем указано в receipt_delay
        final TextMessage responseMessage = (TextMessage) jmsTemplate.receiveSelected(queueName, selectorExpression);
        if(responseMessage == null) {
            throw new TimeoutException("Response message not found.");
        }
        return unmarshalFromString(responseMessage.getText());
    }

    private String sendMessage(String message, String queueName) throws JMSException {
        MQQueue orderRequestQueue = new MQQueue(queueName);

        final AtomicReference<Message> msg = new AtomicReference<>();
        jmsTemplate.convertAndSend(orderRequestQueue, message,  textMessage -> {
            msg.set(textMessage);
            return textMessage;
        });

        return msg.get().getJMSMessageID();
    }

    private static String marshalToString(CreateVacationDocumentsRq request) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CreateVacationDocumentsRq.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(request, writer);
        return writer.toString();
    }

    private static CreateVacationDocumentsRs unmarshalFromString(String response) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CreateVacationDocumentsRs.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (CreateVacationDocumentsRs)unmarshaller.unmarshal(new StringReader(response));
    }
}
