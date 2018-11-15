package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import com.github.rahmnathan.interview.wallethub.repository.LogEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogEntryWriter implements ItemWriter<LogEntry> {
    private final Logger logger = LoggerFactory.getLogger(LogEntryWriter.class);
    private final LogEntryRepository logEntryRepository;

    public LogEntryWriter(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    @Override
    public void write(List<? extends LogEntry> list) {
        logger.info("Persisting log entries of size: {}", list.size());
        logEntryRepository.saveAll(list);
    }
}
