package ru.kotikov.soapspringboot.utils;

import ru.kotikov.api.NotificationRequest;

import java.util.ArrayList;
import java.util.List;

public class Validation {

    public static final String CHANGE_DATE = "changeDate";
    public static final String INCIDENT_DESCRIPTION = "incidentDescription";
    public static final String INCIDENT_ID = "incidentId";
    public static final String INCIDENT_STATUS = "incidentStatus";
    public static final String NOTIFICATION_CODE = "notificationCode";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String SENDER = "sender";

    public static List<String> checkNotificationRequest(NotificationRequest request) {
        List<String> emptyRequiredFields = new ArrayList<>();
        if (request.getSender() == null || request.getSender().isBlank()) {
            emptyRequiredFields.add(SENDER);
        }
        if (request.getNotificationId() == 0) {
            emptyRequiredFields.add(NOTIFICATION_ID);
        }
        if (request.getIncidentId() == 0) {
            emptyRequiredFields.add(INCIDENT_ID);
        }
        if (request.getNotificationCode() == null || request.getNotificationCode().isBlank()) {
            emptyRequiredFields.add(NOTIFICATION_CODE);
        }
        if (request.getChangeDate() == null) {
            emptyRequiredFields.add(CHANGE_DATE);
        }
        if (request.getIncidentStatus() == null) {
            emptyRequiredFields.add(INCIDENT_STATUS);
        }
        if (request.getIncidentDescription() == null || request.getIncidentDescription().isBlank()) {
            emptyRequiredFields.add(INCIDENT_DESCRIPTION);
        }
        return emptyRequiredFields;
    }
}
