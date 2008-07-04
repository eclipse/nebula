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
 * This object can take many shapes, here is a list:<br>
 * <ul>
 * <li>Normal event
 * <li>Checkpoint event
 * <li>Scope event
 * <li>Image event
 * </ul>
 * The event may also take revised start and end dates, and can be modified individually to be locked, unmoveable, unresizable and much more.
 * <p>
 * Events <b>may be</b> modified on the fly to become a different object type from the above list. Please do ensure that the ALL parameters are set for it to become the new object
 * before you do so.
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
 * 
 */
public class GanttEvent extends AbstractGanttEvent implements IGanttChartItem {

	/*
	 * public static final int TYPE_EVENT = 0; public static final int TYPE_CHECKPOINT = 1; public static final int TYPE_IMAGE = 2; public static final int TYPE_SCOPE = 3;
	 */
	public static final int	FIXED_ROW_HEIGHT_AUTOMATIC	= -1;

	private Object			mData;
	private String			mName;
	private Calendar		mRevisedStart;
	private Calendar		mRevisedEnd;
	private Calendar		mStartDate;
	private Calendar		mEndDate;
	private int				mPercentComplete;
	private boolean			mCheckpoint;
	private boolean			mScope;
	private boolean			mLocked;
	private boolean			mImage;
	private boolean			mResizable					= true;
	private boolean			mMoveable					= true;
	private int				x, y, width, height;
	private Color			mStatusColor;
	private Color			mGradientStatusColor;
	private boolean			mShowBoldText;
	private String			mTextDisplayFormat;
	private ArrayList		mScopeEvents;
	private Image			mPicture;
	private Menu			mMenu;
	private GanttChart		mParentChart;
	private GanttGroup		mGanttGroup;
	private boolean			mHidden;
	private int				mWidthWithtText;
	// TODO: Implement. Less constructors, more user power.
	// private int mEventType = TYPE_EVENT;
	private AdvancedTooltip	mAdvancedTooltip;
	private int				mVisibility;
	private boolean			mBoundsHaveBeenSet;

	private int				mFixedRowHeight				= FIXED_ROW_HEIGHT_AUTOMATIC;
	private int				mVerticalEventAlignment		= SWT.TOP;

	private int				mHorizontalLineTopY;
	private int				mHorizontalLineBottomY;

	private Calendar		mNoMoveBeforeDate;
	private Calendar		mNoMoveAfterDate;

	// private items
	private boolean			mNameChanged				= true;
	private Point			mNameExtent;
	private String			mParsedString;

	private int				mHorizontalTextLocation		= SWT.RIGHT;
	private int				mVerticalTextLocation		= SWT.CENTER;

	private Font			mTextFont;

	private int				mDaysBetweenStartAndEnd;

	// cloned holders used for cancelling a move/resize via ESC
	private Calendar		mPreMoveDateEstiStart;
	private Calendar		mPreMoveDateEstiEnd;
	private Calendar		mPreMoveDateRevisedStart;
	private Calendar		mPreMoveDateRevisedEnd;
	private Rectangle		mPreMoveBounds;
	private boolean			mMoving;
	
