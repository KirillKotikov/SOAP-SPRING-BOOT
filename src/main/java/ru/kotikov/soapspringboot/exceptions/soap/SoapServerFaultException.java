package ru.kotikov.soapspringboot.exceptions.soap;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class SoapServerFaultException extends Exception {

    public SoapServerFaultException(String message) {
        super(message);
    }
}
