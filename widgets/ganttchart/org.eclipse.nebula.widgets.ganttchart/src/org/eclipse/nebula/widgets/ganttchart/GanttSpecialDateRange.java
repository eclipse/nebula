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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.nebula.widgets.ganttchart.utils.DateRange;
import org.eclipse.swt.graphics.Color;

/**
 * This class allows you to color a certain date range in a special background color, as well as set things such as if
 * events should be allowed to be moved onto this range or not. You can also set a repeating range by setting certain
 * days (Monday, Tuesday, etc) that will be repeatedly blocked. <p /> For example, to block all events from ending up on
 * weekends, you would do:
 * 
 * <pre>
 * GanttSpecialDateRange weekends = new GanttSpecialDateRange(parentChart);
 * weekends.addRecurDay(Calendar.SATURDAY);
 * weekends.addRecurDay(Calendar.SUNDAY);
 * weekends.setAllowEventsOnDates(false);
 * </pre>
 * 
 * To block a Tuesday before 8.30am and after 5.30pm, 10 times, starting Jan 1, 2009, you would do:
 * 
 * <pre>
 * Calendar cal = Calendar.getInstance(Locale.getDefault());
 * cal.set(Calendar.YEAR, 2009);
 * cal.set(Calendar.MONTH, Calendar.JANUARY);
 * cal.set(Calendar.DATE, 1);
 * 
 * GanttSpecialDateRange blockPre = new GanttSpecialDateRange(parentChart);
 * blockPre.setStart(cal);
 * blockPre.addRecurDay(Calendar.SATURDAY);
 * blockPre.setEndHour(8);
 * blockPre.setEndMinute(29);
 * blockPre.setEndAfter(10);
 * 
 * GanttSpecialDateRange blockPost = new GanttSpecialDateRange(parentChart);
 * blockPre.setStart(cal);
 * blockPost.addRecurDay(Calendar.SATURDAY);
 * blockPost.setStartHour(17);
 * blockPost.setStartMinute(30);
 * blockPost.setEndAfter(10);
 * 
 * blockPre.setAllowEventsOnDates(false);
 * blockPost.setAllowEventsOnDates(false);
 * </pre>
 * 
 * For a D-Day calendar (which does not use actual dates (at least visibily)) a typical creation may look like this:
 * 
 * <pre>
 * Calendar start = (Calendar) _ddayRootCalendar.clone();
 * Calendar end = (Calendar) start.clone();
 * end.add(Calendar.DATE, 50);
 * GanttSpecialDateRange range = new GanttSpecialDateRange(_ganttChart, start, end);
 * // these need to be set to indicate that the range should adapt to D-Day logic 
 * range.setFrequency(GanttSpecialDateRange.REPEAT_DDAY);
 * range.setDDayRepeatInterval(10);
 * // --  
 * range.setRecurCount(50);
 * range.setBackgroundColorTop(ColorCache.getRandomColor());
 * range.setBackgroundColorBottom(ColorCache.getRandomColor());
 * </pre>
 * 
 * @author cre
 */
public class GanttSpecialDateRange {

    public static final int REPEAT_DAILY        = 1;
    public static final int REPEAT_WEEKLY       = 2;
    public static final int REPEAT_MONTHLY      = 3;
    public static final int REPEAT_YEARLY       = 4;
    public static final int REPEAT_DDAY         = 5;

    public static final int NO_END              = -1;

    private Calendar        _start;
    private Calendar        _end;
    private Color           _bgColorTop         = ColorCache.getWhite();
    private Color           _bgColorBottom      = ColorCache.getBlack();
    private GanttChart      _parentChart;
    private GanttComposite  _parentComposite;
    private boolean         _allowEventsOnDates = true;

    private int             _frequency          = REPEAT_WEEKLY;
    private int             _recurCount         = 1;
    private List            _recurDays;
    private int             _startHour          = 0;
    private int             _startMinute        = 0;
    private final int       _startSecond        = 0;
    private int             _endHour            = 23;
    private int             _endMinute          = 59;
    private final int       _endSecond          = 59;
    private int             _endAfter           = NO_END;

    private Calendar        _lastActualEndDate;

    private List            _cachedRanges       = null;

    private int             _ddayRepeatInterval = 0;

    GanttSpecialDateRange() {
        this(null, null, null);
    }

