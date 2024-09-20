package com.kerneldc.common.domain;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.function.Function;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class AbstractEntity {
	
	protected static final String OFFSET_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSSZ";
	protected static final String LOCAL_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSS";
	// This is the same fprmat as produced by ISO_INSTANT except it displays milliseconds
	// Source from ISO_INSTANT declaration
	protected static final DateTimeFormatter OFFSET_DATE_TIME_UTC_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendInstant(3) // milliseconds
            .toFormatter();
	protected static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT);
	
	public static final Function<AbstractEntity, Object> idExtractor = AbstractEntity::getId;

	abstract Long getId(); 

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    
}
