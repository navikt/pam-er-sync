package no.nav.pam.ad.persistence;

import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.cat.IndicesResponse;
import org.opensearch.client.opensearch.cat.indices.IndicesRecord;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.generic.OpenSearchGenericClient;
import org.opensearch.client.opensearch.generic.Requests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.List;

@Service
class OpenSearchIndexClient implements IndexClient {

    private static final Logger LOG = LoggerFactory.getLogger(OpenSearchIndexClient.class);

    private final OpenSearchClient openSearchClient;
    private final OpenSearchGenericClient genericClient;

    public OpenSearchIndexClient(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
        this.genericClient = openSearchClient.generic()
                .withClientOptions(OpenSearchGenericClient.ClientOptions.throwOnHttpErrors());
    }

    @Override
    public void createIndex(String index, String settings)
            throws IOException {

        String lowerCaseIndex = lower(index);

        try (var _ = genericClient.execute(Requests.builder()
                .endpoint("/" + lowerCaseIndex)
                .method("PUT")
                .json(settings)
                .build())) {
            LOG.debug("createIndex({})", lowerCaseIndex);
        }

    }

    @Override
    public void deleteIndex(String... indices)
            throws IOException {

        String[] lowerCaseIndices = lower(indices);

        if (lowerCaseIndices.length > 0) {
            openSearchClient.indices().delete(request -> request.index(Arrays.asList(lowerCaseIndices)));
        }
    }

    @Override
    public boolean indexExists(String index)
            throws IOException {

        String lowerCaseIndex = lower(index);
        return openSearchClient.indices().exists(request -> request.index(lowerCaseIndex)).value();

    }

    @Override
    public void replaceAlias(String alias, String indexDatestamp)
            throws IOException {

        String lowerCaseAlias = lower(alias);
        openSearchClient.indices().updateAliases(request -> request
                .actions(action -> action.remove(remove -> remove
                        .index("underenhet*")
                        .alias(lowerCaseAlias)))
                .actions(action -> action.add(add -> add
                        .index(lowerCaseAlias + indexDatestamp)
                        .alias(lowerCaseAlias))));
    }

    @Override
    public BulkResponse indexBulk(List<Enhet> contents, String index)
            throws IOException {

        String lowerCaseIndex = lower(index);
        BulkRequest.Builder request = new BulkRequest.Builder();

        for (Enhet content : contents) {
            request.operations(operation -> operation.index(doc -> doc
                    .index(lowerCaseIndex)
                    .id(content.organisasjonsnummer())
                    .document(content)));
        }
        return openSearchClient.bulk(request.build());

    }

    @Override
    public int fetchIndexDocCount(String index)
            throws IOException {

        String lowerCaseIndex = lower(index);
        IndicesResponse response = openSearchClient.cat().indices(request -> request.index(lowerCaseIndex));

        return response.valueBody().stream()
                .map(IndicesRecord::docsCount)
                .filter(StringUtils::isNotBlank)
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(0);

    }

    @Override
    public List<String> fetchAllIndicesStartingWith(String name)
            throws IOException {

        String lowerCaseName = lower(name);
        IndicesResponse response = openSearchClient.cat().indices(request -> request.index(lowerCaseName + "*"));

        List<String> indices = new ArrayList<>();
        for (IndicesRecord record : response.valueBody()) {
            if (StringUtils.isNotBlank(record.index())) {
                indices.add(record.index());
            }
        }

        return indices;
    }

    @Override
    public boolean isHealthy()
            throws IOException {
        return openSearchClient.ping().value();
    }

    private static String[] lower(String... values) {
        String[] lowered = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            lowered[i] = lower(values[i]);
        }
        return lowered;
    }

    private static String lower(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

}
