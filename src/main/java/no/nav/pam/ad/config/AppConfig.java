package no.nav.pam.ad.config;

import tools.jackson.databind.json.JsonMapper;
import no.nav.pam.ad.Application;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.opensearch.client.RestClient;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

@Configuration
@ComponentScan(basePackageClasses = {Application.class})
public class AppConfig {

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

    @Value("${pam.http.proxy.url:#{null}}")
    private String httpProxyUrl;

    @Value("${pam.http.proxy.enabled:true}")
    private boolean proxyEnabled;

    @Bean
    public JsonMapper jacksonMapper() {
        return new JsonMapper();
    }

    @Bean(destroyMethod = "close")
    public RestClient openSearchRestClient(@Value("${elasticsearch.user:foo}") String user,
                                           @Value("${elasticsearch.password:bar}") String password) {

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(null, -1),
                new UsernamePasswordCredentials(user, password.toCharArray()));

        return RestClient.builder(HttpHost.create(URI.create(elasticsearchUrl)))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();
    }

    @Bean
    public OpenSearchClient openSearchClient(RestClient restClient) {
        OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new OpenSearchClient(transport);
    }

    @Bean
    public Proxy proxy()
            throws MalformedURLException {

        if (httpProxyUrl == null || !proxyEnabled) {
            return Proxy.NO_PROXY;
        }
        URL url = new URL(httpProxyUrl);
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.getHost(), url.getPort()));

    }

}
