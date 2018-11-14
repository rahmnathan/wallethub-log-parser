package com.github.rahmnathan.interview.wallethub.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@ConfigurationPropertiesBinding
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd.HH:mm:ss";

    @Override
    public LocalDateTime convert(String source) {
        if(source==null){
            return null;
        }

        return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }
}