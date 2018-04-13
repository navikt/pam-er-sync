package no.nav.pam.ad.config;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Configuration
public class HttpClientConfig {

    @Value("${http.proxy.url}")
    private String proxyEndpoint;

    @Bean
    @Profile("prod")
    public HttpClientProxy httpClientProxy() {
        HttpUrl url = HttpUrl.parse(proxyEndpoint);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.host(), url.port()));
        OkHttpClient veryOk = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).proxy(proxy).build();
        HttpClientProxy httpClientProxy = new HttpClientProxy();
        httpClientProxy.setHttpClient(veryOk);
        return httpClientProxy;
    }

    @Bean
    @Profile({"dev"})
    public HttpClientProxy getUnsafeClient() {
        HttpUrl url = HttpUrl.parse(proxyEndpoint);
        try {
            X509TrustManager trustAllX509Manager = UnsafeSSLContextUtil.newUnsafeTrustManager();
            SSLContext sc = UnsafeSSLContextUtil.newSSLContext(trustAllX509Manager);
            OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .sslSocketFactory(sc.getSocketFactory(), trustAllX509Manager)
                    .hostnameVerifier((s, sslSession) -> true)
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(url.host(), url.port())))
                    .build();
            HttpClientProxy proxy = new HttpClientProxy();
            proxy.setHttpClient(client);
            return proxy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Profile({"test"})
    public HttpClientProxy getUnsafeClientUtenProxy() {
        try {
            X509TrustManager trustAllX509Manager = UnsafeSSLContextUtil.newUnsafeTrustManager();
            SSLContext sc = UnsafeSSLContextUtil.newSSLContext(trustAllX509Manager);
            OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .sslSocketFactory(sc.getSocketFactory(), trustAllX509Manager)
                    .hostnameVerifier((s, sslSession) -> true)
                    .build();
            HttpClientProxy proxy = new HttpClientProxy();
            proxy.setHttpClient(client);
            return proxy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
