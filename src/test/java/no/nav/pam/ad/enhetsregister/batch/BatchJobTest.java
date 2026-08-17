package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.es.Datestamp;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith({MockitoExtension.class})
@SpringJUnitConfig(classes = {TestConfig.class})
@ActiveProfiles("test")
public class BatchJobTest {

    private static final String FILEPATH = "src/test/resources/enhetsregisteret.samples/";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockitoBean
    private JobCompletionNotificationListener listener;

    @MockitoBean
    private JobExecutionListenerImpl executionListener;

    @Autowired
    private TestConfig.IndexClient indexClient;

    @Test
    public void syncUnderenheterTest() throws Exception {
        String type = "TESTUNDERENHET";
        String datestamp = Datestamp.getCurrent();

        JobParameters params = new JobParameters(Set.of(
                new JobParameter<>(JobLauncherService.PARAM_FILENAME, FILEPATH + "underenheter_alle.json.gz", String.class),
                new JobParameter<>(JobLauncherService.PARAM_PREFIX, type, String.class),
                new JobParameter<>(JobLauncherService.PARAM_DATESTAMP, datestamp, String.class)
        ));

        assertTrue(indexClient.getStorage().isEmpty());

        //testing a job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        assertTrue(indexClient.getStorage().containsKey(type+datestamp));
        List<Enhet> storedList = indexClient.getStorage().get(type+datestamp);
        assertEquals(6, storedList.size());

        Enhet oneStoredItem = storedList.stream().filter(e -> e.getOrganisasjonsnummer().equals("914541697")).findAny().get();
        assertNotNull(oneStoredItem);

        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(oneStoredItem.getOrganisasjonsnummer()).isEqualTo("914541697");
        softAssert.assertThat(oneStoredItem.getNavn()).isEqualTo("HAIR & BEAUTY LOUNGE AS");
        softAssert.assertThat(oneStoredItem.getOrganisasjonsform()).isEqualTo("BEDR");
        softAssert.assertThat(oneStoredItem.getAntallAnsatte()).isEqualTo(0);
        softAssert.assertThat(oneStoredItem.getOverordnetEnhet()).isEqualTo("914516552");
        softAssert.assertThat(oneStoredItem.getAdresse()).isNotNull();
        softAssert.assertThat(oneStoredItem.getAdresse().getAdresse()).isEqualTo("Niels Juels gate 51");
        softAssert.assertThat(oneStoredItem.getAdresse().getPostnummer()).isEqualTo("0259");
        softAssert.assertThat(oneStoredItem.getAdresse().getPoststed()).isEqualTo("OSLO");
        softAssert.assertThat(oneStoredItem.getAdresse().getKommune()).isEqualTo("OSLO");
        softAssert.assertThat(oneStoredItem.getAdresse().getLandkode()).isEqualTo("NO");
        softAssert.assertThat(oneStoredItem.getAdresse().getLand()).isEqualTo("Norge");
        softAssert.assertThat(oneStoredItem.getNaringskoder().size()).isEqualTo(1);
        softAssert.assertThat(oneStoredItem.getNaringskoder().get(0).getKode()).isEqualTo("96.020");
        softAssert.assertThat(oneStoredItem.getNaringskoder().get(0).getBeskrivelse()).isEqualTo("Frisering og annen skjønnhetspleie");
        softAssert.assertAll();
    }


}



