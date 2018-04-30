package no.nav.pam.ad.es;

import com.google.common.io.CharStreams;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
public class IndexService {

    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);
    private static final String CLASSPATH_SETTINGS = "/ESUnderenheterSetting.json";
    private static final int INDEX_EXPIRATION_IN_DAYS = 10;

    private final IndexClient client;

    public IndexService(IndexClient client) {
        this.client = client;
    }

    private static String indexOf(String prefix, String datestamp) {
        return (prefix + datestamp);
    }

    public void createAndConfigure(String prefix, String datestamp)
            throws IOException {

        String index = prefix + datestamp;
        LOG.info("Creating and configuring index {}", index);
        if (!client.indexExists(index)) {
            client.createIndex(index, getSettingsFromClasspath());
            LOG.info("Index {} was successfully created with settings and mappings", index);
        }

    }

    private static String getSettingsFromClasspath()
            throws IOException {
        try (InputStreamReader reader = new InputStreamReader(IndexService.class.getResourceAsStream(CLASSPATH_SETTINGS), StandardCharsets.UTF_8)) {
            return CharStreams.toString(reader);
        }
    }

    public void replaceAlias(String prefix, String datestamp)
            throws IOException {

        String index = prefix + datestamp;
        if (client.indexExists(index)) {
            client.replaceAlias(prefix, datestamp);
            LOG.info("Successfully replaced aliases. Index {} is now aliased to {}", index, prefix);
        } else {
            LOG.error("Failed to replace the alias. New index {} doesn't exist", index);
        }

    }

    public int fetchDocCount(String prefix, String datestamp)
            throws IOException {
        return client.fetchIndexDocCount(indexOf(prefix, datestamp));
    }

    public void indexCompanies(String prefix, String datestamp, List<Enhet> list) throws IOException {

        String index = prefix + datestamp;
        if (!list.isEmpty()) {
            BulkResponse bulkResponse = client.indexBulk(list, index);
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

/*
    public void deleteIndex(String index) throws IOException {
        client.deleteIndex(index);
    }
*/

    public void deleteOlderIndices(String prefix)
            throws IOException {

        LocalDate maxAge = LocalDate.now().minusDays(INDEX_EXPIRATION_IN_DAYS);
        client.deleteIndex(
                client
                        .fetchAllIndicesStartingWith(prefix)
                        .stream()
                        .filter(index -> Datestamp.parseFrom(index.replace(prefix, "")).isBefore(maxAge))
                        .toArray(String[]::new)
        );

    }

}
