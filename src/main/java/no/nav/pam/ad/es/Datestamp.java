package no.nav.pam.ad.es;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Datestamp {

    public static final String DATESTAMP_FORMAT = "yyyyMMdd";

    public static String getCurrent() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATESTAMP_FORMAT));
    }

    static LocalDate parseFrom(String datestamp) {
        return LocalDate.parse(datestamp, DateTimeFormatter.ofPattern(DATESTAMP_FORMAT));
    }

}
