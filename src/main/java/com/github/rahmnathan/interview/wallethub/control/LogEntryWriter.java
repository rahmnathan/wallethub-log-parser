package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogEntryWriter implements ItemWriter<LogEntry> {
    private static final String INSERT_LOG_ENTRY_QUERY = "insert into log_entry (date, ip, request, status, user_agent) values (?, ?, ?, ?, ?)";
    private final Logger logger = LoggerFactory.getLogger(LogEntryWriter.class);
    private final JdbcTemplate jdbcTemplate;

    public LogEntryWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(List<? extends LogEntry> list) {
        logger.info("Persisting log entries of size: {}", list.size());

        jdbcTemplate.batchUpdate(
                INSERT_LOG_ENTRY_QUERY,
                list,
                list.size(),
                (ps, logEntry) -> {
                    ps.setObject(1, logEntry.getDate());
                    ps.setString(2, logEntry.getIp());
                    ps.setString(3, logEntry.getRequest());
                    ps.setString(4, logEntry.getStatus());
                    ps.setString(5, logEntry.getUserAgent());
                });
    }
}
