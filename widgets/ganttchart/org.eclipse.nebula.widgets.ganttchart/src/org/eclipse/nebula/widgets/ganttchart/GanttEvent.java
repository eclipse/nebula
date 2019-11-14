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

import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.EventMoveCommand;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.EventResizeCommand;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.IUndoRedoCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;

/**
 * One GanttEvent represents one "active" object in the GANTT chart.
 * <p>
 * This object can take many shapes, here is a list of a few:<br>
 * <ul>
 * <li>Normal event
 * <li>Checkpoint event
 * <li>Scope event
 * <li>Image event
 * <li>And so on...
 * </ul>
 * The event may also take revised start and end dates, and can be modified individually to be locked, non-movable,
 * non-resizable and much more.
 * <p>
 * Events <b>may be</b> modified on the fly to become a different object type from the above list. Please do ensure that
 * the ALL parameters are set for it to become the new object before you do so.
 * <p>
 * Once an event has been created, add it onto the GanttChart widget via the addScopeEvent(...) methods available.
 * <p>
 * <b>Sample Code:</b><br>
 * <br>
 * <code>
 * // make a 10 day long event<br>
 * Calendar cStartDate = Calendar.getInstance(Locale.getDefault());<br>
 * Calendar cEndDate = Calendar.getInstance(Locale.getDefault());<br>
 * cEndDate.add(Calendar.DATE, 10);
 * <br><br>
 * // we're setting the percentage complete to 50%<br>
 * GanttEvent ganttEvent = new GanttEvent(ganttChart, "Event Name", cStartDate, cEndDate, 50);
 * <br><br>
 * </code> <br>
 * <br>
 * This class should not be subclassed, do so at your own risk.
 * 
 * @author Emil Crumhorn <a href="mailto:emil.crumhorn@gmail.com">emil.crumhorn@gmail.com</a>
 */
public class GanttEvent extends AbstractGanttEvent implements IGanttChartItem, Cloneable {

    /*
     * public static final int TYPE_EVENT = 0; public static final int TYPE_CHECKPOINT = 1; public static final int TYPE_IMAGE = 2; public static final int TYPE_SCOPE = 3;
     */
    public static final int FIXED_ROW_HEIGHT_AUTOMATIC = -1;

    private Object          _data;
    private String          _name;
    private Calendar        _revisedStart;
    private Calendar        _revisedEnd;
    private Calendar        _startDate;
    private Calendar        _endDate;
    private int             _percentComplete;
    private boolean         _checkpoint;
    private boolean         _scope;
    private boolean         _locked;
    private boolean         _image;
    private boolean         _resizable                 = true;
    private boolean         _moveable                  = true;
    private int             _x, _y, _width, _height;
    private int             _earliestStartX, _latestEndX, _actualWidth;
    private Color           _statusColor;
    private Color           _gradientStatusColor;
    private boolean         _showBoldText;
    private String          _textDisplayFormat;
    private List            _scopeEvents;
    private Image           _picture;
    private Menu            _menu;
    private GanttChart      _parentChart;
    private GanttComposite  _parentComposite;
    private GanttGroup      _ganttGroup;
    private GanttSection    _ganttSection;
    private boolean         _hidden;
    private int             _widthWithtText;
    // TODO: Implement. Less constructors, more user power.
    // private int mEventType = TYPE_EVENT;
    private AdvancedTooltip _advancedTooltip;
    private int             _visibility;
    private boolean         _boundsHaveBeenSet;

    private int             _fixedRowHeight            = FIXED_ROW_HEIGHT_AUTOMATIC;
    private int             _verticalEventAlignment    = SWT.TOP;

    private int             _horizontalLineTopY;
    private int             _horizontalLineBottomY;

    private Calendar        _noMoveBeforeDate;
    private Calendar        _noMoveAfterDate;

    // private items
    private boolean         _nameChanged               = true;
    private Point           _nameExtent;
    private String          _parsedString;

    private int             _horizontalTextLocation    = SWT.RIGHT;
    private int             _verticalTextLocation      = SWT.CENTER;

    private boolean 		_showText					= true;

    private Font            _textFont;

    private int             _daysBetweenStartAndEnd;

    // cloned holders used for cancelling a move/resize via ESC
    private Calendar        _preMoveDateEstiStart;
    private Calendar        _preMoveDateEstiEnd;
    private Calendar        _preMoveDateRevisedStart;
    private Calendar        _preMoveDateRevisedEnd;
    private Rectangle       _preMoveBounds;
    private boolean         _moving;
    private int             _moveType;
    private int             _preMoveGanttSectionIndex;
    private int             _preMoveGanttSectionEventLocationIndex;

    private GanttEvent      _scopeParent;

    private int             _dDayStart;
    private int             _dDayEnd;

    private int             _savedVerticalDragY;

    private Rectangle       _preVerticalDragBounds;

    /**
     * Creates a new GanttEvent.
     * 
     * @param parent Parent chart
     * @param name Name of event
     * @param startDate Start date
     * @param endDate End date
     * @param percentComplete Percent complete
     */
    public GanttEvent(final GanttChart parent, final String name, final Calendar startDate, final Calendar endDate, final int percentComplete) {
        this(parent, null, name, startDate, endDate, percentComplete);
    }

    /**
     * Creates a new GanttEvent object.
     * 
     * @param parent Parent chart
     * @param data Data object
     * @param name Name of event
     * @param startDate Start date
     * @param endDate End date
     * @param percentComplete Percent complete
     */
    public GanttEvent(final GanttChart parent, final Object data, final String name, final Calendar startDate, final Calendar endDate, final int percentComplete) {
        this._parentChart = parent;
        this._parentComposite = _parentChart.getGanttComposite();
        this._data = data;
        this._name = name;
        this._startDate = startDate;
        this._endDate = endDate;
        this._percentComplete = percentComplete;
        init(); // NOPMD
    }

    /**
     * Creates a new GanttEvent.
     * 
     * @param parent Parent chart
     * @param name Name of event
     * @param startDate Start date
     * @param endDate End date
     * @param revisedStart Revised start
     * @param revisedEnd Revised end
     * @param percentComplete Percent complete
     */
    public GanttEvent(final GanttChart parent, final String name, final Calendar startDate, final Calendar endDate, final Calendar revisedStart, final Calendar revisedEnd, final int percentComplete) {
        this(parent, null, name, startDate, endDate, revisedStart, revisedEnd, percentComplete);
    }

    /**
     * Creates a new GanttEvent object with revised start and end dates.
     * 
     * @param parent Parent GanttChart
     * @param data Data object
     * @param name Display name
     * @param startDate Start date
     * @param endDate End date
     * @param revisedStart Revised start date
     * @param revisedEnd Revised end date
     * @param percentComplete Percentage complete
     */
    public GanttEvent(final GanttChart parent, final Object data, final String name, final Calendar startDate, final Calendar endDate, final Calendar revisedStart, final Calendar revisedEnd, final int percentComplete) {
        this._parentChart = parent;
        this._parentComposite = _parentChart.getGanttComposite();
        this._data = data;
        this._name = name;
        this._startDate = startDate;
        this._endDate = endDate;
        this._revisedStart = revisedStart;
        this._revisedEnd = revisedEnd;
        this._percentComplete = percentComplete;
        init(); // NOPMD
    }

