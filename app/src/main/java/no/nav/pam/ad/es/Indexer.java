package no.nav.pam.ad.es;

import com.google.common.io.CharStreams;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class Indexer {

    private static final Logger LOG = LoggerFactory.getLogger(Indexer.class);

    private final ElasticSearchIndexClient elasticSearchIndexClient;

    public Indexer(ElasticSearchIndexClient elasticSearchIndexClient) {
        this.elasticSearchIndexClient = elasticSearchIndexClient;
    }

    @PostConstruct
    public void initializeIndices() {

        try {
            final String commonSettingsJson = loadJsonStringFromClasspath("/ESCommonSettings.json");

//            if (!elasticSearchIndexClient.isExistingIndex(AD)) {
//                elasticSearchIndexClient.createIndex(new CreateIndexRequest(AD,
//                        Settings.builder().loadFromSource(commonSettingsJson, XContentType.JSON).build()));
//
//                String mappings = loadJsonStringFromClasspath("/ESAdMapping.json");
//                elasticSearchIndexClient.putFieldMapping(
//                        new PutMappingRequest(AD).source(mappings, XContentType.JSON).type(AD));
//            }
//            if (!elasticSearchIndexClient.isExistingIndex(COMPANY)) {
//                elasticSearchIndexClient.createIndex(new CreateIndexRequest(COMPANY,
//                        Settings.builder().loadFromSource(commonSettingsJson, XContentType.JSON).build()));
//
//                String mappings = loadJsonStringFromClasspath("/ESCompanyMapping.json");
//                elasticSearchIndexClient.putFieldMapping(
//                        new PutMappingRequest(COMPANY).source(mappings, XContentType.JSON).type(COMPANY));
//            }
        }
        catch (IOException e) {
            LOG.error("Could not initialize indices",e);
        }
    }

    private String loadJsonStringFromClasspath(String path) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream(path), StandardCharsets.UTF_8)) {
            return CharStreams.toString(reader);
        }
    }


    public void indexCompanies(List<Enhet> companyList) throws IOException {
        if (!companyList.isEmpty()) {
            BulkResponse bulkResponse = elasticSearchIndexClient.indexBulk(companyList, "enhetsindeks");
            reportBulkResponse(bulkResponse, "enhetsindeks");
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
            }
            else {
                success++;
            }
        }
        LOG.info("Indexed {} successfully and {} failed, index: {}", success, failed, index);
    }

}
