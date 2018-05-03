package no.nav.pam.ad.es.scheduled;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.pam.ad.enhetsregister.batch.DataSet;
import no.nav.pam.ad.es.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class DeleteOlderIndicesScheduledJob {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteOlderIndicesScheduledJob.class);

    private final IndexService indexerService;

    @Autowired
    public DeleteOlderIndicesScheduledJob(IndexService indexerService) {
        this.indexerService = indexerService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    @SchedulerLock(name = "deleteOlderIndices")
    public void deleteOlderIndices() {

        try {
            for (DataSet set : DataSet.values()) {
                indexerService.deleteOlderIndices(set.toString());
            }
            LOG.info("Older indices are deleted.");
        } catch (IOException e) {
            LOG.error("Failed to execute job deleteOlderIndices.", e);
        }
    }
}

