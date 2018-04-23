package no.nav.pam.ad.enhetsregister.es;

import no.nav.pam.ad.es.DatestampUtil;
import no.nav.pam.ad.es.ElasticSearchIndexClient;
import no.nav.pam.ad.es.IndexerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IndexerServiceTest {

    @Mock
    ElasticSearchIndexClient client;

    @InjectMocks
    IndexerService service;

    @Captor
    ArgumentCaptor<String[]> captor;


    @Test
    public void shouldDeleteOlderIndices() throws IOException {
        String expired1 = LocalDateTime.now().minusDays(12).format(DateTimeFormatter.ofPattern(DatestampUtil.DATESTAMP_FORMAT));
        String expired2 = LocalDateTime.now().minusDays(16).format(DateTimeFormatter.ofPattern(DatestampUtil.DATESTAMP_FORMAT));

        List<String> allIndices = new ArrayList<>();
        allIndices.add(LocalDate.now().format(DateTimeFormatter.ofPattern(DatestampUtil.DATESTAMP_FORMAT)));
        allIndices.add(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern(DatestampUtil.DATESTAMP_FORMAT)));
        allIndices.add(LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern(DatestampUtil.DATESTAMP_FORMAT)));
        allIndices.add(expired1);
        allIndices.add(expired2);

        when(client.fetchAllIndicesStartingWith("underenheter")).thenReturn(allIndices);

        service.deleteOlderIndices();

        verify(client).deleteIndex(captor.capture());
        List args = captor.getAllValues();

        assertEquals(2, args.size());
        assertTrue(args.stream().filter(s -> s.equals(expired1)).findAny().isPresent());
        assertTrue(args.stream().filter(s -> s.equals(expired2)).findAny().isPresent());
    }
}
