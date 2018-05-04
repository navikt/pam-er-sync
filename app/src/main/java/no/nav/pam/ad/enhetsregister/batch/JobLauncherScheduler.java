package no.nav.pam.ad.enhetsregister.batch;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class JobLauncherScheduler implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(JobLauncherScheduler.class);
    private static final String VALUE_CRON = "${enhetsregister.cron}";

    @Value(VALUE_CRON)
    private String cron;

    private final JobLauncherService service;
    private final Map<DataSet, URL> sync = new HashMap<>(DataSet.values().length);

    JobLauncherScheduler(
            JobLauncherService service,
            BatchConfig.Hovedenhet hovedenhet,
            BatchConfig.Underenhet underenhet
    ) {

        this.service = service;
        if (hovedenhet.isEnabled()) {
            sync.put(DataSet.HOVEDENHET, hovedenhet.getUrl());
        }
        if (underenhet.isEnabled()) {
            sync.put(DataSet.UNDERENHET, underenhet.getUrl());
        }
        if (sync.isEmpty()) {
            LOG.warn("No data sets are configured to be synchronized");
        }

    }

    @Override
    public void afterPropertiesSet() {
        LOG.info("Initialized with cron {}", cron);
    }

    @Scheduled(cron = VALUE_CRON)
    @SchedulerLock(name = "JobLauncherScheduler.run")
    private void run() {

        LOG.info("Starting scheduled synchronization of data set(s) {}", sync.keySet());
        sync.forEach((dataSet, url) -> {
            try {
                service.synchronize(dataSet, url);
            } catch (Exception e) {
                LOG.error("Failed to run scheduled job, will retry with cron {}", cron, e);
            }
        });
        LOG.info("Finished scheduled synchronization of data set(s) {}", sync.keySet());

    }

}
