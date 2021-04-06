package org.eclipse.nebula.widgets.xviewer.util.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Donald G. Dunne
 */
public class DateUtil {

   public static final long MILLISECONDS_IN_A_WEEK = 604800000;
   public static final long MILLISECONDS_IN_A_DAY = 86400000;
   public final static String MMDDYY = "MM/dd/yyyy";
   public final static String YYYYMMDD = "yyyy/MM/dd";
   public final static String YYYY_MM_DD = "yyyy_MM_dd";
   public final static String YYYY_MM_DD_WITH_DASHES = "yyyy-MM-dd";
   public final static String MMDDYYHHMM = "MM/dd/yyyy hh:mm a";
   public final static String HHMMSS = "hh:mm:ss";
   public final static String HHMMSSSS = "hh:mm:ss:SS";
   public final static String HHMM = "hh:mm";
   public static final HashMap<String, DateFormat> dateFormats = new HashMap<String, DateFormat>();
   private static Date SENTINAL = null;

   public static Calendar getCalendar(Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      return calendar;
   }

   public static int getWorkingDaysBetween(Date fromDate, Date toDate) {
      return getWorkingDaysBetween(getCalendar(fromDate), getCalendar(toDate));
   }

   public static boolean isWeekDay(Calendar cal) {
      return cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY;
   }

   public static Date getDate(String format, String value) throws ParseException {
      SimpleDateFormat formatter = new SimpleDateFormat(format);
      Date date = formatter.parse(value);
      return date;
   }

   public static int getWorkingDaysBetween(Calendar fromDate, Calendar toDate) {
      int workingDays = 0;
      while (!fromDate.after(toDate)) {
         int day = fromDate.get(Calendar.DAY_OF_WEEK);
         if (day != Calendar.SATURDAY && day != Calendar.SUNDAY) {
            workingDays++;
         }

         fromDate.add(Calendar.DATE, 1);
      }
      return workingDays;
   }

   public static String getHHMM(Date date) {
      return get(date, HHMM);
   }

   public static String getHHMMSS(Date date) {
      return get(date, HHMMSS);
   }

   public static String getYYYYMMDD() {
      return getYYYYMMDD(new Date());
   }

   public static String getYYYYMMDD(Date date) {
      return get(date, YYYYMMDD);
   }

   public static String getMMDDYY(Date date) {
      return get(date, MMDDYY);
   }

   public static String getMMDDYYHHMM() {
      return getMMDDYYHHMM(new Date());
   }

   public static String getMMDDYYHHMM(Date date) {
      return get(date, MMDDYYHHMM);
   }

   public static String getDateNow() {
      return getDateNow(new Date());
   }

   public static String getDateNow(Date date) {
      return getDateNow(date, MMDDYY);
   }

   public static String getTimeStamp() {
      return getDateNow(new Date(), HHMMSSSS);
   }

   public static String getDateStr(Date date, String format) {
      if (date == null) {
         return "";
      }
      DateFormat dateFormat = dateFormats.get(format);
      if (dateFormat == null) {
         dateFormat = new SimpleDateFormat(format);
         dateFormats.put(format, dateFormat);
      }
      return dateFormat.format(date);
   }

   public static String getDateNow(String format) {
      return get(new Date(), format);
   }

   public static String getDateNow(Date date, String format) {
      return get(date, format);
   }

   public static String get(Date date) {
      if (date == null) {
         return "";
      }
      return DateFormat.getDateInstance().format(date);
   }

   public static String get(Date date, String pattern) {
      return get(date, new SimpleDateFormat(pattern));
   }

   public static String get(Date date, DateFormat dateFormat) {
      if (date == null) {
         return "";
      }
      String result = dateFormat.format(date);
      return result;
   }

