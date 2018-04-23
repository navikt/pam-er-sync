package no.nav.pam.ad.es;

import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexerService {

    private static final Logger LOG = LoggerFactory.getLogger(IndexerService.class);
    private static final String INDEX_ALIAS = "underenheter";
    private static final int INDEX_EXPIRATION_IN_DAYS = 10;

    private final ElasticSearchIndexClient elasticSearchIndexClient;

    public IndexerService(ElasticSearchIndexClient elasticSearchIndexClient) {
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

    public void deleteIndex(String index) throws IOException {
        elasticSearchIndexClient.deleteIndex(index);
    }

    public void deleteOlderIndices() throws IOException {
        LocalDate date = LocalDate.now().minusDays(INDEX_EXPIRATION_IN_DAYS);
        List<String> indices = elasticSearchIndexClient.fetchAllIndicesStartingWith(INDEX_ALIAS);

        List<String> oldIndices = indices.stream().filter(index -> {
            String datestamp = index.replace(INDEX_ALIAS, "");
            LocalDate indexDate = DatestampUtil.parseDatestamp(datestamp);
            return indexDate.isBefore(date);
        }).collect(Collectors.toList());

        elasticSearchIndexClient.deleteIndex(oldIndices.toArray(new String[0]));
    }
}
