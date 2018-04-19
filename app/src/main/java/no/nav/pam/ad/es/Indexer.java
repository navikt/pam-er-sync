package no.nav.pam.ad.es;

import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class Indexer {

    private static final Logger LOG = LoggerFactory.getLogger(Indexer.class);
    private static final String INDEX_ALIAS = "underenheter";

    private final ElasticSearchIndexClient elasticSearchIndexClient;

    public Indexer(ElasticSearchIndexClient elasticSearchIndexClient) {
        this.elasticSearchIndexClient = elasticSearchIndexClient;
    }

    public void replaceAlias(String indexDatestamp) throws IOException {
        if (elasticSearchIndexClient.indexExists(INDEX_ALIAS + indexDatestamp)) {
            elasticSearchIndexClient.replaceAlias(INDEX_ALIAS, indexDatestamp);
            LOG.info("Successfully replaced aliases. Index {} is now aliased to {}", INDEX_ALIAS + indexDatestamp, INDEX_ALIAS);
        } else {
            LOG.error("Failed to replace the alias. New index {} doesn't exist", INDEX_ALIAS + indexDatestamp);
        }
    }

    public int fetchDocCount(String indexDatestamp) throws IOException {
        return elasticSearchIndexClient.fetchIndexDocCount(INDEX_ALIAS + indexDatestamp);
    }

    public void indexCompanies(List<Enhet> companyList, String indexDatestamp) throws IOException {
        String index = INDEX_ALIAS + indexDatestamp;

        if (!companyList.isEmpty()) {
            BulkResponse bulkResponse = elasticSearchIndexClient.indexBulk(companyList, index);
            reportBulkResponse(bulkResponse, index);
        }
    }

    private void reportBulkResponse(BulkResponse bulkResponse, String index) {
        int failed = 0;
        int success = 0;
        for (BulkItemResponse item : bulkResponse.getItems()) {
            if (item.isFailed()) {
                // TODO implement failed handling later
                LOG.error("Failed item: {}, index: {}", item.getFailureMessage(), index);
                failed++;
            } else {
                success++;
            }
        }
        LOG.info("Indexed {} successfully and {} failed, index: {}", success, failed, index);
    }
}
