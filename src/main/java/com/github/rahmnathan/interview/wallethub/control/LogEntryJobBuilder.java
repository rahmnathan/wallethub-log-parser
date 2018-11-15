package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.config.ParserConfig;
import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import com.github.rahmnathan.interview.wallethub.repository.LogEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class LogEntryJobBuilder {
    private final Logger logger = LoggerFactory.getLogger(LogEntryJobBuilder.class);
    private final JobCompleteProcessor jobCompleteProcessor;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final LogEntryRepository logEntryRepository;
    private final ParserConfig parserConfig;

    public LogEntryJobBuilder(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                              ParserConfig parserConfig, JobCompleteProcessor jobCompleteProcessor,
                              LogEntryRepository logEntryRepository) {
        this.jobCompleteProcessor = jobCompleteProcessor;
        this.logEntryRepository = logEntryRepository;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.parserConfig = parserConfig;
    }

    @Bean
    public Job loadLogEntriesJob(Step step1) {
        return jobBuilderFactory.get("loadLogEntriesJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobCompleteProcessor)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemWriter<LogEntry> logEntryWriter, FlatFileItemReader<LogEntry> logEntryReader) {
        return stepBuilderFactory.get("step1")
                .<LogEntry, LogEntry> chunk(parserConfig.getChunkSize())
                .reader(logEntryReader)
                .writer(logEntryWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<LogEntry> logEntryReader() {
        return new FlatFileItemReaderBuilder<LogEntry>()
                .name("logEntryReader")
                .resource(new FileSystemResource(parserConfig.getAccesslog().toPath()))
                .delimited()
                .delimiter("|")
                .names(new String[]{"date", "ip", "request", "status", "userAgent"})
                .fieldSetMapper(new LogEntryFieldMapper())
                .build();
    }

    @Bean
    public ItemWriter<LogEntry> logEntryWriter() {
        return list -> {
            logger.info("Persisting log entries of size: {}", list.size());
            logEntryRepository.saveAll(list);
        };
    }
}