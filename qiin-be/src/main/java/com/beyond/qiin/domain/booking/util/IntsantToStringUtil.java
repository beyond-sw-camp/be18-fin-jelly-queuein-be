package com.beyond.qiin.domain.booking.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
// TODO : 추가할지 말지

public class IntsantToStringUtil {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String date(Instant instant, String zone) {
        return instant.atZone(ZoneId.of(zone)).format(DATE);
    }

    public static String time(Instant instant, String zone) {
        return instant.atZone(ZoneId.of(zone)).format(TIME);
    }

    public static String dateTime(Instant instant, String zone) {
        return instant.atZone(ZoneId.of(zone)).format(DATE_TIME);
    }
}
