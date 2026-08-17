package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.es.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final IndexService service;
    private final long delay;

    private JobCompletionNotificationListener(
            IndexService service,
            @Qualifier("jobCompletionNotificationListenerDelay") long delay
    ) {
        this.service = service;
        this.delay = delay;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
            return;
        }

        LOG.info("!!! JOB FINISHED! Time to verify the results");
        int writeCount = 0;
        int skipCount = 0;
        for (StepExecution e : jobExecution.getStepExecutions()) {
            writeCount += e.getWriteCount();
            skipCount += e.getSkipCount();
        }
        LOG.info("Total write count: {}, skip count {}", writeCount, skipCount);

        String prefix = jobExecution.getJobParameters().getString(JobLauncherService.PARAM_PREFIX);
        String datestamp = jobExecution.getJobParameters().getString(JobLauncherService.PARAM_DATESTAMP);
        if (prefix == null || datestamp == null) {
            return;
        }
        try {
            Thread.sleep(delay);
            int docCount = service.fetchDocCount(prefix, datestamp);
            if (docCount > 1000 ) {
                LOG.info("Index doc count: {}", docCount);
                LOG.info("replacing the alias.");
                service.replaceAlias(prefix, datestamp);
            } else {
               LOG.error("docCount is {} less than 1000", docCount);
               LOG.error("We should do a manually alias switch of index {}", prefix+datestamp);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Interrupted while waiting for index verification", e);
        } catch (IOException e) {
            LOG.error("Failed to verify job", e);
        }
    }
}