    /**
     * Creates a new Gantt Special Date Range that indicates a certain set of dates with colors.
     * 
     * @param parent Parent chart
     */
    public GanttSpecialDateRange(final GanttChart parent) {
        this(parent, null, null);
    }

    /**
     * Creates a new Gantt Special Date Range that indicates a certain set of dates.
     * 
     * @param parent Parent chart
     * @param start Start date
     * @param end End date
     */
    public GanttSpecialDateRange(final GanttChart parent, final Calendar start, final Calendar end) {
        _recurDays = new ArrayList();
        _parentChart = parent;
        if (parent != null) {
            _parentComposite = parent.getGanttComposite();
        }
        
        _start = (start == null ? null : DateHelper.getNewCalendar(start));
        _end = (end == null ? null : DateHelper.getNewCalendar(end));
        
        if (parent != null) {
            _parentComposite.addSpecialDateRange(this);
        }
        updateCalculations();
    }

    /**
     * Returns the start date.
     * 
     * @return Start date
     */
    public Calendar getStart() {
        return _start;
    }

    /**
     * Sets the start date.
     * 
     * @param start Start date
     */
    public void setStart(final Calendar start) {
        _start = (start == null ? null : DateHelper.getNewCalendar(start));

        updateCalculations();
    }

    /**
     * Returns the end date.
     * 
     * @return End date
     */
    public Calendar getEnd() {
        return _end;
    }

    /**
     * Sets the end date.
     * 
     * @param end End date
     */
    public void setEnd(final Calendar end) {        
        _end = (end == null ? null : DateHelper.getNewCalendar(end));

        updateCalculations();
    }

    /**
     * Returns the gradient top color.
     * 
     * @return Top color
     */
    public Color getBackgroundColorTop() {
        return _bgColorTop;
    }

    /**
     * Sets the gradient top color.
     * 
     * @param backgroundColorTop Top color or null if none (transparent)
     */
    public void setBackgroundColorTop(final Color backgroundColorTop) {
        _bgColorTop = backgroundColorTop;
    }

    /**
     * Returns the gradient bottom color.
     * 
     * @return Bottom color
     */
    public Color getBackgroundColorBottom() {
        return _bgColorBottom;
    }

    /**
     * Sets the gradient bottom color.
     * 
     * @param backgroundColorBottom Bottom color or null if none (transparent)
     */
    public void setBackgroundColorBottom(final Color backgroundColorBottom) {
        _bgColorBottom = backgroundColorBottom;
    }

    /**
     * Returns the chart that this range is associated with.
     * 
     * @return {@link GanttChart} parent
     */
    public GanttChart getParentChart() {
        return _parentChart;
    }

    /**
     * Returns the chart composite this range is associated with.
     * 
     * @return {@link GanttComposite} parent
     */
    public GanttComposite getParentComposite() {
        return _parentComposite;
    }

    /**
     * Whether events can be resized or dropped on the date range specified in this class. Default is true.
     * 
     * @return true if allowed
     */
    public boolean isAllowEventsOnDates() {
        return _allowEventsOnDates;
    }

    /**
     * Sets whether events can be resized or dropped on to the date range specified in this class. Default is true.
     * 
     * @param allowEventsOnDates true if allowed
     */
    public void setAllowEventsOnDates(final boolean allowEventsOnDates) {
        _allowEventsOnDates = allowEventsOnDates;
    }

    /**
     * Adds a date that will be always used as a range date. The date is one of the Calendar dates, such as
     * {@link Calendar#MONDAY}. This is purely for convenience instead of having to create multiple special date ranges
     * to cover things such as weekends. Do note if you add specific hours, only the specified hour on the set days will
     * be covered and not the full day itself. <p /> If the frequency is set to {@value #REPEAT_DDAY} this method does
     * nothing and you should instead be using {@link #setDDayRepeatInterval(int)} as DDay calendars has no notion of
     * weekdates.
     * 
     * @param day Calendar weekday to add
     * @return true if added, false if not
     */
    public boolean addRecurDay(final int day) {
        if (day < Calendar.SUNDAY || day > Calendar.SATURDAY) { return false; }

        if (_recurDays.contains(new Integer(day))) { return false; }

        final boolean ret = _recurDays.add(new Integer(day));
        if (ret) {
            updateCalculations();
        }
        return ret;
    }

