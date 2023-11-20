package no.nav.pam.ad.enhetsregister.batch;

import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.es.Datestamp;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {BatchJobTest.BatchTestConfig.class})
public class BatchJobTest {

    @Configuration
    @Import({TestConfig.class})
    static class BatchTestConfig {
        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }

    }

    private static final String FILEPATH = "src/test/resources/enhetsregisteret.samples/";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private TestConfig.IndexClient indexClient;

    @Test
    public void syncUnderenheterTest() throws Exception {
        String type = "TESTUNDERENHET";
        String datestamp = Datestamp.getCurrent();

        Map<String, JobParameter<?>> params = new HashMap<>();
        params.put(JobLauncherService.PARAM_FILENAME, new JobParameter(FILEPATH + "underenheter_alle.json.gz", String.class));
        params.put(JobLauncherService.PARAM_PREFIX, new JobParameter(type, String.class));
        params.put(JobLauncherService.PARAM_DATESTAMP, new JobParameter(datestamp, String.class));

        assertTrue(indexClient.getStorage().isEmpty());

        //testing a job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParameters(params));
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



