package no.nav.pam.ad.enhetsregister.es;

import no.nav.pam.ad.enhetsregister.batch.DataSet;
import no.nav.pam.ad.persistence.Datestamp;
import no.nav.pam.ad.persistence.IndexClient;
import no.nav.pam.ad.persistence.IndexService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IndexServiceTest {

    @Mock
    private IndexClient client;

    @InjectMocks
    private IndexService service;

    @Captor
    private ArgumentCaptor<String[]> captor;


    @Test
    public void shouldDeleteOlderIndices() throws IOException {
        String expired1 = LocalDateTime.now().minusDays(12).format(DateTimeFormatter.ofPattern(Datestamp.DATESTAMP_FORMAT));
        String expired2 = LocalDateTime.now().minusDays(16).format(DateTimeFormatter.ofPattern(Datestamp.DATESTAMP_FORMAT));

        List<String> allIndices = new ArrayList<>();
        allIndices.add(LocalDate.now().format(DateTimeFormatter.ofPattern(Datestamp.DATESTAMP_FORMAT)));
        allIndices.add(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern(Datestamp.DATESTAMP_FORMAT)));
        allIndices.add(LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern(Datestamp.DATESTAMP_FORMAT)));
        allIndices.add(expired1);
        allIndices.add(expired2);

        when(client.fetchAllIndicesStartingWith(anyString())).thenReturn(allIndices);

        service.deleteOlderIndices(DataSet.UNDERENHET.toString());

        verify(client).deleteIndex(captor.capture());
        String[] args = captor.getValue();

        assertEquals(2, args.length);
        assertTrue(Arrays.stream(args).anyMatch(s -> s.equals(expired1)));
        assertTrue(Arrays.stream(args).anyMatch(s -> s.equals(expired2)));
    }

    @Test
    public void shouldNotDeleteIndexWithAlias() throws IOException {
        String aliased = "underenhet" + LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern(Datestamp.DATESTAMP_FORMAT));
        String expired = "underenhet" + LocalDate.now().minusDays(4).format(DateTimeFormatter.ofPattern(Datestamp.DATESTAMP_FORMAT));

        when(client.fetchIndicesWithAlias("underenhet")).thenReturn(Set.of(aliased));
        when(client.fetchAllIndicesStartingWith("underenhet")).thenReturn(List.of(aliased, expired));

        service.deleteOlderIndices(DataSet.UNDERENHET.toString());

        verify(client).deleteIndex(captor.capture());
        assertArrayEquals(new String[]{expired}, captor.getValue());
    }


}