    /**
     * Removes a set date.
     * 
     * @param calDate Date to remove
     * @return true if removed
     */
    public boolean removeRecurDay(final int calDate) {
        final boolean ret = _recurDays.remove(new Integer(calDate));
        if (ret) {
            updateCalculations();
        }
        return ret;
    }

    /**
     * Returns the frequency.
     * 
     * @return frequency
     */
    public int getFrequency() {
        return _frequency;
    }

    /**
     * Sets the repeat frequency. Options are {@link #REPEAT_DAILY}, {@link #REPEAT_MONTHLY}, {@link #REPEAT_WEEKLY},
     * {@link #REPEAT_YEARLY} or {@link #REPEAT_DDAY} for DDay calendars.
     * 
     * @param frequency Frequency to set
     */
    public void setFrequency(final int frequency) {
        _frequency = frequency;
        updateCalculations();
    }

    /**
     * Returns the currently set DDay repeat interval. This is only used if frequency is set to {@link #REPEAT_DDAY}.
     * 
     * @return repeat interval
     */
    public int getDDayRepeatInterval() {
        return _ddayRepeatInterval;
    }

    /**
     * Sets the custom DDay repeat interval. This is only used if frequency is set to {@link #REPEAT_DDAY}.
     * 
     * @param interval Custom repeat interval of n DDays
     */
    public void setDDayRepeatInterval(final int interval) {
        _ddayRepeatInterval = interval;
    }

    /**
     * Returns the "recurs every" value.
     * 
     * @return recurs every value
     */
    public int getRecurCount() {
        return _recurCount;
    }

    /**
     * How often this event re-occurs. By default it's always 1. To end after a certain number of recurrences, use
     * {@link #setEndAfter(int)}.
     * 
     * @param recurMax Recurrence frequency
     */
    public void setRecurCount(final int recurMax) {
        _recurCount = recurMax;
        updateCalculations();
    }

    /**
     * Returns the list of currently set recurring days.
     * 
     * @return List of recurring days
     */
    public List getRecurDays() {
        return _recurDays;
    }

    /**
     * Returns the start hour.
     * 
     * @return Start hour
     */
    public int getStartHour() {
        return _startHour;
    }

    /**
     * Sets the start hour. Hour should be in a 24h format from 0 to 23.
     * 
     * @param startHour start hour
     * @return true if set
     */
    public boolean setStartHour(final int startHour) {
        if (startHour < 0 || startHour > 23) { return false; }

        _startHour = startHour;
        updateCalculations();
        return true;
    }

    /**
     * Returns the start minute.
     * 
     * @return start minute
     */
    public int getStartMinute() {
        return _startMinute;
    }

    /**
     * Sets the start minute. Minute should be between 0 and 59.
     * 
     * @param startMinute start minute
     * @return true if set
     */
    public boolean setStartMinute(final int startMinute) {
        if (startMinute < 0 || startMinute > 59) { return false;

        }

        _startMinute = startMinute;
        updateCalculations();
        return true;
    }

    /**
     * Returns the end hour.
     * 
     * @return end hour
     */
    public int getEndHour() {
        return _endHour;
    }

    /**
     * Sets the end hour. Hour should be in a 24h format from 0 to 23.
     * 
     * @param endHour end hour
     * @return true if set
     */
    public boolean setEndHour(final int endHour) {
        if (endHour < 0 || endHour > 23) { return false; }

        _endHour = endHour;
        updateCalculations();
        return true;
    }

    /**
     * Returns the end minute
     * 
     * @return end minute
     */
    public int getEndMinute() {
        return _endMinute;
    }

    /**
     * Sets the end minute. Minute should be between 0 and 59.
     * 
     * @param endMinute start minute
     * @return true if set
     */
    public boolean setEndMinute(final int endMinute) {
        if (endMinute < 0 || endMinute > 59) { return false; }

        _endMinute = endMinute;
        updateCalculations();
        return true;
    }

    /**
     * Returns the end after value that defines the number of recurring repetitions of the event.
     * 
     * @return end after value
     */
    public int getEndAfter() {
        return _endAfter;
    }

    /**
     * Sets how many times an event should re-occur and then end. This is the end value. To set a no-end, use
     * {@link #NO_END} as value.
     * 
     * @param endAfter After how many re-occurances to stop.
     */
    public void setEndAfter(final int endAfter) {
        _endAfter = endAfter;
        updateCalculations();
    }

