package ru.kotikov.soapspringboot.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    NotificationService service;

    @Test
    public void testNeedEnrichByBD() {
        assertThat(service.needEnrichByBD("NEW")).isTrue();
        assertThat(service.needEnrichByBD("REOPEN")).isTrue();
        assertThat(service.needEnrichByBD("UPDATE")).isTrue();
        assertThat(service.needEnrichByBD("CLOSED")).isFalse();
    }
}
