package no.nav.pam.ad.config;

import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tools.jackson.databind.json.JsonMapper;
import no.nav.pam.ad.Application;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.Proxy;

@Configuration
public class AppConfig {

    @Bean
    public OpenSearchClient openSearchClient(OpenSearchTransport transport) {
        return new OpenSearchClient(transport);
    }

    @Bean(destroyMethod = "close")
    public OpenSearchTransport openSearchTransport(@Value("${opensearch.user:foo}") String user,
                                                   @Value("${opensearch.password:bar}") String password,
                                                   @Value("${opensearch.url}") String openSearchUrl) {

        var credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(null, -1),
                new UsernamePasswordCredentials(user, password.toCharArray()));

        return ApacheHttpClient5TransportBuilder.builder(HttpHost.create(URI.create(openSearchUrl)))
                .setMapper(new JacksonJsonpMapper())
                .setCompressionEnabled(true)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();
    }

    @Bean
    @ConditionalOnProperty("pam.http.proxy.enabled")
    public Proxy proxy(@Value("${pam.http.proxy.url:#{null}}") String httpProxyUrl) {
        var url = URI.create(httpProxyUrl);
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.getHost(), url.getPort()));

    }

    @Bean
    @ConditionalOnProperty(value = "pam.http.proxy.enabled", havingValue = "false")
    public Proxy noProxy() {
        return Proxy.NO_PROXY;
    }

}
