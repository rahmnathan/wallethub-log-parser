package com.github.rahmnathan.interview.wallethub.entity;

import javax.persistence.*;

@Entity
@Table(name = "above_threshold")
public class LogEntryAboveThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private LogEntry logEntry;
    private String comment;

    public LogEntryAboveThreshold(LogEntry logEntry, String comment) {
        this.logEntry = logEntry;
        this.comment = comment;
    }

    public LogEntryAboveThreshold() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(LogEntry logEntry) {
        this.logEntry = logEntry;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
