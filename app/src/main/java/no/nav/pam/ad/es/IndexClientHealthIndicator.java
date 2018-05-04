package no.nav.pam.ad.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class IndexClientHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(IndexClientHealthIndicator.class);

    private IndexClient client;

    @Autowired
    private IndexClientHealthIndicator(IndexClient client) {
        this.client = client;
    }

    @Override
    public Health health() {
        return Health.up().build();
/*        try {
            return client.isHealthy() ? Health.up().build() : Health.down().build();
        } catch (IOException e) {
            LOG.error("Failed health check of {}", client.getClass().getSimpleName(), e);
            return Health.down(e).build();
        }*/
    }

}
