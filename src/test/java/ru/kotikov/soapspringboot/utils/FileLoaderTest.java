package ru.kotikov.soapspringboot.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FileLoaderTest {

    @Test
    public void testLoadQueryFromFile() {
        FileLoader fileLoader = new FileLoader();

        assertThat(fileLoader.loadQueryFromFile("queries/getEquipmentAndAddressesFromDB.sql"))
                .isEqualTo("SELECT equipmentId, addressId FROM incident WHERE incidentId = %d AND changeDate = '%s'");
    }
}
