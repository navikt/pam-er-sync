package no.nav.pam.ad.es.rest;

import no.nav.pam.ad.es.IndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/enhetsregister/es")
public class ElasticSearchController {

    private final IndexerService service;

    @Autowired
    private ElasticSearchController(IndexerService service) {
        this.service = service;
    }

    @PutMapping("/alias/{datestamp}")
    public ResponseEntity changeAlias(@PathVariable("datestamp") String datestamp) {
        try {
            service.replaceAlias(datestamp);

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/index/{index}")
    public ResponseEntity deleteIndex(@PathVariable("index") String index) {
        try {
            service.deleteIndex(index);

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
