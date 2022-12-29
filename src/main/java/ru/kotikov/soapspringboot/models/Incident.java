package ru.kotikov.soapspringboot.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.kotikov.api.IncidentStatus;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Map;

@Setter
@Getter
@ToString
public class Incident {

    private long incidentId;
    private String sender;
    private String notificationCode;
    private XMLGregorianCalendar startDate;
    private XMLGregorianCalendar changeDate;
    private XMLGregorianCalendar deadLine;
    private XMLGregorianCalendar endDate;
    private IncidentStatus incidentStatus;
    private String comment;
    private String incidentDescription;
    private Map<Long, Long> equipmentAndAddress;
}
