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

import java.nio.charset.StandardCharsets;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    // tag::readerwriterprocessor[]
    @Bean
    @StepScope
    public FlatFileItemReader<CsvEnhet> reader(@Value("#{jobParameters['type']}") String type,
                                               @Value("#{jobParameters['filename']}") String filename) {

        final CsvProperties csvProperties = CsvProperties.buildCsvProperties(CsvProperties.EnhetType.valueOf(type));

        FlatFileItemReader<CsvEnhet> reader = new FlatFileItemReader<>();
        reader.setEncoding(StandardCharsets.UTF_8.displayName());
        reader.setResource(new FileSystemResource(filename));
        reader.setLineMapper(new DefaultLineMapper<CsvEnhet>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                reader.setLinesToSkip(1);
                setDelimiter(";");
                setIncludedFields(csvProperties.getIncludedFields());
                setNames(csvProperties.getFieldNames());
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
            JobExecutionListenerImpl executionListener) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(executionListener)
                .listener(completionNotificationListener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<CsvEnhet, Enhet>chunk(1000)
                .reader(reader(null, null))
                .processor(processor())
                .writer(writer(null))
                .build();
    }
    // end::jobstep[]
}