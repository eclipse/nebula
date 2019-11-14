/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateHelper {

    private static final long MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;
    private static Map        _dateFormatMap;
    private static int        _todayYear;
    private static int        _todayYearDate;
    private static Locale     _locale;
        
    static {
        _dateFormatMap = new HashMap();
    }

    public static void initialize(Locale locale) {
        _locale = locale;
        Calendar temp = Calendar.getInstance(locale);
        _todayYear = temp.get(Calendar.YEAR);
        _todayYearDate = temp.get(Calendar.DAY_OF_YEAR);
        _dateFormatMap.clear();
    }

    public static int hoursBetween(Calendar start, Calendar end, boolean assumeSameDate) {
        return minutesBetween(start.getTime(), end.getTime(), assumeSameDate, false) / 60;
    }

    public static int hoursBetween(Date start, Date end, boolean assumeSameDate) {
        return minutesBetween(start, end, assumeSameDate, false) / 60;
    }

    public static int minutesBetween(Date start, Date end, boolean assumeSameDate, boolean assumeSameHour) {
        Calendar sDate = Calendar.getInstance(_locale);
        Calendar eDate = Calendar.getInstance(_locale);
        sDate.setTime(start);
        eDate.setTime(end);

        if (assumeSameDate) {
            // set same date
            eDate.set(Calendar.YEAR, 2000);
            sDate.set(Calendar.YEAR, 2000);
            eDate.set(Calendar.DAY_OF_YEAR, 1);
            sDate.set(Calendar.DAY_OF_YEAR, 1);
        }

        if (assumeSameHour) {
            eDate.set(Calendar.HOUR, 1);
            sDate.set(Calendar.HOUR, 1);
        }

        long diff = eDate.getTimeInMillis() - sDate.getTimeInMillis();
        diff /= 1000;
        diff /= 60;
        return (int) diff;
    }

    public static int secondsBetween(Date start, Date end, boolean assumeSameDate, boolean assumeSameHour) {
        Calendar sDate = Calendar.getInstance(_locale);
        Calendar eDate = Calendar.getInstance(_locale);
        sDate.setTime(start);
        eDate.setTime(end);

        if (assumeSameDate) {
            // set same date
            eDate.set(Calendar.YEAR, 2000);
            sDate.set(Calendar.YEAR, 2000);
            eDate.set(Calendar.DAY_OF_YEAR, 1);
            sDate.set(Calendar.DAY_OF_YEAR, 1);
        }

        if (assumeSameHour) {
            eDate.set(Calendar.HOUR, 1);
            sDate.set(Calendar.HOUR, 1);
        }

        long diff = eDate.getTimeInMillis() - sDate.getTimeInMillis();
        diff /= 1000;
        return (int) diff;
    }
    
    public static long daysBetween(Calendar start, Calendar end) {
        // create copies
        GregorianCalendar startDate = new GregorianCalendar(_locale);
        GregorianCalendar endDate = new GregorianCalendar(_locale);

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

        // now we should be able to do a "safe" millisecond/day calculation to
        // get the number of days, note that we need to include the timezone or daylights savings will get lost!! this is a huge issue
        long endMilli = endDate.getTimeInMillis() + endDate.getTimeZone().getOffset(endDate.getTimeInMillis());
        long startMilli = startDate.getTimeInMillis() + startDate.getTimeZone().getOffset(startDate.getTimeInMillis());

        // calculate # of days, finally
        long diff = (endMilli - startMilli) / MILLISECONDS_IN_DAY;

        return diff;
    }

    public static long daysBetweenxX(Calendar start, Calendar end) {
        //reset all hours mins and secs to zero on start date
        Calendar startCal = GregorianCalendar.getInstance();
        startCal.setTime(start.getTime());
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        long startTime = startCal.getTimeInMillis();

        //reset all hours mins and secs to zero on end date
        Calendar endCal = GregorianCalendar.getInstance();
        endCal.setTime(end.getTime());
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        long endTime = endCal.getTimeInMillis();

        return (endTime - startTime) / MILLISECONDS_IN_DAY;
    }

    public static long daysBetween(Date start, Date end) {
        Calendar dEnd = Calendar.getInstance(_locale);
        Calendar dStart = Calendar.getInstance(_locale);
        dEnd.setTime(end);
        dStart.setTime(start);
        return daysBetween(dStart, dEnd);
    }

    public static boolean isNow(Calendar cal, Locale locale, boolean minuteCheck) {
        if (isToday(cal)) {
            Calendar today = Calendar.getInstance(locale);

            if (today.get(Calendar.HOUR_OF_DAY) == cal.get(Calendar.HOUR_OF_DAY)) {

                if (!minuteCheck) { return true; }

                if (today.get(Calendar.MINUTE) == cal.get(Calendar.MINUTE)) { return true; }
            }
        }

        return false;
    }

    public static boolean isToday(Date date) {
        Calendar cal = Calendar.getInstance(_locale);
        cal.setTime(date);

        return isToday(cal);
    }

    /**
     * Remember to ensure the correct locale is set on the calendar before using this method.
     * 
     * @param cal Calendar to check
     * @return true if calendar matches todays date
     */
    public static boolean isToday(Calendar cal) {
        return (cal.get(Calendar.YEAR) == _todayYear && cal.get(Calendar.DAY_OF_YEAR) == _todayYearDate);
    }

    //private static WeakHashMap fastDateMap = new WeakHashMap(1000, 0.75f);

    public static Calendar getNewCalendar(Calendar old) {
        Calendar ret = _locale == null ? Calendar.getInstance() : Calendar.getInstance(_locale);
        ret.setTime(old.getTime());
        return ret;
    }

    public static String getDate(Date date, String dateFormat) {
    	Calendar cal = Calendar.getInstance(_locale);
    	cal.setTime(date);
    	return getDate(cal, dateFormat);
    }
    
    public static String getDate(Calendar cal, String dateFormat) {
        //Calendar toUse = (Calendar) cal.clone();
        Calendar toUse = Calendar.getInstance(_locale);
        toUse.setTime(cal.getTime());
        toUse.add(Calendar.MONTH, -1);

        /*		HashMap dMap = null;
        		if (fastDateMap.get(cal) != null) {
        			dMap = (HashMap) fastDateMap.get(cal);
        			if (dMap.get(dateFormat) != null) {
        				System.err.println("Returned old");
        				return (String) dMap.get(dateFormat);
        			}
        		}
        */
        SimpleDateFormat df = null;
        if (_dateFormatMap.get(dateFormat) != null) {            
            df = (SimpleDateFormat) _dateFormatMap.get(dateFormat);
        } else {
            df = new SimpleDateFormat(dateFormat, _locale);
            _dateFormatMap.put(dateFormat, df);
        }

        df.setLenient(true);
        //String ret = df.format(cal.getTime());

        /*		// cache it
        		if (dMap == null) 
        			dMap = new HashMap();
        		
        		System.err.println("Created new " + cal.getTime());
        		
        		dMap.put(dateFormat, ret);
        		fastDateMap.put(cal, dMap);
        */
        return df.format(cal.getTime());
    }

    public static boolean sameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance(_locale);
        Calendar cal2 = Calendar.getInstance(_locale);

        cal1.setTime(date1);
        cal2.setTime(date2);

        return sameDate(cal1, cal2);
    }

    public static boolean sameDate(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

}
