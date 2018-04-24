package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.es.DatestampUtil;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class JobLauncherService {

    public static final String PARAM_TYPE = "type";
    public static final String PARAM_FILENAME = "filename";
    public static final String PARAM_DATESTAMP = "datestamp";

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public JobExecution syncFromFiles(CsvProperties.EnhetType type, String filename) throws JobExecutionException {
        return jobLauncher.run(job, buildParameters(type, filename));
    }

    private JobParameters buildParameters(CsvProperties.EnhetType type, String filename) {
        return new JobParametersBuilder()
                .addString(PARAM_TYPE, type.toString())
                .addString(PARAM_FILENAME, filename)
                .addString(PARAM_DATESTAMP, DatestampUtil.getDatestamp(LocalDate.now()))
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
    }
}
