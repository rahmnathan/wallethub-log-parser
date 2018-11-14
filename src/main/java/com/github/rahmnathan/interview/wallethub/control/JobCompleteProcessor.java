package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.config.ParserConfig;
import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;

import java.time.LocalDateTime;

public class JobCompleteProcessor implements JobExecutionListener {
    private final Logger logger = LoggerFactory.getLogger(JobCompleteProcessor.class);
    private final LogEntryRepository repository;
    private final ParserConfig parserConfig;

    public JobCompleteProcessor(LogEntryRepository repository, ParserConfig parserConfig) {
        this.parserConfig = parserConfig;
        this.repository = repository;

        logger.info("ParserConfig: {}", parserConfig);
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Strarting job.");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Job is completed. Querying for entries above threshold.");

        LocalDateTime startDate = parserConfig.getStartDate();
        LocalDateTime endDate = startDate.plusHours(parserConfig.getDuration().getHours());

        Iterable<LogEntry> logEntryIterable = repository.findBetweenDatesAndAboveThreshold(startDate, endDate, parserConfig.getThreshold());

        logEntryIterable.forEach(logEntry -> logger.info("LOGENTRY: {}", logEntry));
    }
}
