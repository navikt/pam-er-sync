package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.es.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

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
        LOG.info("Preconfiguring ES before job execution");

        Map<String, JobParameter<?>> parameters = jobExecution.getJobParameters().getParameters();
        if (parameters.containsKey(PARAM_DATESTAMP)) {

            String prefix = parameters.get(PARAM_PREFIX).toString();
            String datestamp = parameters.get(PARAM_DATESTAMP).toString();
            try {
                service.createAndConfigure(prefix, datestamp);
            } catch (IOException e) {
                LOG.error("Couldn't create and configure index. ", e);
            }

        } else {
            LOG.error("No param with name {} could be found. Elastic Search index can't be configured.", PARAM_DATESTAMP);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Does nothing.
    }

}
