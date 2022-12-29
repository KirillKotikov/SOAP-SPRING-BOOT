package ru.kotikov.soapspringboot.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kotikov.soapspringboot.exceptions.db.DbReadException;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DataBaseServiceTest {

    @Autowired
    DataBaseService service;

    @Test
    public void testGetEquipmentAndAddressesFromDB() throws DbReadException {
        Map<Long, Long> map = service.getEquipmentAndAddressesFromDB(2, "2022-12-15T18:32:16+07:00");
        assertThat(map.keySet()).containsAll(Arrays.asList(231632L, 428144L, 492898L, 159355L, 824703L, 220397L,
                209037L, 379583L, 619748L));
        assertThat(map.values()).containsAll(Arrays.asList(7748731L, 6825791L, 4747317L, 2360407L, 6711398L, 7000228L,
                8316991L, 6990627L, 8659086L));
    }
}