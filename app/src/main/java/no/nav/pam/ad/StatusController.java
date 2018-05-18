package no.nav.pam.ad;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.nav.pam.ad.es.IndexClientHealthIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

@Controller
@RequestMapping("/internal/status")
public class StatusController {

    private final IndexClientHealthIndicator indexClientHealthIndicator;
    private final Proxy proxy;

    @Value("${pam.enhetsregister.sources.underenhet.url}")
    private String enhetsregisterUnderenhetUrl;

    public StatusController(IndexClientHealthIndicator indexClientHealthIndicator, Proxy proxy) throws MalformedURLException {
        this.indexClientHealthIndicator = indexClientHealthIndicator;
        this.proxy = proxy;
    }

    @Autowired

    @GetMapping
    public ResponseEntity<ObjectNode> statusHealth() throws IOException {

        boolean isElastisSearchOK = indexClientHealthIndicator.health().getStatus().equals(Status.UP);

        URL url = new URL(enhetsregisterUnderenhetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
        connection.setRequestMethod("HEAD");
        boolean isBrregOK = (connection.getResponseCode() == 200);

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("Elastic Search connection status", (isElastisSearchOK) ? "OK" : "NOT OK");
        node.put("BRREG connection status", (isBrregOK) ? "OK" : "NOT OK");

        return ResponseEntity.ok(node);
    }
}
