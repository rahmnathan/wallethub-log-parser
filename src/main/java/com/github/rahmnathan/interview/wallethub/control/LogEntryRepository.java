package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface LogEntryRepository extends CrudRepository<LogEntry, Long> {
    @Query("from LogEntry l " +
            "where l.ip in (select l2.ip from LogEntry l2 where l2.date between :startDate and :endDate group by l2.ip having count(l2) > :threshold) " +
            "and l.date between :startDate and :endDate")
    Iterable<LogEntry> findBetweenDatesAndAboveThreshold(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate,
                                                         @Param("threshold") Long threshold);
}
