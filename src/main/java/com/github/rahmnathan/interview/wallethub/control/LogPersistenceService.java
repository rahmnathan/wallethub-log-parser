package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.config.ParserConfig;
import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogPersistenceService {
    private final Logger logger = LoggerFactory.getLogger(LogPersistenceService.class);
    private final LogEntryRepository logEntryRepository;
    private final ParserConfig parserConfig;

    public LogPersistenceService(ParserConfig parserConfig, LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
        this.parserConfig = parserConfig;

        logger.info("ParserConfig: {}", parserConfig);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void processLogs() throws Exception {
        Files.lines(parserConfig.getAccesslog().toPath())
                .map(line -> {
                    String[] elements = line.split("\\|");
                    return LogEntry.Builder.newInstance()
                            .setDate(LocalDateTime.parse(elements[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")))
                            .setIp(elements[1])
                            .setRequest(elements[2])
                            .setStatus(elements[3])
                            .setUserAgent(elements[4])
                            .build();
                })
                .forEach(logEntryRepository::save);

        LocalDateTime startDate = parserConfig.getStartDate();
        LocalDateTime endDate = startDate.plusHours(parserConfig.getDuration().getHours());

        Iterable<LogEntry> logEntryIterable = logEntryRepository.findBetweenDatesAndAboveThreshold(startDate, endDate, parserConfig.getThreshold());

        logEntryIterable.forEach(logEntry -> logger.info("LOGENTRY: {}", logEntry));
    }
}
