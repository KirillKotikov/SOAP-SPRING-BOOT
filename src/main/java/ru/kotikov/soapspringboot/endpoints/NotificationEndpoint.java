package ru.kotikov.soapspringboot.endpoints;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.kotikov.api.ConfirmationRequest;
import ru.kotikov.api.NotificationRequest;
import ru.kotikov.api.NotificationResponse;
import ru.kotikov.soapspringboot.exceptions.soap.SoapClientFaultException;
import ru.kotikov.soapspringboot.exceptions.soap.SoapServerFaultException;
import ru.kotikov.soapspringboot.services.NotificationService;

@Slf4j
@Endpoint
@RequiredArgsConstructor
public class NotificationEndpoint {
    private static final String NAMESPACE_URI = "http://kotikov.ru/api";
    private final NotificationService service;

    @ResponsePayload
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "NotificationRequest")
    public NotificationResponse receiveNotification(@RequestPayload NotificationRequest request)
            throws SoapClientFaultException, SoapServerFaultException {
        try{
            return service.process(request);
        } catch (SoapClientFaultException e) {
            throw new SoapClientFaultException(e.getMessage());
        } catch (Exception e) {
            throw new SoapServerFaultException(e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ConfirmationRequest")
    public void testConfirmationReceiver(@RequestPayload ConfirmationRequest request) {
        log.debug("\n\ttestConfirmationReceiver:"
        + "\n\t\trequest.getNotificationId() = " + request.getNotificationId()
        + "\n\t\trequest.getSender() = " + request.getSender()
        + "\n\t\trequest.getRecipient() = " + request.getRecipient()
        + "\n\t\trequest.getResult().getResultCode() = " + request.getResult().getResultCode()
        + "\n\t\trequest.getResult().getResultText() = " + request.getResult().getResultText());
    }
}
