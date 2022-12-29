package ru.kotikov.soapspringboot.utils;

import org.junit.jupiter.api.Test;
import ru.kotikov.api.IncidentStatus;
import ru.kotikov.api.NotificationRequest;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationTest {

    @Test
    public void testCheckNotificationRequest() throws DatatypeConfigurationException {
        NotificationRequest request = new NotificationRequest();

        assertThat(Validation.checkNotificationRequest(request))
                .contains("changeDate", "incidentDescription", "incidentId", "incidentStatus",
                        "notificationCode", "notificationId", "sender");

        request.setNotificationId(11);
        request.setIncidentDescription("test");
        assertThat(Validation.checkNotificationRequest(request))
                .contains("changeDate", "incidentId", "incidentStatus",
                        "notificationCode", "sender");

        request.setIncidentId(132);
        request.setSender("test");
        assertThat(Validation.checkNotificationRequest(request))
                .contains("changeDate", "incidentStatus",
                        "notificationCode");

        request.setIncidentStatus(IncidentStatus.ACTIVE);
        request.setNotificationCode("test");
        assertThat(Validation.checkNotificationRequest(request))
                .contains("changeDate");

        request.setChangeDate(DatatypeFactory.newInstance().newXMLGregorianCalendar());
        assertThat(Validation.checkNotificationRequest(request))
                .isEmpty();
    }
}
