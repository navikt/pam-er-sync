package no.nav.pam.ad.enhetsregister.batch;

import no.nav.pam.ad.es.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static no.nav.pam.ad.enhetsregister.batch.BatchConfig.PROPERTY_ENHETSREGISTER_SCHEDULER_CRON;

class BatchScheduler implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(BatchScheduler.class);
    private static final String VALUE_CRON = "${" + PROPERTY_ENHETSREGISTER_SCHEDULER_CRON + "}";

    @Value(VALUE_CRON)
    private String cron;

    private final JobLauncherService jobService;
    private final IndexService indexService;
    private final Map<DataSet, URL> sync = new HashMap<>(DataSet.values().length);

    BatchScheduler(
            JobLauncherService jobService,
            IndexService indexService,
            BatchConfig.Hovedenhet hovedenhet,
            BatchConfig.Underenhet underenhet
    ) {

        this.jobService = jobService;
        this.indexService = indexService;
        hovedenhet.getUrl().ifPresent(url -> sync.put(DataSet.HOVEDENHET, url));
        underenhet.getUrl().ifPresent(url -> sync.put(DataSet.UNDERENHET, url));
        if (sync.isEmpty()) {
            LOG.warn("No data sets are configured to be synchronized");
        }

    }

    @Override
    public void afterPropertiesSet() {
        LOG.info("Initialized with cron {}", cron);
    }

    @Scheduled(cron = VALUE_CRON)
    private void run() {

        LOG.info("Starting scheduled synchronization of data set(s) {}", sync.keySet());
        sync.forEach((dataSet, url) -> {
            try {
                jobService.synchronize(dataSet, url);
            } catch (Exception e) {
                LOG.error("Failed to run scheduled synchronization of data set {}, will retry with cron {}", dataSet, cron, e);
            }
        });
        LOG.info("Finished scheduled synchronization of data set(s) {}", sync.keySet());

    }

    @Scheduled(cron = "0 0 1 * * *")
    public void cleanup() {

        try {
            for (DataSet set : DataSet.values()) {
                indexService.deleteOlderIndices(set.toString());
            }
            LOG.info("Deleted older indices");
        } catch (IOException e) {
            LOG.error("Failed to delete older indices", e);
        }

    }

}
