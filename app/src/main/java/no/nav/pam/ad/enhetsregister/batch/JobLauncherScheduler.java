package no.nav.pam.ad.enhetsregister.batch;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty("enhetsregister.cron")
@Profile("!test")
class JobLauncherScheduler implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(JobLauncherScheduler.class);
    private static final String VALUE_CRON = "${enhetsregister.cron}";

    @Value(VALUE_CRON)
    private String cron;

    private final JobLauncherService service;
    private final Map<DataSet, URL> sync = new HashMap<>(DataSet.values().length);

    public JobLauncherScheduler(
            JobLauncherService service,
            @Qualifier("enhetsregister.hovedenhet.enabled") boolean enhetsregisterHovedenhetEnabled,
            @Qualifier("enhetsregister.hovedenhet.url") URL enhetsregisterHovedenhetUrl,
            @Qualifier("enhetsregister.underenhet.enabled") boolean enhetsregisterUnderenhetEnabled,
            @Qualifier("enhetsregister.underenhet.url") URL enhetsregisterUnderenhetUrl
    ) {

        this.service = service;
        if (enhetsregisterHovedenhetEnabled) {
            sync.put(DataSet.HOVEDENHET, enhetsregisterHovedenhetUrl);
        }
        if (enhetsregisterUnderenhetEnabled) {
            sync.put(DataSet.UNDERENHET, enhetsregisterUnderenhetUrl);
        }
        if (sync.size() == 0) {
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
