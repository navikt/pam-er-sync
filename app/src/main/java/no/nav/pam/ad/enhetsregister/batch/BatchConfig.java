package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.model.CsvEnhet;
import no.nav.pam.ad.enhetsregister.rest.EnhetsregisterBatchController;
import no.nav.pam.ad.es.IndexService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import static no.nav.pam.ad.enhetsregister.batch.JobLauncherService.*;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

    private static final String PROPERTY_ENHETSREGISTER_SCHEDULER_ENABLED = "enhetsregister.scheduler.enabled";
    static final String PROPERTY_ENHETSREGISTER_SCHEDULER_CRON = "enhetsregister.scheduler.cron";

    @Value("${" + PROPERTY_ENHETSREGISTER_SCHEDULER_ENABLED + "}")
    private boolean enhetsregisterSchedulerEnabled;

    @Value("${enhetsregister.sources.hovedenhet.enabled:false}")
    private boolean enhetsregisterHovedenhetEnabled;

    @Value("${enhetsregister.sources.hovedenhet.url:http://data.brreg.no/enhetsregisteret/download/enheter}")
    private String enhetsregisterHovedenhetUrl;

    @Value("${enhetsregister.sources.underenhet.enabled:true}")
    private boolean enhetsregisterUnderenhetEnabled;

    @Value("${enhetsregister.sources.underenhet.url:http://data.brreg.no/enhetsregisteret/download/underenheter}")
    private String enhetsregisterUnderenhetUrl;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    // tag::readerwriterprocessor[]
    @Bean
    @StepScope
    public FlatFileItemReader<CsvEnhet> reader(@Value("#{jobParameters['" + PARAM_PREFIX + "']}") String prefix,
                                               @Value("#{jobParameters['" + PARAM_FILENAME + "']}") String filename)
            throws IOException {

        final DataSet enhet = DataSet.valueOf(prefix);

        FlatFileItemReader<CsvEnhet> reader = new FlatFileItemReader<>();
        reader.setEncoding(StandardCharsets.UTF_8.displayName());
        reader.setResource(new FileSystemResource(filename));
        reader.setResource(new InputStreamResource(new GZIPInputStream(new FileInputStream(filename))));
        reader.setLineMapper(new DefaultLineMapper<CsvEnhet>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                reader.setLinesToSkip(1);
                setDelimiter(";");
                setIncludedFields(enhet.getIncludedFields());
                setNames(enhet.getFieldNames());
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<CsvEnhet>() {{
                setTargetType(CsvEnhet.class);
            }});
        }});
        return reader;

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

        return jobBuilderFactory.get("importUserJob")
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

        return stepBuilderFactory.get("step1")
                .<CsvEnhet, no.nav.pam.ad.enhetsregister.model.Enhet>chunk(1000)
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

    @Bean
    public EnhetsregisterBatchController enhetsregisterBatchController(JobLauncherService service, Hovedenhet hovedenhet, Underenhet underenhet) {
        return new EnhetsregisterBatchController(service, hovedenhet, underenhet);
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