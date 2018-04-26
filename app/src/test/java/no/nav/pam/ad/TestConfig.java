package no.nav.pam.ad;

import no.nav.pam.ad.config.AppConfig;
import no.nav.pam.ad.enhetsregister.batch.BatchConfig;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.enhetsregister.rest.EnhetsregisterBatchControllerTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains configuration suitable for running tests "offline" by adding {@code @ContextConfiguration} to your test class.
 * Expand as needed for further tests.
 */
@TestConfiguration
@Import({BatchConfig.class, AppConfig.class})
public class TestConfig {

    @Bean(name = "enhetsregister.hovedenhet.url")
    public URL enhetsregisterHovedenhetUrl() {
        return EnhetsregisterBatchControllerTest.class.getResource("/enhetsregisteret.samples/hovedenheter.csv.gz");
    }

    @Bean(name = "enhetsregister.underenhet.url")
    public URL enhetsregisterUnderenhetUrl() {
        return EnhetsregisterBatchControllerTest.class.getResource("/enhetsregisteret.samples/underenheter.csv.gz");
    }

    @Primary
    @Bean
    public no.nav.pam.ad.es.IndexerService indexerService() {
        return new IndexerService();
    }

    /**
     * A simple extension of {@link IndexerService} for convenient access to the resulting "index". Run your test with a custom
     * {@link IndexerService} bean configuration to use, see {@link no.nav.pam.ad.enhetsregister.rest.EnhetsregisterBatchControllerTest}.
     * <br/><br/>
     * Might need some more work if more advanced tests are to be written, for example, {@link #replaceAlias(String)} doesn't do anything.
     */
    public static class IndexerService implements no.nav.pam.ad.es.IndexerService {

        private static final Logger LOG = LoggerFactory.getLogger(IndexerService.class);
        private static final String INDEX_ALIAS = "ALIAS";

        private final Map<String, List<Enhet>> index = new HashMap<>();

        private IndexerService() {
        }

        @Override
        public void createAndConfigure(String indexDatestamp) {
            LOG.debug("createAndConfigure({}) = false", indexDatestamp);
        }

        @Override
        public void replaceAlias(String indexDatestamp) {
            LOG.debug("replaceAlias({}) = false", indexDatestamp);
        }

        @Override
        public int fetchDocCount(String indexDatestamp) {

            List<Enhet> list = index.get(INDEX_ALIAS + indexDatestamp);
            int docCount = list == null ? 0 : list.size();
            LOG.debug("fetchDocCount({}) = {}", indexDatestamp, docCount);
            return docCount;

        }

        @Override
        public void indexCompanies(List<Enhet> companyList, String indexDatestamp) {

            index.put(INDEX_ALIAS + indexDatestamp, companyList);
            LOG.debug("indexCompanies({}, {}) = true", companyList.size(), indexDatestamp);

        }

        @Override
        public void deleteIndex(String index) {

            List<Enhet> removed = this.index.remove(index);
            LOG.debug("deleteIndex({}) = {}", index, removed != null);

        }

        @Override
        public void deleteOlderIndices() {

            LOG.debug("deleteOlderIndices() = false");

        }

        /**
         * Get the contents of the "index".
         *
         * @return The "index", with alias + timestamp as key(s).
         */
        public Map<String, List<Enhet>> getIndex() {
            return index;
        }

    }

}
