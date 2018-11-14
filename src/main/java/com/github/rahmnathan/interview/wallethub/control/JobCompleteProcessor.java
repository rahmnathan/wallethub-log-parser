package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.config.ParserConfig;
import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobCompleteProcessor implements JobExecutionListener {
    private final Logger logger = LoggerFactory.getLogger(JobCompleteProcessor.class);
    private final LogEntryRepository repository;
    private final ParserConfig parserConfig;
    private final JdbcTemplate jdbcTemplate;

    public JobCompleteProcessor(LogEntryRepository repository, ParserConfig parserConfig, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

        List<LogEntry> result = new ArrayList<>();
        logEntryIterable.iterator().forEachRemaining(logEntry -> {
            logger.info("Log entry above threshold: {}", logEntry);
            result.add(logEntry);
        });

        jdbcTemplate.batchUpdate(
                "insert into above_threshold (id, date, ip, request, status, user_agent) values (?, ?, ?, ?, ?, ?)",
                result,
                parserConfig.getChunkSize(),
                (ps, logEntry) -> {
                    ps.setLong(1, logEntry.getId());
                    ps.setObject(2, logEntry.getDate());
                    ps.setString(3, logEntry.getIp());
                    ps.setString(4, logEntry.getRequest());
                    ps.setString(5, logEntry.getStatus());
                    ps.setString(6, logEntry.getUserAgent());
                });
    }
}