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

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public interface ISettings {

	// connection types..
	// --|              ---|
	//   |  <-- type 1  |--|  <-- type 2 .. "type 3" is a mix of 1 and 2 with rounded corners, Project style
	//   V              |->
	public static final int CONNECTION_ARROW_RIGHT_TO_TOP = 1; // TODO: Fix flawed drawing
	public static final int CONNECTION_ARROW_RIGHT_TO_LEFT = 2;
	public static final int CONNECTION_MS_PROJECT_STYLE = 3;
	public static final int DEFAULT_CONNECTION_ARROW = CONNECTION_ARROW_RIGHT_TO_LEFT;

	// gantt view mode
	public static final int VIEW_WEEK = 1;
	public static final int VIEW_MONTH = 2;
	public static final int VIEW_YEAR = 3;
	
	// zoom levels
	public static final int ZOOM_DAY_MAX = 1;
	public static final int ZOOM_DAY_MEDIUM = 2;
	public static final int ZOOM_DAY_NORMAL = 3;
	public static final int ZOOM_MONTH_MAX = 4;
	public static final int ZOOM_MONTH_MEDIUM = 5;
	public static final int ZOOM_MONTH_NORMAL = 6;
	public static final int ZOOM_YEAR_MAX = 7;
	public static final int ZOOM_YEAR_MEDIUM = 8;
	public static final int ZOOM_YEAR_NORMAL = 9;
	public static final int ZOOM_YEAR_SMALL = 10;
	public static final int ZOOM_YEAR_VERY_SMALL = 11;
	
	public static final int SCROLLBAR_INFINITE = 1;
	public static final int SCROLLBAR_INFINITE_BASED_ON_TODAY = 2;
	public static final int SCRLLLBAR_LOCKED_TO_EVENT_SPAN = 3;
	public static final int SCROLLBAR_NONE = 10;
	
	/**
	 * The date format to use when displaying dates in string format.
	 * 
	 * @return Date format. Default is month/day/year.
	 * @see DateFormat
	 * @see DateFormatSymbols
	 */
	public String getDateFormat();
	
	/**
	 * The default color for an event when there is none set on the event itself.
	 * 
	 * @return Event color. Default is gray.
	 */
	public Color getDefaultEventColor();
	
	/**
	 * The default gradient color for an event (if gradients are turned on). 
	 * 
	 * @return Event gradient color. Default is white-ish.
	 */
	public Color getDefaultGradientEventColor();
	
	/**
	 * Whether to show the properties menu option in the right click menu. When clicked the event will be reported to IGanttEventListener.
	 * 
	 * @return True if to show the menu item. Default is true.
	 * @see IGanttEventListener#eventPropertiesSelected(GanttEvent)
	 */
	public boolean showPropertiesMenuOption();
	
	/**
	 * Whether to show the delete menu option in the right click menu. When clicked the event will be reported to IGanttEventListener.
	 * 
	 * @return True if to show the menu item. Default is true.
	 * @see IGanttEventListener#eventsDeleteRequest(java.util.List, org.eclipse.swt.events.MouseEvent)
	 */
	public boolean showDeleteMenuOption();
	
	/**
	 * What type of arrow connection to draw. There are tbree types:
	 * <ul>
	 * <li>CONNECTION_ARROW_RIGHT_TO_TOP - 
	 * 	 Arrow line (and arrow head if turned on) will be drawn into the events top and bottom corners.
	 * 
	 * <li>CONNECTION_ARROW_RIGHT_TO_LEFT - 
	 *   Arrow line (and arrow head if turned on) will be drawn into the events middle left or right side.
	 *   
	 * <li>CONNECTION_MS_PROJECT_STYLE -
	 *   Arrow line (and arrow head if turned on) will be drawn as logically as possible from above event to below. Lines are rounded in corners
	 *   and arrows will go to middle to top of below event or to side depending on where event is situated.
	 * </ul>
	 * @return Arrow head type. Default is CONNECTION_ARROW_RIGHT_TO_LEFT.
	 * @see #CONNECTION_ARROW_RIGHT_TO_LEFT 
	 */
	public int getArrowConnectionType();
	
	/**
	 * What view is used when the chart is initially drawn. Options are:
	 * <ul>
	 * <li>VIEW_WEEK
	 * <li>VIEW_MONTH
	 * <li>VIEW_YEAR
	 * </ul>
	 * 
	 * @return Initial view. Default is VIEW_WEEK. 
	 */
	public int getInitialView();
	
	/**
	 * What initial zoom level is used when the chart is initially drawn. Options are:
	 * 
	 * ZOOM_DAY_MAX
 	 * ZOOM_DAY_MEDIUM
	 * ZOOM_DAY_NORMAL
	 * ZOOM_MONTH_MAX
	 * ZOOM_MONTH_MEDIUM
	 * ZOOM_MONTH_NORMAL
	 * ZOOM_YEAR_MAX
	 * ZOOM_YEAR_MEDIUM
	 * ZOOM_YEAR_NORMAL
	 * ZOOM_YEAR_SMALL
	 * ZOOM_YEAR_VERY_SMALL 
	 * 
	 * @return Zoom level. Default ZOOM_DAY_NORMAL.
	 * @see ISettings#ZOOM_DAY_MAX
	 */
	public int getInitialZoomLevel();
	
	/**
	 * If to draw arrows on connecting lines or not.
	 * 
	 * @return True if to show arrowheads on connecting lines. Default is true.
	 */
	public boolean showArrows();
	
	/**
	 * Whether to enable auto scroll which causes the chart to scroll either left or right when events are dragged or resized beyond the bounds of the chart. 
	 * 
	 * @return True if to enable. Default is true.
	 */
	public boolean enableAutoScroll();
	
	/**
	 * Whether to show tooltips when mouse is lingering over events.
	 * 
	 * @return True if to show tooltips. Default is true.
	 */
	public boolean showToolTips();
	
	/**
	 * Whether to show date tooltips when events are moved or resized.
	 * 
	 * @return True if to show tooltips. Default is true.
	 */
	public boolean showDateTips();
	
	/**
	 * Whether to show events in 3D (meaning, a drop shadow is drawn below and to the right of the event)
	 * 
	 * @return True if to show events in 3D. Default is true.
	 */
	public boolean showBarsIn3D();
	
	/**
	 * Whether to draw the color on the bars using gradients. The colors are controlled by setting the following two variables on the GanttEvent.
	 * 
	 * GanttEvent.setGradientStatusColor(...)
	 * GanttEvent.setStatusColor(...)
	 * 
	 * @return True if to draw with gradients. Default is true.
	 * @see GanttEvent#setGradientStatusColor(Color)
	 * @see GanttEvent#setStatusColor(Color)
	 */
	public boolean showGradientBars();
	
	/**
	 * Whether to only show the dependency lines only when an event is selected, as opposed to always showing the lines.
	 * 
	 * @return True if to show them only on selections. Default is false.
	 */
	public boolean showOnlyDependenciesForSelectedItems();
	
	/**
	 * Whether to show a "plaque" on the event bars that displays a number of how many days the event spans over. 
	 * 
	 * @return True if to show the plaque. Default is false.
	 */
	public boolean showNumberOfDaysOnBars();
	
	/**
	 * Whether to draw lines that show where the revised dates would have been on the chart (assuimg revised dates are set). 
	 * 
	 * @return True if to draw lines for the revised dates. Default is false.
	 */
	public boolean showRevisedDates();
	
	/**
	 * Returns the height of the header section that displays the month names.
	 * 
	 * @return Pixel value. Default is 20.
	 */
	public int getHeaderMonthHeight();
	
	/**
	 * Returns the height of the header section that displays the days.
	 * 
	 * @return Pixel value. Default is 20.
	 */
	public int getHeaderDayHeight();
	
	/**
	 * Returns the width for each day that is drawn. Zoom levels use multipliers with these numbers.
	 * 
	 * @return Pixel value. Default is 16.
	 */
	public int getDayWidth();
	
	/**
	 * Returns the width for each day that is drawn in the monthly view. Zoom levels use multipliers with these numbers.
	 * 
	 * @return Pixel value. Default is 6.
	 */
	public int getMonthDayWidth();
	
	/**
	 * Returns the width for each day that is drawn in the yearly view. Zoom levels use multipliers with these numbers.
	 * 
	 * <p><font color="red"><b>WARNING: Setting this value below 3 will cause an infinite loop and probable application crash.</b></font>
	 * 
	 * @return Pixel value. Default is 3.
	 */
	public int getYearMonthDayWidth();
	
	/**
	 * Returns the height for each event, including checkpoints.  
	 * 
	 * @return Pixel value. Default is 12.
	 */
	public int getEventHeight();
	
	/**
	 * Returns the height of the bar that is drawn inside the events that represents the percentage done.
	 * 
	 * @return Pixel value. Default is 3.
	 */
	public int getEventPercentageBarHeight();
	
	/**
	 * Returns the number of pixels off each side of an event that is the area for which the resize cursor is shown. If people find it hard to 
	 * grab events, increase this number.
	 * 
	 * @return Pixel value. Default is 3.
	 */
	public int getResizeBorderSensitivity();
	
	/**
	 * Returns the number of pixels off each side of an event towards the center of it that is ignored when trying to move an event. This is to help
	 * resize distinguish from a move cursor and to help users find the two different areas. 
	 * 
	 * @return Pixel value. Default is 6. Remember that this value is handled in a negative calculation, but it should be a positive value.
	 */
	public int getMoveAreaNegativeSensitivity();
	
	/**
	 * Returns the space between the actual event and the text that is written after the event.  
	 * 
	 * @return Pixel value. Default is 8.
	 */
	public int getTextSpacer();
	
	/**
	 * Returns the space from the left of the border where the day letter is printed.
	 * 
	 * @return Pixel value. Default is 3.
	 */
	public int getDayVerticalSpacing();
	
	/**
	 * Returns the spacing from the top where the day letter is printed.
	 * 
	 * @return Pixel value. Default is 4.
	 */
	public int getDayHorizontalSpacing();
	
	/**
	 * Returns the vertical space between each event.
	 *  
	 * @return Pixel value. Default is 12.
	 */
	public int getEventSpacer();
	
	/**
	 * Whether the name of the scope is drawn in bold or plain.
	 * 
	 * @return True if bold. Default is true.
	 */
	public boolean showBoldScopeText();
	
	/**
	 * Whether events that are no longer in visible range should "not exist", which means that they aren't counted vertically. 
	 * The effect of this will be that visible events might jump around vertically as the chart is scrolled, as events that are in-between 
	 * come and go into view.
	 * <p>
	 * This might be useful if there are loads of events, however, it may also be confusing.
	 * 
	 * @return True if to consume events. Default is false.
	 */
	public boolean consumeEventWhenOutOfRange();
	
	/**
	 * Letters have different width. If turned on, this will try to make all day letters appear centered, whereas if turned off, 
	 * letter width will be ignored. By default, things are adjusted for Latin letters. 
	 * 
	 * @return True if to adjust for letter widths. Default is true.
	 */
	public boolean adjustForLetters();
	
	/**
	 * Whether event-resizing is turned on or off.
	 * 
	 * @return True if turned on. If turned off, event resizing is not possible. Default is true.
	 */
	public boolean enableResizing();
	
	/**
	 * Whether users can hold down SHIFT to move an event, and all dependent events will move with the selected event.
	 * 
	 * @return True if to move all linked events. Default is true.
	 */
	public boolean moveLinkedEventsWhenEventsAreMoved();
	
	/**
	 * Whether event drag-and-drop is turned on or off.
	 * 
	 * @return True if turned on. If turned off, drag and drop is not possible. Default is true.
	 */
	public boolean enableDragAndDrop();
	
	/**
	 * Whether when a user zooms in or out (only via CTRL + Scroll Wheel) to display a box in the bottom left corner that shows the zoom level.
	 * 
	 * @return True if to show a box when zooming. Default is true.
	 */
	public boolean showZoomLevelBox();
	
	/**
	 * Whether to mimic Project's infitie scrollbar which lets users scroll into the future or past indefinitely.
	 * 
	 * If turned off, the scrollbar will reflect the range of the oldest event's start date, to the latest event end date.
	 * 
	 * @return True if to turn on infinite scrollbar. Default is true.
	 */
	public boolean allowInfiniteHorizontalScrollBar();
	
	/**
	 * Whether the date tooltip box (if turned on) when resizing should hug the border of the event on the left or right side or if it should be simply
	 * stick somewhat to the event resize location.
	 * 
	 * @return True if to stick to the resize sides. Default is true.
	 */
	public boolean showResizeDateTipOnBorders();
	
	/**
	 * If true, it lets the move forwards or backwards in time by clicking in a "blank" area and dragging the mouse. Similar to the hand tool in Photoshop.
	 *  
	 * @return true if to allow clear-area drag. Default is true.
	 */
	public boolean allowBlankAreaDragAndDropToMoveDates();
	
	/**
	 * If for some reason the drag left vs. drag right directions feel reversed, simply flip this to switch them around. Only active if <code>allowBlankAreaDragAndDropToMoveDates()</code> is active.
	 * 
	 * @return true if to flip them around. Default is false.
	 * @see #allowBlankAreaDragAndDropToMoveDates()
	 */
	public boolean flipBlankAreaDragDirection();
	
	/**
	 * If true, draws a dotted selection marker around the currently selected event to visualize the selection.
	 * 
	 * @return true if to show a selection marker. Default is true.
	 */
	public boolean drawSelectionMarkerAroundSelectedEvent();
	
	/**
	 * Whether checkpoints can be resized (assuming resizing is on).
	 * 
	 * @return true if checkpoints can be resized. Default is false.	 
	 */
	public boolean allowCheckpointResizing();
	
	/**
	 * Whether to show any menu items at all when right clicking an event or a chart.
	 * 
	 * @return true if to show menu items on right click. Default is true.
	 */
	public boolean showMenuItemsOnRightClick();
		
	/**
	 * Returns the space between the head of the dependecy arrowhead and the event.
	 * 
	 * @return pixel space. Default is 1.
	 */
	public int getArrowHeadEventSpacer();
	
	/**
	 * If you for some reason wish to move the arrow head up or down vertically, setting a number here
	 * will modify the vertical "extra".
	 * 
	 * @return pixel length. Default is 0.
	 */
	public int getArrowHeadVerticalAdjuster();
	
	/**
	 * Returns the position that the calendar should start on when first created.
	 * 
	 * If null, the current date will be used. Please remember to use a Locale when you create your Calendar object.
	 * 
	 * @return Calendar, or null.
	 */
	public Calendar getStartupCalendarDate();
	
	/**
	 * Date offset of the startup date that the calendar should start at. By default, you probably do not want this to be 0 as that will
	 * hug the leftmost side of the widget. It's suggested to set a negative value.
	 * 
	 * @return date offset in number of days. Default is -4. 
	 */
	public int getCalendarStartupDateOffset();
	
	/**
	 * Moves the calendar to start on the first day of the week of the either current date or the date set in <code>getStartupCalendarDate()</code>
	 * 
	 * Please note, if the <code>getCalendarStartupDateOffset()</code> is set, these two methods will most likely clash.
	 * 
	 * @return true whether calendar should start on the first day of the week. Default is false.
	 * @see #getCalendarStartupDateOffset()
	 * @see #getStartupCalendarDate()
	 */
	public boolean startCalendarOnFirstDayOfWeek();
	
	/**
	 * If zooming in/out should be enabled or disabled.
	 * 
	 * @return true if enabled. Default is true.
	 */
	public boolean enableZooming();
	
	/**
	 * Returns the image used for displaying something as locked in the gantt chart. 
	 * 
	 * @return Image or null. Default is the lock_tiny.gif.
	 */
	public Image getLockImage();
	
	/**
	 * Decides how the string behind the event is displayed. You may ovverride this idividually by setting the GanttEvent parameter by the same name.
	 * <ul>
	 * <li>#n# = Name of event
	 * <li>#p# = Percentage finished
	 * <li>#sd# = Start date
	 * <li>#ed# = End date
	 * <li>#rsd# = Revised start date
	 * <li>#red# = Revised end date 
	 * <li>#nd# = Number of days event spans over
	 * </ul>
	 * @return String format. Default is "#n# (#p#%)"
	 */
	public String getTextDisplayFormat();
	
	/**
	 * The distance from the event top/bottom that is used to draw the line portraying revised start and end dates. 
	 * 
	 * @return Pixel spacing. Default is 3.
	 */
	public int getRevisedLineSpacer();
}
