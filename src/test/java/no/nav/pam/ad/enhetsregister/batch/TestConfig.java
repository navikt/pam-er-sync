package no.nav.pam.ad.enhetsregister.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.config.AppConfig;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.enhetsregister.rest.EnhetsregisterBatchControllerTest;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains configuration suitable for running tests "offline" by adding {@code @ContextConfiguration} to your test class.
 * Expand as needed for further tests.
 */
@TestConfiguration
@Import({AppConfig.class})
public class TestConfig extends BatchConfig {

    @Autowired
    public TestConfig() {
        super(new ObjectMapper());
    }

    @Override
    @Bean
    public Hovedenhet hovedenhet() {
        return new Hovedenhet(false, EnhetsregisterBatchControllerTest.class.getResource("/enhetsregisteret.samples/enheter_alle.json.gz"));
    }

    @Override
    @Bean
    public Underenhet underenhet() {
        return new Underenhet(true, EnhetsregisterBatchControllerTest.class.getResource("/enhetsregisteret.samples/underenheter_alle.json.gz"));
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

    public static class IndexClient implements no.nav.pam.ad.es.IndexClient {

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

            LOG.debug("indexBulk({}, {}) = {}", contents.size(), index, DocWriteRequest.OpType.CREATE);
            storage.put(index, contents);
            BulkItemResponse[] responses = new BulkItemResponse[]{
                    new BulkItemResponse(1337, DocWriteRequest.OpType.CREATE, (BulkItemResponse.Failure) null)
            };
            return new BulkResponse(responses, 0);
        }

        @Override
        public int fetchIndexDocCount(String index) {

            List<Enhet> content = storage.get(index);
            int count = content == null ? 0 : content.size();
            LOG.info("fetchIndexDocCount({}) = {}", index, count);
            return 0;
        }

        @Override
        public List<String> fetchAllIndicesStartingWith(String name) {

            LOG.info("fetchAllIndicesStartingWith({}) = null", name);
            return null;

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
