package com.github.rahmnathan.wallethub.log.parser.control;

import com.github.rahmnathan.wallethub.log.parser.entity.LogEntry;
import com.github.rahmnathan.wallethub.log.parser.entity.LogEntryAboveThreshold;
import com.github.rahmnathan.wallethub.log.parser.repository.AboveThresholdRepository;
import com.github.rahmnathan.wallethub.log.parser.repository.LogEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogEntryPersistenceService {
    private static final String ABOVE_THRESHOLD_QUERY =
            "select * from log_entry logEntry " +
            "where logEntry.ip in " +
                "(select logEntry2.ip from log_entry logEntry2 " +
                "where logEntry2.date between ? and ? " +
                "group by logEntry2.ip " +
                "having count(*) > ?) " +
            "and logEntry.date between ? and ?";

    private final Logger logger = LoggerFactory.getLogger(LogEntryPersistenceService.class);
    private static final String COMMENT = "Entry above threshold.";
    private final AboveThresholdRepository aboveThresholdRepository;
    private final LogEntryRepository logEntryRepository;
    private final JdbcTemplate jdbcTemplate;

    public LogEntryPersistenceService(AboveThresholdRepository aboveThresholdRepository, JdbcTemplate jdbcTemplate,
                                      LogEntryRepository logEntryRepository) {
        this.aboveThresholdRepository = aboveThresholdRepository;
        this.logEntryRepository = logEntryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    Collection<LogEntry> queryForLogEntriesAboveThreshold(LocalDateTime startDate, LocalDateTime endDate, Long threshold) {
        return jdbcTemplate.query(ABOVE_THRESHOLD_QUERY,
                new Object[]{startDate, endDate, threshold, startDate, endDate},
                (resultSet, i) -> LogEntry.Builder.newInstance()
                        .setId(resultSet.getLong(1))
                        .setDate(resultSet.getTimestamp(2).toLocalDateTime())
                        .setIp(resultSet.getString(3))
                        .setRequest(resultSet.getString(4))
                        .setStatus(resultSet.getString(5))
                        .setUserAgent(resultSet.getString(6))
                        .build());
    }

    void insertAboveThresholdEntries(Collection<LogEntry> logEntries){
        List<LogEntryAboveThreshold> logEntryAboveThresholdList = logEntries.stream()
                .map(logEntry -> new LogEntryAboveThreshold(logEntry, COMMENT))
                .collect(Collectors.toList());

        aboveThresholdRepository.saveAll(logEntryAboveThresholdList);
    }

    void insertLogEntries(List<? extends LogEntry> logEntries){
        logger.info("Persisting log entry collection of size: {}", logEntries.size());
        logEntryRepository.saveAll(logEntries);
    }
}
