package ru.lanit.bpm.jedu.hrjedi.adapter.accounting.messages;

import ru.lanit.bpm.jedu.hrjedi.domain.Vacation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@XmlRootElement(name="CreateVacationDocumentsRq")
@XmlType(propOrder = { "login", "startDate", "endDate" })
public class CreateVacationDocumentsRq {
    private String login;
    private Date startDate;
    private Date endDate;

    public CreateVacationDocumentsRq(Vacation vacation) {
        this.login = vacation.getEmployee().getLogin();
        this.startDate = java.sql.Date.valueOf(vacation.getStart());
        this.endDate = java.sql.Date.valueOf(vacation.getEnd());
    }

    public CreateVacationDocumentsRq() {
    }

    public String getLogin() {
        return login;
    }

    @XmlElement
    public void setLogin(String login) {
        this.login = login;
    }

    public Date getStartDate() {
        return startDate;
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
