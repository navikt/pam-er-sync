package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.model.CsvEnhet;
import no.nav.pam.ad.enhetsregister.model.Enhet;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

    @Value("${enhetsregister.hovedenhet.url:http://data.brreg.no/enhetsregisteret/download/enheter}")
    private String enhetsregisterHovedenhetUrl;

    @Value("${enhetsregister.underenhet.url:http://data.brreg.no/enhetsregisteret/download/underenheter}")
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
    public FlatFileItemReader<CsvEnhet> reader(@Value("#{jobParameters['type']}") String type,
                                               @Value("#{jobParameters['filename']}") String filename)
            throws IOException {

        final DataSet enhet = DataSet.valueOf(type);

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
    public EnhetJsonWriter writer(@Value("#{jobParameters['datestamp']}") String datestamp) {
        EnhetJsonWriter writer = new EnhetJsonWriter();
        writer.setDatestamp(datestamp);

        return writer;
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
                .<CsvEnhet, Enhet>chunk(1000)
                .reader(reader(null, null))
                .processor(processor())
                .writer(writer(null))
                .build();

    }
    // end::jobstep[]

    /**
     * Gives the URL to the location of Hovedenhet data. Override in custom config for testing if needed.
     *
     * @return A valid URL.
     * @throws MalformedURLException If the configured URL is invalid.
     */
    @Bean(name = "enhetsregister.hovedenhet.url")
    public URL getEnhetsregisterHovedenhetUrl()
            throws MalformedURLException {
        return new URL(enhetsregisterHovedenhetUrl);
    }

    /**
     * Gives the URL to the location of Underenhet data. Override in custom config for testing if needed.
     *
     * @return A valid URL.
     * @throws MalformedURLException If the configured URL is invalid.
     */
    @Bean(name = "enhetsregister.underenhet.url")
    public URL getEnhetsregisterUnderenhetUrl()
            throws MalformedURLException {
        return new URL(enhetsregisterUnderenhetUrl);
    }

}