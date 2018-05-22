package no.nav.pam.ad.enhetsregister;

import no.nav.pam.ad.enhetsregister.batch.BatchConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final BatchConfig.Underenhet underenhet;
    private final Proxy proxy;

    @Autowired
    public BrregHealthIndicator(BatchConfig.Underenhet underenhet, Proxy proxy) {
        this.underenhet = underenhet;
        this.proxy = proxy;
    }


    @Override
    public Health health() {
        if(!underenhet.getUrl().isPresent()){
            return Health.down().build();
        }

        try {
            URL url = underenhet.getUrl().get();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestMethod("HEAD");
            return (connection.getResponseCode() == 200) ? Health.up().build() : Health.down().build();
        } catch (IOException e) {
            LOG.error("Failed health check for url {}", underenhet.getUrl().get(), e);
            return Health.down(e).build();
        }
    }
}
