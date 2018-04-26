package no.nav.pam.ad.enhetsregister.rest;

import no.nav.pam.ad.enhetsregister.batch.DataSet;
import no.nav.pam.ad.enhetsregister.batch.JobLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.net.URL;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Controller
@RequestMapping("/enhetsregister")
public class EnhetsregisterBatchController {

    private static final Logger LOG = LoggerFactory.getLogger(EnhetsregisterBatchController.class);

    @Value("${enhetsregister.timeout.millis:5000}")
    private int timeoutMillis;

    private final JobLauncherService service;
    private final URL enhetsregisterHovedenhetUrl;
    private final URL enhetsregisterUnderenhetUrl;

    @Autowired
    EnhetsregisterBatchController(
            JobLauncherService service,
            @Qualifier("enhetsregister.hovedenhet.url") URL enhetsregisterHovedenhetUrl,
            @Qualifier("enhetsregister.underenhet.url") URL enhetsregisterUnderenhetUrl
    ) {

        this.service = service;
        this.enhetsregisterHovedenhetUrl = enhetsregisterHovedenhetUrl;
        LOG.info("Will read data set {} from URL {}", DataSet.HOVEDENHET, enhetsregisterHovedenhetUrl);
        this.enhetsregisterUnderenhetUrl = enhetsregisterUnderenhetUrl;
        LOG.info("Will read data set {} from URL {}", DataSet.UNDERENHET, enhetsregisterUnderenhetUrl);

    }

    @PostMapping("/sync/hovedenheter")
    public ResponseEntity syncHovedenheter() {
        return synchronize(DataSet.HOVEDENHET, enhetsregisterHovedenhetUrl, timeoutMillis);
    }

    @PostMapping("/sync/underenheter")
    public ResponseEntity syncUnderenheter() {
        return synchronize(DataSet.UNDERENHET, enhetsregisterUnderenhetUrl, timeoutMillis);
    }

    private ResponseEntity synchronize(DataSet set, URL url, int timeoutMillis) {

        LOG.info("Synchronizing data set {} from source {} with timeout {}ms", set, url, timeoutMillis);
        try {

            service.synchronize(set, url, timeoutMillis);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            LOG.error("Unable to synchronize data set {}", set, e);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unable to synchronize", e);
        }

    }

}
