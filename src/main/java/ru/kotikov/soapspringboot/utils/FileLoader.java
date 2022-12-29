package ru.kotikov.soapspringboot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kotikov.soapspringboot.exceptions.FileIsBlankException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class FileLoader {

    public String loadQueryFromFile(String filePath) {
        try (InputStream inputStream = FileLoader.class.getClassLoader().getResourceAsStream(filePath)) {
            log.debug("filePath: {}", filePath);
            if (inputStream != null) {
                String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replaceAll("\\s+", " ");
                log.debug("sql: {}", sql);
                return sql;
            } else {
                String errorMsg = "File is blank, filePath =  " + filePath;
                log.error(errorMsg);
                throw new FileIsBlankException(errorMsg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
