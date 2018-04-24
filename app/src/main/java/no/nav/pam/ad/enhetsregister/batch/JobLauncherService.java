package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.es.DatestampUtil;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class JobLauncherService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public JobExecution syncFromFiles(DataSet type, String filename) throws JobExecutionException {
        return jobLauncher.run(job, buildParameters(type, filename));
    }

    private JobParameters buildParameters(DataSet type, String filename) {
        return new JobParametersBuilder()
                .addString("type", type.toString())
                .addString("filename", filename)
                .addString("datestamp", DatestampUtil.getDatestamp(LocalDate.now()))
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
    }
}