    /**
     * D-day event creation.
     * 
     * @param parent Parent chart
     * @param dDayStart D day start value (zero based)
     * @param dDayEnd D day end value (zero based)
     */
    public GanttEvent(final GanttChart parent, final int dDayStart, final int dDayEnd) {
        this._parentChart = parent;
        this._parentComposite = _parentChart.getGanttComposite();
        this._dDayStart = dDayStart;
        this._dDayEnd = dDayEnd;

        _startDate = _parentChart.getGanttComposite().getDDayCalendar();
        _endDate = _parentChart.getGanttComposite().getDDayCalendar();
        _startDate.add(Calendar.DATE, _dDayStart);
        _endDate.add(Calendar.DATE, _dDayEnd);

        init(); // NOPMD
    }

    /**
     * Creates a GanttEvent intended to be a scope.
     * 
     * @param parent Chart parent
     * @param name Name of scope
     */
    public GanttEvent(final GanttChart parent, final String name) {
        this(parent, null, name);
    }

    /**
     * Creates a GanttEvent intended to be a scope.
     * 
     * @param parent Chart parent
     * @param data Data object
     * @param name Name of scope
     */
    public GanttEvent(final GanttChart parent, final Object data, final String name) {
        this._parentChart = parent;
        this._parentComposite = _parentChart.getGanttComposite();
        this._data = data;
        this._name = name;
        this._scope = true;
        try {
            init(); // NOPMD
        } catch (Exception err) {
            SWT.error(SWT.ERROR_UNSPECIFIED, err);
        }
    }

    /**
     * Creates a GanttEvent intended to be a checkpoint.
     * 
     * @param parent Chart parent
     * @param name Name of checkpoint
     * @param date Checkpoint start (and end) date
     */
    public GanttEvent(final GanttChart parent, final String name, final Calendar date) {
        this(parent, null, name, date);
    }

    /**
     * Creates a GanttEvent intended to be a checkpoint.
     * 
     * @param parent Chart parent
     * @param data Data object
     * @param name Name of checkpoint
     * @param date Start (and end) date
     */
    public GanttEvent(final GanttChart parent, final Object data, final String name, final Calendar date) {
        this._parentChart = parent;
        this._parentComposite = _parentChart.getGanttComposite();
        this._data = data;
        this._name = name;
        this._startDate = date;
        this._endDate = date;
        this._checkpoint = true;
        try {
            init(); // NOPMD
        } catch (Exception err) {
            SWT.error(SWT.ERROR_UNSPECIFIED, err);
        }
    }

    /**
     * Creates a GanttEvent intended to be an image.
     * 
     * @param parent Chart parent
     * @param name Name of image
     * @param date Start (and end) date
     * @param picture Image to show
     */
    public GanttEvent(final GanttChart parent, final String name, final Calendar date, final Image picture) {
        this(parent, null, name, date, picture);
    }

    /**
     * Creates a GanttEvent intended to be an image.
     * 
     * @param parent Chart parent
     * @param data Data object
     * @param name Name of image
     * @param date Start (and end) date
     * @param picture Image to show
     */
    public GanttEvent(final GanttChart parent, final Object data, final String name, final Calendar date, final Image picture) {
        this._parentChart = parent;
        this._parentComposite = _parentChart.getGanttComposite();
        this._data = data;
        this._name = name;
        this._startDate = date;
        this._endDate = date;
        this._picture = picture;
        this._image = true;
        try {
            init(); // NOPMD
        } catch (Exception err) {
            SWT.error(SWT.ERROR_UNSPECIFIED, err);
        }
    }

    private final void init() {
        _scopeEvents = new ArrayList();
        _parentComposite.addEvent(this, true);

        updateDaysBetweenStartAndEnd();
    }

    /**
     * Returns the currently set data object.
     * 
     * @return Data object
     */
    public Object getData() {
        return _data;
    }

    /**
     * Sets the current data object.
     * 
     * @param data Data object
     */
    public void setData(final Object data) {
        this._data = data;
    }

    /**
     * Returns the display name of this event.
     * 
     * @return Display name
     */
    public String getName() {
        return this._name;
    }

    /**
     * Sets the display name of this event.
     * 
     * @param name Display name
     */
    public void setName(final String name) {
        this._name = name;
        _nameChanged = true;
    }

    /**
     * Returns the start date of this event.
     * 
     * @return Start date
     */
    public Calendar getStartDate() {
        if (_startDate == null) { return null; }

        return DateHelper.getNewCalendar(_startDate);
    }

    /**
     * Returns the revised start date if set, or the start date if not.
     * 
     * @return Start date or null
     */
    public Calendar getActualStartDate() {
        final Calendar ret = _revisedStart == null ? _startDate : _revisedStart;
        return ret == null ? null : DateHelper.getNewCalendar(ret);
    }

    /**
     * Returns the revised end date if set, or the end date if not.
     * 
     * @return End date or null
     */
    public Calendar getActualEndDate() {
        final Calendar ret = _revisedEnd == null ? _endDate : _revisedEnd;
        return ret == null ? null : DateHelper.getNewCalendar(ret);
    }

    /**
     * Returns whatever is the earliest calendar of the start date and the actual start date. If any of them are null,
     * whichever has a calendar is returned. If both are null, null is returned.
     * 
     * @return Earliest start date
     */
    public Calendar getEarliestStartDate() {
        if (_revisedStart == null && _startDate == null) { return null; }

        if (_revisedStart == null) { return _startDate; }
        if (_startDate == null) { return _revisedStart; }

        return _startDate.before(_revisedStart) ? _startDate : _revisedStart;
    }

    /**
     * Returns whatever is the latest calendar of the end date and the actual end date. If any of them are null,
     * whichever has a calendar is returned. If both are null, null is returned.
     * 
     * @return Latest end date
     */
    public Calendar getLatestEndDate() {
        if (_revisedEnd == null && _endDate == null) { return null; }

        if (_revisedEnd == null) {
            return _endDate;
        } else if (_endDate == null) { return _revisedEnd; }

        return _endDate.after(_revisedEnd) ? _endDate : _revisedEnd;
    }

    /**
     * Sets the end date of this event.
     * 
     * @param startDate Start date
     */
    public void setStartDate(final Calendar startDate) {
        if (startDate == null) {
            _startDate = startDate;
            updateDaysBetweenStartAndEnd();
            return;
        }

        Calendar sDate = DateHelper.getNewCalendar(startDate);

        if (_noMoveBeforeDate != null && startDate.before(_noMoveBeforeDate)) { return; }

        Calendar aEnd = getActualEndDate();
        if (aEnd != null && startDate.after(aEnd)) {
            sDate = aEnd;
        }

        this._startDate = sDate;
        updateDaysBetweenStartAndEnd();
    }

    /**
     * Forces the chart to recognize that something within this event has changed and that it needs an update. This
     * method will cause a redraw if told to redraw.
     * 
     * @param redraw if to redraw the chart after notifying of changes.
     */
    public void update(final boolean redraw) {
        _parentComposite.eventDatesChanged(this, redraw);
    }

    /**
     * Returns the end date of this event.
     * 
     * @return End date
     */
    public Calendar getEndDate() {
        if (_endDate == null) { return null; }

        return DateHelper.getNewCalendar(_endDate);
    }

    /**
     * Sets the end date of this event.
     * 
     * @param endDate End date
     */
    public void setEndDate(final Calendar endDate) {
        if (endDate == null) {
            _endDate = endDate;
            updateDaysBetweenStartAndEnd();
            return;
        }

        Calendar eDate = DateHelper.getNewCalendar(endDate);

        if (_noMoveAfterDate != null && endDate.after(_noMoveAfterDate)) { return; }

        Calendar aDate = getActualStartDate();
        if (aDate != null && endDate.before(aDate)) {
            eDate = aDate;
        }

        this._endDate = eDate;
        updateDaysBetweenStartAndEnd();
    }

