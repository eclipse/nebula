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

package org.eclipse.nebula.widgets.ganttchart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;

/**
 * One GanttEvent represents one "active" object int the GANTT chart.
 * <p>
 * This object can take many shapes, here is a list of a few:<br>
 * <ul>
 * <li>Normal event
 * <li>Checkpoint event
 * <li>Scope event
 * <li>Image event
 * </ul>
 * The event may also take revised start and end dates, and can be modified individually to be locked, unmoveable,
 * unresizable and much more.
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
 * // the first parameter is the data object, as this is an example, we'll leave it null<br>
 * // we're also setting the percentage complete to 50%<br>
 * GanttEvent ganttEvent = new GanttEvent(ganttChart, "Event Name", cStartDate, cEndDate, 50);
 * <br><br>
 * </code> <br>
 * <br>
 * This class may not be subclassed.
 * 
 * @author Emil Crumhorn <a href="mailto:emil.crumhorn@gmail.com">emil.crumhorn@gmail.com</a>
 */
public class GanttEvent extends AbstractGanttEvent implements IGanttChartItem, Cloneable {

    /*
     * public static final int TYPE_EVENT = 0; public static final int TYPE_CHECKPOINT = 1; public static final int TYPE_IMAGE = 2; public static final int TYPE_SCOPE = 3;
     */
    public static final int FIXED_ROW_HEIGHT_AUTOMATIC = -1;

    private Object          mData;
    private String          mName;
    private Calendar        mRevisedStart;
    private Calendar        mRevisedEnd;
    private Calendar        mStartDate;
    private Calendar        mEndDate;
    private int             mPercentComplete;
    private boolean         mCheckpoint;
    private boolean         mScope;
    private boolean         mLocked;
    private boolean         mImage;
    private boolean         mResizable                 = true;
    private boolean         mMoveable                  = true;
    private int             x, y, width, height;
    private int             mEarliestStartX, mLatestEndX, mActualWidth;
    private Color           mStatusColor;
    private Color           mGradientStatusColor;
    private boolean         mShowBoldText;
    private String          mTextDisplayFormat;
    private List            mScopeEvents;
    private Image           mPicture;
    private Menu            mMenu;
    private GanttChart      mParentChart;
    private GanttComposite  mParentComposite;
    private GanttGroup      mGanttGroup;
    private GanttSection    mGanttSection;
    private boolean         mHidden;
    private int             mWidthWithtText;
    // TODO: Implement. Less constructors, more user power.
    // private int mEventType = TYPE_EVENT;
    private AdvancedTooltip mAdvancedTooltip;
    private int             mVisibility;
    private boolean         mBoundsHaveBeenSet;

    private int             mFixedRowHeight            = FIXED_ROW_HEIGHT_AUTOMATIC;
    private int             mVerticalEventAlignment    = SWT.TOP;

    private int             mHorizontalLineTopY;
    private int             mHorizontalLineBottomY;

    private Calendar        mNoMoveBeforeDate;
    private Calendar        mNoMoveAfterDate;

    // private items
    private boolean         mNameChanged               = true;
    private Point           mNameExtent;
    private String          mParsedString;

    private int             mHorizontalTextLocation    = SWT.RIGHT;
    private int             mVerticalTextLocation      = SWT.CENTER;

    private Font            mTextFont;

    private int             mDaysBetweenStartAndEnd;

    // cloned holders used for cancelling a move/resize via ESC
    private Calendar        mPreMoveDateEstiStart;
    private Calendar        mPreMoveDateEstiEnd;
    private Calendar        mPreMoveDateRevisedStart;
    private Calendar        mPreMoveDateRevisedEnd;
    private Rectangle       mPreMoveBounds;
    private boolean         mMoving;

    private GanttEvent      mScopeParent;

    private int             mDDayStart;
    private int             mDDayEnd;

    private int             mSavedVerticalDragY;

    private Rectangle       mPreVerticalDragBounds;

