package com.github.rahmnathan.wallethub.log.parser.repository;

import com.github.rahmnathan.wallethub.log.parser.entity.LogEntryAboveThreshold;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboveThresholdRepository extends CrudRepository<LogEntryAboveThreshold, Long> {
}
