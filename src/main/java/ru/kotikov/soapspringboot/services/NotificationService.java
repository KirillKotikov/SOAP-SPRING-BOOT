package ru.kotikov.soapspringboot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kotikov.api.NotificationRequest;
import ru.kotikov.api.NotificationResponse;
import ru.kotikov.api.Result;
import ru.kotikov.soapspringboot.configurations.NotificationConfiguration;
import ru.kotikov.soapspringboot.enums.NotificationCode;
import ru.kotikov.soapspringboot.enums.ResultCode;
import ru.kotikov.soapspringboot.exceptions.db.DbReadException;
import ru.kotikov.soapspringboot.exceptions.soap.SoapClientFaultException;
import ru.kotikov.soapspringboot.exceptions.soap.SoapServerFaultException;
import ru.kotikov.soapspringboot.mappers.NotificationMapper;
import ru.kotikov.soapspringboot.models.Incident;
import ru.kotikov.soapspringboot.utils.Validation;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
@EnableAsync
@EnableScheduling
public class NotificationService {

    private final NotificationConfiguration configuration;
    private final ConfirmationService confirmationService;
    private final DataBaseService dataBaseService;
    private final NotificationMapper mapper;
    private final RabbitTemplate rabbitTemplate;
    private final Queue<NotificationRequest> requests;

    public NotificationService(ConfirmationService confirmationService, DataBaseService dataBaseService, NotificationConfiguration configuration,
                               NotificationMapper mapper, RabbitTemplate rabbitTemplate) {
        this.confirmationService = confirmationService;
        this.dataBaseService = dataBaseService;
        this.configuration = configuration;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
        this.requests = new ConcurrentLinkedQueue<>();
    }

    @Async
    @Scheduled(fixedRate = 1000)
    void messageProcessing() {
        while (requests.size() > 0) {
                NotificationRequest request = requests.poll();
            try {
                Incident incident = mapper.requestToIncident(request);
                if (needEnrichByBD(incident.getNotificationCode())) {
                    incident.setEquipmentAndAddress(dataBaseService
                            .getEquipmentAndAddressesFromDB(incident.getIncidentId(), incident.getChangeDate().toString()));
                }
                ObjectMapper mapper = new ObjectMapper();
                try {
                    rabbitTemplate.convertAndSend(configuration.topicExchangeName, "test.incident",
                            mapper.writeValueAsString(incident));
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException:", e);
                }
                confirmationService.sendConfirmation(request, configuration.getOutputUrl(), ResultCode.OK);
            } catch (DbReadException exception) {
                confirmationService.sendConfirmation(request, configuration.getOutputUrl(), ResultCode.ERROR);
            }
        }
    }

    public boolean needEnrichByBD(String notificationCode) {
        if (NotificationCode.names().contains(notificationCode)) {
            return NotificationCode.valueOf(notificationCode).isNeedUpdateData();
        } else {
            log.error("NotificationCode = {} is unknown!", notificationCode);
            return false;
        }
    }

    public NotificationResponse process(NotificationRequest request) throws SoapClientFaultException, SoapServerFaultException {
        try {
            NotificationResponse response = new NotificationResponse();
            Result result = new Result();
            response.setSender(configuration.getSenderName());
            response.setRecipient(request.getSender());
            response.setNotificationId(request.getNotificationId());
            List<String> emptyRequiredFields = Validation.checkNotificationRequest(request);
            if (emptyRequiredFields.isEmpty()) {
                requests.add(request);
                result.setResultCode(ResultCode.OK.name());
            } else {
                if (emptyRequiredFields.contains(Validation.SENDER)
                        || emptyRequiredFields.contains(Validation.NOTIFICATION_ID)) {
                    throw new SoapClientFaultException(String.format("Missing required fields: %s.", String.join(", ",
                            emptyRequiredFields)));
                } else {
                    result.setResultCode(ResultCode.ERROR.name());
                    result.setResultText(String.format("Missing required fields: %s.", String.join(", ",
                            emptyRequiredFields)));
                }
            }
            response.setResult(result);
            return response;
        } catch (SoapClientFaultException e) {
            throw new SoapClientFaultException(e.getMessage());
        } catch (Exception e) {
            throw new SoapServerFaultException(e.getMessage());
        }
    }
}