    /**
     * Returns the percentage complete of this event.
     * 
     * @return Percentage complete
     */
    public int getPercentComplete() {
        return _percentComplete;
    }

    /**
     * Sets the percentage complete of this event.
     * 
     * @param percentComplete Percentage complete
     */
    public void setPercentComplete(final int percentComplete) {
        this._percentComplete = percentComplete;
    }

    /**
     * Returns the individual Menu for this event for which custom events can be created. Custom events will be mixed
     * with built-in events (if they exist). The menu will be created on the first get call to this method, any
     * subsequent method calls will return the same menu object. Do remember that a menu counts as a handle towards the
     * maximum number of handles. If you have very many events, it is probably better to use a different way of creating
     * menus, this is merely meant as a convenience.
     * 
     * @return Menu
     */
    public Menu getMenu() {
        if (_menu == null) {
            _menu = new Menu(_parentComposite);
        }

        return _menu;
    }

    /**
     * Sets the bounds of the event. This is done internally.
     * 
     * @param x x location
     * @param y y location
     * @param width width of event
     * @param height height of event
     */
    void setBounds(final int x, final int y, final int width, final int height) {
        _boundsHaveBeenSet = true;
        this._x = x;
        this._y = y;
        this._width = width;
        this._height = height;
    }

    /**
     * Sets the bounds of the event.
     * 
     * @param bounds New bounds
     */
    void setBounds(final Rectangle bounds) {
        _boundsHaveBeenSet = true;
        this._x = bounds.x;
        this._y = bounds.y;
        this._width = bounds.width;
        this._height = bounds.height;
        //if (_name.indexOf("2") > -1)
        //System.err.println("Updatebounds " + mName + " " + getBounds());

        if (getEarliestStartDate() != null) {
            setEarliestStartX(_parentComposite.getStartingXFor(getEarliestStartDate()));
        }
        if (getLatestEndDate() != null) {
            setLatestEndX(_parentComposite.getXForDate(getLatestEndDate()) + _parentComposite.getDayWidth());
        }
    }

    void updateX(final int x) {
        this._x = x;

        updateOtherXs();
    }

    private void updateOtherXs() {
        if (getEarliestStartDate() != null) {
            setEarliestStartX(_parentComposite.getStartingXFor(getEarliestStartDate()));
        }
        if (getLatestEndDate() != null) {
            setLatestEndX(_parentComposite.getXForDate(getLatestEndDate()) + _parentComposite.getDayWidth());
        }
    }

    void updateY(final int y) {
        this._y = y;
    }

    void updateHeight(final int height) {
        this._height = height;
    }

    void updateWidth(final int width) {
        this._width = width;
    }

    /**
     * Whether this is a checkpoint or not.
     * 
     * @return true if this is a checkpoint
     */
    public boolean isCheckpoint() {
        return _checkpoint;
    }

    /**
     * Sets the event to be a checkpoint.
     * 
     * @param checkpoint true if the event should be a checkpoint
     */
    public void setCheckpoint(final boolean checkpoint) {
        this._scope = false;
        this._image = false;
        this._checkpoint = checkpoint;
    }

    /**
     * Returns the x location of this event.
     * 
     * @return x location
     */
    public int getX() {
        return _x;
    }

    /**
     * Returns the ending x location for this event.
     * 
     * @return x end location
     */
    public int getXEnd() {
        return _x + _width;
    }

    /**
     * Returns the y location of this event.
     * 
     * @return y location
     */
    public int getY() {
        return _y;
    }

    /**
     * Returns the bottom y of this event.
     * 
     * @return y bottom
     */
    public int getBottomY() {
        return _y + _height;
    }

    /**
     * Returns the width of this event.
     * 
     * @return width
     */
    public int getWidth() {
        return _width;
    }

    /**
     * Returns the height of this event.
     * 
     * @return height
     */
    public int getHeight() {
        return _height;
    }

    /**
     * Returns the revised start date of this event.
     * 
     * @return Revised date
     */
    public Calendar getRevisedStart() {
        if (_revisedStart == null) { return null; }

        return DateHelper.getNewCalendar(_revisedStart);
    }

    /**
     * Sets the revised start date of this event.
     * 
     * @param revisedStart
     */
    public void setRevisedStart(final Calendar revisedStart) {
        setRevisedDates(revisedStart, null);
    }

    /**
     * Returns the revised end date of this event.
     * 
     * @return revised end date
     */
    public Calendar getRevisedEnd() {
        if (_revisedEnd == null) { return null; }

        return DateHelper.getNewCalendar(_revisedEnd);
    }

    /**
     * Sets the revised end date of this event.
     * 
     * @param revisedEnd Revised end date
     */
    public void setRevisedEnd(final Calendar revisedEnd) {
        setRevisedDates(null, revisedEnd);
    }

    /**
     * Sets the revised D-day start.
     * 
     * @param dDayStart d-day start.
     */
    public void setRevisedStart(final int dDayStart) {
        _revisedStart = _parentComposite.getDDayCalendar();
        _revisedStart.add(Calendar.DATE, dDayStart);
    }

    /**
     * Sets the revised D-day end.
     * 
     * @param dDayEnd d-day end
     */
    public void setRevisedEnd(final int dDayEnd) {
        _revisedEnd = _parentComposite.getDDayCalendar();
        _revisedEnd.add(Calendar.DATE, dDayEnd);
    }

    /**
     * Sets new revised dates. This is useful when you need to update two dates that move at the same time (such as
     * manually doing a move via setDates). Normally each setting of a date would check it against its start date or end
     * date to make sure it doesn't overlap. This does too, but at the same time, thus, no oddity in movement will
     * appear. This is rather difficult to explain, but if you experience event-length changes when using individual
     * start and end date sets that appear at the same time, you probably want to use this method instead.
     * <p>
     * Either parameter may be null to set just one, but both may not be null
     * 
     * @param revisedStart New revised Start date
     * @param revisedEnd New revised End date
     */
    private void setRevisedDates(final Calendar revisedStart, final Calendar revisedEnd) {
        if (revisedStart == null && revisedEnd == null) { return; }

        if (revisedStart != null && getActualEndDate() != null && revisedStart.after(getActualEndDate())) { return; }

        if (revisedEnd != null && getActualStartDate() != null && revisedEnd.before(getActualStartDate())) { return; }

        if (revisedStart != null) {
            _revisedStart = DateHelper.getNewCalendar(revisedStart);
        }

        if (revisedEnd != null) {
            _revisedEnd = DateHelper.getNewCalendar(revisedEnd);
        }

        // check movement constraints
        if (_noMoveBeforeDate != null && revisedStart != null && revisedStart.before(_noMoveBeforeDate)) {
            _revisedStart = DateHelper.getNewCalendar(_noMoveBeforeDate);
        }

        if (_noMoveAfterDate != null && revisedEnd != null && revisedEnd.after(_noMoveAfterDate)) {
            _revisedEnd = DateHelper.getNewCalendar(_noMoveAfterDate);
        }

        updateDaysBetweenStartAndEnd();
        // mParentChart.getGanttComposite().eventDatesChanged(this);
    }

