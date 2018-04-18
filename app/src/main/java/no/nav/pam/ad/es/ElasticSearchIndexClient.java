package no.nav.pam.ad.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
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
    private final static String TYPE = "underenhet";

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

    public Response createIndex(CreateIndexRequest request) throws IOException {
        StringEntity entity = null;
        String index = request.index();
        if (request.settings() != null) {
            Map<String, Object> settingsMap = request.settings().getAsStructuredMap();
            String settings = objectMapper.writeValueAsString(settingsMap);
            entity = new StringEntity(settings, ContentType.APPLICATION_JSON);
        }
        LOG.debug("creating index: " + index);
        return getLowLevelClient().performRequest("PUT", "/" + index, Collections.emptyMap(), entity);
    }

    public void putFieldMapping(PutMappingRequest request) throws IOException {
        StringEntity entity = new StringEntity(request.source(), ContentType.APPLICATION_JSON);
        for (String index : request.indices()) {
            LOG.debug("updating mappings for index: " + index);
            getLowLevelClient().performRequest("PUT", "/" + index
                    + "/" + request.type() + "/_mapping", Collections.emptyMap(), entity);
        }
    }

    public Response deleteIndex(String index) throws IOException {
        return getLowLevelClient().performRequest("DELETE", "/" + index);
    }

    public boolean isExistingIndex(String index) throws IOException {
        try {
            Response restResponse = getLowLevelClient().performRequest("GET", "/" + index);
            return true;
        } catch (ResponseException e) {
            LOG.debug("Exception while calling isExistingIndex" + e.getMessage());
        }
        return false;
    }

    public BulkResponse indexBulk(List<Enhet> contents, String index) throws IOException {

        BulkRequest request = new BulkRequest();

        for (Enhet content : contents) {
            request.add(new IndexRequest(index, TYPE, content.getOrganisasjonsnummer())
                    .source(objectMapper.writeValueAsString(content), XContentType.JSON));
        }
        return bulk(request);
    }


    public IndexResponse index(Enhet content, String index) throws IOException {

        IndexRequest request = new IndexRequest(index, TYPE, content.getOrganisasjonsnummer())
                .source(objectMapper.writeValueAsString(content), XContentType.JSON);
        return index(request);
    }
}