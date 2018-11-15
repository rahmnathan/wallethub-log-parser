package com.github.rahmnathan.interview.wallethub.repository;

import com.github.rahmnathan.interview.wallethub.entity.LogEntryAboveThreshold;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboveThresholdRepository extends CrudRepository<LogEntryAboveThreshold, Long> {
}
