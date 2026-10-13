package no.nav.pam.ad.enhetsregister.batch;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import no.nav.pam.ad.persistence.IndexClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

class IndexAgeMetrics {

    private static final Logger LOG = LoggerFactory.getLogger(IndexAgeMetrics.class);
    private static final String METRIC_NAME = "enhetsregister.index.created.timestamp.seconds";
    private static final long UNKNOWN = 0;

    private final IndexClient client;
    private final Map<DataSet, AtomicLong> createdEpochSeconds = new EnumMap<>(DataSet.class);

    IndexAgeMetrics(IndexClient client, MeterRegistry registry, Set<DataSet> dataSets) {
        this.client = client;
        dataSets.forEach(dataSet -> createdEpochSeconds.put(dataSet, registry.gauge(
                METRIC_NAME,
                Tags.of("dataset", dataSet.name().toLowerCase(Locale.ROOT)),
                new AtomicLong(UNKNOWN))));
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    void refresh() {
        createdEpochSeconds.forEach((dataSet, created) -> created.set(fetchCreatedEpochSeconds(dataSet)));
    }

    private long fetchCreatedEpochSeconds(DataSet dataSet) {
        try {
            return client.fetchCreationTime(dataSet.name()).map(Instant::getEpochSecond).orElse(UNKNOWN);
        } catch (IOException | RuntimeException e) {
            LOG.warn("Failed to fetch creation time of index aliased {}", dataSet, e);
            return UNKNOWN;
        }
    }

}
