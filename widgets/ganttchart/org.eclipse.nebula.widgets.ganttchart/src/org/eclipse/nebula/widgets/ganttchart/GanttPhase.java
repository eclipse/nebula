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

import java.util.Calendar;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;

/**
 * This class represents one GanttPhase in the chart. A phase is basically a background color and a title that spans
 * over two dates. It's merely a visual aid to help users locate certain "important date ranges" in the chart.
 * <p />
 * Compared to a recurring date range a Phase can be moved / resized by the user and also has a title. 
 * 
 * @author cre
 */
public final class GanttPhase {

    private Calendar       _startDate;
    private Calendar       _endDate;
    private String         _title;
    private boolean        _locked;
    private Color          _headerBgColor;
    private Color          _headerFgColor;
    private Color          _headerTextColor;
    private Color          _bodyTopColor;
    private Color          _bodyBottomColor;
    private int            _alpha;
    private Object         _data;
    private boolean        _hidden;
    private boolean        _resizable   = true;
    private boolean        _moveable    = true;
    private boolean        _drawBorders = true;
    private int            _borderWidth;
    private Color          _borderColor;
    private long           _start;
    private long           _end;
    private Font           _headerFont;
    private boolean        _allowZeroWidth;

    private Rectangle      _headerBounds;
    private Rectangle      _bounds;

    private int            _dDayStart;
    private int            _dDayEnd;

    private GanttChart     _parentChart;
    private GanttComposite _parentComposite;

    private int            _daysBtwStartEnd;

    private Calendar       _dragStartCal;
    private Calendar       _dragEndCal;
    private long           _dragStartLong;
    private long           _dragEndLong;

    GanttPhase() {
        _headerTextColor = ColorCache.getWhite();
        _headerFgColor = ColorCache.getColor(74, 123, 173);
        _headerBgColor = ColorCache.getColor(42, 83, 125);
        _bodyTopColor = ColorCache.getColor(217, 238, 167);
        _bodyBottomColor = ColorCache.getColor(155, 178, 99);
        _alpha = 255;
        _borderWidth = 1;
        _borderColor = ColorCache.getColor(19, 50, 81);
    }

    public GanttPhase(final GanttChart parent, final String title) {
        this(parent, null, null, title);
    }

    public GanttPhase(final GanttChart parent, final Calendar start, final Calendar end, final String title) {
        this();
        _parentChart = parent;
        _title = title;
        _parentComposite = parent.getGanttComposite();

        setStartDate(start);
        setEndDate(end);

        _parentComposite.addPhase(this);
        
        updateDaysBetweenStartAndEnd();
    }

    public Calendar getStartDate() {
        return _startDate;
    }

    public void setStartDate(final Calendar startDate) {
        if (startDate == null) {
            _startDate = null;
            _start = -1;
            return;
        }
        _startDate = DateHelper.getNewCalendar(startDate);
        _start = _startDate.getTimeInMillis();
        
        updateDaysBetweenStartAndEnd();
    }

    public Calendar getEndDate() {
        return _endDate;
    }

    public void setEndDate(final Calendar endDate) {
        if (endDate == null) {
            _endDate = null;
            _end = -1;
            return;
        }
        _endDate = DateHelper.getNewCalendar(endDate);
        _end = _endDate.getTimeInMillis();
        
        updateDaysBetweenStartAndEnd();
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(final String title) {
        _title = title;
    }

    public boolean isLocked() {
        return _locked;
    }

    public void setLocked(final boolean locked) {
        _locked = locked;
    }

    public Color getHeaderBackgroundColor() {
        return _headerBgColor;
    }

    public void setHeaderBackgroundColor(final Color color) {
        _headerBgColor = color;
    }

    public Color getHeaderForegroundColor() {
        return _headerFgColor;
    }

    public void setHeaderForegroundColor(final Color color) {
        _headerFgColor = color;
    }

    public Color getBodyTopColor() {
        return _bodyTopColor;
    }

    public void setBodyTopColor(final Color bodyTopColor) {
        _bodyTopColor = bodyTopColor;
    }

    public Color getBodyBottomColor() {
        return _bodyBottomColor;
    }

    public void setBodyBottomColor(final Color bodyBottomColor) {
        _bodyBottomColor = bodyBottomColor;
    }

    public int getAlpha() {
        return _alpha;
    }

    public void setAlpha(final int alpha) {
        _alpha = alpha;
    }

    public Object getData() {
        return _data;
    }

    public void setData(final Object data) {
        _data = data;
    }

    public boolean isHidden() {
        return _hidden;
    }

    public void setHidden(final boolean hidden) {
        _hidden = hidden;
    }

    public boolean isResizable() {
        return _resizable;
    }

    public void setResizable(final boolean resizable) {
        _resizable = resizable;
    }

    public boolean isMoveable() {
        return _moveable;
    }

    public void setMoveable(final boolean moveable) {
        _moveable = moveable;
    }

    public Font getHeaderFont() {
        return _headerFont;
    }

    public void setHeaderFont(final Font headerFont) {
        _headerFont = headerFont;
    }

    public boolean isDrawBorders() {
        return _drawBorders;
    }

    public void setDrawBorders(final boolean drawBorders) {
        _drawBorders = drawBorders;
    }

    public int getBorderWidth() {
        return _borderWidth;
    }

    public void setBorderWidth(final int borderWidth) {
        _borderWidth = borderWidth;
    }

    public Color getBorderColor() {
        return _borderColor;
    }

    public void setBorderColor(final Color borderColor) {
        _borderColor = borderColor;
    }

    public void setStart(final long start) {
        _start = start;
    }

    public void setEnd(final long end) {
        _end = end;
    }

    public Color getHeaderTextColor() {
        return _headerTextColor;
    }

    public void setHeaderTextColor(final Color headerTextColor) {
        _headerTextColor = headerTextColor;
    }

    /**
     * Returns the D-day start value.
     * 
     * @return
     */
    public int getDDayStart() {
        return _dDayStart;
    }

    public int getDDayRevisedStart() {
        return (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), _startDate);
    }

