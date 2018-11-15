package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.config.ParserConfig;
import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Collection;

public class JobCompleteProcessor implements JobExecutionListener {
    private static final String ABOVE_THRESHOLD_QUERY = "select * from log_entry logEntry where logEntry.ip in " +
            "(select logEntry2.ip from log_entry logEntry2 where logEntry2.date between ? and ? group by logEntry2.ip having count(*) > ?) " +
            "and logEntry.date between ? and ?";

    private static final String INSERT_ABOVE_THRESHOLD_QUERY = "insert into above_threshold (log_entry_id) values (?)";
    private final Logger logger = LoggerFactory.getLogger(JobCompleteProcessor.class);
    private final ParserConfig parserConfig;
    private final JdbcTemplate jdbcTemplate;

    public JobCompleteProcessor(ParserConfig parserConfig, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.parserConfig = parserConfig;

        logger.info("ParserConfig: {}", parserConfig);
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Starting job.");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Job is completed. Querying for entries above threshold.");

        LocalDateTime startDate = parserConfig.getStartDate();
        LocalDateTime endDate = startDate.plusHours(parserConfig.getDuration().getHours());

        Collection<LogEntry> logEntries = jdbcTemplate.query(ABOVE_THRESHOLD_QUERY,
                new Object[]{startDate, endDate, parserConfig.getThreshold(), startDate, endDate},
                (resultSet, i) -> LogEntry.Builder.newInstance()
                            .setId(resultSet.getLong(1))
                            .setDate(resultSet.getTimestamp(2).toLocalDateTime())
                            .setIp(resultSet.getString(3))
                            .setRequest(resultSet.getString(4))
                            .setStatus(resultSet.getString(5))
                            .setUserAgent(resultSet.getString(6))
                            .build());

        jdbcTemplate.batchUpdate(
                INSERT_ABOVE_THRESHOLD_QUERY,
                logEntries,
                parserConfig.getChunkSize(),
                (ps, logEntry) -> {
                    ps.setObject(1, logEntry.getId());
                });
    }
}