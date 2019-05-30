package no.nav.pam.ad.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.Application;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
@ComponentScan(basePackageClasses = {Application.class})
public class AppConfig {

    @Value("${pam.elasticsearch.url}")
    private String elasticsearchUrl;

    @Value("${pam.http.proxy.url:#{null}}")
    private String httpProxyUrl;

    @Value("${pam.http.proxy.enabled:true}")
    private boolean proxyEnabled;

    @Bean
    @Profile("prod")
    public RestClientBuilder elasticClientBuilder() {
        return RestClient.builder(HttpHost.create(elasticsearchUrl));
    }

    @Bean
    @Profile({"test", "dev"})
    public RestClientBuilder unsafeElasticClientBuilder()
            throws KeyManagementException, NoSuchAlgorithmException {

        SSLContext unsafeContext = UnsafeSSLContextUtil.newSSLContext(UnsafeSSLContextUtil.newUnsafeTrustManager());
        return RestClient
                .builder(HttpHost.create(elasticsearchUrl))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setSSLContext(unsafeContext));
    }

    @Bean
    public ObjectMapper jacksonMapper() {
        return new ObjectMapper();
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