package de.xenadu.learningcards.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class XenaduDateTimeFormatter {

    private XenaduDateTimeFormatter() {}

    private static final DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String localDateTimeToIsoString(LocalDateTime localDateTime) {
        return isoFormatter.format(localDateTime);
    }

    public static LocalDateTime isoStringToLocalDateTime(String isoLocalDateTime) {
        return LocalDateTime.parse(isoLocalDateTime, isoFormatter);
    }
}
