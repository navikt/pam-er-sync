package no.nav.pam.ad.enhetsregister.batch;

import no.nav.pam.ad.es.Datestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class JobLauncherService {

    private static final Logger LOG = LoggerFactory.getLogger(JobLauncherService.class);

    static final String PARAM_PREFIX = "type";
    static final String PARAM_FILENAME = "filename";
    static final String PARAM_DATESTAMP = "datestamp";

    @Value("${pam.enhetsregister.sources.timeout.millis:60000}")
    private int timeoutMillis;

    private final Proxy proxy;
    private final JobLauncher launcher;
    private final Job job;

    JobLauncherService(Proxy proxy, JobLauncher launcher, Job job) {
        this.proxy = proxy;
        this.launcher = launcher;
        this.job = job;
    }

    public void synchronize(DataSet dataSet, URL url)
            throws IOException, TimeoutException, InterruptedException, ExecutionException, JobExecutionException {

        LOG.info("Synchronizing data set {} from source {} using timeout {} ms", dataSet, url, timeoutMillis);
        long start = System.currentTimeMillis();
        try (Downloader downloader = new Downloader(proxy, url)) {

            File file = downloader.download().get(timeoutMillis, TimeUnit.MILLISECONDS);
            LOG.info("Downloaded {} bytes to temporary file {} in {} ms", file.length(), file.getAbsolutePath(), System.currentTimeMillis() - start);
            JobParameters parameters = new JobParametersBuilder()
                    .addString(PARAM_PREFIX, dataSet.toString())
                    .addString(PARAM_FILENAME, file.getAbsolutePath())
                    .addString(PARAM_DATESTAMP, Datestamp.getCurrent())
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
