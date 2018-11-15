package com.github.rahmnathan.interview.wallethub.repository;

import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEntryRepository extends CrudRepository<LogEntry, Long> {
}
