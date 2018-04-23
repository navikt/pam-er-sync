package no.nav.pam.ad.es;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatestampUtil {

    public static final String DATESTAMP_FORMAT = "yyyyMMdd";

    public static String getDatestamp(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DATESTAMP_FORMAT));
    }

    public static LocalDate parseDatestamp(String datestamp) {
        return LocalDate.parse(datestamp, DateTimeFormatter.ofPattern(DATESTAMP_FORMAT));
    }
}
