package no.nav.pam.ad.persistence.rest;

import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.persistence.IndexClient;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenSearchControllerTest {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @TestConfiguration
    static class TestIndexClientConfig {
        @Bean
        @Primary
        IndexClient indexClient() {
            return new IndexClient() {
                @Override
                public void createIndex(String index, String settings) {}

                @Override
                public void deleteIndex(String... indices) {}

                @Override
                public boolean indexExists(String index) {
                    return true;
                }

                @Override
                public void replaceAlias(String alias, String indexDatestamp) {}

                @Override
                public BulkResponse indexBulk(List<Enhet> contents, String index) {
                    return null;
                }

                @Override
                public int fetchIndexDocCount(String index) {
                    return 0;
                }

                @Override
                public List<String> fetchAllIndicesStartingWith(String name) {
                    return List.of();
                }

                @Override
                public boolean isHealthy() {
                    return true;
                }
            };
        }
    }

    @Test
    void supportsBothLegacyAndNewAliasPaths() {
        assertStatus(HttpMethod.PUT, "/internal/enhetsregister/es/alias/underenhet/20260101", 200);
        assertStatus(HttpMethod.PUT, "/internal/enhetsregister/opensearch/alias/underenhet/20260101", 200);
        assertStatus(HttpMethod.DELETE, "/internal/enhetsregister/es/index/underenhet20260101", 200);
        assertStatus(HttpMethod.DELETE, "/internal/enhetsregister/opensearch/index/underenhet20260101", 200);
    }

    private void assertStatus(HttpMethod method, String path, int expectedStatus) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                    .method(method.name(), HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != expectedStatus) {
                throw new AssertionError("Expected HTTP " + expectedStatus + " but got " + response.statusCode() + " for " + path);
            }
        } catch (Exception e) {
            throw new AssertionError("Request failed for " + path, e);
        }
    }
}
