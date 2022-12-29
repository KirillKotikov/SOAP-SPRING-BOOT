package ru.kotikov.soapspringboot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kotikov.api.NotificationRequest;
import ru.kotikov.soapspringboot.configurations.NotificationConfiguration;
import ru.kotikov.soapspringboot.enums.ResultCode;

import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmationService {

    private final String DB_READ_ERROR = "Error while getting data from database.";

    private final NotificationConfiguration configuration;

    public void sendConfirmation(NotificationRequest request, String outputURL, ResultCode resultCode) {
        SOAPConnection soapConnection = null;
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            soapConnection.call(createSOAPRequest(request, resultCode), outputURL);
            soapConnection.close();

            log.debug("Confirmation sent to outputUrl = {}, notificationId = {}, incidentId = {}.",
                    outputURL, request.getNotificationId(), request.getIncidentId());
        } catch (SOAPException e) {
            log.error("\nError occurred while sending SOAP Request to Server!\noutputUrl = {}\n", outputURL, e);
            long timer = System.currentTimeMillis();
            boolean successfully = false;
            do {
                try {
                    Thread.sleep(60000);
                    soapConnection.call(createSOAPRequest(request, resultCode), outputURL);
                    soapConnection.close();
                    successfully = true;
                } catch (Exception ex) {
                    log.error("Bad request for confirmation, outputUrl = {}.", outputURL, ex);
                }
                // 5 minutes
            } while (!successfully && System.currentTimeMillis() - timer < 300000);
        } catch (Exception e) {
            log.error("\nError occurred while sending SOAP Request to Server!\noutputUrl = {}\n", outputURL, e);
        }
    }

    private void createSoapEnvelope(SOAPMessage soapMessage, NotificationRequest request, ResultCode code) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String myNamespaceURI = "http://kotikov.ru/api";
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();

        SOAPElement confirmationRequest = soapBody.addChildElement("ConfirmationRequest", "", myNamespaceURI);
        SOAPElement sender = confirmationRequest.addChildElement("sender");
        sender.addTextNode(configuration.getSenderName());
        SOAPElement recipient = confirmationRequest.addChildElement("recipient");
        recipient.addTextNode(request.getSender());
        SOAPElement notificationId = confirmationRequest.addChildElement("notificationId");
        notificationId.addTextNode(String.valueOf(request.getNotificationId()));
        SOAPElement result = confirmationRequest.addChildElement("result");
        SOAPElement resultCode = result.addChildElement("resultCode");
        SOAPElement resultText = result.addChildElement("resultText");


        if (code.equals(ResultCode.OK)) {
            resultCode.addTextNode(ResultCode.OK.name());
        } else if (code.equals(ResultCode.ERROR)) {
            resultCode.addTextNode(ResultCode.ERROR.name());
            resultText.addTextNode(DB_READ_ERROR);
        }
    }

    private SOAPMessage createSOAPRequest(NotificationRequest request, ResultCode code) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        createSoapEnvelope(soapMessage, request, code);
        soapMessage.saveChanges();

        if (log.isDebugEnabled()) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                soapMessage.writeTo(out);
                log.debug("Request SOAP Message:\n" + out + "\n");
            }
        }
        return soapMessage;
    }
}
