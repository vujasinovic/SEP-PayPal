package rs.ac.ftn.uns.sep.paypal.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateHelper {
    private static final int DAY = 1;
    private static final String WHITESPACE = " ";
    private static final String TIME_ARGUMENT = "T";
    private static final String TIME_ZONE = "Z";
    private static final String DATE_PATTERN = "yyyy-MM-dd hh:mm:ss";

    public static String tomorrow() {
        return LocalDateTime.now().plusDays(DAY).format(DateTimeFormatter.ofPattern(DATE_PATTERN)).replace(WHITESPACE, TIME_ARGUMENT) + TIME_ZONE;
    }
}
