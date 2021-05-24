package org.alp.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.DAYS;

public class DateService {

	public static int daysBetween(LocalDate first, LocalDate second) {
		return Math.round(DAYS.between(first, second));
	}

	public static int daysInYear(LocalDate date) {
		return date.lengthOfYear();
	}

	public static int daysSinceBeginningOfYear(LocalDate date) {
		return daysBetween(LocalDate.of(date.getYear(), 1, 1), date);
	}
}
