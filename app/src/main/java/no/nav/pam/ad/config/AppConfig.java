package no.nav.pam.ad.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.Application;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
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

    // TODO re-enable SSL validation when a proper certificate store is in place
    // WCPGW What could possibly go wrong.

    @PostConstruct
    public void disableJavaSslValidation() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext unsafeContext = UnsafeSSLContextUtil.newSSLContext(UnsafeSSLContextUtil.newUnsafeTrustManager());
        HttpsURLConnection.setDefaultSSLSocketFactory(unsafeContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new NoopHostnameVerifier());
    }

    @Bean
    public RestClientBuilder unsafeElasticClientBuilder() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext unsafeContext = UnsafeSSLContextUtil.newSSLContext(UnsafeSSLContextUtil.newUnsafeTrustManager());
        return RestClient
                .builder(HttpHost.create(elasticsearchUrl))
                .setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setSSLContext(unsafeContext)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier()));
    }

    @Bean
    public ObjectMapper jacksonMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Proxy proxy()
            throws MalformedURLException {

        if (httpProxyUrl == null) {
            return Proxy.NO_PROXY;
        }
        URL url = new URL(httpProxyUrl);
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.getHost(), url.getPort()));

    }

}