package com.github.rahmnathan.wallethub.log.parser.persistence.repository;

import com.github.rahmnathan.wallethub.log.parser.persistence.entity.LogEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEntryRepository extends CrudRepository<LogEntry, Long> {
}
