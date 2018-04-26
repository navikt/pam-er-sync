package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.es.IndexerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JobExecutionListenerImpl implements JobExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(JobExecutionListenerImpl.class);


    @Autowired
    IndexerService indexer;


    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOG.info("Preconfiguring ES before job execution");

        if (jobExecution.getJobParameters().getParameters().containsKey(JobLauncherService.PARAM_DATESTAMP)) {
            String datestamp = jobExecution.getJobParameters().getParameters().get(JobLauncherService.PARAM_DATESTAMP).toString();

            try {
                indexer.createAndConfigure(datestamp);
            } catch (IOException e) {
                LOG.error("Couldn't create and configure index. ", e);
            }

        } else {
            LOG.error("No param with name {} could be found. Elastic Search index can't be configured.", JobLauncherService.PARAM_DATESTAMP);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

    }
}
