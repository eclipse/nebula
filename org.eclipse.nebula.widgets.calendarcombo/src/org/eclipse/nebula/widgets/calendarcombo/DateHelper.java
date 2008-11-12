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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateHelper {

	private static final long MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;
	
	public static long daysBetween(Calendar start, Calendar end, Locale locale) {		
		// create copies
		GregorianCalendar startDate = new GregorianCalendar(locale);
		GregorianCalendar endDate = new GregorianCalendar(locale);

		// switch calendars to pure Julian mode for correct day-between calculation, from the Java API:
		// - To obtain a pure Julian calendar, set the change date to Date(Long.MAX_VALUE).
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
	
		// now we should be able to do a "safe" millisecond/day caluclation to get the number of days
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
        Date d = getDate(str, dateFormat);
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
    
    /**
     * This method will try its best to parse a date based on the current locale.
     * 
     * @param str String to parse
     * @param locale Current locale
     * @return Calendar or null on failure
     * @throws Exception on any unforseen issues or bad parse errors
     */
    public static Calendar parseDateHard(String str, Locale locale) throws Exception {
    	/*    	 
    	 // this code will potentially give us the format, but it's a bit tricky to make it out and if it's consistent 								
    	 ResourceBundle r = LocaleData.getDateFormatData(mSettings.getLocale());
		String [] foo = r.getStringArray("DateTimePatterns"); 
		for (int i = 0; i < foo.length; i++)
			System.err.println(foo[i]);
    	 */

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
    				return numericParse(str, locale);
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
    				return numericParse(fixed, locale);
    			}
    			catch (Exception forgetit) {
    				throw new Exception(forgetit);
    			}    			
    		}
    	}    	
    }
    
    private static Calendar numericParse(String str, Locale locale) throws Exception {
    	String usFormat6 = "MMddyy";
		String usFormat8 = "MMddyyyy";
		String euFormat6 = "ddMMyy";
		String euFormat8 = "ddMMyyyy";

		Date parsed = null;
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
		
		if (parsed != null) {
			return calendarize(parsed, locale);
		}
		
		return null;
    }
}
