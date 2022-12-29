package ru.kotikov.soapspringboot.mappers;

import org.mapstruct.Mapper;
import ru.kotikov.api.NotificationRequest;
import ru.kotikov.soapspringboot.models.Incident;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

Incident requestToIncident(NotificationRequest request);
}
