package com.kerneldc.ipm.util;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.junit.jupiter.api.Test;

class TimeUtilsTest {

	@Test
	void testCompareDatePart() {
		var dateFormatter1 = DateTimeFormatter.ofPattern("uuuu-MM-dd").withZone(ZoneId.systemDefault());
		var now = OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
		var yesterday = OffsetDateTime.ofInstant(
				LocalDate.parse("2023-06-11", dateFormatter1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
				ZoneId.systemDefault());
		System.out.println("now date part: " + dateFormatter1.format(now));
		System.out.println("yesterday: " + dateFormatter1.format(yesterday));
		
		int result = TimeUtils.compareDatePart(yesterday, now);
		System.out.println("compare result: " + result);
		assertThat(TimeUtils.compareDatePart(yesterday, now), is(-1));
	}

	@Test
	void testDateTimeFormatter_ofLocalizedDateTime() {
		var dateFormatter1 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withZone(ZoneId.systemDefault());
		var dateFormatter2 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withZone(ZoneId.systemDefault());
		var dateFormatter3 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
		var dateFormatter4 = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
		var now = OffsetDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
		System.out.println("FormatStyle.FULL: " + now.format(dateFormatter1));
		System.out.println("FormatStyle.LONG: " + now.format(dateFormatter2));
		System.out.println("FormatStyle.MEDIUM: " + now.format(dateFormatter3));
		System.out.println("FormatStyle.SHORT: " + now.format(dateFormatter4));
	}

}
