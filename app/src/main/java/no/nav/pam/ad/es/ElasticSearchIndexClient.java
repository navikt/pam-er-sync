package no.nav.pam.ad.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty(prefix = "elasticsearch", name = "usemock", havingValue = "false", matchIfMissing = true)
@Service
public class ElasticSearchIndexClient extends RestHighLevelClient {

    private final static Logger LOG = LoggerFactory.getLogger(ElasticSearchIndexClient.class);
    private final static String UNDERENHET_TYPE = "underenhet";

    private final ObjectMapper objectMapper;

    @Autowired
    public ElasticSearchIndexClient(RestClientBuilder client,
                                    ObjectMapper objectMapper) {
        super(client);
        this.objectMapper = objectMapper;
    }

    public void shutdown() throws IOException {
        close();
    }

    public Response deleteIndex(String index) throws IOException {
        return getLowLevelClient().performRequest("DELETE", "/" + index);
    }

    public boolean indexExists(String index) throws IOException {
        try {
            Response restResponse = getLowLevelClient().performRequest("GET", "/" + index);
            return true;
        } catch (ResponseException e) {
            LOG.debug("Exception while calling indexExists" + e.getMessage());
        }
        return false;
    }

    public Response replaceAlias(String alias, String indexDatestamp) throws IOException {

        String jsonString = "{\n" +
                "    \"actions\" : [\n" +
                "        { \"remove\" : { \"index\" : \"" + alias + "*\", \"alias\" : \"" + alias + "\" } },\n" +
                "        { \"add\" : { \"index\" : \"" + alias + indexDatestamp + "\", \"alias\" : \"" + alias + "\" } }\n" +
                "    ]\n" +
                "}";

        Map<String, String> params = Collections.emptyMap();
        HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);

        return getLowLevelClient().performRequest("POST", "/_aliases", params, entity);
    }

    public BulkResponse indexBulk(List<Enhet> contents, String index) throws IOException {

        BulkRequest request = new BulkRequest();

        for (Enhet content : contents) {
            request.add(new IndexRequest(index, UNDERENHET_TYPE, content.getOrganisasjonsnummer())
                    .source(objectMapper.writeValueAsString(content), XContentType.JSON));
        }
        return bulk(request);
    }

    public IndexResponse index(Enhet content, String index) throws IOException {

        IndexRequest request = new IndexRequest(index, UNDERENHET_TYPE, content.getOrganisasjonsnummer())
                .source(objectMapper.writeValueAsString(content), XContentType.JSON);
        return index(request);
    }

    public int fetchIndexDocCount(String index) throws IOException {
        Response response = getLowLevelClient().performRequest("GET", "/_cat/indices/" + index);
        String line = EntityUtils.toString(response.getEntity());
        String[] tokenized = line.split(" ");

        return Integer.parseInt(tokenized[6]);
    }
}