package ru.lanit.bpm.jedu.hrjedi.adapter.accounting.messages;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="CreateVacationDocumentsRs")
@XmlType(propOrder = { "status", "description" })
public class CreateVacationDocumentsRs {
    private String status;
    private String description;

    public String getStatus() {
        return status;
    }

    @XmlElement
    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    @XmlElement
    public void setDescription(String description) {
        this.description = description;
    }
}