    /**
     * When you need to move events manually you may run into issues as one date always has to be set before the other.
     * So when you set a new end and start date one is checked for overlap before the other and is thus ignored.
     * Sometimes you just want to set the dates and not enforce validation. Do remember, you need to validate the dates
     * yourself before setting them. If the start comes after the end or vice versa you will run into serious drawing
     * issues and strange behavior.
     * 
     * @param revisedStart New revised start date
     * @param validate true if to validate the date as normal, false to just set the date as is.
     */
    public void setRevisedStart(final Calendar revisedStart, final boolean validate) {
        if (validate) {
            setRevisedStart(revisedStart);
        } else {
            _revisedStart = DateHelper.getNewCalendar(revisedStart);
            updateDaysBetweenStartAndEnd();
        }
    }

    /**
     * When you need to move events manually you may run into issues as one date always has to be set before the other.
     * So when you set a new end and start date one is checked for overlap before the other and is thus ignored.
     * Sometimes you just want to set the dates and not enforce validation. Do remember, you need to validate the dates
     * yourself before setting them. If the start comes after the end or vice versa you will run into serious drawing
     * issues and strange behavior.
     * 
     * @param revisedStart New revised start date
     * @param validate true if to validate the date as normal, false to just set the date as is.
     */
    public void setRevisedEnd(final Calendar revisedEnd, final boolean validate) {
        if (validate) {
            setRevisedEnd(revisedEnd);
        } else {
            _revisedEnd = DateHelper.getNewCalendar(revisedEnd);
            updateDaysBetweenStartAndEnd();
        }
    }

    /**
     * Another utility method for setting new dates but this method enforces the usual validation. The difference here
     * is that you can tell the method in which order the new dates should be set. If you say left to right, the start
     * is set first, the end last.
     * 
     * @param revisedStart Revised start date
     * @param revisedEnd Revised end date
     * @param order <code>SWT.LEFT_TO_RIHT</code> or <code>SWT.RIGHT_TO_LEFT</code>
     */
    public void setRevisedDates(final Calendar revisedStart, final Calendar revisedEnd, final int order) {
        if (order == SWT.LEFT_TO_RIGHT) {
            setRevisedStart(revisedStart);
            setRevisedEnd(revisedEnd);
        } else {
            setRevisedEnd(revisedEnd);
            setRevisedStart(revisedStart);
        }

        updateDaysBetweenStartAndEnd();
    }

    /**
     * Whether this event is locked or not.
     * 
     * @return true if event is locked.
     */
    public boolean isLocked() {
        return _locked;
    }

    /**
     * Sets whether this event is locked or not.
     * 
     * @param locked true if locked
     */
    public void setLocked(final boolean locked) {
        this._locked = locked;
    }

    /**
     * Returns the status color used as part of the gradient colors used to draw the event.
     * 
     * @return Gradient color
     */
    public Color getGradientStatusColor() {
        return _gradientStatusColor;
    }

    /**
     * Sets the status color used as part of the gradient colors used to draw the event.
     * 
     * @param gradientStatusColor Gradient color
     */
    public void setGradientStatusColor(final Color gradientStatusColor) {
        _gradientStatusColor = gradientStatusColor;
    }

    /**
     * Returns the status color of this event. This is also used for drawing gradients.
     * 
     * @return Status color
     */
    public Color getStatusColor() {
        return _statusColor;
    }

    /**
     * Sets the status color of this event. This is also used for drawing gradients.
     * 
     * @param statusColor Status color
     */
    public void setStatusColor(final Color statusColor) {
        _statusColor = statusColor;
    }

    /**
     * Whether to show the display name in bold text or not.
     * 
     * @return true if to show bold text
     */
    public boolean showBoldText() {
        return _showBoldText;
    }

    /**
     * Sets whether to show the display name in bold text or not.
     * 
     * @param showBoldText Set to true if to show bold text
     */
    public void setShowBoldText(final boolean showBoldText) {
        _showBoldText = showBoldText;
    }

    /**
     * Returns the bounds of this event (do note that events may have no bounds if they are invisible or hidden and will
     * not get actual bounds set until they are shown).
     * 
     * @return Bounds
     */
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Returns the text display format of this event. Please see ISettings.getTextDisplayFormat() for an overview. If
     * this method returns null, whatever is set in the settings will be used.
     * 
     * @return Text format or null
     * @see ISettings#getTextDisplayFormat()
     */
    public String getTextDisplayFormat() {
        return _textDisplayFormat;
    }

    /**
     * Sets the text display format of this event. Please see ISettings.getTextDisplayFormat() for an overview.
     * 
     * @see ISettings#getTextDisplayFormat()
     */
    public void setTextDisplayFormat(final String format) {
        _textDisplayFormat = format;
    }

    /**
     * Sets this event to be a scope. Don't forget to add events that the scope is supposed to encompass with
     * {@link #addScopeEvent(GanttEvent)}.
     * 
     * @param scope
     * @see #addScopeEvent(GanttEvent)
     */
    public void setScope(final boolean scope) {
        _checkpoint = false;
        _image = false;
        _scope = scope;

        if (!scope) {
            for (int i = 0; i < _scopeEvents.size(); i++) {
                ((GanttEvent) _scopeEvents.get(i)).setScopeParent(null);
            }

            _scopeEvents.clear();
        }
    }

    /**
     * Returns whether this is a scope or not.
     * 
     * @return true if scope
     */
    public boolean isScope() {
        return _scope;
    }

    /**
     * Adds an event that this event should cover if it has been set to be a scope.
     * 
     * @param event GanttEvent to encompass
     */
    public void addScopeEvent(final GanttEvent event) {
        if (event == this) { return; }

        if (_scopeEvents.contains(event)) { return; }

        _scopeEvents.add(event);

        event.setScopeParent(this);
    }

    /**
     * Removes event that this event encompasses as a scope.
     * 
     * @param event GanttEvent
     */
    public void removeScopeEvent(final GanttEvent event) {
        _scopeEvents.remove(event);
    }

    /**
     * Returns the list of GanttEvents that the scope encompasses.
     * 
     * @return List of events
     */
    public List getScopeEvents() {
        return _scopeEvents;
    }

    private GanttEvent getEarliestOrLatestScopeEvent(final boolean earliest) {
        Calendar ret = null;
        GanttEvent retEvent = null;

        for (int i = 0; i < _scopeEvents.size(); i++) {
            final GanttEvent ge = (GanttEvent) _scopeEvents.get(i);
            if (earliest) {
                if (ret == null) {
                    ret = ge.getActualStartDate();
                    retEvent = ge;
                    continue;
                }

                if (ge.getActualStartDate().before(ret)) {
                    ret = ge.getActualStartDate();
                    retEvent = ge;
                }
            } else {
                if (ret == null) {
                    ret = ge.getActualEndDate();
                    retEvent = ge;
                    continue;
                }

                if (ge.getActualEndDate().after(ret)) {
                    ret = ge.getActualEndDate();
                    retEvent = ge;
                }
            }
        }

        return retEvent;
    }

    /**
     * Returns the event that has the earliest date of all events in a scope.
     * 
     * @return Earliest event or null if none
     */
    public GanttEvent getEarliestScopeEvent() {
        if (!isScope() || _scopeEvents.isEmpty()) { return null; }

        return getEarliestOrLatestScopeEvent(true);
    }

