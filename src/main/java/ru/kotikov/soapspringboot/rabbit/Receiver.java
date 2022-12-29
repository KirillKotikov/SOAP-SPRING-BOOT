package ru.kotikov.soapspringboot.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kotikov.soapspringboot.models.Incident;

@Slf4j
@Component
public class Receiver {

    public void receiveMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        Incident incident = null;
        try {
            incident = mapper.readValue(message, Incident.class);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException:", e);
        }
        if (incident != null) {
            log.debug("Received <" + incident + ">");
        } else {
            log.error("Received incident is null!");
        }
    }
}
