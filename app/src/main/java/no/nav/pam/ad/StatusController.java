package no.nav.pam.ad;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.nav.pam.ad.enhetsregister.BrregHealthIndicator;
import no.nav.pam.ad.es.IndexClientHealthIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/internal/status")
public class StatusController {

    private final IndexClientHealthIndicator indexClientHealthIndicator;
    private final BrregHealthIndicator brregHealthIndicator;

    @Autowired
    public StatusController(IndexClientHealthIndicator indexClientHealthIndicator, BrregHealthIndicator brregHealthIndicator) {
        this.indexClientHealthIndicator = indexClientHealthIndicator;
        this.brregHealthIndicator = brregHealthIndicator;
    }

    @Autowired

    @GetMapping
    public ResponseEntity<ObjectNode> statusHealth() throws IOException {

        boolean isElastisSearchOK = indexClientHealthIndicator.health().getStatus().equals(Status.UP);
        boolean isBrregOk = brregHealthIndicator.health().getStatus().equals(Status.UP);


        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("Elastic Search connection status", (isElastisSearchOK) ? "OK" : "NOT OK");
        node.put("BRREG connection status", (isBrregOk) ? "OK" : "NOT OK");

        return ResponseEntity.ok(node);
    }
}
