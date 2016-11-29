package edu.uncfsu.softwaredesign.f16.r2.cost;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;

import com.google.common.collect.Range;

public enum Season implements Serializable {
	SPRING(Range.closedOpen(MonthDay.of(Month.MARCH, 20), MonthDay.of(Month.JUNE, 21))),
	SUMMER(Range.closedOpen(MonthDay.of(Month.JUNE, 21), MonthDay.of(Month.SEPTEMBER, 22))), 
	FALL(Range.closedOpen(MonthDay.of(Month.SEPTEMBER, 22), MonthDay.of(Month.DECEMBER, 21))), 
	WINTER(Range.openClosed(MonthDay.of(Month.MARCH, 20), MonthDay.of(Month.DECEMBER, 21))); 
	
	private final Range<MonthDay> dateRange;
	
	Season(Range<MonthDay> range) {
		dateRange = range;
	}
	
	public Range<MonthDay> getRange() {
		return dateRange;
	}
	
	public static Season getSeasonFor(LocalDate date) {
		
		for (Season season : values()) {
			if (season.dateRange.contains(MonthDay.of(date.getMonth(), date.getDayOfMonth()))) {
				return season;
			}
		}
		
		return null;
	}
	
}