    public int getDDayRevisedEnd() {
        return (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), _endDate);
    }

    /**
     * Sets the D-day start value.
     * 
     * @param day
     */
    public void setDDayStart(final int day) {
        _dDayStart = day;

        _startDate = _parentComposite.getDDayCalendar();
        _startDate.add(Calendar.DATE, day);
        updateDaysBetweenStartAndEnd();
    }

    public int getDDayEnd() {
        return _dDayEnd;
    }

    public void setDDayEnd(final int day) {
        _dDayEnd = day;

        _endDate = _parentComposite.getDDayCalendar();
        _endDate.add(Calendar.DATE, day);
        updateDaysBetweenStartAndEnd();
    }
    
    // --------------- PRIVATE METHODS ------------------

    private void updateDaysBetweenStartAndEnd() {
        final Calendar start = getStartDate();
        final Calendar end = getEndDate();
        
        if (start == null || end == null) {
            _daysBtwStartEnd = -1;
            return;
        }
                
        _daysBtwStartEnd = (int) DateHelper.daysBetween(start, end);

        if (_parentComposite.getCurrentView() == ISettings.VIEW_D_DAY) {
            _dDayStart = (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), start);
            _dDayEnd = (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), end);
            _dDayStart--;
        }
        
    }

    boolean isAllowZeroWidth() {
        return _allowZeroWidth;
    }

    void setAllowZeroWidth(final boolean allowZeroWidth) {
        _allowZeroWidth = allowZeroWidth;
    }

    Rectangle getHeaderBounds() {
        return _headerBounds;
    }

    void setHeaderBounds(final Rectangle headerBounds) {
        _headerBounds = headerBounds;
    }

    Rectangle getBounds() {
        return _bounds;
    }

    void setBounds(final Rectangle bounds) {
        _bounds = bounds;
    }

    long getStart() {
        return _start;
    }

    long getEnd() {
        return _end;
    }

    boolean isDisplayable() {
        return (_startDate != null && _endDate != null);
    }

    boolean overlaps(final GanttPhase other) {
        return ((other.getStart() > _start && other.getEnd() < _end) || (other.getStart() < _start && other.getEnd() > _start));
    }
    
    boolean willOverlapResize(final GanttPhase other, final int calType, final int val, final boolean start) {
        final Calendar temp = DateHelper.getNewCalendar(_startDate);
        if (start) {
            temp.add(calType, val);
        }
        final Calendar tempEnd = DateHelper.getNewCalendar(_endDate);
        if (!start) {
            temp.add(calType, val);
        }
        
        final long startMillis = temp.getTimeInMillis();
        final long endMillis = tempEnd.getTimeInMillis();
        
        //System.err.println(tempEnd.getTime() + " >= " + other.getStartDate().getTime());
        
        if (start) {
            return (startMillis < other.getEnd());
        }
        else {
            return (endMillis >= other.getStart() && other.getStart() >= _startDate.getTimeInMillis());
        }
    }

    void move(int calType, int val) {
        _startDate.add(calType, val);
        _endDate.add(calType, val);
        _start = _startDate.getTimeInMillis();
        _end = _endDate.getTimeInMillis();
        updateDaysBetweenStartAndEnd();
    }

    void moveStart(int calType, int val) {
        final Calendar temp = DateHelper.getNewCalendar(_startDate);
        temp.add(calType, val);
        final Calendar tempEnd = DateHelper.getNewCalendar(_endDate);
        if (!isAllowZeroWidth()) {
            tempEnd.add(calType, -1);
        }
        if (temp.after(tempEnd)) { return; }

        setStartDate(temp);

        updateDaysBetweenStartAndEnd();
    }

    void moveEnd(int calType, int val) {
        final Calendar temp = DateHelper.getNewCalendar(_endDate);
        temp.add(calType, val);
        final Calendar tempStart = DateHelper.getNewCalendar(_startDate);
        if (!isAllowZeroWidth()) {
            tempStart.add(calType, 1);
        }
        if (temp.before(tempStart)) { return; }

        setEndDate(temp);

        updateDaysBetweenStartAndEnd();
    }

    int getDaysBetweenStartAndEnd() {
        return _daysBtwStartEnd;
    }

    void markDragStart() {
        _dragStartLong = _start;
        _dragEndLong = _end;
        _dragStartCal = DateHelper.getNewCalendar(_startDate);
        _dragEndCal = DateHelper.getNewCalendar(_endDate);
    }

    void undoLastDragDrop() {
        _start = _dragStartLong;
        _end = _dragEndLong;
        setStartDate(_dragStartCal);
        setEndDate(_dragEndCal);
        updateDaysBetweenStartAndEnd();
    }

}