    /**
     * Returns the event that has the latest date of all events in a scope.
     * 
     * @return Latest event or null if none
     */
    public GanttEvent getLatestScopeEvent() {
        if (!isScope() || _scopeEvents.isEmpty()) { return null; }

        return getEarliestOrLatestScopeEvent(false);
    }

    /**
     * Returns the picture image for this event.
     * 
     * @return Picture
     */
    public Image getPicture() {
        return _picture;
    }

    /**
     * Sets the picture image for this event.
     * 
     * @param picture Image picture
     */
    public void setPicture(final Image picture) {
        this._picture = picture;
    }

    /**
     * Sets this event to be represented by an image.
     * 
     * @param image true if it's an image
     */
    public void setImage(final boolean image) {
        _checkpoint = false;
        _scope = false;

        this._image = image;
    }

    /**
     * Returns whether this event is an image representation or not.
     * 
     * @return true if this event is an image representation
     */
    public boolean isImage() {
        return _image;
    }

    /**
     * Returns what GanttGroup this event belongs to. Grouped events are drawn on the same line. Set null for no group.
     * 
     * @return GanttGroup
     */
    public GanttGroup getGanttGroup() {
        return _ganttGroup;
    }

    /**
     * Sets what group this event belongs to. Grouped events are drawn on the same line.
     * 
     * @param group GanttGroup or null if none
     */
    public void setGanttGroup(final GanttGroup group) {
        _ganttGroup = group;
    }

    /**
     * Returns the {@link GanttSection} that this event belongs to, or null if none.
     * 
     * @return Parent {@link GanttSection}
     */
    public GanttSection getGanttSection() {
        return _ganttSection;
    }

    /**
     * Sets the GanttSection that this event belongs to
     * 
     * @param ganttSection
     */
    void setGanttSection(final GanttSection ganttSection) {
        _ganttSection = ganttSection;
    }

    /**
     * Sets if this event should be hidden or not. Hidden events are not shown on the chart and the space they normally
     * would occupy is free.
     * 
     * @param hidden true to hide event.
     */
    public void setHidden(boolean hidden) {
        _hidden = hidden;
    }

    /**
     * Returns whether this event is hidden or not. Hidden events are not shown on the chart and the space they normally
     * would occupy is free.
     * 
     * @return true if hidden.
     */
    public boolean isHidden() {
        return _hidden;
    }

    private void internalSetAllChildrenHidden(final boolean hidden) {
        if (_scopeEvents == null) { return; }

        for (int i = 0; i < _scopeEvents.size(); i++) {
            ((GanttEvent) _scopeEvents.get(i)).setHidden(hidden);
        }
    }

    /**
     * Hides all children from view. Children only exist on Scoped events.
     * 
     * @see #isHidden()
     */
    public void hideAllChildren() {
        internalSetAllChildrenHidden(true);
        _parentComposite.updateHorizontalScrollbar();
        _parentComposite.updateVerticalScrollBar(false);

        _parentComposite.heavyRedraw();
    }

    /**
     * Un-hides all children from view. Children only exist on Scoped events.
     * 
     * @see #isHidden()
     */
    public void showAllChildren() {
        internalSetAllChildrenHidden(false);
        _parentComposite.updateHorizontalScrollbar();
        _parentComposite.updateVerticalScrollBar(false);

        _parentComposite.heavyRedraw();
    }

    /**
     * Returns whether all children are hidden or not. Children only exist on Scoped events.
     * 
     * @return true if all children are hidden.
     */
    public boolean isChildrenHidden() {
        if (_scopeEvents == null) { return false; }

        for (int i = 0; i < _scopeEvents.size(); i++) {
            if (!((GanttEvent) _scopeEvents.get(i)).isHidden()) { return false; }
        }

        return true;
    }

    /**
     * Returns the full right most x value if event has text or images drawn after it.
     * 
     * @return x position
     */
    public int getWidthWithText() {
        return _widthWithtText;
    }

    /**
     * Sets the full width of the event width and the text and images that come after it.
     * 
     * @param width x width
     */
    public void setWidthWithText(final int width) {
        this._widthWithtText = width;
    }

    /**
     * Returns the currently set Advanced tooltip, or null if none is set.
     * 
     * @return Advanced tooltip
     */
    public AdvancedTooltip getAdvancedTooltip() {
        return _advancedTooltip;
    }

    /**
     * Sets the advanced tooltip. If none is set, a default tooltip will be used.
     * 
     * @param advancedTooltip
     */
    public void setAdvancedTooltip(final AdvancedTooltip advancedTooltip) {
        this._advancedTooltip = advancedTooltip;
    }

    /**
     * Returns the fixed row height in pixels.
     * 
     * @return Fixed row height
     */
    public int getFixedRowHeight() {
        return _fixedRowHeight;
    }

    /**
     * Sets the fixed row height in pixels.
     * 
     * @param fixedRowHeight Fixed row height
     */
    public void setFixedRowHeight(final int fixedRowHeight) {
        this._fixedRowHeight = fixedRowHeight;
    }

    /**
     * Whether the row height is calculated automatically or if it's fixed
     * 
     * @return true if automatic
     */
    public boolean isAutomaticRowHeight() {
        return (getFixedRowHeight() == FIXED_ROW_HEIGHT_AUTOMATIC);
    }

    /**
     * Resets the row height to be calculated automatically should the row height have been set to a specific value.
     */
    public void setAutomaticRowHeight() {
        setFixedRowHeight(FIXED_ROW_HEIGHT_AUTOMATIC);
    }

    /**
     * Returns the vertical alignment set. Alignments are only valid if the row height is a fixed value.
     * 
     * @return Row alignment, one of <code>SWT.TOP</code>, <code>SWT.CENTER</code>, <code>SWT.BOTTOM</code>. Default is
     *         <code>SWT.TOP</code>.
     */
    public int getVerticalEventAlignment() {
        return _verticalEventAlignment;
    }

    /**
     * Sets the vertical alignment for this event. Alignments are only valid if the row height is a fixed value.
     * 
     * @param verticalEventAlignment one of <code>SWT.TOP</code>, <code>SWT.CENTER</code>, <code>SWT.BOTTOM</code>
     */
    public void setVerticalEventAlignment(final int verticalEventAlignment) {
        this._verticalEventAlignment = verticalEventAlignment;
    }

    /**
     * Whether this event is resizable. If resizing is turned off in the settings this method does nothing.
     * 
     * @return true if resizable. Default is true.
     */
    public boolean isResizable() {
        return _resizable;
    }

    /**
     * Sets this event to be resizable or not. If resizing is turned off in the settings this method does nothing.
     * 
     * @param resizable true to make event resizable.
     */
    public void setResizable(final boolean resizable) {
        _resizable = resizable;
    }

    /**
     * Whether this event is moveable. If moving is turned off in the settings this method does nothing.
     * 
     * @return true if moveable. Default is true.
     */
    public boolean isMoveable() {
        return _moveable;
    }

    /**
     * Sets this event to be moveable or not. If moving is turned off in the settings this method does nothing.
     * 
     * @param resizable true to make event moveable.
     */
    public void setMoveable(final boolean moveable) {
        _moveable = moveable;
    }

    /**
     * Returns the date prior to which no moves or resizes are allowed. Any events moved towards or across the date mark
     * will stop at this mark.
     * 
     * @return Calendar or null.
     */
    public Calendar getNoMoveBeforeDate() {
        return _noMoveBeforeDate;
    }

