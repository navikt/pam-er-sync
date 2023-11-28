package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.es.IndexService;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class EnhetJsonWriter implements ItemWriter<Enhet> {

    @Autowired
    private IndexService service;

    private final String prefix;
    private final String datestamp;

    EnhetJsonWriter(String prefix, String datestamp) {
        this.prefix = prefix;
        this.datestamp = datestamp;
    }

    @Override
    public void write(Chunk<? extends Enhet> chunk) throws Exception {
        service.indexCompanies(prefix, datestamp, (List<Enhet>) chunk.getItems());
    }
}
