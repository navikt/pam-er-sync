package no.nav.pam.ad.enhetsregister.rest;

import no.nav.pam.ad.enhetsregister.batch.DataSet;
import no.nav.pam.ad.enhetsregister.batch.JobLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.net.URL;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/api/enhetsregister")
public class EnhetsregisterBatchController {

    private static final Logger LOG = LoggerFactory.getLogger(EnhetsregisterBatchController.class);

    private final JobLauncherService service;
    private final URL enhetsregisterHovedenhetUrl;
    private final URL enhetsregisterUnderenhetUrl;

    @Autowired
    EnhetsregisterBatchController(
            JobLauncherService service,
            @Qualifier("enhetsregister.hovedenhet.enabled") boolean enhetsregisterHovedenhetEnabled,
            @Qualifier("enhetsregister.hovedenhet.url") URL enhetsregisterHovedenhetUrl,
            @Qualifier("enhetsregister.underenhet.enabled") boolean enhetsregisterUnderenhetEnabled,
            @Qualifier("enhetsregister.underenhet.url") URL enhetsregisterUnderenhetUrl
    ) {

        this.service = service;
        this.enhetsregisterHovedenhetUrl = enhetsregisterHovedenhetEnabled ? enhetsregisterHovedenhetUrl : null;
        this.enhetsregisterUnderenhetUrl = enhetsregisterUnderenhetEnabled ? enhetsregisterUnderenhetUrl : null;

    }

    @PostMapping("/sync/hovedenheter")
    public ResponseEntity syncHovedenheter() {
        return synchronize(DataSet.HOVEDENHET, enhetsregisterHovedenhetUrl);
    }

    @PostMapping("/sync/underenheter")
    public ResponseEntity syncUnderenheter() {
        return synchronize(DataSet.UNDERENHET, enhetsregisterUnderenhetUrl);
    }

    private ResponseEntity synchronize(DataSet set, URL url)
            throws ResponseStatusException {

        if (url == null) {
            LOG.warn("Synchronization of data set {} is configured to be disabled", set);
            throw new ResponseStatusException(NOT_FOUND, "Synchronization of this data set is disabled");
        }
        try {
            service.synchronize(set, url);
            return ResponseEntity.ok().build();
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(NOT_FOUND, "Resource currently configured as unavailable", e);
        } catch (Exception e) {
            LOG.error("Unable to synchronize data set {}", set, e);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unable to synchronize", e);
        }

    }

}
