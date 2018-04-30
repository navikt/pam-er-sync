package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.es.IndexerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger LOG = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    IndexerService indexer;

    @Override
    public void afterJob(JobExecution jobExecution) {

        String datestamp = null;

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            LOG.info("!!! JOB FINISHED! Time to verify the results");

            int writeCount = 0;
            int skipCount = 0;
            for (StepExecution e : jobExecution.getStepExecutions()) {
                writeCount += e.getWriteCount();
                skipCount += e.getSkipCount();
            }
            LOG.info("Total write count: {}, skip count {}", writeCount, skipCount);

            if (jobExecution.getJobParameters().getParameters().containsKey(JobLauncherService.PARAM_DATESTAMP)) {
                datestamp = jobExecution.getJobParameters().getParameters().get(JobLauncherService.PARAM_DATESTAMP).toString();

                try {
                    int docCount = indexer.fetchDocCount(datestamp);

                    if (docCount >= writeCount) {
                        LOG.info("Index doc count: {}", docCount);
                        LOG.info("Verifying the new index and replacing the alias.");
                        indexer.replaceAlias(datestamp);
                    } else {
                        LOG.error("Write count {} is greater than index doc count {}. Skipping verification, aliasing and deleting the new index.", writeCount, docCount);
                        indexer.deleteIndexWithDatestamp(datestamp);
                    }
                } catch (Exception e) {
                    LOG.error("Failed to verify job", e);
                }
            }
        } else {
            LOG.error("Batch job for indexing didn't complete.");
        }
    }

}