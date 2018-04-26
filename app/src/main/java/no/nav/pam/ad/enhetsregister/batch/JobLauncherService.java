package no.nav.pam.ad.enhetsregister.batch;

import no.nav.pam.ad.es.DatestampUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
public class JobLauncherService {

    private static final Logger LOG = LoggerFactory.getLogger(JobLauncherService.class);

    private static final String PARAM_TYPE = "type";
    private static final String PARAM_FILENAME = "filename";
    private static final String PARAM_DATESTAMP = "datestamp";

    @Value("${enhetsregister.timeout.millis:5000}")
    private int timeoutMillis;

    private final JobLauncher launcher;
    private final Job job;

    JobLauncherService(JobLauncher launcher, Job job) {
        this.launcher = launcher;
        this.job = job;
    }

    public void synchronize(DataSet dataSet, URL url)
            throws Exception {

        LOG.info("Synchronizing data set {} from source {} using timeout {}ms", dataSet, url, timeoutMillis);
        try (Downloader downloader = new Downloader(url)) {

            File file = downloader.download().get(timeoutMillis, TimeUnit.MILLISECONDS);
            JobParameters parameters = new JobParametersBuilder()
                    .addString(PARAM_TYPE, dataSet.toString())
                    .addString(PARAM_FILENAME, file.getAbsolutePath())
                    .addString(PARAM_DATESTAMP, DatestampUtil.getDatestamp(LocalDate.now()))
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            BatchStatus status = launcher
                    .run(job, parameters)
                    .getStatus();
            if (status.isUnsuccessful()) {
                throw new JobExecutionException("Synchronization failed with status " + status);
            }

        }

    }

}
