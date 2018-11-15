package com.github.rahmnathan.interview.wallethub.control;

import com.github.rahmnathan.interview.wallethub.entity.LogEntry;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.validation.DataBinder;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntryMapper extends BeanWrapperFieldSetMapper<LogEntry> {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public LogEntryMapper(){
        setTargetType(LogEntry.class);
    }

    @Override
    public void initBinder(DataBinder binder) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text != null) {
                    setValue(java.time.LocalDateTime.parse(text, formatter));
                } else {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() throws IllegalArgumentException {
                Object date = getValue();
                if (date != null) {
                    return formatter.format((java.time.LocalDateTime) getValue());
                } else {
                    return "";
                }
            }
        });
    }
}