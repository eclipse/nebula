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
public class GanttPhase {

    private Calendar       _startDate;
    private Calendar       _endDate;
    private String         _title;
    private boolean        _locked;
    private Color          _headerBackgroundColor;
    private Color          _headerForegroundColor;
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

    private int            _DDayStart;
    private int            _DDayEnd;

    private GanttChart     _parentChart;
    private GanttComposite _parentComposite;

    private int            _daysBetweenStartAndEnd;

    private Calendar       _dragStartCalendar;
    private Calendar       _dragEndCalendar;
    private long           _dragStartLong;
    private long           _dragEndLong;

    GanttPhase() {
        _headerTextColor = ColorCache.getWhite();
        _headerForegroundColor = ColorCache.getColor(74, 123, 173);
        _headerBackgroundColor = ColorCache.getColor(42, 83, 125);
        _bodyTopColor = ColorCache.getColor(217, 238, 167);
        _bodyBottomColor = ColorCache.getColor(155, 178, 99);
        _alpha = 255;
        _borderWidth = 1;
        _borderColor = ColorCache.getColor(19, 50, 81);
    }

    public GanttPhase(GanttChart parent, String title) {
        this(parent, null, null, title);
    }

    public GanttPhase(GanttChart parent, Calendar start, Calendar end, String title) {
        this();
        setStartDate(start);
        setEndDate(end);
        _title = title;
        _parentChart = parent;
        _parentComposite = parent.getGanttComposite();

        _parentComposite.addPhase(this);
    }

    public Calendar getStartDate() {
        return _startDate;
    }

    public void setStartDate(Calendar startDate) {
        if (startDate == null) {
            _startDate = startDate;
            _start = -1;
            return;
        }
        _startDate = (Calendar) startDate.clone();
        _start = _startDate.getTimeInMillis();
    }

    public Calendar getEndDate() {
        return _endDate;
    }

    public void setEndDate(Calendar endDate) {
        if (endDate == null) {
            _endDate = endDate;
            _end = -1;
            return;
        }
        _endDate = (Calendar) endDate.clone();
        _end = _endDate.getTimeInMillis();
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public boolean isLocked() {
        return _locked;
    }

    public void setLocked(boolean locked) {
        _locked = locked;
    }

    public Color getHeaderBackgroundColor() {
        return _headerBackgroundColor;
    }

    public void setHeaderBackgroundColor(Color headerBackgroundColor) {
        _headerBackgroundColor = headerBackgroundColor;
    }

    public Color getHeaderForegroundColor() {
        return _headerForegroundColor;
    }

    public void setHeaderForegroundColor(Color headerForegroundColor) {
        _headerForegroundColor = headerForegroundColor;
    }

    public Color getBodyTopColor() {
        return _bodyTopColor;
    }

    public void setBodyTopColor(Color bodyTopColor) {
        _bodyTopColor = bodyTopColor;
    }

    public Color getBodyBottomColor() {
        return _bodyBottomColor;
    }

    public void setBodyBottomColor(Color bodyBottomColor) {
        _bodyBottomColor = bodyBottomColor;
    }

    public int getAlpha() {
        return _alpha;
    }

    public void setAlpha(int alpha) {
        _alpha = alpha;
    }

    public Object getData() {
        return _data;
    }

    public void setData(Object data) {
        _data = data;
    }

    public boolean isHidden() {
        return _hidden;
    }

    public void setHidden(boolean hidden) {
        _hidden = hidden;
    }

    public boolean isResizable() {
        return _resizable;
    }

    public void setResizable(boolean resizable) {
        _resizable = resizable;
    }

    public boolean isMoveable() {
        return _moveable;
    }

    public void setMoveable(boolean moveable) {
        _moveable = moveable;
    }

    public Font getHeaderFont() {
        return _headerFont;
    }

    public void setHeaderFont(Font headerFont) {
        _headerFont = headerFont;
    }

    public boolean isDrawBorders() {
        return _drawBorders;
    }

    public void setDrawBorders(boolean drawBorders) {
        _drawBorders = drawBorders;
    }

    public int getBorderWidth() {
        return _borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        _borderWidth = borderWidth;
    }

    public Color getBorderColor() {
        return _borderColor;
    }

    public void setBorderColor(Color borderColor) {
        _borderColor = borderColor;
    }

    public void setStart(long start) {
        _start = start;
    }

    public void setEnd(long end) {
        _end = end;
    }

    public Color getHeaderTextColor() {
        return _headerTextColor;
    }

    public void setHeaderTextColor(Color headerTextColor) {
        _headerTextColor = headerTextColor;
    }

    /**
     * Returns the D-day start value.
     * 
     * @return
     */
    public int getDDayStart() {
        return _DDayStart;
    }

    public int getDDayRevisedStart() {
        return (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), _startDate, _parentChart.getSettings().getDefaultLocale());
    }

