package com.github.rahmnathan.wallethub.log.parser.control;

import com.github.rahmnathan.wallethub.log.parser.config.ParserConfig;
import com.github.rahmnathan.wallethub.log.parser.entity.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;

@Component
public class JobCompleteProcessor implements JobExecutionListener {
    private final Logger logger = LoggerFactory.getLogger(JobCompleteProcessor.class);
    private final LogEntryPersistenceService logEntryPersistenceService;
    private final ParserConfig parserConfig;

    public JobCompleteProcessor(ParserConfig parserConfig, LogEntryPersistenceService logEntryPersistenceService) {
        this.logEntryPersistenceService = logEntryPersistenceService;
        this.parserConfig = parserConfig;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Starting job with parser config: {}", parserConfig);
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        logger.info("Job is complete. Querying for entries above threshold.");

        Collection<LogEntry> logEntries = queryForLogEntriesAboveThreshold();

        logEntries.forEach(logEntry -> logger.info("Log entry above threshold: {}", logEntry));

        logEntryPersistenceService.insertAboveThresholdEntries(logEntries);
    }

    private Collection<LogEntry> queryForLogEntriesAboveThreshold() {
        LocalDateTime startDate = parserConfig.getStartDate();
        LocalDateTime endDate = startDate.plusHours(parserConfig.getDuration().getHours());

        return logEntryPersistenceService.queryForLogEntriesAboveThreshold(startDate, endDate, parserConfig.getThreshold());
    }
}