    public void setParentChart(final GanttChart parentChart) {
        _parentChart = parentChart;
    }

    public void setParentComposite(final GanttComposite parentComposite) {
        _parentComposite = parentComposite;
    }

    private void updateCalculations() {
        _lastActualEndDate = null;
        _cachedRanges = null;
    }
    
    /**
     * Checks whether a set of dates overlap any of the dates in this range.
     * 
     * @param start Start date
     * @param end End date
     * @return true if no date is overlapping the dates of this range, false otherwise
     */
    public boolean canEventOccupy(final Calendar start, final Calendar end) {
    	if (isAllowEventsOnDates()) {
    		return true;
    	}
    	
    	// we're not in range, check this first as it's faster
    	if (!isVisible(start, end)) {
    		return true;
    	}
    	
    	// get all blocks that we occupy
    	List blocks = getBlocks(start, end);
    	
    	DateRange us = new DateRange(_start, _end);
    	
    	for (int i = 0; i < blocks.size(); i++) {
    		ArrayList block = (ArrayList) blocks.get(i);
    		
    		Calendar blockStart = (Calendar) block.get(0);
    		Calendar blockEnd = (Calendar) block.get(1);
    		
    		DateRange range = new DateRange(blockStart, blockEnd);    		
    		if (us.Overlaps(range)) {
    			return false;
    		}
    	}
    	
    	return true;
    }

    /*
     * Checks whether this range is visible in the given start/end date range
     */
    boolean isVisible(final Calendar start, final Calendar end) {
        if (!isUseable()) { return false; }

        // TODO: DDay calendar is fucked here

        //System.err.println(start.getTime() + " # " + end.getTime() + " --- " + _start.getTime() + " # " + _end.getTime() + " -- " + getActualEndDate().getTime() + " + " + _recurCount + " + " + _lastActualEndDate.getTime());

        // doesn't recur on any days at all
        // TODO: This should be possible to be empty, then we just use the start / end dates
        if (_recurDays.isEmpty() && _frequency != REPEAT_DDAY) { return false; }

        // doesn't recur, what's the point??
        if (_recurCount <= 0) { return false; }

        // ends on specific date which is in the past
        if (_end != null && _end.before(start)) { return false; }

        // same deal
        final Calendar aEnd = getActualEndDate();
        //System.err.println(aEnd.getTime() + " "+ end.getTime());
        if (aEnd != null && aEnd.before(start)) { return false; }

        // now it's easy
        if (_start.before(end) && aEnd.after(start)) { return true; }

        return false;
    }

    Calendar getActualStartDate() {
        return _start;
    }

    Calendar getActualEndDate() {
        if (_lastActualEndDate != null) { return _lastActualEndDate; }
        if (_end != null) {
            _lastActualEndDate = DateHelper.getNewCalendar(_end);
            return _end;
        }

        // move calendar to end recurring date
        final Calendar cal = DateHelper.getNewCalendar(_start);
        for (int i = 0; i < _recurCount; i++) {
            switch (_frequency) {
                case REPEAT_DAILY:
                    cal.add(Calendar.DATE, 1);
                    break;
                case REPEAT_WEEKLY:
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                    break;
                case REPEAT_MONTHLY:
                    cal.add(Calendar.MONTH, 1);
                    break;
                case REPEAT_YEARLY:
                    cal.add(Calendar.YEAR, 1);
                    break;
                case REPEAT_DDAY:
                    cal.add(Calendar.DATE, _ddayRepeatInterval);
                    break;
                default:
                    break;
            }
        }

        // set the end day to the highest day of that week
        final int d = getHighestRecurDate();
        cal.set(Calendar.DAY_OF_WEEK, d);

        cal.set(Calendar.HOUR_OF_DAY, _endHour);
        cal.set(Calendar.MINUTE, _endMinute);
        cal.set(Calendar.SECOND, _endSecond);
        cal.set(Calendar.MILLISECOND, 999);

        _lastActualEndDate = DateHelper.getNewCalendar(cal);

        return cal;
    }

    List getBlocks() {
        return getBlocks(null, null);
    }

