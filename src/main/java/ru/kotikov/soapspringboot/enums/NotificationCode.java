package ru.kotikov.soapspringboot.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum NotificationCode {
    NEW(true),
    REOPEN(true),
    UPDATE(true),
    CLOSED(false);

    private final boolean needUpdateData;

    NotificationCode(boolean needUpdateData) {
        this.needUpdateData = needUpdateData;
    }

    public boolean isNeedUpdateData() {
        return needUpdateData;
    }

    public static List<String> names() {
        return Arrays.stream(values()).map(Enum::name).collect(Collectors.toList());
    }
}
