package no.nav.pam.ad.enhetsregister.batch;


import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JobParameterBuilderUtil {

    public static JobParameters buildParameters(CsvProperties.EnhetType type, String filename) {
        return new JobParametersBuilder()
                .addString("type", type.toString())
                .addString("filename", filename)
                .addString("datestamp", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")))
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
    }
}