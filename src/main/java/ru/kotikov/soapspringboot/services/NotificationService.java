package ru.kotikov.soapspringboot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.kotikov.api.NotificationRequest;
import ru.kotikov.api.NotificationResponse;
import ru.kotikov.api.Result;
import ru.kotikov.soapspringboot.configurations.NotificationConfiguration;
import ru.kotikov.soapspringboot.enums.NotificationCode;
import ru.kotikov.soapspringboot.enums.ResultCode;
import ru.kotikov.soapspringboot.exceptions.db.DbReadException;
import ru.kotikov.soapspringboot.exceptions.soap.SoapClientFaultException;
import ru.kotikov.soapspringboot.mappers.NotificationMapper;
import ru.kotikov.soapspringboot.models.Incident;
import ru.kotikov.soapspringboot.utils.Validation;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class NotificationService {

    private final ConfirmationService confirmationService;
    private final DataBaseService dataBaseService;
    private final NotificationConfiguration configuration;
    private final NotificationMapper mapper;
    private final RabbitTemplate rabbitTemplate;


    public final Queue<Thread> confirmationQueue;
    private final Thread confirmationExecutor;
    public final Queue<Thread> messageProcessingQueue;
    private final Thread messageProcessingExecutor;

    public NotificationService(ConfirmationService confirmationService, DataBaseService dataBaseService, NotificationConfiguration configuration,
                               NotificationMapper mapper, RabbitTemplate rabbitTemplate) {
        this.confirmationService = confirmationService;
        this.dataBaseService = dataBaseService;
        this.configuration = configuration;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
        this.confirmationQueue = new ConcurrentLinkedQueue<>();
        this.confirmationExecutor = new Thread(this::confirmationRunner);
        this.confirmationExecutor.start();
        this.messageProcessingQueue = new ConcurrentLinkedQueue<>();
        this.messageProcessingExecutor = new Thread(this::messageProcessingRunner);
        this.messageProcessingExecutor.start();
    }

    private void messageProcessing(NotificationRequest request) {
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

            confirmationQueue.add(new Thread(() -> confirmationService.sendConfirmation(request, configuration.getOutputUrl(), ResultCode.OK)));
        } catch (DbReadException exception) {
            confirmationQueue.add(new Thread(() -> confirmationService.sendConfirmation(request, configuration.getOutputUrl(), ResultCode.ERROR)));
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

    public NotificationResponse process(NotificationRequest request) throws SoapClientFaultException {
        NotificationResponse response = new NotificationResponse();
        Result result = new Result();
        response.setSender(configuration.getSenderName());
        response.setRecipient(request.getSender());
        response.setNotificationId(request.getNotificationId());
        List<String> emptyRequiredFields = Validation.checkNotificationRequest(request);
        if (emptyRequiredFields.isEmpty()) {
            messageProcessingQueue.add(new Thread(() -> messageProcessing(request)));
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
    }

    public void confirmationRunner() {
        while (!confirmationExecutor.isInterrupted()) {
            if (confirmationQueue.size() > 0) {
                log.debug("confirmationQueue.size() = " + confirmationQueue.size());
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
                        .newFixedThreadPool(configuration.getConfirmationFixedThreads());
                while (confirmationQueue.size() > 0) {
                    executor.submit(confirmationQueue.poll());
                }
            }
        }
    }

    public void messageProcessingRunner() {
        while (!messageProcessingExecutor.isInterrupted()) {
            if (messageProcessingQueue.size() > 0) {
                log.debug("messageProcessingQueue.size() = " + messageProcessingQueue.size());
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
                        .newFixedThreadPool(configuration.getMessageProcessingFixedThreads());
                while (messageProcessingQueue.size() > 0) {
                    executor.submit(messageProcessingQueue.poll());
                }
            }
        }
    }
}
