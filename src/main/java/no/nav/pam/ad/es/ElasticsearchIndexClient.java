package no.nav.pam.ad.es;

import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.List;

/**
 * Elasticsearch client implementation.
 * <br/><br>
 * Note that in cases where parameters are used as part of an index name, the value(s) are converted to lower case before being used.
 */
@Service
public class ElasticsearchIndexClient implements IndexClient {

    private final static Logger LOG = LoggerFactory.getLogger(ElasticsearchIndexClient.class);

    private final OpenSearchClient openSearchClient;
    private final RestClient lowLevelClient;

    public ElasticsearchIndexClient(OpenSearchClient openSearchClient,
                                    RestClient lowLevelClient) {
        this.openSearchClient = openSearchClient;
        this.lowLevelClient = lowLevelClient;
    }

    @Override
    public void createIndex(String index, String settings)
            throws IOException {

        String lowerCaseIndex = lower(index);
        Request request = new Request("PUT", "/" + lowerCaseIndex);
        request.setEntity(new StringEntity(settings, ContentType.APPLICATION_JSON));
        lowLevelClient.performRequest(request);

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
        Response response = lowLevelClient.performRequest(new Request("GET", "/_cat/indices/" + lowerCaseIndex));
        String line = responseBody(response);
        return Integer.parseInt(line.split(" ")[6]);

    }

    @Override
    public List<String> fetchAllIndicesStartingWith(String name)
            throws IOException {

        String lowerCaseName = lower(name);
        List<String> indices = new ArrayList<>();
        Response response = lowLevelClient.performRequest(new Request("GET", "/_cat/indices/" + lowerCaseName + "*"));

        String full = responseBody(response);

        if (!StringUtils.isBlank(full)) {
            String[] lines = full.split("\\r?\\n");

            for (String line : lines) {
                String[] tokenized = line.split("\\s");
                indices.add(tokenized[2]);
            }
        }

        return indices;
    }

    @Override
    public boolean isHealthy()
            throws IOException {
        return openSearchClient.ping().value();
    }

    private String responseBody(Response response) throws IOException {
        try {
            return EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            throw new IOException("Failed to parse OpenSearch response", e);
        }
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
