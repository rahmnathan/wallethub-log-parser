package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.config.ParserConfig;
import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import com.github.rahmnathan.interview.wallethub.repository.AboveThresholdRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableBatchProcessing
public class BatchFileProcessor {
    private final AboveThresholdRepository aboveThresholdRepository;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final ParserConfig parserConfig;
    private final JdbcTemplate jdbcTemplate;

    public BatchFileProcessor(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                              ParserConfig parserConfig, JdbcTemplate jdbcTemplate,
                              AboveThresholdRepository aboveThresholdRepository) {
        this.aboveThresholdRepository = aboveThresholdRepository;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.jdbcTemplate = jdbcTemplate;
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
                .fieldSetMapper(new LogEntryMapper())
                .build();
    }

    @Bean
    public Job importLogEntryJob(Step step1) {
        return jobBuilderFactory.get("importLogEntryJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobCompleteProcessor(parserConfig, jdbcTemplate, aboveThresholdRepository))
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
}
