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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Configuration
@ComponentScan(basePackageClasses = {Application.class})
public class AppConfig {


    @Bean
    @Profile("prod")
    public RestClientBuilder elasticClientBuilder(@Value("${pam.elasticsearch.url}") String url) {
        return RestClient.builder(HttpHost.create(url));
    }

    @Bean
    @Profile({"test", "dev"})
    public RestClientBuilder unsafeElasticClientBuilder(@Value("${pam.elasticsearch.url}") String url)
            throws KeyManagementException, NoSuchAlgorithmException {

        X509TrustManager trustAllX509Manager = mockX509TrustManager();
        SSLContext sslContext = getSslContext(trustAllX509Manager);

        return RestClient.builder(HttpHost.create(url))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setSSLContext(sslContext)
                );
    }

    private SSLContext getSslContext(X509TrustManager trustAllX509Manager)
            throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{trustAllX509Manager}, new SecureRandom());
        return sc;
    }

    private X509TrustManager mockX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
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