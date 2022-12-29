package ru.kotikov.soapspringboot.exceptions.soap;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT)
public class SoapClientFaultException extends Exception {

    public SoapClientFaultException(String message) {
        super(message);
    }
}