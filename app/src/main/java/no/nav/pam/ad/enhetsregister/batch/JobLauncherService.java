package no.nav.pam.ad.enhetsregister.batch;


import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class JobLauncherService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public JobExecution syncFromFiles(CsvProperties.EnhetType type, String filename) throws JobExecutionException {
        return jobLauncher.run(job, buildParameters(type, filename));
    }

    private JobParameters buildParameters(CsvProperties.EnhetType type, String filename) {
        return new JobParametersBuilder()
                .addString("type", type.toString())
                .addString("filename", filename)
                .addString("datestamp", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")))
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
    }
}