   public static int getDifference(Date a, Date b) {
      int tempDifference = 0;
      int difference = 0;
      Calendar earlier = Calendar.getInstance();
      Calendar later = Calendar.getInstance();

      if (a.compareTo(b) < 0) {
         earlier.setTime(a);
         later.setTime(b);
      } else {
         earlier.setTime(b);
         later.setTime(a);
      }

      while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR)) {
         tempDifference = 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
         difference += tempDifference;

         earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
      }

      if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR)) {
         tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR);
         difference += tempDifference;

         earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
      }

      return difference;
   }

   /**
    * @param startDate The first date of the interpolation. MUST be before endDate.
    * @param endDate The last date of the interpolation. MUST be after startDate
    * @param interDate The date between startDate and endDate.
    * @return The interpolation ratio of interDate between startDate and endDate. Where if interDate <= startDate then
    * return 0.0 and if interDate >= endDate return 1.0.
    */
   public static double getInterpolationRatioBetweenDates(Date startDate, Date endDate, Date interDate) {
      double interRatio = 0.0;

      if (interDate.before(startDate) || interDate.compareTo(startDate) == 0) {
         return 0.0;
      }
      if (interDate.after(endDate) || interDate.compareTo(endDate) == 0) {
         return 1.0;
      }
      if (endDate.before(startDate)) {
         return 0.0;
      }

      long startMillis = startDate.getTime();
      long endMillis = endDate.getTime();
      long interMillis = interDate.getTime();

      long rangeMillis = endMillis - startMillis;
      if (rangeMillis == 0.0) {
         return 0.0;
      }
      long normalizedInterMillis = interMillis - startMillis;
      interRatio = (double) normalizedInterMillis / (double) rangeMillis;

      return interRatio;
   }

   /**
    * @return [date] + [manyWeeks]
    */
   public static Date addWeeks(Date date, int manyWeeks) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.add(Calendar.WEEK_OF_YEAR, manyWeeks);
      return cal.getTime();
   }

   /**
    * @param a One date. Sequential order with other date parameter does not matter.
    * @param b Another date. Sequential order with other date parameter does not matter.
    * @return the number of weeks difference between Date a and Date b.
    */
   public static int getManyWeeksDifference(Date a, Date b) {
      int weeks = 0;
      Calendar aCal = Calendar.getInstance();
      Calendar bCal = Calendar.getInstance();
      aCal.setTime(a);
      bCal.setTime(b);

      Calendar startCal, endCal;
      if (aCal.before(bCal)) {
         startCal = aCal;
         endCal = bCal;
      } else {
         startCal = bCal;
         endCal = aCal;
      }

      while (startCal.before(endCal)) {
         startCal.add(Calendar.WEEK_OF_YEAR, 1);
         weeks++;
      }

      return weeks;
   }

   /**
    * @return The date that is the Monday before the date. If the provided date is Monday then it will be returned
    * as-is.
    */
   public static Date getMondayBefore(Date date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
         cal.add(Calendar.DATE, -1);
      }
      return cal.getTime();
   }

   /**
    * @return The date that is the Monday after the date. If the provided date is Monday then it will be returned as-is.
    */
   public static Date getMondayAfter(Date date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
         cal.add(Calendar.DATE, 1);
      }
      return cal.getTime();
   }

   /**
    * @return The same date but with time equal to 00:00:00
    */
   public static Date convertToStartOfDay(Date date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      return cal.getTime();
   }

   /**
    * @return The same date but with time equal to 23:59:59
    */
   public static Date convertToEndOfDay(Date date) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.set(Calendar.HOUR_OF_DAY, 23);
      cal.set(Calendar.MINUTE, 59);
      cal.set(Calendar.SECOND, 59);
      return cal.getTime();
   }

   /**
    * @return Jan 1, 2001 1:1:1
    */
   public static Date getSentinalDate() {
      if (SENTINAL == null) {
         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.YEAR, 2001);
         cal.set(Calendar.DAY_OF_MONTH, 1);
         cal.set(Calendar.MONTH, 1);
         cal.set(Calendar.HOUR_OF_DAY, 1);
         cal.set(Calendar.MINUTE, 1);
         cal.set(Calendar.SECOND, 1);
         SENTINAL = cal.getTime();
      }
      return SENTINAL;
   }

}
