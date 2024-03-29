package com.github.rahmnathan.wallethub.log.parser.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.time.LocalDateTime;

@Configuration
@ConfigurationProperties
public class ParserConfig {
    private Long threshold;
    private Duration duration;
    private LocalDateTime startDate;
    private File accesslog;
    private Integer chunkSize = 25000;

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public File getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(File accesslog) {
        this.accesslog = accesslog;
    }

    @Override
    public String toString() {
        return "ParserConfig{" +
                "threshold=" + threshold +
                ", duration=" + duration +
                ", startDate=" + startDate +
                ", accesslog=" + accesslog +
                ", chunkSize=" + chunkSize +
                '}';
    }

    public enum Duration {
        DAILY(24),
        HOURLY(1);

        private final Integer hours;

        Duration(Integer hours) {
            this.hours = hours;
        }

        public Integer getHours() {
            return hours;
        }
    }
}
