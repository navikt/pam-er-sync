package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.es.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger LOG = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final IndexService service;
    private final long delay;

    @Autowired
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

        Map<String, JobParameter> parameters = jobExecution.getJobParameters().getParameters();
        if (!parameters.containsKey(JobLauncherService.PARAM_DATESTAMP)) {
            return;
        }

        String prefix = parameters.get(JobLauncherService.PARAM_PREFIX).toString();
        String datestamp = parameters.get(JobLauncherService.PARAM_DATESTAMP).toString();
        try {

            Thread.sleep(60000);
            int docCount = service.fetchDocCount(prefix, datestamp);
            if (docCount >= writeCount) {
                LOG.info("Index doc count: {}", docCount);
                LOG.info("Verifying the new index and replacing the alias.");
                service.replaceAlias(prefix, datestamp);
            } else {
               LOG.error("Write count {} is greater than index doc count {}. Skipping verification, aliasing", writeCount, docCount);
               LOG.error("We should do a manually alias switch");
            }
        } catch (Exception e) {
            LOG.error("Failed to verify job", e);
        }
    }
}