    /**
     * Sets the date prior to which no moves or resizes are allowed. Any events moved towards or across the date mark
     * will stop at this mark.
     * 
     * @param noMoveBeforeDate Calendar or null.
     */
    public void setNoMoveBeforeDate(final Calendar noMoveBeforeDate) {
        _noMoveBeforeDate = noMoveBeforeDate;
    }

    /**
     * Returns the date after which no moves or resizes are allowed. Any events moved towards or across the date mark
     * will stop at this mark.
     * 
     * @return Calendar or null.
     */
    public Calendar getNoMoveAfterDate() {
        return _noMoveAfterDate;
    }

    /**
     * Sets the date after which no moves or resizes are allowed. Any events moved towards or across the date mark will
     * stop at this mark.
     * 
     * @param noMoveAfterDate Calendar or null.
     */
    public void setNoMoveAfterDate(final Calendar noMoveAfterDate) {
        _noMoveAfterDate = noMoveAfterDate;
    }

    /**
     * Returns the location where the event text will be displayed. Default is SWT.RIGHT.
     * 
     * @return Event text location
     */
    public int getHorizontalTextLocation() {
        return _horizontalTextLocation;
    }

    /**
     * Sets the location of where the event text will be displayed. Options are: <code>SWT.LEFT</code>,
     * <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>. Center means the text will be drawn inside the event. Default is
     * <code>SWT.RIGHT</code>.
     * 
     * @param textLocation Text location, one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>.
     */
    public void setHorizontalTextLocation(final int textLocation) {
        _horizontalTextLocation = textLocation;
    }

    /**
     * Returns the lcoation where the event text will be displayed vertically. Default is <code>SWT.CENTER</code> which
     * is right behind the event itself.
     * 
     * @return Vertical text location.
     */
    public int getVerticalTextLocation() {
        return _verticalTextLocation;
    }

    /**
     * Sets the vertical location where the event text will be displayed. Options are: <code>SWT.TOP</code>,
     * <code>SWT.CENTER</code>, <code>SWT.BOTTOM</code>. Default is <code>SWT.CENTER</code>.
     * 
     * @param verticalTextLocation Vertical text location
     */
    public void setVerticalTextLocation(final int verticalTextLocation) {
        _verticalTextLocation = verticalTextLocation;
    }

    /**
     * Returns the text font to use when drawing the event text. Default is null.
     * 
     * @return Event font
     */
    public Font getTextFont() {
        return _textFont;
    }

    /**
     * Sets the text font to use when drawing the event text. Default is null. Setting a font will override any flags
     * for font settings in the Settings implementation.
     * 
     * @param textFont Font or null.
     */
    public void setTextFont(final Font textFont) {
        _textFont = textFont;
    }

    /**
     * Removes this item from the chart.
     */
    public void dispose() {
        _parentComposite.removeEvent(this);
    }

    public int getActualDDayStart() {
        return _revisedStart == null ? getDDayStart() : getDDayRevisedStart();
    }

