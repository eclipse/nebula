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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
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
 * Events <b>may be</b> modified on the fly to become a different object type from the above list. Please do ensure that the ALL parameters are set for it to become the new object before you do so.
 * <p>
 * Once an event has been created, add it onto the GanttChart widget via the addEvent(...) methods available.
 * <p>
 * <b>Sample Code:</b><br><br>
 * <code>
 * // make a 10 day long event<br>
 * Calendar cStartDate = Calendar.getInstance(Locale.getDefault());<br>
 * Calendar cEndDate = Calendar.getInstance(Locale.getDefault());<br>
 * cEndDate.add(Calendar.DATE, 10);
 * <br><br>
 * // the first parameter is the data object, as this is an example, we'll leave it null<br>
 * // we're also setting the percentage complete to 50%<br>
 * GanttEvent ganttEvent = new GanttEvent(null, "Event Name", cStartDate, cEndDate, 50);
 * <br><br>
 * // add the event to the chart<br>
 * ganttChart.addEvent(ganttEvent);<br>
 * </code>
 * 
 * @author Emil Crumhorn <a href="mailto:emil.crumhorn@gmail.com">emil.crumhorn@gmail.com</a>
 *
 */
public class GanttEvent {
	
	public static final int TYPE_EVENT = 0;
	public static final int TYPE_CHECKPOINT = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_SCOPE = 3;

    private Object mData;
    private String mName;
    private Calendar mRevisedStart;
    private Calendar mRevisedEnd;
    private Calendar mStartDate;
    private Calendar mEndDate;
    private int mPercentComplete;
    private boolean mCheckpoint;
    private boolean mScope;
    private boolean mLocked;
    private boolean mImage;
    private int x, y, width, height;
    private Color mStatusColor;
    private Color mGradientStatusColor;    
    private boolean mShowBoldText = true;
    private String mTextDisplayFormat;
    private List<GanttEvent> mScopeEvents;
    private Image mPicture;
    private Menu mMenu;
    private GanttChart mParentChart;
    private GanttGroup mGanttGroup;
    private boolean mHidden;
    //TODO: Implement. Less constructors, more user power.
    //private int mEventType = TYPE_EVENT;

    /**
     * Creates a new GanttEvent object.
     * 
     * @param parent
     * @param data
     * @param name
     * @param startDate
     * @param endDate
     * @param percentComplete
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
     * Use for scope initializing only.
     * 
     * @param parent Parent GanttChart
     * @param data Data
     * @param name Display name
     */
    public GanttEvent(GanttChart parent, Object data, String name) {
    	this.mParentChart = parent;
    	this.mData = data;
    	this.mName = name;
    	this.mScope = true;
    	init();
    }
    
    /**
     * Use for checkpoint initializing only.
     * 
     * @param parent Parent GanttChart
     * @param data Data
     * @param name Display name
     * @param date Start (and end) date
     */
    public GanttEvent(GanttChart parent, Object data, String name, Calendar date) {
    	this.mParentChart = parent;
    	this.mData = data;
    	this.mName = name;
    	this.mStartDate = date;
    	this.mEndDate = date;
    	this.mCheckpoint = true;
    	init();
    }
    
    /**
     * Use for image initializing only.
     * 
     * @param parent Parent GanttChart
     * @param data Data
     * @param name Display name
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
    	init();
    }
    
    private void init() {
    	mScopeEvents = new ArrayList<GanttEvent>();
    	mParentChart.getGanttComposite().addEvent(this, true);
    	mMenu = new Menu(mParentChart.getGanttComposite());
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
     * Sets the end date of this event.
     * 
     * @param startDate Start date
     */
    public void setStartDate(Calendar startDate) {
        this.mStartDate = startDate;
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
     * with built-in events (if they exist).
     *  
     * @return Menu
     */
    public Menu getMenu() {
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
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
     * Sets the x location of this event. This is done internally.
     * 
     * @param x x location
     */
    public void setX(int x) {
        this.x = x;
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
     * Sets the y location of this event. This is done internally.
     * 
     * @param y y location
     */
    public void setY(int y) {
        this.y = y;
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
     * Sets the width of this event. This is done internally.
     * 
     * @param width Event width
     */
   public void setWidth(int width) {
        this.width = width;
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
     * Sets the height of this event. This is done internally.
     * 
     * @param height Event height
     */
    public void setHeight(int height) {
        this.height = height;
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
    }

    /**
     * Whether this event can be resized or not.
     * 
     * @return true if it can be resized
     */
    public boolean canResize() {
        return canMove();
    }

    /**
     * Whether this event can be moved or not.
     * 
     * @return true if event can be moved.
     */
    public boolean canMove() {      
        return true;
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
	 * Returns the text display format of this event. Please see ISettings.getTextDisplayFormat() for an overview. If this method returns null,
	 * whatever is set in the settings will be used.
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
	 * Sets this event to be a scope. Don't forget to add events that the scope is supposed to encompass with addScopeEvent(GanttEvent event);
	 * 
	 * @param scope
	 * @see #addScopeEvent(GanttEvent)
	 */
	public void setScope(boolean scope) {
		mCheckpoint = false;
		mImage = false;
		mScope = scope;
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
	public List<GanttEvent> getScopeEvents() {
		return mScopeEvents;
	}
	
	/**
	 * Internal method for calculating the earliest and latest dates of the scope. Do not call externally.
	 *
	 */
	public void calculateScope() {		
		Calendar earliest = null;
		Calendar latest = null;
		
		float percentage = 0f;
				
		for (GanttEvent event : mScopeEvents) {
			if (earliest == null) {
				earliest = event.getStartDate();
			}
			else {
				if (event.getStartDate().before(earliest))
					earliest = event.getStartDate();
			}
			
			if (latest == null) {
				latest = event.getEndDate();
			}
			else {
				if (event.getEndDate().after(latest))
					latest = event.getEndDate();
			}
			
			percentage += (float)event.getPercentComplete();
		}

		percentage /= (mScopeEvents.size() > 0 ? mScopeEvents.size() : 1);
		
		// allow start/end dates to override if we have zero events
		if (earliest == null && mStartDate != null)
			earliest = mStartDate;
		if (latest == null && mEndDate != null)
			latest = mEndDate;		
		
		setStartDate(earliest);
		setEndDate(latest);
		setPercentComplete((int)percentage);
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
	
	private void _setAllChildrenHidden(boolean hidden) {
		if (mScopeEvents == null)
			return;
		
		for (int i = 0; i < mScopeEvents.size(); i++) 
			mScopeEvents.get(i).setHidden(hidden);				
	}

	/**
	 * Hides all children from view. Children only exist on Scoped events.  
	 * 
	 * @see #isHidden()
	 */
	public void hideAllChildren() {
		_setAllChildrenHidden(true);
	}

	/**
	 * Un-hides all children from view. Children only exist on Scoped events.  
	 * 
	 * @see #isHidden()
	 */
	public void showAllChildren() {
		_setAllChildrenHidden(false);		
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
			if (!mScopeEvents.get(i).isHidden())
				return false;
		}
		
		return true;
	}
	
	public String toString() {
        return mName;
    }
}
