package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.config.ParserConfig;
import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchFileProcessor {
    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final ParserConfig parserConfig;

    public BatchFileProcessor(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ParserConfig parserConfig) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.parserConfig = parserConfig;
    }

    @Bean
    public FlatFileItemReader<LogEntry> reader() {
        return new FlatFileItemReaderBuilder<LogEntry>()
                .name("logEntryReader")
                .resource(new FileSystemResource(parserConfig.getAccesslog().toPath()))
                .delimited()
                .delimiter("|")
                .names(new String[]{"date", "ip", "request", "status", "userAgent"})
                .fieldSetMapper(new LogEntryFieldSetMapper())
                .build();
    }

    @Bean
    public Job importLogEntryJob(Step step1, LogEntryRepository repository) {
        return jobBuilderFactory.get("importLogEntryJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobCompleteProcessor(repository, parserConfig))
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(LogEntryWriter logEntryWriter, FlatFileItemReader<LogEntry> reader) {
        return stepBuilderFactory.get("step1")
                .<LogEntry, LogEntry> chunk(parserConfig.getChunkSize())
                .reader(reader)
                .writer(logEntryWriter)
                .build();
    }

    @Bean
    public Step step2(LogEntryWriter logEntryWriter, FlatFileItemReader<LogEntry> reader) {
        return stepBuilderFactory.get("step2")
                .<LogEntry, LogEntry> chunk(parserConfig.getChunkSize())
                .reader(reader)
                .writer(logEntryWriter)
                .build();
    }
}