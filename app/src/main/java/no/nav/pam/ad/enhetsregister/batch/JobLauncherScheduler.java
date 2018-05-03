package no.nav.pam.ad.enhetsregister.batch;

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

@Component
@ConditionalOnProperty("enhetsregister.cron")
@Profile("!test")
class JobLauncherScheduler implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(JobLauncherScheduler.class);
    private static final String VALUE_CRON = "${enhetsregister.cron}";

    @Value(VALUE_CRON)
    private String cron;

    private final JobLauncherService service;
    private final URL enhetsregisterHovedenhetUrl;
    private final URL enhetsregisterUnderenhetUrl;

    public JobLauncherScheduler(
            JobLauncherService service,
            @Qualifier("enhetsregister.hovedenhet.enabled") boolean enhetsregisterHovedenhetEnabled,
            @Qualifier("enhetsregister.hovedenhet.url") URL enhetsregisterHovedenhetUrl,
            @Qualifier("enhetsregister.underenhet.enabled") boolean enhetsregisterUnderenhetEnabled,
            @Qualifier("enhetsregister.underenhet.url") URL enhetsregisterUnderenhetUrl
    ) {

        this.service = service;
        this.enhetsregisterHovedenhetUrl = enhetsregisterHovedenhetEnabled ? enhetsregisterHovedenhetUrl : null;
        this.enhetsregisterUnderenhetUrl = enhetsregisterUnderenhetEnabled ? enhetsregisterUnderenhetUrl : null;
        if (enhetsregisterHovedenhetUrl == null && enhetsregisterUnderenhetUrl == null) {
            LOG.warn("No data sets are configured to be synchronized");
        }

    }

    @Override
    public void afterPropertiesSet() {
        LOG.info("Initialized with cron {}", cron);
    }

    @Scheduled(cron = VALUE_CRON)
    private void run() {

        try {
            if (enhetsregisterHovedenhetUrl != null) {
                service.synchronize(DataSet.HOVEDENHET, enhetsregisterHovedenhetUrl);
            }
            if (enhetsregisterUnderenhetUrl != null) {
                service.synchronize(DataSet.UNDERENHET, enhetsregisterUnderenhetUrl);
            }
        } catch (Exception e) {
            LOG.error("Failed to run scheduled job, will retry with cron {}", cron, e);
        }

    }

}
