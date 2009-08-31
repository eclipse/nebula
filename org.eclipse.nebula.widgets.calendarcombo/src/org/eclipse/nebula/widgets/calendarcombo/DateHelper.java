/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.calendarcombo;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class DateHelper {

	private static final long	MILLISECONDS_IN_DAY	= 24 * 60 * 60 * 1000;

	public static long daysBetween(Calendar start, Calendar end, Locale locale) {
		// create copies
		GregorianCalendar startDate = new GregorianCalendar(locale);
		GregorianCalendar endDate = new GregorianCalendar(locale);

		// switch calendars to pure Julian mode for correct day-between
		// calculation, from the Java API:
		// - To obtain a pure Julian calendar, set the change date to
		// Date(Long.MAX_VALUE).
		startDate.setGregorianChange(new Date(Long.MAX_VALUE));
		endDate.setGregorianChange(new Date(Long.MAX_VALUE));

		// set them
		startDate.setTime(start.getTime());
		endDate.setTime(end.getTime());

		// force times to be exactly the same
		startDate.set(Calendar.HOUR_OF_DAY, 12);
		endDate.set(Calendar.HOUR_OF_DAY, 12);
		startDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		endDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		endDate.set(Calendar.MILLISECOND, 0);

		// now we should be able to do a "safe" millisecond/day caluclation to
		// get the number of days
		long endMilli = endDate.getTimeInMillis();
		long startMilli = startDate.getTimeInMillis();

		// calculate # of days, finally
		long diff = (endMilli - startMilli) / MILLISECONDS_IN_DAY;

		return diff;
	}

	public static long daysBetween(Date start, Date end, Locale locale) {
		Calendar dEnd = Calendar.getInstance(locale);
		Calendar dStart = Calendar.getInstance(locale);
		dEnd.setTime(end);
		dStart.setTime(start);
		return daysBetween(dStart, dEnd, locale);
	}

	public static boolean isToday(Date date, Locale locale) {
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(date);

		return isToday(cal, locale);
	}

	public static boolean isToday(Calendar cal, Locale locale) {
		Calendar today = Calendar.getInstance(locale);

		if (today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
			if (today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)) {
				return true;
			}
		}

		return false;
	}

	public static String getDate(Calendar cal, String dateFormat) {
		Calendar toUse = (Calendar) cal.clone();
		toUse.add(Calendar.MONTH, -1);

		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		df.setLenient(true);
		return df.format(cal.getTime());
	}

	public static boolean sameDate(Date date1, Date date2, Locale locale) {
		Calendar cal1 = Calendar.getInstance(locale);
		Calendar cal2 = Calendar.getInstance(locale);

		cal1.setTime(date1);
		cal2.setTime(date2);

		return sameDate(cal1, cal2);
	}

	public static boolean sameDate(Calendar cal1, Calendar cal2) {
		if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
			if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
				return true;
			}
		}

		return false;
	}

	public static Date getDate(String str, String dateFormat) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		df.setLenient(false);

		return df.parse(str);
	}

	public static Calendar getDate(String str, String dateFormat, Locale locale) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat(dateFormat, locale);
		Date d = df.parse(str);
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(d);
		return cal;
	}

	public static Calendar parseDate(String str, Locale locale) throws Exception {
		Date foo = DateFormat.getDateInstance(DateFormat.SHORT, locale).parse(str);
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(foo);
		return cal;
	}

	private static Calendar calendarize(Date date, Locale locale) {
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(date);
		return cal;
	}

	public static Calendar parse(final String comboText, final Locale locale, final String dateFormat, final char[] acceptedSeparatorChars, final List additionalDateFormats)
			throws CalendarDateParseException, Exception {
		boolean isNumeric = comboText.replaceAll("[^0-9]", "").length() == comboText.length();

		if (isNumeric) {
			return numericParse(comboText, locale, false);
		}
		else {
			return slashParse(comboText, dateFormat, acceptedSeparatorChars, locale);
		}

		//return null;
		//}

		/*if (comboText.length() == 0) {
			return null;
		}

		try {
			// start with a hard parse as date format parses can return
			// false positives on various locales.
			// false positives may sound good, but they're bad, as they can
			// cause a year to end up 2000 years off...
			try {
				mStartDate = DateHelper.parseDateHard(comboText, locale);
				return mStartDate;
			}
			catch (Exception err) {

			}
			
			// try true date format parse
			mStartDate = DateHelper.getDate(comboText, dateFormat, locale);
			return mStartDate;
			// System.err.println("Got here 2 - Settings parse " +
			// mStartDate.getTime());
		}
		catch (Exception err) {
			// try the locale (this is error prone due to how java parses
			// dates)
			try {
				mStartDate = DateHelper.parseDate(comboText, locale);
				return mStartDate;
				// System.err.println("Got here 3 - Locale parse " +
				// mStartDate.getTime());
			}
			catch (Exception deeper) {
				try {
					mStartDate = DateHelper.slashParse(comboText, dateFormat, acceptedSeparatorChars, locale);
					return mStartDate;
				}
				catch (Exception ohwell) {
					// System.err.println("Failed parse, trying additional formats");
					if (additionalDateFormats != null) {
						try {
							for (int i = 0; i < additionalDateFormats.size(); i++) {
								try {
									String format = (String) additionalDateFormats.get(i);
									Date date = DateHelper.getDate(comboText, format);
									return mStartDate;
								}
								catch (Exception failed) {
									// keep trying
								}
							}
						}
						catch (Exception err2) {
							// don't care
						}
					}
				}

				return mStartDate;
			}
		}
		}
		catch (Exception err) {
		err.printStackTrace();
		}*/

		//return null;
	}

	/**
	 * This method will try its best to parse a date based on the current
	 * Locale.
	 * 
	 * @param str
	 *            String to parse
	 * @param locale
	 *            Current Locale
	 * @return Calendar or null on failure
	 * @throws CalendarDateParseException
	 *             If date could not be parsed
	 * @throws Exception
	 *             on any unforseen issues or bad parse errors
	 */
	public static Calendar parseDateHard(final String str, final Locale locale) throws CalendarDateParseException, Exception {

		try {
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			String actualLocalePattern = ((SimpleDateFormat) df).toPattern();

			try {
				Calendar foo = slashParse(str, actualLocalePattern, new char[] {
						'/', '-', '.'
				}, locale);
				return foo;
			}
			catch (Exception err) {

			}

			try {
				Date foo = df.parse(str);
				return calendarize(foo, locale);
			}
			catch (Exception err) {
				// some locales already have 4 y's
				if (actualLocalePattern.indexOf("yyyy") == -1)
					actualLocalePattern = actualLocalePattern.replaceAll("yy", "yyyy");

				try {
					Date foo = df.parse(str);
					return calendarize(foo, locale);
				}
				catch (Exception err2) {
					// fall through
				}
			}
		}
		catch (Exception err) {
			// fall through
		}

		try {
			Date foo = DateFormat.getDateInstance().parse(str);
			return calendarize(foo, locale);
		}
		catch (Exception err) {
			try {
				Integer.parseInt(str);

				try {
					DateFormat df = DateFormat.getDateInstance();
					df.setLenient(false);
					Date foo = df.parse(str);
					return calendarize(foo, locale);
				}
				catch (Exception err2) {
					return numericParse(str, locale, true);
				}
			}
			catch (Exception failedInt) {
				// clear the bad chars and try again
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) >= '0' && str.charAt(i) <= '9')
						buf.append(str.charAt(i));
				}

				String fixed = buf.toString();
				try {
					Integer.parseInt(fixed);
					return numericParse(fixed, locale, true);
				}
				catch (Exception forgetit) {
					throw new CalendarDateParseException(forgetit, CalendarDateParseException.TYPE_EXCEPTION);
				}
			}
		}
	}

	// date formats with a single M d y etc are highly problematic (US dates),
	// so replace them with their proper format so that we can parse
	// or else we'll be parsing years like "03" as "0003"
	public static String dateFormatFix(String str) {
		if (str.indexOf("M") != -1 && str.indexOf("MM") == -1 && str.indexOf("MMM") == -1) {
			str = str.replaceAll("M", "MM");
		}
		if (str.indexOf("d") != -1 && str.indexOf("dd") == -1 && str.indexOf("ddd") == -1) {
			str = str.replaceAll("d", "dd");
		}
		if (str.indexOf("y") != -1 && str.indexOf("yy") == -1 && str.indexOf("yyy") == -1) {
			str = str.replaceAll("y", "yy");
		}

		return str;

	}

	public static Calendar numericParse(String str, Locale locale, boolean doUsEuParse) throws Exception {
		// we always start with the locale and try to parse that numerically, if
		// that fails we'll try another few possibilities before we give up
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		String actualLocalePattern = ((SimpleDateFormat) df).toPattern();

		// remove all non letters which will leave us with a clean date pattern
		actualLocalePattern = dateFormatFix(actualLocalePattern.replaceAll("[^a-zA-Z]", ""));
		actualLocalePattern = actualLocalePattern.replaceAll("G", "");

		// parse it into long / short versions where the year is 4 or 2 digits
		String actualLocaleLong = "";
		String actualLocaleShort = "";
		if (actualLocalePattern.indexOf("yyyy") == -1) {
			actualLocaleShort = actualLocalePattern;
			actualLocaleLong = actualLocalePattern.replaceAll("yy", "yyyy");
		}
		else {
			actualLocaleLong = actualLocalePattern;
			actualLocaleShort = actualLocalePattern.replaceAll("yyyy", "yy");
		}

		Date parsed = null;

		// now parse it according to locale if we can
		try {
			if (str.length() == 6) {
				SimpleDateFormat sdf = new SimpleDateFormat(actualLocaleShort);
				parsed = sdf.parse(str);
			}
			else if (str.length() == 5) {
				// if a user enters a 5-digit date we assume they were clever enough to get the day and month in 2 digit formats, and the last being the year in a 1 digit format.
				// so we need to 2-digitize the year and re-parse. As any 1900-year would be 2 digit except for early 1900's (we don't care) we assume it's 2000+. As I said, if we
				// get here the user is really pushing their luck on parsing anyway and we're doing them a favor to begin with
				StringBuffer buf = new StringBuffer();
				buf.append(str.substring(0, 4));
				buf.append("0");
				buf.append(str.substring(4, 5));
				return numericParse(buf.toString(), locale, doUsEuParse);
			}
			else {
				SimpleDateFormat sdf = new SimpleDateFormat(actualLocaleLong);
				parsed = sdf.parse(str);
			}

			if (parsed != null)
				return calendarize(parsed, locale);
		}
		catch (ParseException pe) {
			// ignore, try more
		}

		if (doUsEuParse) {
			// try a couple of pre-defined formats, it's highly likely it's
			// either
			// US or European
			String usFormat6 = "MMddyy";
			String usFormat8 = "MMddyyyy";
			String euFormat6 = "ddMMyy";
			String euFormat8 = "ddMMyyyy";

			if (locale.equals(Locale.US)) {
				if (str.length() == 6) {
					SimpleDateFormat sdf = new SimpleDateFormat(usFormat6);
					parsed = sdf.parse(str);
				}
				else {
					SimpleDateFormat sdf = new SimpleDateFormat(usFormat8);
					parsed = sdf.parse(str);
				}
			}
			else {
				if (str.length() == 6) {
					SimpleDateFormat sdf = new SimpleDateFormat(euFormat6);
					parsed = sdf.parse(str);
				}
				else {
					SimpleDateFormat sdf = new SimpleDateFormat(euFormat8);
					parsed = sdf.parse(str);
				}
			}
		}

		if (parsed != null) {
			return calendarize(parsed, locale);
		}

		return null;
	}

	public static int getCalendarTypeForString(String oneChar) {

		int calType = -1;

		switch (oneChar.charAt(0)) {
			case 'G':
				calType = Calendar.ERA;
				break;
			case 'y':
				calType = Calendar.YEAR;
				break;
			case 'M':
				calType = Calendar.MONTH;
				break;
			case 'd':
				calType = Calendar.DAY_OF_MONTH;
				break;
			case 'E':
				calType = Calendar.DAY_OF_WEEK;
				break;
			case 'D':
				calType = Calendar.DAY_OF_YEAR;
				break;
			case 'F':
				calType = Calendar.DATE;
				break;
			case 'h':
				calType = Calendar.HOUR;
				break;
			case 'm':
				calType = Calendar.MINUTE;
				break;
			case 's':
				calType = Calendar.SECOND;
				break;
			case 'S':
				calType = Calendar.MILLISECOND;
				break;
			case 'w':
				calType = Calendar.WEEK_OF_YEAR;
				break;
			case 'W':
				calType = Calendar.WEEK_OF_MONTH;
				break;
			case 'a':
				calType = Calendar.AM_PM;
				break;
			case 'k':
				calType = Calendar.HOUR_OF_DAY;
				break;
			case 'K':
				// ?
				break;
			case 'z':
				calType = Calendar.ZONE_OFFSET;
				break;
		}

		return calType;
	}

	/**
	 * This method assumes the dateFormat has a separator char in it, and that
	 * we can use that to determine what the user entered by using that
	 * separator to split up the user entered date, and then do some logic on
	 * it. This is by no means a foolproof method and should not be relied upon
	 * returning 100% correct dates all the time.
	 * 
	 * @param str
	 *            String to parse
	 * @param dateFormat
	 *            DateFormat to use
	 * @param separators
	 *            Separator chars that can be encountered
	 * @param locale
	 *            Locale
	 * @return Calendar
	 * @throws CalendarDateParseException
	 *             If date could not be parsed
	 * @throws Exception
	 *             If any step of the parsing failed
	 */
	public static Calendar slashParse(final String str, final String dateFormat, final char[] separators, final Locale locale) throws CalendarDateParseException, Exception {
		int start = -1;
		String splitter = null;
		String dateFormatToUse = dateFormat;
		for (int i = 0; i < separators.length; i++) {
			start = str.indexOf(separators[i]);
			if (start != -1) {
				splitter = String.valueOf(separators[i]);
				break;
			}
		}
		if (start == -1)
			throw new CalendarDateParseException("Failed to find splitter char", CalendarDateParseException.TYPE_NO_SLPITTER_CHAR);

		// replace dateFormat until we have same splitter
		for (int i = 0; i < separators.length; i++) {
			if (String.valueOf(separators[i]).equals(splitter))
				continue;

			dateFormatToUse = dateFormatToUse.replaceAll("\\" + String.valueOf(separators[i]), splitter);
		}

		Calendar toReturn = Calendar.getInstance(locale);
		StringTokenizer st = new StringTokenizer(str, splitter);
		StringTokenizer st2 = new StringTokenizer(dateFormatToUse, splitter);

		if (st.countTokens() != st2.countTokens())
			throw new CalendarDateParseException("Date format does not match date string in terms of splitter character numbers", CalendarDateParseException.TYPE_INSUFFICIENT_SPLITTERS);

		// variables we'll be extracting
		int monthToSet = -1;
		int dayToSet = -1;
		int yearToSet = -1;
	
		// reset, skipping month this time
		st = new StringTokenizer(str, splitter);
		st2 = new StringTokenizer(dateFormatToUse, splitter);

		while (st.hasMoreTokens()) {
			String dateValue = st.nextToken();
			String dateType = st2.nextToken();

			dateValue = dateValue.replaceAll(" ", "");
			dateType = dateType.replaceAll(" ", "");

			int calType = getCalendarTypeForString(dateType);
			// we already did month
			if (calType == Calendar.MONTH) {
			    monthToSet = Integer.parseInt(dateValue);
				continue;
			}
			if (calType == Calendar.YEAR) {
			    yearToSet = Integer.parseInt(dateValue);
			    continue;
			}
            if (calType == Calendar.DATE) {
                dayToSet = Integer.parseInt(dateValue);
                continue;
            }

			toReturn.set(calType, Integer.parseInt(dateValue));
		}

		// set all date parameters at the same time, or else we'll get month-skipping due to setting a value later (such as a date that is too high
		// for the current month). (-1 for month as Calendar class is month-zero-based).
		if (monthToSet != -1 && dayToSet != -1 && yearToSet != -1) {
		    toReturn.set(yearToSet, monthToSet-1, dayToSet);
		}
		else {
		    // set what we know
		    if (yearToSet != -1) {
		        toReturn.set(Calendar.YEAR, yearToSet);
		    }
		    if (monthToSet != -1) {
		        toReturn.set(Calendar.MONTH, monthToSet-1);
		    }
		    if (dayToSet != -1) {
		        toReturn.set(Calendar.DATE, dayToSet);
		    }
		}
		
		if (toReturn.get(Calendar.YEAR) < 100)
			toReturn.set(Calendar.YEAR, toReturn.get(Calendar.YEAR) + 2000);

		toReturn.set(Calendar.HOUR_OF_DAY, 0);
		toReturn.set(Calendar.MINUTE, 0);
		toReturn.set(Calendar.SECOND, 0);
		toReturn.set(Calendar.MILLISECOND, 0);

		return toReturn;
	}

	/**
	 * Parses a string (representing a month) and returns it's corresponding
	 * value as a Calendar month. This is used to parse MMM month dates
	 * 
	 * @param monthStr
	 *            String to parse
	 * @param locale
	 *            Locale to use
	 * @return Month value or -1 if not found
	 */
	private static int getMonthForString(String monthStr, Locale locale) {
		DateFormatSymbols dfs = new DateFormatSymbols(locale);
		String[] months = dfs.getMonths();
		for (int i = 0; i < months.length; i++) {
			if (months[i].toLowerCase(locale).startsWith(monthStr.toLowerCase(locale))) {
				return i + 1;
			}
		}

		return -1;
	}

	/*public Calendar smartParse(String dateStr, Locale locale) {
		
		// Samples:
		// 080101 20080101
		// 08/01/01 2008/01/01
		return null;
	}*/
}
