package no.nav.pam.ad.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.Application;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
@ComponentScan(basePackageClasses = {Application.class})
public class AppConfig {

    private static final int TIMEOUT_MILLIS = 60000;

    @Value("${pam.elasticsearch.url}")
    private String elasticsearchUrl;

    @Value("${pam.http.proxy.url}")
    private String httpProxyUrl;

    @Bean
    @Profile("prod")
    public RestClientBuilder elasticClientBuilder() {
        return RestClient.builder(HttpHost.create(elasticsearchUrl));
    }

    @Bean
    @Profile({"test", "dev"})
    public RestClientBuilder unsafeElasticClientBuilder()
            throws KeyManagementException, NoSuchAlgorithmException {

        SSLContext context = getSslContext(mockX509TrustManager());
        return RestClient
                .builder(HttpHost.create(elasticsearchUrl))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setSSLContext(context));

    }

    private SSLContext getSslContext(X509TrustManager trustAllX509Manager)
            throws NoSuchAlgorithmException, KeyManagementException {

        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, new TrustManager[]{trustAllX509Manager}, new SecureRandom());
        return context;

    }

    private X509TrustManager mockX509TrustManager() {

        return new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

        };

    }

    @Bean
    public ObjectMapper jacksonMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Proxy proxy()
            throws MalformedURLException {

        URL url = new URL(httpProxyUrl);
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.getHost(), url.getPort()));

    }

    @Bean
    @Profile("prod")
    public HttpClientBuilder httpClientBuilder() {
        return HttpClientBuilder.create();
    }

    @Bean
    @Profile({"dev", "test"})
    public HttpClientBuilder unsafeHttpClientBuilder() throws Exception {
        return UnsafeSSLContextUtil.unsafeHttpClientBuilder();
    }

}