    List getBlocks(final Calendar start, final Calendar end) {
        // if (_cachedRanges != null) { return _cachedRanges; }

        _cachedRanges = new ArrayList();

        final Calendar cal = DateHelper.getNewCalendar(_start);

        final Calendar ourEnd = getActualEndDate();

        for (int i = 0; i < _recurCount; i++) {
            final Calendar calEnd = DateHelper.getNewCalendar(cal);

            if (_recurDays.isEmpty() && _frequency == REPEAT_DDAY) {
                cal.set(Calendar.HOUR_OF_DAY, _startHour);
                cal.set(Calendar.MINUTE, _startMinute);
                cal.set(Calendar.SECOND, _startSecond);
                cal.set(Calendar.MILLISECOND, 0);

                calEnd.set(Calendar.HOUR_OF_DAY, _endHour);
                calEnd.set(Calendar.MINUTE, _endMinute);
                calEnd.set(Calendar.SECOND, _endSecond);
                calEnd.set(Calendar.MILLISECOND, 999);

                if (calEnd.after(end)) {
                    continue;
                }

                if (ourEnd != null && calEnd.after(ourEnd)) {
                    continue;
                }

                final List foo = new ArrayList();
                foo.add(DateHelper.getNewCalendar(cal));
                foo.add(DateHelper.getNewCalendar(calEnd));
                _cachedRanges.add(foo);

            } else {
                for (int x = 0; x < _recurDays.size(); x++) {
                    final int day = ((Integer) _recurDays.get(x)).intValue();

                    cal.set(Calendar.HOUR_OF_DAY, _startHour);
                    cal.set(Calendar.MINUTE, _startMinute);
                    cal.set(Calendar.SECOND, _startSecond);
                    cal.set(Calendar.MILLISECOND, 0);
                    cal.set(Calendar.DAY_OF_WEEK, day);

                    calEnd.set(Calendar.HOUR_OF_DAY, _endHour);
                    calEnd.set(Calendar.MINUTE, _endMinute);
                    calEnd.set(Calendar.SECOND, _endSecond);
                    calEnd.set(Calendar.MILLISECOND, 999);
                    calEnd.set(Calendar.DAY_OF_WEEK, day);

                    if (start != null && calEnd.before(start)) {
                        continue;
                    }
                    if (end != null && cal.after(end) || cal.after(ourEnd)) {
                        continue;
                    }

                    final List foo = new ArrayList();
                    foo.add(DateHelper.getNewCalendar(cal));
                    foo.add(DateHelper.getNewCalendar(calEnd));
                    _cachedRanges.add(foo);
                }
            }

            switch (_frequency) {
                case REPEAT_DAILY:
                    cal.add(Calendar.DATE, 1);
                    break;
                case REPEAT_WEEKLY:
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                    break;
                case REPEAT_MONTHLY:
                    cal.add(Calendar.MONTH, 1);
                    break;
                case REPEAT_YEARLY:
                    cal.add(Calendar.YEAR, 1);
                    break;
                case REPEAT_DDAY:
                    cal.add(Calendar.DATE, _ddayRepeatInterval);
                    break;
                default:
                    break;
            }
        }

        return _cachedRanges;
    }

    int getHighestRecurDate() {
        int max = 0;
        for (int i = 0; i < _recurDays.size(); i++) {
            final Integer day = (Integer) _recurDays.get(i); // NOPMD
            max = Math.max(max, day.intValue());
        }
        return max;
    }

    boolean isUseable() {
        if (_start == null) { return false; }

        return true;
    }
    
    public String toString() {
    	String freq = "";
    	switch (_frequency) {
	        case REPEAT_DAILY:
	            freq = "Daily";
	            break;
	        case REPEAT_WEEKLY:
	            freq = "Weekly";
	            break;
	        case REPEAT_MONTHLY:
	            freq = "Monthly";
	            break;
	        case REPEAT_YEARLY:
	            freq = "Yearly";
	            break;
	        case REPEAT_DDAY:
	            freq = "DDay";
	            break;
	        default:
	            break;
	    }
    	
    	return "[GanttSpecialDateRange: "+ (_start == null ? null : _start.getTime()) + " - " + (_end == null ? null : _end.getTime()) + ". Freqency: " + freq + ". Recur Count: " + _recurCount + ". Last actual end date: " + (_lastActualEndDate == null ? null : _lastActualEndDate.getTime()) + "]";
    }

}