    /**
     * Creates a new GanttEvent.
     * 
     * @param parent Parent chart
     * @param name Name of event
     * @param startDate Start date
     * @param endDate End date
     * @param percentComplete Percent complete
     */
    public GanttEvent(GanttChart parent, String name, Calendar startDate, Calendar endDate, int percentComplete) {
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
    public GanttEvent(GanttChart parent, Object data, String name, Calendar startDate, Calendar endDate, int percentComplete) {
        this.mParentChart = parent;
        this.mParentComposite = mParentChart.getGanttComposite();
        this.mData = data;
        this.mName = name;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
        this.mPercentComplete = percentComplete;
        init();
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
    public GanttEvent(GanttChart parent, String name, Calendar startDate, Calendar endDate, Calendar revisedStart, Calendar revisedEnd, int percentComplete) {
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
    public GanttEvent(GanttChart parent, Object data, String name, Calendar startDate, Calendar endDate, Calendar revisedStart, Calendar revisedEnd, int percentComplete) {
        this.mParentChart = parent;
        this.mParentComposite = mParentChart.getGanttComposite();
        this.mData = data;
        this.mName = name;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
        this.mRevisedStart = revisedStart;
        this.mRevisedEnd = revisedEnd;
        this.mPercentComplete = percentComplete;
        init();
    }

    /**
     * D-day event creation.
     * 
     * @param parent Parent chart
     * @param dDayStart D day start value (zero based)
     * @param dDayEnd D day end value (zero based)
     */
    public GanttEvent(GanttChart parent, int dDayStart, int dDayEnd) {
        this.mParentChart = parent;
        this.mParentComposite = mParentChart.getGanttComposite();
        this.mDDayStart = dDayStart;
        this.mDDayEnd = dDayEnd;

        mStartDate = mParentChart.getGanttComposite().getDDayCalendar();
        mEndDate = mParentChart.getGanttComposite().getDDayCalendar();
        mStartDate.add(Calendar.DATE, mDDayStart);
        mEndDate.add(Calendar.DATE, mDDayEnd);

        init();
    }

    /**
     * Creates a GanttEvent intended to be a scope.
     * 
     * @param parent Chart parent
     * @param name Name of scope
     */
    public GanttEvent(GanttChart parent, String name) {
        this(parent, null, name);
    }

    /**
     * Creates a GanttEvent intended to be a scope.
     * 
     * @param parent Chart parent
     * @param data Data object
     * @param name Name of scope
     */
    public GanttEvent(GanttChart parent, Object data, String name) {
        this.mParentChart = parent;
        this.mParentComposite = mParentChart.getGanttComposite();
        this.mData = data;
        this.mName = name;
        this.mScope = true;
        try {
            init();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Creates a GanttEvent intended to be a checkpoint.
     * 
     * @param parent Chart parent
     * @param name Name of checkpoint
     * @param date Checkpoint start (and end) date
     */
    public GanttEvent(GanttChart parent, String name, Calendar date) {
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
    public GanttEvent(GanttChart parent, Object data, String name, Calendar date) {
        this.mParentChart = parent;
        this.mParentComposite = mParentChart.getGanttComposite();
        this.mData = data;
        this.mName = name;
        this.mStartDate = date;
        this.mEndDate = date;
        this.mCheckpoint = true;
        try {
            init();
        } catch (Exception err) {
            err.printStackTrace();
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
    public GanttEvent(GanttChart parent, String name, Calendar date, Image picture) {
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
    public GanttEvent(GanttChart parent, Object data, String name, Calendar date, Image picture) {
        this.mParentChart = parent;
        this.mParentComposite = mParentChart.getGanttComposite();
        this.mData = data;
        this.mName = name;
        this.mStartDate = date;
        this.mEndDate = date;
        this.mPicture = picture;
        this.mImage = true;
        try {
            init();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    protected final void init() {
        mScopeEvents = new ArrayList();
        mParentComposite.addEvent(this, true);

        updateDaysBetweenStartAndEnd();
    }

    /**
     * Returns the currently set data object.
     * 
     * @return Data object
     */
    public Object getData() {
        return mData;
    }

    /**
     * Sets the current data object.
     * 
     * @param data Data object
     */
    public void setData(Object data) {
        this.mData = data;
    }

    /**
     * Returns the display name of this event.
     * 
     * @return Display name
     */
    public String getName() {
        return this.mName;
    }

    /**
     * Sets the display name of this event.
     * 
     * @param name Display name
     */
    public void setName(String name) {
        this.mName = name;
        mNameChanged = true;
    }

    /**
     * Returns the start date of this event.
     * 
     * @return Start date
     */
    public Calendar getStartDate() {
        if (mStartDate == null) return null;

        return (Calendar) mStartDate.clone();
    }

    /**
     * Returns the revised start date if set, or the start date if not.
     * 
     * @return Start date or null
     */
    public Calendar getActualStartDate() {
        Calendar ret = mRevisedStart != null ? mRevisedStart : mStartDate;
        return ret == null ? null : (Calendar) ret.clone();
    }

    /**
     * Returns the revised end date if set, or the end date if not.
     * 
     * @return End date or null
     */
    public Calendar getActualEndDate() {
        Calendar ret = mRevisedEnd != null ? mRevisedEnd : mEndDate;
        return ret == null ? null : (Calendar) ret.clone();
    }

    /**
     * Returns whatever is the earliest calendar of the start date and the actual start date. If any of them are null,
     * whichever has a calendar is returned. If both are null, null is returned.
     * 
     * @return Earliest start date
     */
    public Calendar getEarliestStartDate() {
        if (mRevisedStart == null && mStartDate == null) { return null; }

        if (mRevisedStart == null) { return mStartDate; }
        if (mStartDate == null) { return mRevisedStart; }

        return mStartDate.before(mRevisedStart) ? mStartDate : mRevisedStart;
    }

    /**
     * Returns whatever is the latest calendar of the end date and the actual end date. If any of them are null,
     * whichever has a calendar is returned. If both are null, null is returned.
     * 
     * @return Latest end date
     */
    public Calendar getLatestEndDate() {
        if (mRevisedEnd == null && mEndDate == null) { return null; }

        if (mRevisedEnd == null) {
            return mEndDate;
        } else if (mEndDate == null) { return mRevisedEnd; }

        return mEndDate.after(mRevisedEnd) ? mEndDate : mRevisedEnd;
    }

    /**
     * Sets the end date of this event.
     * 
     * @param startDate Start date
     */
    public void setStartDate(Calendar startDate) {
        if (mNoMoveBeforeDate != null) {
            if (startDate.before(mNoMoveBeforeDate)) return;
        }

        if (getActualEndDate() != null) {
            if (startDate.after(getActualEndDate())) startDate = (Calendar) getActualEndDate().clone();
        }

        this.mStartDate = (Calendar) startDate.clone();
        updateDaysBetweenStartAndEnd();
    }

    /**
     * Forces the chart to recognize that something within this event has changed and that it needs an update. This
     * method will cause a redraw if told to redraw.
     * 
     * @param redraw if to redraw the chart after notifying of changes.
     */
    public void update(boolean redraw) {
        mParentComposite.eventDatesChanged(this, redraw);
    }

    /**
     * Returns the end date of this event.
     * 
     * @return End date
     */
    public Calendar getEndDate() {
        if (mEndDate == null) return null;

        return (Calendar) mEndDate.clone();
    }

    /**
     * Sets the end date of this event.
     * 
     * @param endDate End date
     */
    public void setEndDate(Calendar endDate) {
        if (mNoMoveAfterDate != null) {
            if (endDate.after(mNoMoveAfterDate)) return;
        }

        if (getActualStartDate() != null) {
            if (endDate.before(getActualStartDate())) endDate = (Calendar) getActualStartDate().clone();
        }

        this.mEndDate = (Calendar) endDate.clone();
        updateDaysBetweenStartAndEnd();
    }

    /**
     * Returns the percentage complete of this event.
     * 
     * @return Percentage complete
     */
    public int getPercentComplete() {
        return mPercentComplete;
    }

    /**
     * Sets the percentage complete of this event.
     * 
     * @param percentComplete Percentage complete
     */
    public void setPercentComplete(int percentComplete) {
        this.mPercentComplete = percentComplete;
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
        if (mMenu == null) {
            mMenu = new Menu(mParentComposite);
        }

        return mMenu;
    }

    /**
     * Sets the bounds of the event. This is done internally.
     * 
     * @param x x location
     * @param y y location
     * @param width width of event
     * @param height height of event
     */
    void setBounds(int x, int y, int width, int height) {
        mBoundsHaveBeenSet = true;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the bounds of the event.
     * 
     * @param bounds New bounds
     */
    void setBounds(Rectangle bounds) {
        mBoundsHaveBeenSet = true;
        this.x = bounds.x;
        this.y = bounds.y;
        this.width = bounds.width;
        this.height = bounds.height;
        if (mName.indexOf("2") > -1)
        //System.err.println("Updatebounds " + mName + " " + getBounds());
        if (getEarliestStartDate() != null) {
            setEarliestStartX(mParentComposite.getStartingXfor(getEarliestStartDate()));
        }
        if (getLatestEndDate() != null) {
            setLatestEndX(mParentComposite.getXForDate(getLatestEndDate()) + mParentComposite.getDayWidth());
        }
    }

    void updateX(int x) {
        this.x = x;

        updateOtherXs();
    }

    private void updateOtherXs() {
        if (getEarliestStartDate() != null) {
            setEarliestStartX(mParentComposite.getStartingXfor(getEarliestStartDate()));
        }
        if (getLatestEndDate() != null) {
            setLatestEndX(mParentComposite.getXForDate(getLatestEndDate()) + mParentComposite.getDayWidth());
        }
    }

    void updateY(int y) {
        this.y = y;
    }

    void updateHeight(int height) {
        this.height = height;
    }

    void updateWidth(int width) {
        this.width = width;
    }

    /**
     * Whether this is a checkpoint or not.
     * 
     * @return true if this is a checkpoint
     */
    public boolean isCheckpoint() {
        return mCheckpoint;
    }

    /**
     * Sets the event to be a checkpoint.
     * 
     * @param checkpoint true if the event should be a checkpoint
     */
    public void setCheckpoint(boolean checkpoint) {
        this.mScope = false;
        this.mImage = false;
        this.mCheckpoint = checkpoint;
    }

    /**
     * Returns the x location of this event.
     * 
     * @return x location
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the ending x location for this event.
     * 
     * @return x end location
     */
    public int getXEnd() {
        return x + width;
    }

    /**
     * Returns the y location of this event.
     * 
     * @return y location
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the bottom y of this event.
     * 
     * @return y bottom
     */
    public int getBottomY() {
        return y + height;
    }

    /**
     * Returns the width of this event.
     * 
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of this event.
     * 
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the revised start date of this event.
     * 
     * @return Revised date
     */
    public Calendar getRevisedStart() {
        if (mRevisedStart == null) return null;

        return (Calendar) mRevisedStart.clone();
    }

    /**
     * Sets the revised start date of this event.
     * 
     * @param revisedStart
     */
    public void setRevisedStart(Calendar revisedStart) {
        setRevisedDates(revisedStart, null);
    }

    /**
     * Returns the revised end date of this event.
     * 
     * @return revised end date
     */
    public Calendar getRevisedEnd() {
        if (mRevisedEnd == null) return null;

        return (Calendar) mRevisedEnd.clone();
    }

    /**
     * Sets the revised end date of this event.
     * 
     * @param revisedEnd Revised end date
     */
    public void setRevisedEnd(Calendar revisedEnd) {
        setRevisedDates(null, revisedEnd);
    }

    /**
     * Sets the revised D-day start.
     * 
     * @param dDayStart d-day start.
     */
    public void setRevisedStart(int dDayStart) {
        mRevisedStart = mParentComposite.getDDayCalendar();
        mRevisedStart.add(Calendar.DATE, dDayStart);
    }

    /**
     * Sets the revised D-day end.
     * 
     * @param dDayEnd d-day end
     */
    public void setRevisedEnd(int dDayEnd) {
        mRevisedEnd = mParentComposite.getDDayCalendar();
        mRevisedEnd.add(Calendar.DATE, dDayEnd);
    }

    /**
     * Sets new revised dates. This is useful when you need to update two dates that move at the same time (such as manually doing a move via setDates). Normally each setting of a
     * date would check it against its start date or end date to make sure it doesn't overlap. This does too, but at the same time, thus, no oddity in movement will appear. This is
     * rather difficult to explain, but if you experience event-length changes when using individual start and end date sets that appear at the same time, you probably want to use
     * this method instead. <p> Either parameter may be null to set just one, but both may not be null
     * 
     * @param revisedStart New revised Start date
     * @param revisedEnd New revised End date
     */
    private void setRevisedDates(Calendar revisedStart, Calendar revisedEnd) {
        if (revisedStart == null && revisedEnd == null) return;

        if (revisedStart != null && getActualEndDate() != null) {
            if (revisedStart.after(getActualEndDate())) return;
        }

        if (revisedEnd != null && getActualStartDate() != null) {
            if (revisedEnd.before(getActualStartDate())) // || revisedEnd.equals(revisedStart))
            return;
        }

        if (revisedStart != null) mRevisedStart = (Calendar) revisedStart.clone();

        if (revisedEnd != null) mRevisedEnd = (Calendar) revisedEnd.clone();

        // check movement constraints
        if (mNoMoveBeforeDate != null && revisedStart != null) {
            if (revisedStart.before(mNoMoveBeforeDate)) mRevisedStart = (Calendar) mNoMoveBeforeDate.clone();
        }
        if (mNoMoveAfterDate != null && revisedEnd != null) {
            if (revisedEnd.after(mNoMoveAfterDate)) mRevisedEnd = (Calendar) mNoMoveAfterDate.clone();
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
    public void setRevisedStart(Calendar revisedStart, boolean validate) {
        if (validate) setRevisedStart(revisedStart);
        else {
            mRevisedStart = (Calendar) revisedStart.clone();
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
    public void setRevisedEnd(Calendar revisedEnd, boolean validate) {
        if (validate) setRevisedEnd(revisedEnd);
        else {
            mRevisedEnd = (Calendar) revisedEnd.clone();
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
    public void setRevisedDates(Calendar revisedStart, Calendar revisedEnd, int order) {
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
        return mLocked;
    }

    /**
     * Sets whether this event is locked or not.
     * 
     * @param locked true if locked
     */
    public void setLocked(boolean locked) {
        this.mLocked = locked;
    }

    /**
     * Returns the status color used as part of the gradient colors used to draw the event.
     * 
     * @return Gradient color
     */
    public Color getGradientStatusColor() {
        return mGradientStatusColor;
    }

    /**
     * Sets the status color used as part of the gradient colors used to draw the event.
     * 
     * @param gradientStatusColor Gradient color
     */
    public void setGradientStatusColor(Color gradientStatusColor) {
        mGradientStatusColor = gradientStatusColor;
    }

    /**
     * Returns the status color of this event. This is also used for drawing gradients.
     * 
     * @return Status color
     */
    public Color getStatusColor() {
        return mStatusColor;
    }

    /**
     * Sets the status color of this event. This is also used for drawing gradients.
     * 
     * @param statusColor Status color
     */
    public void setStatusColor(Color statusColor) {
        mStatusColor = statusColor;
    }

    /**
     * Whether to show the display name in bold text or not.
     * 
     * @return true if to show bold text
     */
    public boolean showBoldText() {
        return mShowBoldText;
    }

    /**
     * Sets whether to show the display name in bold text or not.
     * 
     * @param showBoldText Set to true if to show bold text
     */
    public void setShowBoldText(boolean showBoldText) {
        mShowBoldText = showBoldText;
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
        return mTextDisplayFormat;
    }

    /**
     * Sets the text display format of this event. Please see ISettings.getTextDisplayFormat() for an overview.
     * 
     * @see ISettings#getTextDisplayFormat()
     */
    public void setTextDisplayFormat(String format) {
        mTextDisplayFormat = format;
    }

    /**
     * Sets this event to be a scope. Don't forget to add events that the scope is supposed to encompass with
     * {@link #addScopeEvent(GanttEvent)}.
     * 
     * @param scope
     * @see #addScopeEvent(GanttEvent)
     */
    public void setScope(boolean scope) {
        mCheckpoint = false;
        mImage = false;
        mScope = scope;

        if (!scope) {
            for (int i = 0; i < mScopeEvents.size(); i++) {
                ((GanttEvent) mScopeEvents.get(i)).setScopeParent(null);
            }

            mScopeEvents.clear();
        }
    }

    /**
     * Returns whether this is a scope or not.
     * 
     * @return true if scope
     */
    public boolean isScope() {
        return mScope;
    }

    /**
     * Adds an event that this event should cover if it has been set to be a scope.
     * 
     * @param event GanttEvent to encompass
     */
    public void addScopeEvent(GanttEvent event) {
        if (event == this) {
            return;
        }

        if (mScopeEvents.contains(event)) {
            return;
        }

        mScopeEvents.add(event);

        event.setScopeParent(this);
    }

    /**
     * Removes event that this event encompasses as a scope.
     * 
     * @param event GanttEvent
     */
    public void removeScopeEvent(GanttEvent event) {
        mScopeEvents.remove(event);
    }

    /**
     * Returns the list of GanttEvents that the scope encompasses.
     * 
     * @return List of events
     */
    public List getScopeEvents() {
        return mScopeEvents;
    }

    private GanttEvent getEarliestOrLatestScopeEvent(boolean earliest) {
        Calendar ret = null;
        GanttEvent retEvent = null;

        for (int i = 0; i < mScopeEvents.size(); i++) {
            GanttEvent ge = (GanttEvent) mScopeEvents.get(i);
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
        if (!isScope() || mScopeEvents.size() == 0) {
            return null;
        }

        return getEarliestOrLatestScopeEvent(true);
    }

    /**
     * Returns the event that has the latest date of all events in a scope.
     * 
     * @return Latest event or null if none
     */
    public GanttEvent getLatestScopeEvent() {
        if (!isScope() || mScopeEvents.size() == 0) {
            return null;
        }

        return getEarliestOrLatestScopeEvent(false);
    }

    /**
     * Returns the picture image for this event.
     * 
     * @return Picture
     */
    public Image getPicture() {
        return mPicture;
    }

    /**
     * Sets the picture image for this event.
     * 
     * @param picture Image picture
     */
    public void setPicture(Image picture) {
        this.mPicture = picture;
    }

    /**
     * Sets this event to be represented by an image.
     * 
     * @param image true if it's an image
     */
    public void setImage(boolean image) {
        mCheckpoint = false;
        mScope = false;

        this.mImage = image;
    }

    /**
     * Returns whether this event is an image representation or not.
     * 
     * @return true if this event is an image representation
     */
    public boolean isImage() {
        return mImage;
    }

    /**
     * Returns what GanttGroup this event belongs to. Grouped events are drawn on the same line. Set null for no group.
     * 
     * @return GanttGroup
     */
    public GanttGroup getGanttGroup() {
        return mGanttGroup;
    }        

    /**
     * Sets what group this event belongs to. Grouped events are drawn on the same line.
     * 
     * @param group GanttGroup or null if none
     */
    public void setGanttGroup(GanttGroup group) {
        mGanttGroup = group;
    }
       
    /**
     * Returns the {@link GanttSection} that this event belongs to, or null if none.
     * 
     * @return Parent {@link GanttSection}
     */
    public GanttSection getGanttSection() {
        return mGanttSection;
    }

    /**
     * Sets the GanttSection that this event belongs to
     * 
     * @param ganttSection
     */
    void setGanttSection(GanttSection ganttSection) {
        mGanttSection = ganttSection;
    }

    /**
     * Sets if this event should be hidden or not. Hidden events are not shown on the chart and the space they normally
     * would occupy is free.
     * 
     * @param hidden true to hide event.
     */
    public void setHidden(boolean hidden) {
        mHidden = hidden;
    }

    /**
     * Returns whether this event is hidden or not. Hidden events are not shown on the chart and the space they normally
     * would occupy is free.
     * 
     * @return true if hidden.
     */
    public boolean isHidden() {
        return mHidden;
    }

    private void internalSetAllChildrenHidden(boolean hidden) {
        if (mScopeEvents == null) {
            return;
        }

        for (int i = 0; i < mScopeEvents.size(); i++) {
            ((GanttEvent) mScopeEvents.get(i)).setHidden(hidden);
        }
    }

    /**
     * Hides all children from view. Children only exist on Scoped events.
     * 
     * @see #isHidden()
     */
    public void hideAllChildren() {
        internalSetAllChildrenHidden(true);
        mParentComposite.updateHorizontalScrollbar();
        mParentComposite.updateVerticalScrollBar(false);

        mParentComposite.heavyRedraw();
    }

    /**
     * Un-hides all children from view. Children only exist on Scoped events.
     * 
     * @see #isHidden()
     */
    public void showAllChildren() {
        internalSetAllChildrenHidden(false);
        mParentComposite.updateHorizontalScrollbar();
        mParentComposite.updateVerticalScrollBar(false);

        mParentComposite.heavyRedraw();
    }

    /**
     * Returns whether all children are hidden or not. Children only exist on Scoped events.
     * 
     * @return true if all children are hidden.
     */
    public boolean isChildrenHidden() {
        if (mScopeEvents == null) return false;

        for (int i = 0; i < mScopeEvents.size(); i++) {
            if (!((GanttEvent) mScopeEvents.get(i)).isHidden()) return false;
        }

        return true;
    }

    /**
     * Returns the full right most x value if event has text or images drawn after it.
     * 
     * @return x position
     */
    public int getWidthWithText() {
        return mWidthWithtText;
    }

    /**
     * Sets the full width of the event width and the text and images that come after it.
     * 
     * @param width x width
     */
    public void setWidthWithText(int width) {
        this.mWidthWithtText = width;
    }

    /**
     * Returns the currently set Advanced tooltip, or null if none is set.
     * 
     * @return Advanced tooltip
     */
    public AdvancedTooltip getAdvancedTooltip() {
        return mAdvancedTooltip;
    }

    /**
     * Sets the advanced tooltip. If none is set, a default tooltip will be used.
     * 
     * @param advancedTooltip
     */
    public void setAdvancedTooltip(AdvancedTooltip advancedTooltip) {
        this.mAdvancedTooltip = advancedTooltip;
    }

    /**
     * Returns the fixed row height in pixels.
     * 
     * @return Fixed row height
     */
    public int getFixedRowHeight() {
        return mFixedRowHeight;
    }

    /**
     * Sets the fixed row height in pixels.
     * 
     * @param fixedRowHeight Fixed row height
     */
    public void setFixedRowHeight(int fixedRowHeight) {
        this.mFixedRowHeight = fixedRowHeight;
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
        return mVerticalEventAlignment;
    }

    /**
     * Sets the vertical alignment for this event. Alignments are only valid if the row height is a fixed value.
     * 
     * @param verticalEventAlignment one of <code>SWT.TOP</code>, <code>SWT.CENTER</code>, <code>SWT.BOTTOM</code>
     */
    public void setVerticalEventAlignment(int verticalEventAlignment) {
        this.mVerticalEventAlignment = verticalEventAlignment;
    }

    /**
     * Whether this event is resizable. If resizing is turned off in the settings this method does nothing.
     * 
     * @return true if resizable. Default is true.
     */
    public boolean isResizable() {
        return mResizable;
    }

    /**
     * Sets this event to be resizable or not. If resizing is turned off in the settings this method does nothing.
     * 
     * @param resizable true to make event resizable.
     */
    public void setResizable(boolean resizable) {
        mResizable = resizable;
    }

    /**
     * Whether this event is moveable. If moving is turned off in the settings this method does nothing.
     * 
     * @return true if moveable. Default is true.
     */
    public boolean isMoveable() {
        return mMoveable;
    }

    /**
     * Sets this event to be moveable or not. If moving is turned off in the settings this method does nothing.
     * 
     * @param resizable true to make event moveable.
     */
    public void setMoveable(boolean moveable) {
        mMoveable = moveable;
    }

    /**
     * Returns the date prior to which no moves or resizes are allowed. Any events moved towards or across the date mark
     * will stop at this mark.
     * 
     * @return Calendar or null.
     */
    public Calendar getNoMoveBeforeDate() {
        return mNoMoveBeforeDate;
    }

    /**
     * Sets the date prior to which no moves or resizes are allowed. Any events moved towards or across the date mark
     * will stop at this mark.
     * 
     * @param noMoveBeforeDate Calendar or null.
     */
    public void setNoMoveBeforeDate(Calendar noMoveBeforeDate) {
        mNoMoveBeforeDate = noMoveBeforeDate;
    }

    /**
     * Returns the date after which no moves or resizes are allowed. Any events moved towards or across the date mark
     * will stop at this mark.
     * 
     * @return Calendar or null.
     */
    public Calendar getNoMoveAfterDate() {
        return mNoMoveAfterDate;
    }

    /**
     * Sets the date after which no moves or resizes are allowed. Any events moved towards or across the date mark will
     * stop at this mark.
     * 
     * @param noMoveAfterDate Calendar or null.
     */
    public void setNoMoveAfterDate(Calendar noMoveAfterDate) {
        mNoMoveAfterDate = noMoveAfterDate;
    }

    /**
     * Returns the location where the event text will be displayed. Default is SWT.RIGHT.
     * 
     * @return Event text location
     */
    public int getHorizontalTextLocation() {
        return mHorizontalTextLocation;
    }

    /**
     * Sets the location of where the event text will be displayed. Options are: <code>SWT.LEFT</code>,
     * <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>. Center means the text will be drawn inside the event. Default is
     * <code>SWT.RIGHT</code>.
     * 
     * @param textLocation Text location, one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>.
     */
    public void setHorizontalTextLocation(int textLocation) {
        mHorizontalTextLocation = textLocation;
    }

    /**
     * Returns the lcoation where the event text will be displayed vertically. Default is <code>SWT.CENTER</code> which
     * is right behind the event itself.
     * 
     * @return Vertical text location.
     */
    public int getVerticalTextLocation() {
        return mVerticalTextLocation;
    }

    /**
     * Sets the vertical location where the event text will be displayed. Options are: <code>SWT.TOP</code>,
     * <code>SWT.CENTER</code>, <code>SWT.BOTTOM</code>. Default is <code>SWT.CENTER</code>.
     * 
     * @param verticalTextLocation Vertical text location
     */
    public void setVerticalTextLocation(int verticalTextLocation) {
        mVerticalTextLocation = verticalTextLocation;
    }

    /**
     * Returns the text font to use when drawing the event text. Default is null.
     * 
     * @return Event font
     */
    public Font getTextFont() {
        return mTextFont;
    }

    /**
     * Sets the text font to use when drawing the event text. Default is null. Setting a font will override any flags
     * for font settings in the Settings implementation.
     * 
     * @param textFont Font or null.
     */
    public void setTextFont(Font textFont) {
        mTextFont = textFont;
    }

    /**
     * Removes this item from the chart.
     */
    public void dispose() {
        mParentComposite.removeEvent(this);
    }

    public int getActualDDayStart() {
        return mRevisedStart == null ? getDDayStart() : getDDayRevisedStart();
    }

    public int getActualDDayEnd() {
        return mRevisedEnd == null ? getDDayEnd() : getDDayRevisedEnd();
    }

    /**
     * Returns the D-day start value.
     * 
     * @return
     */
    public int getDDayStart() {
        return mDDayStart;
    }

    public int getDDayRevisedStart() {
        if (mRevisedStart == null) return Integer.MAX_VALUE;

        return (int) DateHelper.daysBetween(mParentComposite.getDDayCalendar(), mRevisedStart, mParentChart.getSettings().getDefaultLocale());
    }

    public int getDDayRevisedEnd() {
        if (mRevisedEnd == null) return Integer.MAX_VALUE;

        return (int) DateHelper.daysBetween(mParentComposite.getDDayCalendar(), mRevisedEnd, mParentChart.getSettings().getDefaultLocale());
    }

    /**
     * Sets the D-day start value.
     * 
     * @param day
     */
    public void setDDayStart(int day) {
        mDDayStart = day;

        this.mStartDate = mParentComposite.getDDayCalendar();
        mStartDate.add(Calendar.DATE, day);
        updateDaysBetweenStartAndEnd();
    }

    public int getDDayEnd() {
        return mDDayEnd;
    }

    public void setDDayEnd(int day) {
        mDDayEnd = day;

        this.mEndDate = mParentComposite.getDDayCalendar();
        mEndDate.add(Calendar.DATE, day);
        updateDaysBetweenStartAndEnd();
    }

    public int getDDateRange() {
        return (int) DateHelper.daysBetween(getStartDate(), getEndDate(), mParentChart.getSettings().getDefaultLocale());
    }

    public int getRevisedDDateRange() {
        return (int) DateHelper.daysBetween(getActualStartDate(), getActualEndDate(), mParentChart.getSettings().getDefaultLocale()) + 1;
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
        if (mParentComposite.isShowingPlannedDates()) {
            if (getStartDate() != null && getRevisedStart() != null) {
                if (getStartDate().before(getRevisedStart())) {
                    // this is a negative value as it's to the left
                    plannedExtraLeft = getX() - mEarliestStartX;
                }
                /*                else {
                                    System.err.println(mName + " ----> " + getStartDate().getTime() + " " + getRevisedStart().getTime());
                                }
                */}
            /*            else {
                            System.err.println(mName);
                        }
            */if (getEndDate() != null && getRevisedEnd() != null) {
                if (getEndDate().after(getRevisedEnd())) {
                    plannedExtraRight = mLatestEndX - getXEnd() + mParentComposite.getDayWidth();
                }
            }
        }

        // now calculate text widths, later we'll check what takes up the most space, planned dates or text width. Whoever is bigger
        // becomes the "bonus" width that we add to the actual bounds
        int xExtra = 0, yExtra = 0, wExtra = 0, hExtra = 0;

        // TODO: this should take connected/not connected into account
        int textSpacer = mParentChart.getSettings().getTextSpacerConnected();
        if (getNameExtent() != null) {
            switch (mHorizontalTextLocation) {
                case SWT.LEFT:
                    int extra = getNameExtent().x + textSpacer;
                    if (plannedExtraLeft > extra) {
                        usePlannedLeft = true;
                    }
                    xExtra -= extra;
                    wExtra += extra;
                    usePlannedRight = plannedExtraRight > 0;
                    break;
                case SWT.CENTER:
                    int start = width / 2;
                    start += getNameExtent().x + textSpacer;
                    if (start > (x + width)) {
                        wExtra += (start - (x + width)); // add on the difference
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
            }

            switch (mVerticalTextLocation) {
                case SWT.TOP:
                    yExtra -= getNameExtent().y;
                    hExtra += getNameExtent().y;
                    break;
                case SWT.CENTER:
                    if (getNameExtent().y > height) {
                        int diff = height - getNameExtent().y;
                        yExtra -= (diff / 2);
                        hExtra += (diff / 2);
                    }
                    break;
                case SWT.BOTTOM:
                    hExtra += getNameExtent().y;
                    yExtra -= getNameExtent().y;
                    break;
            }
        }

        if (mParentComposite.isShowingPlannedDates()) {
            //System.err.println(plannedExtraLeft + " " + plannedExtraRight + " " + usePlannedLeft + " " + usePlannedRight);
            if (usePlannedRight && usePlannedLeft) {
                xExtra -= plannedExtraLeft;
                wExtra = plannedExtraLeft;
                wExtra += plannedExtraRight;
            } else {
                if (usePlannedRight) {
                    wExtra = plannedExtraRight + mParentComposite.getDayWidth();
                } else if (usePlannedLeft) {
                    xExtra = -(plannedExtraLeft + mParentComposite.getDayWidth());
                    wExtra += Math.abs(plannedExtraLeft) + mParentComposite.getDayWidth();
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

        for (int i = 0; i < mScopeEvents.size(); i++) {
            GanttEvent event = (GanttEvent) mScopeEvents.get(i);

            if (earliest == null) {
                earliest = event.getActualStartDate();
            } else {
                if (event.getActualStartDate().before(earliest)) earliest = event.getActualStartDate();
            }

            if (latest == null) {
                latest = event.getActualEndDate();
            } else {
                if (event.getActualEndDate().after(latest)) latest = event.getActualEndDate();
            }

            percentage += (float) event.getPercentComplete();
        }

        percentage /= (mScopeEvents.size() > 0 ? mScopeEvents.size() : 1);

        // allow start/end dates to override if we have zero events
        if (earliest == null && mStartDate != null) earliest = mStartDate;
        if (latest == null && mEndDate != null) latest = mEndDate;

        setStartDate(earliest);
        setEndDate(latest);
        setPercentComplete((int) percentage);

        updateDaysBetweenStartAndEnd();
    }

    boolean hasMovementConstraints() {
        return (mNoMoveAfterDate != null || mNoMoveBeforeDate != null);
    }

    // internal
    int getVisibility() {
        return mVisibility;
    }

    // internal
    void setVisibility(int visibility) {
        this.mVisibility = visibility;
    }

    // internal
    boolean isBoundsSet() {
        return mBoundsHaveBeenSet;
    }

    void setBoundsSet(boolean set) {
        mBoundsHaveBeenSet = set;
    }

    void setHorizontalLineTopY(int y) {
        this.mHorizontalLineTopY = y;
    }

    int getHorizontalLineTopY() {
        return mHorizontalLineTopY;
    }

    int getHorizontalLineBottomY() {
        return mHorizontalLineBottomY;
    }

    void setHorizontalLineBottomY(int y) {
        this.mHorizontalLineBottomY = y;
    }

    void setNameChanged(boolean changed) {
        this.mNameChanged = changed;
    }

    boolean isNameChanged() {
        return mNameChanged;
    }

    Point getNameExtent() {
        return mNameExtent;
    }

    void setNameExtent(Point extent) {
        this.mNameExtent = extent;
    }

    String getParsedString() {
        return mParsedString;
    }

    void setParsedString(String parsed) {
        this.mParsedString = parsed;
    }

    int getDaysBetweenStartAndEnd() {
        return this.mDaysBetweenStartAndEnd;
    }

    void updateDaysBetweenStartAndEnd() {
        if (getActualStartDate() == null || getActualEndDate() == null) {
            mDaysBetweenStartAndEnd = -1;
            return;
        }

        mDaysBetweenStartAndEnd = (int) DateHelper.daysBetween(getActualStartDate(), getActualEndDate(), mParentChart.getSettings().getDefaultLocale());

        if (mParentComposite.getCurrentView() == ISettings.VIEW_D_DAY) {
            mDDayStart = (int) DateHelper.daysBetween(mParentComposite.getDDayCalendar(), getStartDate(), mParentChart.getSettings().getDefaultLocale());
            mDDayEnd = (int) DateHelper.daysBetween(mParentComposite.getDDayCalendar(), getEndDate(), mParentChart.getSettings().getDefaultLocale());
            mDDayStart--;
        }

    }

    void moveStarted() {
        if (mMoving) return;

        if (mStartDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mStartDate.getTime());
            mPreMoveDateEstiStart = cal;
        }
        if (mEndDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mEndDate.getTime());
            mPreMoveDateEstiEnd = cal;
        }
        if (mRevisedStart != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mRevisedStart.getTime());
            mPreMoveDateRevisedStart = cal;
        }
        if (mRevisedEnd != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mRevisedEnd.getTime());
            mPreMoveDateRevisedEnd = cal;
        }

        mPreMoveBounds = new Rectangle(x, y, width, height);

        mMoving = true;
    }

    void moveCancelled() {
        mMoving = false;
        mStartDate = mPreMoveDateEstiStart;
        mEndDate = mPreMoveDateEstiEnd;
        mRevisedStart = mPreMoveDateRevisedStart;
        mRevisedEnd = mPreMoveDateRevisedEnd;
        if (mPreMoveBounds != null) {
            x = mPreMoveBounds.x;
            y = mPreMoveBounds.y;
            width = mPreMoveBounds.width;
            height = mPreMoveBounds.height;
        }
    }

    void moveFinished() {
        mMoving = false;
    }

    /**
     * Returns the scope parent of this event if it has one.
     * 
     * @return Scope parent
     */
    public GanttEvent getScopeParent() {
        return mScopeParent;
    }

    // not external as it's the wrong way to set a scope parent
    void setScopeParent(GanttEvent parent) {
        mScopeParent = parent;
    }

    void setEarliestStartX(int x) {
        mEarliestStartX = x;
    }

    void setLatestEndX(int x) {
        mLatestEndX = x;
    }

    int getEarliestStartX() {
        return mEarliestStartX;
    }

    int getLatestEndX() {
        return mLatestEndX;
    }

    int getActualWidth() {
        return mActualWidth;
    }

    void updateActualWidth() {
        mActualWidth = Math.abs(Math.abs(mLatestEndX) - Math.abs(mEarliestStartX));
    }

    void flagDragging() {
        mSavedVerticalDragY = y;
        mPreVerticalDragBounds = new Rectangle(x, y, width, height);
    }

    Rectangle getPreVerticalDragBounds() {
        return mPreVerticalDragBounds;
    }

    void undoVerticalDragging() {
        y = mSavedVerticalDragY;
    }

    boolean wasVerticallyMovedUp() {
        return (y < mPreVerticalDragBounds.y); 
    }
    
    boolean hasMovedVertically() {
        return y != mSavedVerticalDragY;
    }
    
    /**
     * Reparents this event from the current {@link GanttSection} to a new {@link GanttSection}
     * 
     * @param index index to put event at in new section
     * @param newSection new section to put event in
     */
    public void reparentToNewGanttSection(int index, GanttSection newSection) {
        if (mGanttSection != null) {
            mGanttSection.removeGanttEvent(this);
            newSection.addGanttEvent(index, this);
        }
    }
    
    public String toString() {
        return mName;
    }

    /**
     * Clones the GanttEvent and all objects of it (assuming they are cloneable). The clone should be exactly like the
     * original except for a possible few internal flags. The clone does not need to be re-added to the chart, but if it
     * needs to exist below a certain parent it will need to be re-added to that parent (such as a scope).
     */
    public Object clone() {
        final GanttEvent clone = new GanttEvent(mParentChart, getName());
        if (mEndDate != null) {
            clone.mEndDate = (Calendar) mEndDate.clone();
        }
        if (mNoMoveAfterDate != null) {
            clone.mNoMoveAfterDate = (Calendar) mNoMoveAfterDate.clone();
        }
        if (mNoMoveBeforeDate != null) {
            clone.mNoMoveBeforeDate = (Calendar) mNoMoveBeforeDate.clone();
        }
        if (mPreMoveDateEstiStart != null) {
            clone.mPreMoveDateEstiEnd = (Calendar) mPreMoveDateEstiEnd.clone();
        }
        if (mPreMoveDateEstiStart != null) {
            clone.mPreMoveDateEstiStart = (Calendar) mPreMoveDateEstiStart.clone();
        }
        if (mPreMoveDateRevisedEnd != null) {
            clone.mPreMoveDateRevisedEnd = (Calendar) mPreMoveDateRevisedEnd.clone();
        }
        if (mPreMoveDateRevisedStart != null) {
            clone.mPreMoveDateRevisedStart = (Calendar) mPreMoveDateRevisedStart.clone();
        }
        if (mRevisedEnd != null) {
            clone.mRevisedEnd = (Calendar) mRevisedEnd.clone();
        }
        if (mRevisedStart != null) {
            clone.mRevisedStart = (Calendar) mRevisedStart.clone();
        }
        clone.mScopeParent = mScopeParent;
        clone.mScopeEvents = new ArrayList(mScopeEvents);
        if (mStartDate != null) {
            clone.mStartDate = (Calendar) mStartDate.clone();
        }
        clone.mAdvancedTooltip = mAdvancedTooltip;
        clone.mBoundsHaveBeenSet = mBoundsHaveBeenSet;
        clone.mCheckpoint = mCheckpoint;
        clone.mData = mData;
        clone.mDaysBetweenStartAndEnd = mDaysBetweenStartAndEnd;
        clone.mDDayEnd = mDDayEnd;
        clone.mDDayStart = mDDayStart;
        clone.mFixedRowHeight = mFixedRowHeight;
        clone.mGanttGroup = mGanttGroup;
        clone.mGradientStatusColor = mGradientStatusColor;
        clone.mHidden = mHidden;
        clone.mHorizontalLineBottomY = mHorizontalLineBottomY;
        clone.mHorizontalLineTopY = mHorizontalLineTopY;
        clone.mHorizontalTextLocation = mHorizontalTextLocation;
        clone.mImage = mImage;
        clone.mLocked = mLocked;
        clone.mMenu = mMenu;
        clone.mMoveable = mMoveable;
        // it's 100% sure a clone is not being moved, we'd rather not have some funky result of matching this variable
        clone.mMoving = false;
        clone.mNameChanged = mNameChanged;
        clone.mNameExtent = mNameExtent;
        clone.mParsedString = mParsedString;
        clone.mPercentComplete = mPercentComplete;
        clone.mPicture = mPicture;
        clone.mPreMoveBounds = mPreMoveBounds;
        clone.mResizable = mResizable;
        clone.mScope = mScope;
        clone.mScopeEvents = new ArrayList(mScopeEvents);
        clone.mScopeParent = mScopeParent;
        clone.mShowBoldText = mShowBoldText;
        clone.mStatusColor = mStatusColor;
        clone.mTextDisplayFormat = mTextDisplayFormat;
        clone.mTextFont = mTextFont;
        clone.mVerticalEventAlignment = mVerticalEventAlignment;
        clone.mVerticalTextLocation = mVerticalTextLocation;
        clone.mVisibility = mVisibility;
        clone.mWidthWithtText = mWidthWithtText;
        clone.mLatestEndX = mLatestEndX;
        clone.mEarliestStartX = mEarliestStartX;
        clone.mActualWidth = mActualWidth;
        clone.mSavedVerticalDragY = mSavedVerticalDragY;
        return clone;
    }

}
