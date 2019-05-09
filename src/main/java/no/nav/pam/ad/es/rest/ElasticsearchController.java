package no.nav.pam.ad.es.rest;

import no.nav.pam.ad.es.IndexClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/internal/enhetsregister/es")
public class ElasticsearchController {

    private final Logger LOG = LoggerFactory.getLogger(ElasticsearchController.class);
    private final IndexClient service;

    @Autowired
    private ElasticsearchController(IndexClient service) {
        this.service = service;
    }

    @PutMapping("/alias/{prefix}/{datestamp}")
    public ResponseEntity changeAlias(
            @PathVariable("prefix") String prefix,
            @PathVariable("datestamp") String datestamp
    ) {

        try {
            service.replaceAlias(prefix, datestamp);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOG.error("Got exception while trying to replace alias", e);
            throw new ResponseStatusException(NOT_FOUND);
        }

    }

    @DeleteMapping("/index/{index}")
    public ResponseEntity deleteIndex(@PathVariable("index") String index) {
        try {
            service.deleteIndex(index);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOG.error("Got exceptioni while trying to delete index",e);
            return ResponseEntity.notFound().build();
        }
    }
}
