package no.nav.pam.ad.enhetsregister.batch;

import no.nav.pam.ad.config.AppConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Ignore //TODO: Find a better way to mock ES client
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {BatchJobTest.BatchTestConfig.class})
public class BatchJobTest {

    @Configuration
    @EnableBatchProcessing
    @Import({BatchConfig.class, AppConfig.class})
    static class BatchTestConfig {
        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }

    }

    private static final String FILEPATH = "src/test/resources/enhetsregisteret.samples/";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void syncUnderenheterTest() throws Exception {
        Map<String, JobParameter> params = new HashMap<>();
        params.put("type", new JobParameter(CsvProperties.EnhetType.UNDERENHET.toString()));
        params.put("filename", new JobParameter(FILEPATH + "underenheter.csv"));

        //testing a job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParameters(params));
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}



