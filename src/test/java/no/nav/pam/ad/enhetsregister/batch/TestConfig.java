package no.nav.pam.ad.enhetsregister.batch;

import tools.jackson.databind.json.JsonMapper;
import no.nav.pam.ad.config.AppConfig;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.core.bulk.BulkResponseItem;
import org.opensearch.client.opensearch.core.bulk.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains configuration suitable for running tests "offline" by adding {@code @ContextConfiguration} to your test class.
 * Expand as needed for further tests.
 */
@TestConfiguration
@Import({AppConfig.class, TestBatchConfig.class})
public class TestConfig extends BatchConfig {


    public TestConfig(JobRepository jobRepository,
                      PlatformTransactionManager batchTransactionManager) {
        super(new JsonMapper(), jobRepository, batchTransactionManager);
    }

    @Override
    @Bean
    public Hovedenhet hovedenhet() {
        return new Hovedenhet(false, TestConfig.class.getResource("/enhetsregisteret.samples/enheter_alle.json.gz"));
    }

    @Override
    @Bean
    public Underenhet underenhet() {
        return new Underenhet(true, TestConfig.class.getResource("/enhetsregisteret.samples/underenheter_alle.json.gz"));
    }

    @Override
    @Bean(name = "jobCompletionNotificationListenerDelay")
    public long jobCompletionNotificationListenerDelay() {
        return 1000;
    }

    @Primary
    @Bean
    public IndexClient indexClient() {
        return new IndexClient();
    }

    public static class IndexClient implements no.nav.pam.ad.persistence.IndexClient {

        private static final Logger LOG = LoggerFactory.getLogger(IndexClient.class);

        private final Map<String, List<Enhet>> storage = new HashMap<>();

        @Override
        public void createIndex(String index, String settings) {

            LOG.debug("createIndex({}, settings)", index);
            storage.put(index, new ArrayList<>());

        }

        @Override
        public void deleteIndex(String... indices) {

            LOG.debug("deleteIndex({}) = false", (Object[]) indices); // We're keeping the indices for inspection.

        }

        @Override
        public boolean indexExists(String index) {

            boolean exists = storage.containsKey(index);
            LOG.debug("indexExists({}) = {}", index, exists);
            return exists;

        }

        @Override
        public void replaceAlias(String alias, String indexDatestamp) {

            LOG.debug("replaceAlias({}, {})", alias, indexDatestamp);

        }

        @Override
        public BulkResponse indexBulk(List<Enhet> contents, String index) {

            LOG.debug("indexBulk({}, {})", contents.size(), index);
            storage.put(index, contents);
            return new BulkResponse.Builder()
                    .errors(false)
                    .took(0L)
                    .items(contents.stream()
                            .map(enhet -> new BulkResponseItem.Builder()
                                    .operationType(OperationType.Index)
                                    .index(index)
                                    .id(enhet.organisasjonsnummer())
                                    .status(200)
                                    .build())
                            .toList())
                    .build();
        }

        @Override
        public int fetchIndexDocCount(String index) {

            List<Enhet> content = storage.get(index);
            int count = content == null ? 0 : content.size();
            LOG.info("fetchIndexDocCount({}) = {}", index, count);
            return count;
        }

        @Override
        public List<String> fetchAllIndicesStartingWith(String name) {

            LOG.info("fetchAllIndicesStartingWith({}) = []", name);
            return Collections.emptyList();

        }

        @Override
        public boolean isHealthy() {
            return true;
        }

        public Map<String, List<Enhet>> getStorage() {
            return storage;
        }

    }


}