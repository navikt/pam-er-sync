package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.persistence.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static no.nav.pam.ad.enhetsregister.batch.JobLauncherService.PARAM_DATESTAMP;
import static no.nav.pam.ad.enhetsregister.batch.JobLauncherService.PARAM_PREFIX;

@Component
public class JobExecutionListenerImpl implements JobExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(JobExecutionListenerImpl.class);

    private final IndexService service;

    public JobExecutionListenerImpl(IndexService service) {
        this.service = service;
    }


    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOG.info("Preconfiguring OpenSearch before job execution");

        String prefix = jobExecution.getJobParameters().getString(PARAM_PREFIX);
        String datestamp = jobExecution.getJobParameters().getString(PARAM_DATESTAMP);
        if (prefix != null && datestamp != null) {
            try {
                service.createAndConfigure(prefix, datestamp);
            } catch (IOException e) {
                LOG.error("Couldn't create and configure index. ", e);
            }

        } else {
            LOG.error("No param with name {} could be found. OpenSearch index can't be configured.", PARAM_DATESTAMP);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Does nothing.
    }

}