    public int getActualDDayEnd() {
        return _revisedEnd == null ? getDDayEnd() : getDDayRevisedEnd();
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
        if (_revisedStart == null) { return Integer.MAX_VALUE; }

        return (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), _revisedStart);
    }

    public int getDDayRevisedEnd() {
        if (_revisedEnd == null) { return Integer.MAX_VALUE; }

        return (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), _revisedEnd);
    }

    /**
     * Sets the D-day start value.
     * 
     * @param day
     */
    public void setDDayStart(final int day) {
        _dDayStart = day;

        this._startDate = _parentComposite.getDDayCalendar();
        _startDate.add(Calendar.DATE, day);
        updateDaysBetweenStartAndEnd();
    }

    public int getDDayEnd() {
        return _dDayEnd;
    }

    public void setDDayEnd(final int day) {
        _dDayEnd = day;

        this._endDate = _parentComposite.getDDayCalendar();
        _endDate.add(Calendar.DATE, day);
        updateDaysBetweenStartAndEnd();
    }

    public int getDDateRange() {
        return (int) DateHelper.daysBetween(getStartDate(), getEndDate());
    }

    public int getRevisedDDateRange() {
        return (int) DateHelper.daysBetween(getActualStartDate(), getActualEndDate()) + 1;
    }

    // internal methods

    /**
     * This takes any text drawing widths into account and returns the bounds as such
     * 
     * @return Rectangle of bounds
     */
    public Rectangle getActualBounds() {
        // for starters the bounds are the same as normal, then we add on text widths, planned date widths etc
        Rectangle ret = getBounds();

        int plannedExtraRight = 0;
        int plannedExtraLeft = 0;
        boolean usePlannedLeft = false;
        boolean usePlannedRight = false;

        // if we're showing planned dates, check to see what is further away, the text width or our planned date width
        // we save the values and compare to the bonuses calculated for text widths below
        if (_parentComposite.isShowingPlannedDates()) {
        	// note to self: we used to have date checks here, but I can't remember what for, as these checks aren't necessarily valid. Revised vs. Planned
        	// dates can come before one or another, doesn't matter, we still need offsets, so I took them out as it fixes a reported bug as well (no bugzilla)
        	
            if (getStartDate() != null && getRevisedStart() != null) { // && getStartDate().before(getRevisedStart())) {
                // this is a negative value as it's to the left
                plannedExtraLeft = getX() - _earliestStartX;
            }
            if (getEndDate() != null && getRevisedEnd() != null) { // && getEndDate().after(getRevisedEnd())) {
                plannedExtraRight = _latestEndX - getXEnd() + _parentComposite.getDayWidth();
            }
        }

        // now calculate text widths, later we'll check what takes up the most space, planned dates or text width. Whoever is bigger
        // becomes the "bonus" width that we add to the actual bounds
        int xExtra = 0, yExtra = 0, wExtra = 0, hExtra = 0;

        // TODO: this should take connected/not connected into account
        final int textSpacer = _parentChart.getSettings().getTextSpacerConnected();
        if (getNameExtent() != null) {
            switch (_horizontalTextLocation) {
                case SWT.LEFT:
                    final int extra = getNameExtent().x + textSpacer;
                    if (plannedExtraLeft > extra) {
                        usePlannedLeft = true;
                    }
                    xExtra -= extra;
                    wExtra += extra;
                    usePlannedRight = plannedExtraRight > 0;
                    break;
                case SWT.CENTER:
                    int start = _width / 2;
                    start += getNameExtent().x + textSpacer;
                    if (start > (_x + _width)) {
                        wExtra += (start - (_x + _width)); // add on the difference
                    }
                    if (plannedExtraRight > wExtra) {
                        usePlannedRight = true;
                    }
                    usePlannedLeft = plannedExtraLeft > 0;
                    break;
                case SWT.RIGHT:
                    wExtra += getNameExtent().x + textSpacer;
                    if (plannedExtraRight > wExtra) {
                        usePlannedRight = true;
                    }
                    usePlannedLeft = plannedExtraLeft > 0;
                    break;
                default:
                    break;
            }

            switch (_verticalTextLocation) {
                case SWT.TOP:
                    yExtra -= getNameExtent().y;
                    hExtra += getNameExtent().y;
                    break;
                case SWT.CENTER:
                    if (getNameExtent().y > _height) {
                        final int diff = _height - getNameExtent().y;
                        yExtra -= (diff / 2);
                        hExtra += (diff / 2);
                    }
                    break;
                case SWT.BOTTOM:
                    hExtra += getNameExtent().y;
                    yExtra -= getNameExtent().y;
                    break;
                default:
                    break;
            }
        }

        if (_parentComposite.isShowingPlannedDates()) {
            //System.err.println(plannedExtraLeft + " " + plannedExtraRight + " " + usePlannedLeft + " " + usePlannedRight);
            if (usePlannedRight && usePlannedLeft) {
                xExtra -= plannedExtraLeft;
                wExtra = plannedExtraLeft;
                wExtra += plannedExtraRight;
            } else {
                if (usePlannedRight) {
                    wExtra = plannedExtraRight + _parentComposite.getDayWidth();
                } else if (usePlannedLeft) {
                    xExtra = -(plannedExtraLeft + _parentComposite.getDayWidth());
                    wExtra += Math.abs(plannedExtraLeft) + _parentComposite.getDayWidth();
                }
            }
        }

        // widths and horizontal
        ret.x += xExtra;
        ret.width += wExtra;

        // heights and vertical
        ret.y += yExtra;
        ret.height += hExtra;

        return ret;
    }

    // Internal method for calculating the earliest and latest dates of the scope.
    void calculateScope() {
        Calendar earliest = null;
        Calendar latest = null;

        float percentage = 0f;

        for (int i = 0; i < _scopeEvents.size(); i++) {
            final GanttEvent event = (GanttEvent) _scopeEvents.get(i);

            if (earliest == null) {
                earliest = event.getActualStartDate();
            } else {
                if (event.getActualStartDate().before(earliest)) {
                    earliest = event.getActualStartDate();
                }
            }

            if (latest == null) {
                latest = event.getActualEndDate();
            } else {
                if (event.getActualEndDate().after(latest)) {
                    latest = event.getActualEndDate();
                }
            }

            percentage += (float) event.getPercentComplete();
        }

        percentage /= (_scopeEvents.isEmpty() ? 1 : _scopeEvents.size());

        // allow start/end dates to override if we have zero events
        if (earliest == null && _startDate != null) {
            earliest = _startDate;
        }
        if (latest == null && _endDate != null) {
            latest = _endDate;
        }

        setStartDate(earliest);
        setEndDate(latest);
        setPercentComplete((int) percentage);

        updateDaysBetweenStartAndEnd();
    }

    boolean hasMovementConstraints() {
        return (_noMoveAfterDate != null || _noMoveBeforeDate != null);
    }

    // internal
    int getVisibility() {
        return _visibility;
    }

    // internal
    void setVisibility(final int visibility) {
        this._visibility = visibility;
    }

    // internal
    boolean isBoundsSet() {
        return _boundsHaveBeenSet;
    }

    void setBoundsSet(final boolean set) {
        _boundsHaveBeenSet = set;
    }

    void setHorizontalLineTopY(final int y) {
        this._horizontalLineTopY = y;
    }

    int getHorizontalLineTopY() {
        return _horizontalLineTopY;
    }

    int getHorizontalLineBottomY() {
        return _horizontalLineBottomY;
    }

    void setHorizontalLineBottomY(final int y) {
        this._horizontalLineBottomY = y;
    }

    void setNameChanged(final boolean changed) {
        this._nameChanged = changed;
    }

    boolean isNameChanged() {
        return _nameChanged;
    }

    Point getNameExtent() {
        return _nameExtent;
    }

    void setNameExtent(final Point extent) {
        this._nameExtent = extent;
    }

    String getParsedString() {
        return _parsedString;
    }

    void setParsedString(final String parsed) {
        this._parsedString = parsed;
    }

    int getDaysBetweenStartAndEnd() {
        return this._daysBetweenStartAndEnd;
    }

    private final void updateDaysBetweenStartAndEnd() {
        if (getActualStartDate() == null || getActualEndDate() == null) {
            _daysBetweenStartAndEnd = -1;
            return;
        }

        _daysBetweenStartAndEnd = (int) DateHelper.daysBetween(getActualStartDate(), getActualEndDate());

        if (_parentComposite.getCurrentView() == ISettings.VIEW_D_DAY) {
            _dDayStart = (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), getStartDate());
            _dDayEnd = (int) DateHelper.daysBetween(_parentComposite.getDDayCalendar(), getEndDate());
            _dDayStart--;
        }

    }

    void moveStarted(final int moveType) {
        if (_moving) { return; }

        _moveType = moveType;

        if (_startDate != null) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(_startDate.getTime());
            _preMoveDateEstiStart = cal;
        } else {
        	_preMoveDateEstiStart = null;
        }
        if (_endDate != null) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(_endDate.getTime());
            _preMoveDateEstiEnd = cal;
        } else {
        	_preMoveDateEstiEnd = null;
        }
        if (_revisedStart != null) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(_revisedStart.getTime());
            _preMoveDateRevisedStart = cal;
        } else {
        	_preMoveDateRevisedStart = null;
        }
        if (_revisedEnd != null) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(_revisedEnd.getTime());
            _preMoveDateRevisedEnd = cal;
        } else {
        	_preMoveDateRevisedEnd = null;
        }

        _preMoveBounds = new Rectangle(_x, _y, _width, _height);

        _preMoveGanttSectionIndex = _parentComposite.getGanttSections().indexOf(_ganttSection);
        if (_ganttSection != null) {
            _preMoveGanttSectionEventLocationIndex = _ganttSection.getEvents().indexOf(this);
        }
        else {
        	_preMoveGanttSectionEventLocationIndex = _parentComposite.getEvents().indexOf(this);
        }

        _moving = true;
    }

    /**
     * Call after a move/resize is done to get the command to undo/redo the latest move/resize
     * 
     * @return Command or null if none of the given move events matched
     */
    IUndoRedoCommand getPostMoveOrResizeUndoCommand() {
        switch (_moveType) {
            case Constants.TYPE_MOVE:
                int indexNow = 0;
                if (_ganttSection != null) {
                    indexNow = _ganttSection.getEvents().indexOf(this);
                }
                else {
                	indexNow = _parentComposite.getEvents().indexOf(this);
                }

                GanttSection gs = null;
                if (_preMoveGanttSectionIndex > -1 
                		&& _preMoveGanttSectionIndex < _parentComposite.getGanttSections().size() ) {
                	gs = (GanttSection) _parentComposite.getGanttSections().get(_preMoveGanttSectionIndex);
                }
                
                return new EventMoveCommand(this, _preMoveDateEstiStart, _startDate, _preMoveDateEstiEnd, _endDate, _preMoveDateRevisedStart, _revisedStart, _preMoveDateRevisedEnd, _revisedEnd, gs, _ganttSection,
                        _preMoveGanttSectionEventLocationIndex, indexNow);

            case Constants.TYPE_RESIZE_LEFT:
            case Constants.TYPE_RESIZE_RIGHT:
                return new EventResizeCommand(this, _preMoveDateEstiStart, _startDate, _preMoveDateEstiEnd, _endDate, _preMoveDateRevisedStart, _revisedStart, _preMoveDateRevisedEnd, _revisedEnd);
            default:
                break;
        }

        return null;
    }

    void moveCancelled() {
        _moving = false;
        _startDate = _preMoveDateEstiStart;
        _endDate = _preMoveDateEstiEnd;
        _revisedStart = _preMoveDateRevisedStart;
        _revisedEnd = _preMoveDateRevisedEnd;
        if (_preMoveBounds != null) {
            _x = _preMoveBounds.x;
            _y = _preMoveBounds.y;
            _width = _preMoveBounds.width;
            _height = _preMoveBounds.height;
        }
    }

    void moveFinished() {
        _moving = false;
    }

    /**
     * Returns the scope parent of this event if it has one.
     * 
     * @return Scope parent
     */
    public GanttEvent getScopeParent() {
        return _scopeParent;
    }

    // not external as it's the wrong way to set a scope parent
    void setScopeParent(final GanttEvent parent) {
        _scopeParent = parent;
    }

    void setEarliestStartX(final int x) {
        _earliestStartX = x;
    }

    void setLatestEndX(final int x) {
        _latestEndX = x;
    }

    int getEarliestStartX() {
        return _earliestStartX;
    }

    int getLatestEndX() {
        return _latestEndX;
    }

    int getActualWidth() {
        return _actualWidth;
    }

    void updateActualWidth() {
        _actualWidth = Math.abs(Math.abs(_latestEndX) - Math.abs(_earliestStartX));
    }

    void flagDragging() {
        _savedVerticalDragY = _y;
        _preVerticalDragBounds = new Rectangle(_x, _y, _width, _height);
    }

    Rectangle getPreVerticalDragBounds() {
        return _preVerticalDragBounds;
    }

    void undoVerticalDragging() {
        _y = _savedVerticalDragY;
        _preVerticalDragBounds = null;
    }
    
    boolean wasVerticallyMovedUp() {
    	if (_preVerticalDragBounds == null) {
    		return false;
    	}
        return (_y < _preVerticalDragBounds.y);
    }

    boolean hasMovedVertically() {
        return _y != _savedVerticalDragY;
    }

    /**
     * Reparents this event from the current {@link GanttSection} to a new {@link GanttSection}
     * 
     * @param index index to put event at in new section
     * @param newSection new section to put event in
     */
    public void reparentToNewGanttSection(final int index, final GanttSection newSection) {
        if (_ganttSection != null) {
            _ganttSection.removeGanttEvent(this);
            newSection.addGanttEvent(index, this);
        }
    }
    
    public String toString() {
        return _name;
    }

    /**
     * Returns the parent {@link GanttChart}
     * 
     * @return {@link GanttChart}
     */
    public GanttChart getParentChart() {
        return _parentChart;
    }

    /**
     * Returns the parent {@link GanttComposite}
     * 
     * @return {@link GanttComposite}
     */
    public GanttComposite getParentComposite() {
        return _parentComposite;
    }
    

	/**
	 * Will update planned dates without question or internal checks. This is
	 * used internally, it's not recommended to be used externally.
	 * 
	 * @param start
	 *            Date or null
	 * @param end
	 *            Date or null
	 */
	public void setNoUpdatePlannedDates(Calendar start, Calendar end) {
		_startDate = start == null ? null : (Calendar) start.clone();
		_endDate = end == null ? null : (Calendar) end.clone();
		updateDaysBetweenStartAndEnd();
	}

	/**
	 * Will update revised dates without question or internal checks. This is
	 * used internally, it's not recommended to be used externally.
	 * 
	 * @param start
	 *            Date or null
	 * @param end
	 *            Date or null
	 */
	public void setNoUpdateRevisedDates(Calendar start, Calendar end) {
		_revisedStart = start == null ? null : (Calendar) start.clone();
		_revisedEnd = end == null ? null : (Calendar) end.clone();
		updateDaysBetweenStartAndEnd();
	}

    /**
     * Clones the GanttEvent and all objects of it (assuming they are cloneable). The clone should be exactly like the
     * original except for a possible few internal flags. The clone does not need to be re-added to the chart, but if it
     * needs to exist below a certain parent it will need to be re-added to that parent (such as a scope).
     */
    public Object clone() throws CloneNotSupportedException { // NOPMD
        final GanttEvent clone = new GanttEvent(_parentChart, getName());
        if (_endDate != null) {
            clone._endDate = (Calendar) _endDate.clone();
        }
        if (_noMoveAfterDate != null) {
            clone._noMoveAfterDate = (Calendar) _noMoveAfterDate.clone();
        }
        if (_noMoveBeforeDate != null) {
            clone._noMoveBeforeDate = (Calendar) _noMoveBeforeDate.clone();
        }
        if (_preMoveDateEstiStart != null) {
            clone._preMoveDateEstiEnd = (Calendar) _preMoveDateEstiEnd.clone();
        }
        if (_preMoveDateEstiStart != null) {
            clone._preMoveDateEstiStart = (Calendar) _preMoveDateEstiStart.clone();
        }
        if (_preMoveDateRevisedEnd != null) {
            clone._preMoveDateRevisedEnd = (Calendar) _preMoveDateRevisedEnd.clone();
        }
        if (_preMoveDateRevisedStart != null) {
            clone._preMoveDateRevisedStart = (Calendar) _preMoveDateRevisedStart.clone();
        }
        if (_revisedEnd != null) {
            clone._revisedEnd = (Calendar) _revisedEnd.clone();
        }
        if (_revisedStart != null) {
            clone._revisedStart = (Calendar) _revisedStart.clone();
        }
        clone._scopeParent = _scopeParent;
        clone._scopeEvents = new ArrayList(_scopeEvents);
        if (_startDate != null) {
            clone._startDate = (Calendar) _startDate.clone();
        }
        clone._advancedTooltip = _advancedTooltip;
        clone._boundsHaveBeenSet = _boundsHaveBeenSet;
        clone._checkpoint = _checkpoint;
        clone._data = _data;
        clone._daysBetweenStartAndEnd = _daysBetweenStartAndEnd;
        clone._dDayEnd = _dDayEnd;
        clone._dDayStart = _dDayStart;
        clone._fixedRowHeight = _fixedRowHeight;
        clone._ganttGroup = _ganttGroup;
        clone._gradientStatusColor = _gradientStatusColor;
        clone._hidden = _hidden;
        clone._horizontalLineBottomY = _horizontalLineBottomY;
        clone._horizontalLineTopY = _horizontalLineTopY;
        clone._horizontalTextLocation = _horizontalTextLocation;
        clone._image = _image;
        clone._locked = _locked;
        clone._menu = _menu;
        clone._moveable = _moveable;
        // it's 100% sure a clone is not being moved, we'd rather not have some funky result of matching this variable
        clone._moving = false;
        clone._nameChanged = _nameChanged;
        clone._nameExtent = _nameExtent;
        clone._parsedString = _parsedString;
        clone._percentComplete = _percentComplete;
        clone._picture = _picture;
        clone._preMoveBounds = _preMoveBounds;
        clone._resizable = _resizable;
        clone._scope = _scope;
        clone._scopeEvents = new ArrayList(_scopeEvents);
        clone._scopeParent = _scopeParent;
        clone._showBoldText = _showBoldText;
        clone._statusColor = _statusColor;
        clone._textDisplayFormat = _textDisplayFormat;
        clone._textFont = _textFont;
        clone._verticalEventAlignment = _verticalEventAlignment;
        clone._verticalTextLocation = _verticalTextLocation;
        clone._visibility = _visibility;
        clone._widthWithtText = _widthWithtText;
        clone._latestEndX = _latestEndX;
        clone._earliestStartX = _earliestStartX;
        clone._actualWidth = _actualWidth;
        clone._savedVerticalDragY = _savedVerticalDragY;
        return clone;
    }

	/**
	 * @return the _showText
	 */
	public boolean isShowText() {
		return _showText;
	}

	/**
	 * @param _showText the _showText to set
	 */
	public void setShowText(boolean _showText) {
		this._showText = _showText;
	}

}
