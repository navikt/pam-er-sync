package no.nav.pam.ad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        LOG.info("Environment: {}", System.getenv().keySet());
        LOG.info("Properties: {}", System.getProperties().keySet());
        SpringApplication.run(Application.class, args);

    }
}