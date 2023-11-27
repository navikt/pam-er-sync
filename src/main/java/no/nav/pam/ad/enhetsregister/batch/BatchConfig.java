package no.nav.pam.ad.enhetsregister.batch;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderEnhet;
import no.nav.pam.ad.es.IndexService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
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
//@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

    private static final String PROPERTY_ENHETSREGISTER_SCHEDULER_ENABLED = "pam.enhetsregister.scheduler.enabled";
    static final String PROPERTY_ENHETSREGISTER_SCHEDULER_CRON = "pam.enhetsregister.scheduler.cron";

    @Value("${" + PROPERTY_ENHETSREGISTER_SCHEDULER_ENABLED + ":false}")
    private boolean enhetsregisterSchedulerEnabled;

    @Value("${pam.enhetsregister.sources.hovedenhet.enabled:false}")
    private boolean enhetsregisterHovedenhetEnabled;

    @Value("${pam.enhetsregister.sources.hovedenhet.url:https://data.brreg.no/enhetsregisteret/api/enheter/lastned}")
    private String enhetsregisterHovedenhetUrl;

    @Value("${pam.enhetsregister.sources.underenhet.enabled:true}")
    private boolean enhetsregisterUnderenhetEnabled;

    @Value("${pam.enhetsregister.sources.underenhet.url:https://data.brreg.no/enhetsregisteret/api/underenheter/lastned}")
    private String enhetsregisterUnderenhetUrl;

    private final ObjectMapper objectMapper;
    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;
    private final PlatformTransactionManager batchTransactionManager;


    @Autowired
    public BatchConfig(ObjectMapper objectMapper,
            JobLauncher jobLauncher,
                       JobRepository jobRepository,
                       PlatformTransactionManager batchTransactionManager) {
        this.objectMapper = objectMapper;
        this.jobLauncher = jobLauncher;
        this.jobRepository = jobRepository;
        this.batchTransactionManager = batchTransactionManager;
    }

    // tag::readerwriterprocessor[]
    @Bean
    @StepScope
    public JsonItemReader<ReaderEnhet> reader(@Value("#{jobParameters['" + PARAM_PREFIX + "']}") String prefix,
                                              @Value("#{jobParameters['" + PARAM_FILENAME + "']}") String filename)
            throws IOException {

        JacksonJsonObjectReader<ReaderEnhet> jsonObjectReader =
                new JacksonJsonObjectReader<>(ReaderEnhet.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<ReaderEnhet>()
                .jsonObjectReader(jsonObjectReader)
                .resource(new InputStreamResource(new GZIPInputStream(new FileInputStream(filename))))
                .name("tradeJsonItemReader")
                .build();

    }

    @Bean
    public EnhetItemProcessor processor() {
        return new EnhetItemProcessor();
    }

    @Bean
    @StepScope
    public EnhetJsonWriter writer(
            @Value("#{jobParameters['" + PARAM_PREFIX + "']}") String prefix,
            @Value("#{jobParameters['" + PARAM_DATESTAMP + "']}") String datestamp
    ) {
        return new EnhetJsonWriter(prefix, datestamp);
    }

    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(
            JobCompletionNotificationListener completionNotificationListener,
            JobExecutionListenerImpl executionListener)
            throws IOException {

        return new JobBuilder("importUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(executionListener)
                .listener(completionNotificationListener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1()
            throws IOException {

        return new StepBuilder("step1", jobRepository)
                .<ReaderEnhet, Enhet>chunk(1000, batchTransactionManager)
                .reader(reader(null, null))
                .processor(processor())
                .writer(writer(null, null))
                .build();

    }
    // end::jobstep[]

    /**
     * Replacement for hardcoded sleep value in {@link JobCompletionNotificationListener}, to reduce time spent in tests.
     *
     * @return 10000
     */
    @Bean(name = "jobCompletionNotificationListenerDelay")
    public long jobCompletionNotificationListenerDelay() {
        return 10000;
    }

    @Bean
    @ConditionalOnProperty({PROPERTY_ENHETSREGISTER_SCHEDULER_ENABLED, PROPERTY_ENHETSREGISTER_SCHEDULER_CRON})
    @Profile("!test")
    public BatchScheduler batchScheduler(JobLauncherService jobService, IndexService indexService, Hovedenhet hovedenhet, Underenhet underenhet) {
        return new BatchScheduler(jobService, indexService, hovedenhet, underenhet);
    }

    @Bean
    public Hovedenhet hovedenhet()
            throws MalformedURLException {
        return new Hovedenhet(enhetsregisterHovedenhetEnabled, new URL(enhetsregisterHovedenhetUrl));
    }

    @Bean
    public Underenhet underenhet()
            throws MalformedURLException {
        return new Underenhet(enhetsregisterUnderenhetEnabled, new URL(enhetsregisterUnderenhetUrl));
    }

    public abstract static class SourceConfiguration {

        private final URL url;

        private SourceConfiguration(boolean enabled, URL url) {
            this.url = enabled ? url : null;
        }

        public Optional<URL> getUrl() {
            return Optional.ofNullable(url);
        }

    }

    public static class Hovedenhet extends SourceConfiguration {

        Hovedenhet(boolean enabled, URL url) {
            super(enabled, url);
        }

    }

    public static class Underenhet extends SourceConfiguration {

        Underenhet(boolean enabled, URL url) {
            super(enabled, url);
        }

    }

}