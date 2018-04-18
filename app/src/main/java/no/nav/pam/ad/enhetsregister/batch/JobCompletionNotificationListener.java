package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.es.Indexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger LOG = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    Indexer indexer;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            LOG.info("!!! JOB FINISHED! Time to verify the results");

            if (jobExecution.getJobParameters().getParameters().containsKey("datestamp")) {
                String indexSuffix = jobExecution.getJobParameters().getParameters().get("datestamp").toString();

                try {
                    LOG.info("Verifying the new index and replacing the alias.");
                    indexer.replaceAlias(indexSuffix);

                } catch (IOException e) {
                    LOG.error("Failed to verify job", e);
                }
            }
        }
    }
}