	private GanttEvent		mScopeParent;

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
		mParentChart.getGanttComposite().addEvent(this, true);
		checkDates();
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
		return mStartDate;
	}

	/**
	 * Returns the revised start date if set, or the start date if not.
	 * 
	 * @return Start date or null
	 */
	public Calendar getActualStartDate() {
		return mRevisedStart != null ? mRevisedStart : mStartDate;
	}

	/**
	 * Returns the revised end date if set, or the end date if not.
	 * 
	 * @return End date or null
	 */
	public Calendar getActualEndDate() {
		return mRevisedEnd != null ? mRevisedEnd : mEndDate;
	}

	/**
	 * Sets the end date of this event.
	 * 
	 * @param startDate Start date
	 */
	public void setStartDate(Calendar startDate) {
		this.mStartDate = startDate;
		checkDates();
		updateDaysBetweenStartAndEnd();
	}

	/**
	 * Returns the end date of this event.
	 * 
	 * @return End date
	 */
	public Calendar getEndDate() {
		return mEndDate;
	}

	/**
	 * Sets the end date of this event.
	 * 
	 * @param endDate End date
	 */
	public void setEndDate(Calendar endDate) {
		this.mEndDate = endDate;
		checkDates();
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
	 * Returns the individual Menu for this event for which custom events can be created. Custom events will be mixed with built-in events (if they exist). The menu will be created
	 * on the first get call to this method, any subsequent method calls will return the same menu object. Do remember that a menu counts as a handle towards the maximum number of
	 * handles. If you have very many events, it is probably better to use a different way of creating menus, this is merely meant as a convenience.
	 * 
	 * @return Menu
	 */
	public Menu getMenu() {
		if (mMenu == null)
			mMenu = new Menu(mParentChart.getGanttComposite());

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
	public void setBounds(int x, int y, int width, int height) {
		mBoundsHaveBeenSet = true;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setBounds(Rectangle bounds) {
		mBoundsHaveBeenSet = true;
		this.x = bounds.x;
		this.y = bounds.y;
		this.width = bounds.width;
		this.height = bounds.height;
	}

	void updateX(int x) {
		this.x = x;
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
		return mRevisedStart;
	}

	/**
	 * Sets the revised start date of this event.
	 * 
	 * @param revisedStart
	 */
	public void setRevisedStart(Calendar revisedStart) {
		this.mRevisedStart = revisedStart;
		checkDates();
		updateDaysBetweenStartAndEnd();
	}

	/**
	 * Returns the revised end date of this event.
	 * 
	 * @return revised end date
	 */
	public Calendar getRevisedEnd() {
		return mRevisedEnd;
	}

	/**
	 * Sets the revised end date of this event.
	 * 
	 * @param revisedEnd Revised end date
	 */
	public void setRevisedEnd(Calendar revisedEnd) {
		this.mRevisedEnd = revisedEnd;
		checkDates();
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
	 * Returns the bounds of this event.
	 * 
	 * @return Bounds
	 */
	public Rectangle getBounds() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	/**
	 * Returns the text display format of this event. Please see ISettings.getTextDisplayFormat() for an overview. If this method returns null, whatever is set in the settings will
	 * be used.
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
	 * Sets this event to be a scope. Don't forget to add events that the scope is supposed to encompass with {@link #addScopeEvent(GanttEvent)}.
	 * 
	 * @param scope
	 * @see #addScopeEvent(GanttEvent)
	 */
	public void setScope(boolean scope) {
		mCheckpoint = false;
		mImage = false;
		mScope = scope;
		
		if (!scope) {
			for (int i = 0; i < mScopeEvents.size(); i++)
				((GanttEvent)mScopeEvents.get(i)).setScopeParent(null);
			
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
		if (event == this)
			return;

		if (mScopeEvents.contains(event))
			return;
		
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
	public ArrayList getScopeEvents() {
		return mScopeEvents;
	}

	// fixes dates being illogical, such as end date prior to start date which would cause very strange behavior in chart
	// fix to bugzilla #236840
	private void checkDates() {
		if (mStartDate != null && mEndDate != null) {
			if (mEndDate.before(mStartDate))
				mEndDate = mStartDate;
		}

		if (mRevisedStart != null && mRevisedEnd != null) {
			if (mRevisedEnd.before(mRevisedStart))
				mRevisedEnd = mRevisedStart;
		}
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

	public GanttEvent getEarliestScopeEvent() {
		if (!isScope() || mScopeEvents.size() == 0)
			return null;

		return getEarliestOrLatestScopeEvent(true);
	}

	public GanttEvent getLatestScopeEvent() {
		if (!isScope() || mScopeEvents.size() == 0)
			return null;

		return getEarliestOrLatestScopeEvent(false);
	}

	// this takes any text drawing widths into account and returns the bounds as such
	Rectangle getActualBounds() {
		// by default the bounds are the same as normal
		Rectangle ret = new Rectangle(x, y, width, height);

		// TODO: this should take connected/not connected into account
		int textSpacer = mParentChart.getSettings().getTextSpacerConnected();
		if (getNameExtent() != null) {
			switch (mHorizontalTextLocation) {
				case SWT.LEFT:
					ret.x -= getNameExtent().x + textSpacer;
					break;
				case SWT.CENTER:
					int start = width / 2;
					start += getNameExtent().x + textSpacer;
					if (start > (x + width))
						ret.width += (start - (x + width)); // add on the difference
					break;
				case SWT.RIGHT:
					ret.width += getNameExtent().x + textSpacer;
					break;
			}

			switch (mVerticalTextLocation) {
				case SWT.TOP:
					ret.y -= getNameExtent().y;
					break;
				case SWT.CENTER:
					if (getNameExtent().y > height) {
						int diff = height - getNameExtent().y;
						ret.y -= (diff / 2);
						ret.height += (diff / 2);
					}
					break;
				case SWT.BOTTOM:
					ret.height += getNameExtent().y;
					break;
			}
		}

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
				if (event.getActualStartDate().before(earliest))
					earliest = event.getActualStartDate();
			}

			if (latest == null) {
				latest = event.getActualEndDate();
			} else {
				if (event.getActualEndDate().after(latest))
					latest = event.getActualEndDate();
			}

			percentage += (float) event.getPercentComplete();
		}

		percentage /= (mScopeEvents.size() > 0 ? mScopeEvents.size() : 1);

		// allow start/end dates to override if we have zero events
		if (earliest == null && mStartDate != null)
			earliest = mStartDate;
		if (latest == null && mEndDate != null)
			latest = mEndDate;

		setStartDate(earliest);
		setEndDate(latest);
		setPercentComplete((int) percentage);
		
		updateDaysBetweenStartAndEnd();
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
	 * Sets if this event should be hidden or not. Hidden events are not shown on the chart and the space they normally would occupy is free.
	 * 
	 * @param hidden true to hide event.
	 */
	public void setHidden(boolean hidden) {
		mHidden = hidden;
	}

	/**
	 * Returns whether this event is hidden or not. Hidden events are not shown on the chart and the space they normally would occupy is free.
	 * 
	 * @return true if hidden.
	 */
	public boolean isHidden() {
		return mHidden;
	}

	private void internalSetAllChildrenHidden(boolean hidden) {
		if (mScopeEvents == null)
			return;

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
	}

	/**
	 * Un-hides all children from view. Children only exist on Scoped events.
	 * 
	 * @see #isHidden()
	 */
	public void showAllChildren() {
		internalSetAllChildrenHidden(false);
	}

	/**
	 * Returns whether all children are hidden or not. Children only exist on Scoped events.
	 * 
	 * @return true if all children are hidden.
	 */
	public boolean isChildrenHidden() {
		if (mScopeEvents == null)
			return false;

		for (int i = 0; i < mScopeEvents.size(); i++) {
			if (!((GanttEvent) mScopeEvents.get(i)).isHidden())
				return false;
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
	 * @return Row alignment, one of <code>SWT.TOP</code>, <code>SWT.CENTER</code>, <code>SWT.BOTTOM</code>. Default is <code>SWT.TOP</code>.
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
	 * Returns the date prior to which no moves or resizes are allowed. Any events moved towards or across the date mark will stop at this mark.
	 * 
	 * @return Calendar or null.
	 */
	public Calendar getNoMoveBeforeDate() {
		return mNoMoveBeforeDate;
	}

	/**
	 * Sets the date prior to which no moves or resizes are allowed. Any events moved towards or across the date mark will stop at this mark.
	 * 
	 * @param noMoveBeforeDate Calendar or null.
	 */
	public void setNoMoveBeforeDate(Calendar noMoveBeforeDate) {
		mNoMoveBeforeDate = noMoveBeforeDate;
	}

	/**
	 * Returns the date after which no moves or resizes are allowed. Any events moved towards or across the date mark will stop at this mark.
	 * 
	 * @return Calendar or null.
	 */
	public Calendar getNoMoveAfterDate() {
		return mNoMoveAfterDate;
	}

	/**
	 * Sets the date after which no moves or resizes are allowed. Any events moved towards or across the date mark will stop at this mark.
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
	 * Sets the location of where the event text will be displayed. Options are: <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>. Center means the text will
	 * be drawn inside the event. Default is <code>SWT.RIGHT</code>.
	 * 
	 * @param textLocation Text location, one of <code>SWT.LEFT</code>, <code>SWT.CENTER</code>, <code>SWT.RIGHT</code>.
	 */
	public void setHorizontalTextLocation(int textLocation) {
		mHorizontalTextLocation = textLocation;
	}

	/**
	 * Returns the lcoation where the event text will be displayed vertically. Default is <code>SWT.CENTER</code> which is right behind the event itself.
	 * 
	 * @return Vertical text location.
	 */
	public int getVerticalTextLocation() {
		return mVerticalTextLocation;
	}

	/**
	 * Sets the vertical location where the event text will be displayed. Options are: <code>SWT.TOP</code>, <code>SWT.CENTER</code>, <code>SWT.BOTTOM</code>. Default is
	 * <code>SWT.CENTER</code>.
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
	 * Sets the text font to use when drawing the event text. Default is null. Setting a font will override any flags for font settings in the Settings implementation.
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
		mParentChart.getGanttComposite().removeEvent(this);
		mParentChart.getGanttComposite().redraw();
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

		mDaysBetweenStartAndEnd = (int) DateHelper.daysBetween(getActualStartDate().getTime(), getActualEndDate().getTime(), mParentChart.getSettings().getDefaultLocale());
	}

	void moveStarted() {
		if (mMoving)
			return;

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
		x = mPreMoveBounds.x;
		y = mPreMoveBounds.y;
		width = mPreMoveBounds.width;
		height = mPreMoveBounds.height;
	}

	void moveFinished() {
		mMoving = false;
	}
	
	GanttEvent getScopeParent() {
		return mScopeParent;
	}
	
	void setScopeParent(GanttEvent parent) {
		mScopeParent = parent;
	}

	public String toString() {
		return mName;
	}
}
