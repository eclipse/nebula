package org.eclipse.nebula.widgets.ganttchart;

import java.util.Calendar;
import java.util.Locale;

// TODO Abstract
public class HeaderLevel {

	private int				calendarType;
	private int				calendarTick;
	private int				calendarTickWidthInPixels;
	private Locale			locale;
	private String			dateFormatString;
	private int				separatorType;
	private int				calendarOffsetType;
	
	public static final int	SEPARATOR_NONE			= 0;
	public static final int	SEPARATOR_BOTTOM_TICK	= 1;
	public static final int	SEPARATOR_FULL_LINE		= 2;
	
	public static final int OFFSET_DATE = 1; 

	public HeaderLevel(int calendarType, int calendarTick, int calendarOffsetType, int calendarTickWidthInPixels, String dateFormatString, int separator, Locale locale) {
		this.calendarType = calendarType;
		this.calendarTick = calendarTick;
		this.calendarTickWidthInPixels = calendarTickWidthInPixels;
		this.dateFormatString = dateFormatString;
		this.locale = locale;
		this.calendarOffsetType = calendarOffsetType;
		this.separatorType = separator;
	}

	public int getMax() {
		return Calendar.getInstance(locale).getMaximum(calendarType);
	}

	public int getMin() {
		return Calendar.getInstance(locale).getMinimum(calendarType);
	}

	public int getCalendarType() {
		return calendarType;
	}

	public void setCalendarType(int calendarType) {
		this.calendarType = calendarType;
	}

	public int getCalendarTick() {
		return calendarTick;
	}

	public void setCalendarTick(int calendarTick) {
		this.calendarTick = calendarTick;
	}

	public int getCalendarTickWidthInPixels() {
		return calendarTickWidthInPixels;
	}

	public void setCalendarTickWidthInPixels(int calendarTickWidthInPixels) {
		this.calendarTickWidthInPixels = calendarTickWidthInPixels;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getDateFormatString() {
		return dateFormatString;
	}

	public void setDateFormatString(String dateFormatString) {
		this.dateFormatString = dateFormatString;
	}

	public int getSeparatorType() {
		return separatorType;
	}

	public void setSeparatorType(int separatorType) {
		this.separatorType = separatorType;
	}

	public int getCalendarOffsetType() {
		return calendarOffsetType;
	}

	public void setCalendarOffsetType(int calendarOffsetType) {
		this.calendarOffsetType = calendarOffsetType;
	}
	
	public int getOffset(Calendar cal) {
		switch (calendarOffsetType) {
			case OFFSET_DATE:
				int day = cal.get(Calendar.DAY_OF_WEEK);
				int firstDayOfWeek = cal.getFirstDayOfWeek();
				int dayOffset = firstDayOfWeek - day;

				// BUGFIX Sep 12/07:
				// On international dates that have getFirstDayOfWeek() on other days
				// than Sundays, we need to do some magic to these
				// or the dates printed will be wrong. As such, if the offset is
				// positive, we simply toss off 7 days to get the correct number.
				// we want a negative value in the end.
				if (dayOffset > 0)
					dayOffset -= 7;

				return dayOffset;
		}
		
		return 0;
	}

}
