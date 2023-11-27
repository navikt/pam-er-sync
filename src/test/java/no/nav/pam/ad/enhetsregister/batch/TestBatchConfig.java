package no.nav.pam.ad.enhetsregister.batch;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderEnhet;
import no.nav.pam.ad.es.IndexService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import static no.nav.pam.ad.enhetsregister.batch.JobLauncherService.*;

@Configuration
public class TestBatchConfig extends DefaultBatchConfiguration {

    @Autowired
    public TestBatchConfig() {
    }

    @Bean
    JobLauncherTestUtils jobLauncherTestUtils(JobRepository jobRepository, JobLauncher jobLauncher, Job importUserJob) {
        JobLauncherTestUtils jobLauncherTestUtils1 = new JobLauncherTestUtils();
        jobLauncherTestUtils1.setJob(importUserJob);
        jobLauncherTestUtils1.setJobRepository(jobRepository);
        jobLauncherTestUtils1.setJobLauncher(jobLauncher);
        return jobLauncherTestUtils1;
    }


}