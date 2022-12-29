package ru.kotikov.soapspringboot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.kotikov.soapspringboot.exceptions.db.DbReadException;
import ru.kotikov.soapspringboot.exceptions.db.EmptyResultException;
import ru.kotikov.soapspringboot.utils.FileLoader;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataBaseService {

    private final String QUERY_EQUIPMENT_AND_ADDRESS = "queries/getEquipmentAndAddressesFromDB.sql";

    private final JdbcTemplate jdbcTemplate;
    private final FileLoader fileLoader;

    public Map<Long, Long> getEquipmentAndAddressesFromDB(long incidentId, String changeDate) throws DbReadException {
        try {
            Map<Long, Long> equipmentAndAddresses = new HashMap<>();
            long m = System.currentTimeMillis();
            log.debug("Start getEquipmentAndAddressesFromDB for incident with notificationId = {} " +
                    "and changeDate = {}", incidentId, changeDate);
            boolean emptyResult = jdbcTemplate
                    .query(String.format(fileLoader.loadQueryFromFile(QUERY_EQUIPMENT_AND_ADDRESS), incidentId, changeDate), (rs, rowNum) -> {
                        equipmentAndAddresses.put(rs.getLong("equipmentId"), rs.getLong("addressId"));
                        return null;
                    }).isEmpty();
            if (emptyResult) throw new EmptyResultException(String.format("Result from db is empty " +
                    "for incidentId = %d and changeDate = %s", incidentId, changeDate));
            log.debug("Enrich time = {} min, count of rows = {}.", ((System.currentTimeMillis() - m) / 60000),
                    equipmentAndAddresses.size());
            return equipmentAndAddresses;
        } catch (BadSqlGrammarException e) {
            throw new DbReadException(String.format("Can't get Equipment And Addresses From DB, error in SQL request. " +
                    "SQL request:\n%s\nmessage: %s", fileLoader.loadQueryFromFile(QUERY_EQUIPMENT_AND_ADDRESS), e.getMessage()));
        } catch (Exception exception) {
            throw new DbReadException(String.format("Can't get Equipment And Addresses From DB " +
                    "with incidentId = %d and changeDate = %s. \nmessage: %s", incidentId, changeDate, exception.getMessage()));
        }
    }
}
