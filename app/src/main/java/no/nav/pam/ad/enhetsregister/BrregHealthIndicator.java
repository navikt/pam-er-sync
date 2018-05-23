package no.nav.pam.ad.enhetsregister;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class BrregHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(BrregHealthIndicator.class);

    private final BatchConfig.Underenhet underenhet;
    private final Proxy proxy;

    private LoadingCache<Integer, Health> healthCache;

    @Autowired
    public BrregHealthIndicator(BatchConfig.Underenhet underenhet, Proxy proxy) {
        this.underenhet = underenhet;
        this.proxy = proxy;

        healthCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<Integer, Health>() {
                            @Override
                            public Health load(Integer integer) throws Exception {
                                return fetchHealthStatus();
                            }
                        });
    }


    @Override
    public Health health() {
        try {
            return healthCache.get(0);
        } catch (ExecutionException e) {
            LOG.error("Caching error for Brreg health status. Fetching directly from Brreg url.");
            return fetchHealthStatus();
        }
    }

    private Health fetchHealthStatus() {
        if (!underenhet.getUrl().isPresent()) {
            return Health.down().build();
        }

        try {
            URL url = underenhet.getUrl().get();
            URLConnection connection =  url.openConnection(proxy);
            if(connection instanceof HttpURLConnection){
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("HEAD");
                return (httpConnection.getResponseCode() == 200) ? Health.up().build() : Health.down().build();
            } else {
                LOG.debug("Brreg URL is not a HTTP connection return health.up");
                return Health.up().build();
            }
        } catch (IOException e) {
            LOG.error("Failed health check for url {}", underenhet.getUrl().get(), e);
            return Health.down(e).build();
        }
    }
}
