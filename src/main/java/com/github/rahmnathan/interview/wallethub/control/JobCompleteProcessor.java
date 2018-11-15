package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.config.ParserConfig;
import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import com.github.rahmnathan.interview.wallethub.entity.LogEntryAboveThreshold;
import com.github.rahmnathan.interview.wallethub.repository.AboveThresholdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JobCompleteProcessor implements JobExecutionListener {
    private static final String ABOVE_THRESHOLD_QUERY =
            "select * from log_entry logEntry where logEntry.ip in " +
            "(select logEntry2.ip from log_entry logEntry2 where logEntry2.date between ? and ? group by logEntry2.ip having count(*) > ?) " +
            "and logEntry.date between ? and ?";

    private static final String COMMENT = "Entry above threshold.";
    private final Logger logger = LoggerFactory.getLogger(JobCompleteProcessor.class);
    private final AboveThresholdRepository aboveThresholdRepository;
    private final ParserConfig parserConfig;
    private final JdbcTemplate jdbcTemplate;

    public JobCompleteProcessor(ParserConfig parserConfig, JdbcTemplate jdbcTemplate, AboveThresholdRepository aboveThresholdRepository) {
        this.aboveThresholdRepository = aboveThresholdRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.parserConfig = parserConfig;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Starting job with parser config: {}", parserConfig);
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        logger.info("Job is completed. Querying for entries above threshold.");

        Collection<LogEntry> logEntries = queryForLogEntriesAboveThreshold();

        logEntries.forEach(logEntry -> logger.info("Log entry above threshold: {}", logEntry));

        insertAboveThresholdEntries(logEntries);
    }

    private Collection<LogEntry> queryForLogEntriesAboveThreshold() {
        LocalDateTime startDate = parserConfig.getStartDate();
        LocalDateTime endDate = startDate.plusHours(parserConfig.getDuration().getHours());

        return jdbcTemplate.query(ABOVE_THRESHOLD_QUERY,
                new Object[]{startDate, endDate, parserConfig.getThreshold(), startDate, endDate},
                (resultSet, i) -> LogEntry.Builder.newInstance()
                        .setId(resultSet.getLong(1))
                        .setDate(resultSet.getTimestamp(2).toLocalDateTime())
                        .setIp(resultSet.getString(3))
                        .setRequest(resultSet.getString(4))
                        .setStatus(resultSet.getString(5))
                        .setUserAgent(resultSet.getString(6))
                        .build());
    }

    private void insertAboveThresholdEntries(Collection<LogEntry> logEntries){
        List<LogEntryAboveThreshold> logEntryAboveThresholdList = logEntries.stream()
                .map(logEntry -> new LogEntryAboveThreshold(logEntry, COMMENT))
                .collect(Collectors.toList());

        aboveThresholdRepository.saveAll(logEntryAboveThresholdList);
    }
}