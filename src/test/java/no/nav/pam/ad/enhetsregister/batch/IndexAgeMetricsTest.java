package no.nav.pam.ad.enhetsregister.batch;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import no.nav.pam.ad.persistence.IndexClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexAgeMetricsTest {

    private static final String METRIC_NAME = "enhetsregister.index.created.timestamp.seconds";
    private static final Instant CREATED = Instant.parse("2026-10-13T03:30:00Z");

    @Mock
    private IndexClient client;

    private final SimpleMeterRegistry registry = new SimpleMeterRegistry();

    @Test
    void shouldReportCreationTimeOfAliasedIndex() throws IOException {
        when(client.fetchCreationTime("UNDERENHET")).thenReturn(Optional.of(CREATED));

        refresh();

        assertThat(gauge("underenhet")).isEqualTo(CREATED.getEpochSecond());
    }

    @Test
    void shouldReportZeroWhenOpenSearchFails() throws IOException {
        when(client.fetchCreationTime("UNDERENHET")).thenThrow(new IOException("no such index [underenhet]"));

        refresh();

        assertThat(gauge("underenhet")).isZero();
    }

    @Test
    void shouldOnlyRegisterConfiguredDataSets() {
        refresh();

        assertThat(registry.find(METRIC_NAME).tag("dataset", "hovedenhet").gauge()).isNull();
    }

    private void refresh() {
        new IndexAgeMetrics(client, registry, Set.of(DataSet.UNDERENHET)).refresh();
    }

    private double gauge(String dataSet) {
        return registry.get(METRIC_NAME).tag("dataset", dataSet).gauge().value();
    }

}
