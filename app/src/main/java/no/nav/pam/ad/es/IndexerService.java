package no.nav.pam.ad.es;

import no.nav.pam.ad.enhetsregister.model.Enhet;

import java.io.IOException;
import java.util.List;

/**
 * Interface to be implemented by indexer services.
 */
public interface IndexerService {

    void createAndConfigure(String indexDatestamp) throws IOException;

    void replaceAlias(String indexDatestamp) throws IOException;

    int fetchDocCount(String indexDatestamp) throws IOException;

    void indexCompanies(List<Enhet> companyList, String indexDatestamp) throws IOException;

    void deleteIndex(String index) throws IOException;

    void deleteOlderIndices() throws IOException;

}
