package no.nav.pam.ad.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Elasticsearch client implementation.
 * <br/><br>
 * Note that in cases where parameters are used as part of an index name, the value(s) are converted to lower case before being used.
 */
@ConditionalOnProperty(prefix = "elasticsearch", name = "usemock", havingValue = "false", matchIfMissing = true)
@Service
public class ElasticsearchIndexClient extends RestHighLevelClient implements IndexClient {

    private final static Logger LOG = LoggerFactory.getLogger(ElasticsearchIndexClient.class);
    private final static String UNDERENHET_TYPE = "underenhet";

    private final ObjectMapper objectMapper;

    @Autowired
    public ElasticsearchIndexClient(RestClientBuilder client,
                                    ObjectMapper objectMapper) {
        super(client);
        this.objectMapper = objectMapper;
    }

    @Override
    public void createIndex(String index, String settings)
            throws IOException {

        String lowerCaseIndex = index.toLowerCase();
        indices().create(new CreateIndexRequest(lowerCaseIndex).source(settings, XContentType.JSON));

    }

    @Override
    public void deleteIndex(String... indices)
            throws IOException {

        String[] lowerCaseIndices = Arrays.stream(indices).map(String::toLowerCase).toArray(String[]::new);
        indices().delete(new DeleteIndexRequest(lowerCaseIndices));


    }

    @Override
    public boolean indexExists(String index)
            throws IOException {

        String lowerCaseIndex = index.toLowerCase();
        try {
            getLowLevelClient().performRequest("GET", "/" + lowerCaseIndex);
            return true;
        } catch (ResponseException e) {
            LOG.debug("Exception while calling indexExists" + e.getMessage());
        }
        return false;

    }

    @Override
    public void replaceAlias(String alias, String indexDatestamp)
            throws IOException {

        String lowerCaseAlias = alias.toLowerCase();
        String jsonString = "{\n" +
                "    \"actions\" : [\n" +
                "        { \"remove\" : { \"index\" : \"" + lowerCaseAlias + "*\", \"alias\" : \"" + lowerCaseAlias + "\" } },\n" +
                "        { \"add\" : { \"index\" : \"" + lowerCaseAlias + indexDatestamp + "\", \"alias\" : \"" + lowerCaseAlias + "\" } }\n" +
                "    ]\n" +
                "}";
        getLowLevelClient().performRequest(
                "POST",
                "/_aliases",
                Collections.emptyMap(),
                new NStringEntity(jsonString, ContentType.APPLICATION_JSON)
        );

    }

    @Override
    public BulkResponse indexBulk(List<Enhet> contents, String index)
            throws IOException {

        String lowerCaseIndex = index.toLowerCase();
        BulkRequest request = new BulkRequest();

        for (Enhet content : contents) {
            request.add(new IndexRequest(lowerCaseIndex, UNDERENHET_TYPE, content.getOrganisasjonsnummer())
                    .source(objectMapper.writeValueAsString(content), XContentType.JSON));
        }
        return bulk(request);

    }

    @Override
    public int fetchIndexDocCount(String index)
            throws IOException {

        String lowerCaseIndex = index.toLowerCase();
        getLowLevelClient().performRequest("POST", "/" + lowerCaseIndex + "/_refresh");
        Response response = getLowLevelClient().performRequest("GET", "/_cat/indices/" + lowerCaseIndex);
        String line = EntityUtils.toString(response.getEntity());
        return Integer.parseInt(line.split(" ")[6]);

    }

    @Override
    public List<String> fetchAllIndicesStartingWith(String name)
            throws IOException {

        String lowerCaseName = name.toLowerCase();
        List<String> indices = new ArrayList<>();
        Response response = getLowLevelClient().performRequest("GET", "/_cat/indices/" + lowerCaseName + "*");

        String full = EntityUtils.toString(response.getEntity());
        String[] lines = full.split("\\r?\\n");

        for (String line : lines) {
            String[] tokenized = line.split("\\s");
            indices.add(tokenized[2]);
        }

        return indices;

    }

}