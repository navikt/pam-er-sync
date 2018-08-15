package no.nav.pam.ad;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.nav.pam.ad.enhetsregister.BrregHealthIndicator;
import no.nav.pam.ad.es.IndexClientHealthIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/internal/status")
public class StatusController {

    private static final Logger LOG = LoggerFactory.getLogger(StatusController.class);

    private final IndexClientHealthIndicator indexClientHealthIndicator;
    private final BrregHealthIndicator brregHealthIndicator;

    @Autowired
    public StatusController(IndexClientHealthIndicator indexClientHealthIndicator, BrregHealthIndicator brregHealthIndicator) {
        this.indexClientHealthIndicator = indexClientHealthIndicator;
        this.brregHealthIndicator = brregHealthIndicator;
    }

    @GetMapping
    public ResponseEntity<ObjectNode> statusHealth() throws Exception {
        try {

            boolean isElastisSearchOK = indexClientHealthIndicator.health().getStatus().equals(Status.UP);
            boolean isBrregOk = brregHealthIndicator.health().getStatus().equals(Status.UP);

            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("Elastic Search connection status", (isElastisSearchOK) ? "OK" : "NOT OK");
            node.put("BRREG connection status", (isBrregOk) ? "OK" : "NOT OK");

            return ResponseEntity.ok(node);
        } catch (Exception e) {
            LOG.error("Status check failed", e);
            throw e;
        }
    }
}
