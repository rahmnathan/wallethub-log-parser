package com.github.rahmnathan.interview.wallethub.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_entry")
public class LogEntry {

    @Id
    @SequenceGenerator(name = "log_entry_seq", sequenceName = "log_entry_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_entry_seq")
    private Long id;

    private LocalDateTime date;
    private String ip;
    private String request;
    private String status;
    private String userAgent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", date=" + date +
                ", ip='" + ip + '\'' +
                ", request='" + request + '\'' +
                ", status='" + status + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }

    public static class Builder {
        private LogEntry logEntry = new LogEntry();

        public static Builder newInstance(){
            return new Builder();
        }

        public Builder setDate(LocalDateTime date){
            logEntry.date = date;
            return this;
        }

        public Builder setIp(String ip){
            logEntry.ip = ip;
            return this;
        }

        public Builder setRequest(String request){
            logEntry.request = request;
            return this;
        }

        public Builder setStatus(String status) {
            logEntry.status = status;
            return this;
        }

        public Builder setUserAgent(String userAgent){
            logEntry.userAgent = userAgent;
            return this;
        }

        public LogEntry build(){
            LogEntry result = logEntry;
            logEntry = new LogEntry();

            return result;
        }
    }
}