    public int getDDayRevisedEnd() {
        return (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), _endDate, _parentChart.getSettings().getDefaultLocale());
    }

    /**
     * Sets the D-day start value.
     * 
     * @param day
     */
    public void setDDayStart(int day) {
        _DDayStart = day;

        _startDate = _parentComposite.getDDayCalendar();
        _startDate.add(Calendar.DATE, day);
        updateDaysBetweenStartAndEnd();
    }

    public int getDDayEnd() {
        return _DDayEnd;
    }

    public void setDDayEnd(int day) {
        _DDayEnd = day;

        _endDate = _parentComposite.getDDayCalendar();
        _endDate.add(Calendar.DATE, day);
        updateDaysBetweenStartAndEnd();
    }
    
    // --------------- PRIVATE METHODS ------------------

    void updateDaysBetweenStartAndEnd() {
        if (getStartDate() == null || getEndDate() == null) {
            _daysBetweenStartAndEnd = -1;
            return;
        }

        _daysBetweenStartAndEnd = (int) DateHelper.daysBetween(getStartDate(), getEndDate(), _parentChart.getSettings().getDefaultLocale());

        if (_parentComposite.getCurrentView() == ISettings.VIEW_D_DAY) {
            _DDayStart = (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), getStartDate(), _parentChart.getSettings().getDefaultLocale());
            _DDayEnd = (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), getEndDate(), _parentChart.getSettings().getDefaultLocale());
            _DDayStart--;
        }

    }

    boolean isAllowZeroWidth() {
        return _allowZeroWidth;
    }

    void setAllowZeroWidth(boolean allowZeroWidth) {
        _allowZeroWidth = allowZeroWidth;
    }

    Rectangle getHeaderBounds() {
        return _headerBounds;
    }

    void setHeaderBounds(Rectangle headerBounds) {
        _headerBounds = headerBounds;
    }

    Rectangle getBounds() {
        return _bounds;
    }

    void setBounds(Rectangle bounds) {
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

    boolean overlaps(GanttPhase other) {
        return ((other.getStart() > _start && other.getEnd() < _end) || (other.getStart() < _start && other.getEnd() > _start));
    }
    
    boolean willOverlapResize(GanttPhase other, int calType, int val, boolean start) {
        Calendar temp = (Calendar) _startDate.clone();
        if (start) {
            temp.add(calType, val);
        }
        Calendar tempEnd = (Calendar) _endDate.clone();
        if (!start) {
            temp.add(calType, val);
        }
        
        long s = temp.getTimeInMillis();
        long e = tempEnd.getTimeInMillis();
        
        if (start) {
            return (s < other.getEnd());
        }
        else {
            return (e >= other.getStart());
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
        Calendar temp = (Calendar) _startDate.clone();
        temp.add(calType, val);
        Calendar tempEnd = (Calendar) _endDate.clone();
        if (!isAllowZeroWidth()) {
            tempEnd.add(calType, -1);
        }
        if (temp.after(tempEnd)) { return; }

        setStartDate(temp);

        updateDaysBetweenStartAndEnd();
    }

    void moveEnd(int calType, int val) {
        Calendar temp = (Calendar) _endDate.clone();
        temp.add(calType, val);
        Calendar tempStart = (Calendar) _startDate.clone();
        if (!isAllowZeroWidth()) {
            tempStart.add(calType, 1);
        }
        if (temp.before(tempStart)) { return; }

        setEndDate(temp);

        updateDaysBetweenStartAndEnd();
    }

    int getDaysBetweenStartAndEnd() {
        return _daysBetweenStartAndEnd;
    }

    void markDragStart() {
        _dragStartLong = _start;
        _dragEndLong = _end;
        _dragStartCalendar = (Calendar) _startDate.clone();
        _dragEndCalendar = (Calendar) _endDate.clone();
    }

    void undoLastDragDrop() {
        _start = _dragStartLong;
        _end = _dragEndLong;
        setStartDate(_dragStartCalendar);
        setEndDate(_dragEndCalendar);
        updateDaysBetweenStartAndEnd();
    }

}
