package no.nav.pam.ad.es;

import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.elasticsearch.action.bulk.BulkResponse;

import java.io.IOException;
import java.util.List;

/**
 * Interface to be implemented by index clients.
 */
public interface IndexClient {

    void createIndex(String index, String settings) throws IOException;

    void deleteIndex(String... indices) throws IOException;

    boolean indexExists(String index) throws IOException;

    void replaceAlias(String alias, String indexDatestamp) throws IOException;

    BulkResponse indexBulk(List<Enhet> contents, String index) throws IOException;

    int fetchIndexDocCount(String index) throws IOException;

    List<String> fetchAllIndicesStartingWith(String name) throws IOException;

}
