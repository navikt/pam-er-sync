package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.es.IndexerService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class EnhetJsonWriter implements ItemWriter<Enhet> {

    @Autowired
    private IndexerService indexer;

    private String datestamp;

    void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    @Override
    public void write(List<? extends Enhet> list) throws Exception {
        indexer.indexCompanies(new ArrayList<>(list), datestamp);
    }
}
