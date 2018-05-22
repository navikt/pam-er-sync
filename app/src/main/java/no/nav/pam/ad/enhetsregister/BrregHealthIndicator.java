package no.nav.pam.ad.enhetsregister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

@Component
public class BrregHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(BrregHealthIndicator.class);

    @Value("${pam.enhetsregister.sources.underenhet.url:http://data.brreg.no/enhetsregisteret/download/underenheter}")
    private String enhetsregisterUnderenhetUrl;

    private final Proxy proxy;

    @Autowired
    public BrregHealthIndicator(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public Health health() {
        try {
            URL url = new URL(enhetsregisterUnderenhetUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestMethod("HEAD");
            return (connection.getResponseCode() == 200) ? Health.up().build() : Health.down().build();
        } catch (IOException e) {
            LOG.error("Failed health check for url {}", enhetsregisterUnderenhetUrl, e);
            return Health.down(e).build();
        }
    }
}
