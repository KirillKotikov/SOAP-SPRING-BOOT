//package ru.kotikov.soapspringboot.endpoints;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.ws.test.server.MockWebServiceClient;
//import org.springframework.xml.transform.StringSource;
//import ru.kotikov.soapspringboot.services.NotificationService;
//
//import java.io.IOException;
//
//import static org.springframework.ws.test.server.RequestCreators.withPayload;
//import static org.springframework.ws.test.server.ResponseMatchers.*;
//
//@WebServiceServerTest
//public class  NotificationEndpointTest {
//
//    @Autowired
//    private MockWebServiceClient client;
//
//    @MockBean
//    private NotificationService service;
//
//    @Test
//    public void testReceiveNotification() throws IOException {
//
//        StringSource request = new StringSource(
//                """
//                        <NotificationRequest xmlns="http://kotikov.ru/api">
//                            <sender>Test</sender>
//                            <notificationId>1123</notificationId>
//                            <incidentId>1</incidentId>
//                            <notificationCode>NEW</notificationCode>
//                            <startDate>2022-12-21T18:32:16+07:00</startDate>
//                            <changeDate>2022-12-21T18:32:16+07:00</changeDate>
//                            <deadLine>2022-12-23T00:00:00+07:00</deadLine>
//                            <incidentStatus>ACTIVE</incidentStatus>
//                            <incidentDescription>Services: Internet</incidentDescription>
//                        </NotificationRequest>"""
//        );
//
//        client.sendRequest(withPayload(request))
//                .andExpect(noFault());
//    }
//}
