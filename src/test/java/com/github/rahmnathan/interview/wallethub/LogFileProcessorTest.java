package com.github.rahmnathan.interview.wallethub;

import com.github.rahmnathan.interview.wallethub.repository.AboveThresholdRepository;
import com.github.rahmnathan.interview.wallethub.repository.LogEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class LogFileProcessorTest {
    private final AboveThresholdRepository aboveThresholdRepository;
    private final LogEntryRepository logEntryRepository;

    @Autowired
    public LogFileProcessorTest(AboveThresholdRepository aboveThresholdRepository, LogEntryRepository logEntryRepository,
                                JobLauncher jobLauncher, @Qualifier("loadLogEntriesJob") Job job) throws Exception {
        this.aboveThresholdRepository = aboveThresholdRepository;
        this.logEntryRepository = logEntryRepository;

        jobLauncher.run(job, new JobParameters());
    }

    @Test
    public void validateLogEntriesTest() {
        assertEquals(12, logEntryRepository.count());
    }

    @Test
    public void validateLogEntriesAboveThresholdTest() {
        assertEquals(6, aboveThresholdRepository.count());
    }
}
