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
import java.util.Locale;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public interface ISettings {

	// connection types..
	// --|              ---|
	//   |  <-- type 1  |--|  <-- type 2 .. "type 3" is a mix of 1 and 2 with rounded corners, Project style
	//   V              |->
	public static final int CONNECTION_ARROW_RIGHT_TO_TOP = 1; // TODO: Fix flawed drawing
	public static final int CONNECTION_ARROW_RIGHT_TO_LEFT = 2;
	/**
	 * A MS Project style connection uses rounded corners and quite a bit of logic to make the line as nice as possible, and also supports
	 * reverse connection coloring. This is the suggested style and is also the default.
	 */
	public static final int CONNECTION_MS_PROJECT_STYLE = 3;
	/**
	 * Birds flight path is an arrow-head-less line from the one event to another in the straightest line
	 */
	public static final int CONNECTION_BIRDS_FLIGHT_PATH = 4;
	public static final int DEFAULT_CONNECTION_ARROW = CONNECTION_ARROW_RIGHT_TO_LEFT;

	// gantt view mode
	public static final int VIEW_DAY = 1;
	public static final int VIEW_WEEK = 2;
	public static final int VIEW_MONTH = 3;
	public static final int VIEW_YEAR = 4;
	
	// zoom levels
	public static final int MIN_ZOOM_LEVEL = 0;
    public static final int ZOOM_HOURS_MAX = 0;
    public static final int ZOOM_HOURS_MEDIUM = 1;
    public static final int ZOOM_HOURS_NORMAL = 2;
	public static final int ZOOM_DAY_MAX = 3;
	public static final int ZOOM_DAY_MEDIUM = 4;
	public static final int ZOOM_DAY_NORMAL = 5;
	public static final int ZOOM_MONTH_MAX = 6;
	public static final int ZOOM_MONTH_MEDIUM = 7;
	public static final int ZOOM_MONTH_NORMAL = 8;
	public static final int ZOOM_YEAR_MAX = 9;
	public static final int ZOOM_YEAR_MEDIUM = 10;
	public static final int ZOOM_YEAR_NORMAL = 11;
	public static final int ZOOM_YEAR_SMALL = 12;
	public static final int ZOOM_YEAR_VERY_SMALL = 13;
	public static final int MAX_ZOOM_LEVEL = 13;
	
	public static final int SCROLLBAR_INFINITE = 1;
	// TODO: allow different type scrollbars
	//public static final int SCROLLBAR_INFINITE_BASED_ON_TODAY = 2;
	//public static final int SCRLLLBAR_LOCKED_TO_EVENT_SPAN = 3;
	//public static final int SCROLLBAR_NONE = 10;
	
	/**
	 * The date format to use when displaying dates in string format.
	 * 
	 * @return Date format. Default is month/day/year.
	 * @see DateFormat
	 * @see DateFormatSymbols
	 */
	public String getDateFormat();

	/**
	 * The date format to use when displaying dates in string format in the hours view.
	 * 
	 * @return Date format. Default is month/day/year/ hh:mm.
	 * @see DateFormat
	 * @see DateFormatSymbols
	 */
	public String getHourDateFormat();

	
	// TODO: Move these to color manager
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
	 * What type of arrow connection to draw. There are three types:
	 * <ul>
	 * <li><code>CONNECTION_ARROW_RIGHT_TO_TOP</code> - 
	 * 	 Arrow line (and arrow head if turned on) will be drawn into the events top and bottom corners.
	 * 
	 * <li><code>CONNECTION_ARROW_RIGHT_TO_LEFT</code> - 
	 *   Arrow line (and arrow head if turned on) will be drawn into the events middle left or right side.
	 *   
	 * <li><code>CONNECTION_MS_PROJECT_STYLE</code> -
	 *   Arrow line (and arrow head if turned on) will be drawn as logically as possible from above event to below. Lines are rounded in corners
	 *   and arrows will go to middle to top of below event or to side depending on where event is situated.
	 * </ul>
	 * @return Arrow head type. Default is <code>CONNECTION_MS_PROJECT_STYLE</code>.
	 * @see #CONNECTION_MS_PROJECT_STYLE 
	 */
	public int getArrowConnectionType();
	
	/**
	 * What view is used when the chart is initially drawn. Options are:
	 * <ul>
	 * <li><code>VIEW_WEEK</code>
	 * <li><code>VIEW_MONTH</code>
	 * <li><code>VIEW_YEAR</code>
	 * </ul>
	 * 
	 * @return Initial view. Default is VIEW_WEEK. 
	 */
	public int getInitialView();
	
	/**
	 * What initial zoom level is used when the chart is initially drawn. Options are:
	 * <ul>
	 * <li><code>ZOOM_HOURS_MAX</code>
	 * <li><code>ZOOM_HOURS_MEDIUM</code>
	 * <li><code>ZOOM_HOURS_NORMAL</code>
	 * <li><code>ZOOM_DAY_MAX</code>
 	 * <li><code>ZOOM_DAY_MEDIUM</code>
	 * <li><code>ZOOM_DAY_NORMAL</code>
	 * <li><code>ZOOM_MONTH_MAX</code>
	 * <li><code>ZOOM_MONTH_MEDIUM</code>
	 * <li><code>ZOOM_MONTH_NORMAL</code>
	 * <li><code>ZOOM_YEAR_MAX</code>
	 * <li><code>ZOOM_YEAR_MEDIUM</code>
	 * <li><code>ZOOM_YEAR_NORMAL</code>
	 * <li><code>ZOOM_YEAR_SMALL</code>
	 * <li><code>ZOOM_YEAR_VERY_SMALL</code>
	 * </ul> 
	 * 
	 * @return Zoom level. Default is <code>ZOOM_DAY_NORMAL</code>.
	 * @see ISettings#ZOOM_DAY_MAX
	 */
	public int getInitialZoomLevel();
	
	/**
	 * If to draw arrows on connecting lines or not.
	 * 
	 * @return true if to show arrowheads on connecting lines. Default is true.
	 */
	public boolean showArrows();
	
	/**
	 * Whether to enable auto scroll which causes the chart to scroll either left or right when events are dragged or resized beyond the bounds of the chart. 
	 * 
	 * @return true if to enable. Default is true.
	 */
	public boolean enableAutoScroll();
	
	/**
	 * Whether to show tooltips when mouse is lingering over events.
	 * 
	 * @return true if to show tooltips. Default is true.
	 */
	public boolean showToolTips();
	
	/**
	 * Whether to show date tooltips when events are moved or resized.
	 * 
	 * @return true if to show tooltips. Default is true.
	 */
	public boolean showDateTips();
	
	/**
	 * Whether to show events in 3D (meaning, a drop shadow is drawn below and to the right of the event)
	 * 
	 * @return true if to show events in 3D. Default is true.
	 */
	public boolean showBarsIn3D();
	
	/**
	 * Whether to draw the color on the events using gradients. The colors are controlled by setting the following two variables on the GanttEvent.
	 * 
	 * GanttEvent.setGradientStatusColor(...)
	 * GanttEvent.setStatusColor(...)
	 * 
	 * @return true if to draw with gradients. Default is true.
	 * @see GanttEvent#setGradientStatusColor(Color)
	 * @see GanttEvent#setStatusColor(Color)
	 */
	public boolean showGradientEventBars();
	
	/**
	 * Whether to only show the dependency connections only when an event is selected, as opposed to always showing the lines.
	 * 
	 * @return true if to show them only on selections. Default is false.
	 */
	public boolean showOnlyDependenciesForSelectedItems();
	
	/**
	 * Whether to show a "plaque" on the event bars that displays a number of how many days the event spans over. 
	 * 
	 * @return true if to show the plaque. Default is false.
	 */
	public boolean showNumberOfDaysOnBars();
	
	/**
	 * Whether to draw lines that show where the revised dates would have been on the chart (assuming revised dates are set). 
	 * 
	 * @return true if to draw lines for the revised dates. Default is false.
	 */
	public boolean showPlannedDates();
	
	/**
	 * Returns the height of the header section that displays the month names.
	 * 
	 * @return Pixel value. Default is 18.
	 */
	public int getHeaderMonthHeight();
	
	/**
	 * Returns the height of the header section that displays the days.
	 * 
	 * @return Pixel value. Default is 18.
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
	 * <p>
	 * Note: If the event height is an even number, this number does best being odd, and vice versa.
	 * 
	 * @return Pixel value. Default is 3.
	 */
	public int getEventPercentageBarHeight();
	
	/**
	 * The percentage bar always draws as a line in the center of the event but normally it only draws the part that displays the % complete.
	 * If this returns true it will draw that part, but also the remainder. You can set the color for the remainder different than that from the actual
	 * percentage complete as well.
	 * 
	 * @return true if to draw the entire bar. Default is true.
	 */
	public boolean drawFullPercentageBar();
	
	/**
	 * The Alpha level of the percentage complete bar. Only draws if Alpha is turned on.
	 * 
	 * @return Alpha value. Default is 255.
	 */
	public int getPercentageBarAlpha();

	/**
	 * The Alpha level of the remaining percentage complete bar. Only draws if Alpha is turned on.
	 * 
	 * @return Alpha value. Default is 70.
	 */
	public int getRemainderPercentageBarAlpha();
	
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
	 * Returns the space between the actual event and the text that is written after the event when the event has a connecting line extending from it.  
	 * 
	 * @return Pixel value. Default is 9.
	 */
	public int getTextSpacerConnected();

	/**
	 * Returns the space between the actual event and the text that is written after the event when the event has no connecting line extending from it.  
	 * 
	 * @return Pixel value. Default is 9.
	 */
	public int getTextSpacerNonConnected();
	
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
	 * @return true if bold. Default is true.
	 */
	public boolean showBoldScopeText();
	
	/**
	 * Whether events that are no longer in visible range should "not exist", which means that they aren't counted vertically. 
	 * The effect of this will be that visible events might jump around vertically as the chart is scrolled, as events that are in-between 
	 * come and go into view.
	 * <p>
	 * This might be useful if there are loads of events, however, it may also be confusing.
	 * 
	 * @return true if to consume events. Default is false.
	 */
	public boolean consumeEventWhenOutOfRange();
	
	/**
	 * Letters have different width. If turned on, this will try to make all day letters appear centered, whereas if turned off, 
	 * letter width will be ignored.  
	 * 
	 * @return true if to adjust for letter widths. Default is true.
	 */
	public boolean adjustForLetters();
	
	/**
	 * Whether event-resizing is turned on or off.
	 * 
	 * @return true if turned on. If turned off, event resizing is not possible. Default is true.
	 */
	public boolean enableResizing();
	
	/**
	 * Whether users can hold down <code>SHIFT</code> (or whatever the settings say) to move an event, and all dependent events will move with the selected event. 
	 * 
	 * @return true if to move all linked events. Default is true.
	 * @see ISettings#getDragAllModifierKey()
	 */
	public boolean moveLinkedEventsWhenEventsAreMoved();
	
	/**
	 * Whether event drag-and-drop is turned on or off.
	 * 
	 * @return true if turned on. If turned off, drag and drop is not possible. Default is true.
	 */
	public boolean enableDragAndDrop();
	
	/**
	 * Whether when a user zooms in or out (only via <code>CTRL</code> (or whatever the settings say) + <code>Scroll Wheel</code>) to display a box in the bottom left corner that shows the zoom level.
	 * 
	 * @return true if to show a box when zooming. Default is true.
	 * @see ISettings#getZoomWheelModifierKey()
	 */
	public boolean showZoomLevelBox();
	
	/**
	 * Whether to mimic Project's infinite scrollbar which lets users scroll into the future or past indefinitely.
	 * 
	 * If turned off, the scrollbar will reflect the range of the oldest event's start date, to the latest event end date.
	 * 
	 * @return true if to turn on infinite scrollbar. Default is true.
	 */
	public boolean allowInfiniteHorizontalScrollBar();
	
	/**
	 * Whether the date tooltip box (if turned on) when resizing should hug the border of the event on the left or right side or if it should be simply
	 * stick somewhat to the event resize location.
	 * 
	 * @return true if to stick to the resize sides. Default is true.
	 */
	public boolean showResizeDateTipOnBorders();
	
	/**
	 * If true, it lets the move forwards or backwards in time by clicking in a "blank" area and dragging the mouse. Similar to the hand tool in Photoshop or Acrobat.
	 *  
	 * @return true if to allow clear-area drag. Default is true.
	 */
	public boolean allowBlankAreaDragAndDropToMoveDates();
	
	/**
	 * If for some reason the drag left vs. drag right directions feel reversed, simply flip this to switch them around. Only active if <code>allowBlankAreaDragAndDropToMoveDates()</code> is active.
	 * 
	 * @return true if to flip them around. Default is false.
	 * @see ISettings#allowBlankAreaDragAndDropToMoveDates()
	 */
	public boolean flipBlankAreaDragDirection();
	
	/**
	 * If true, draws a dotted selection marker around the currently selected event to visualize the selection.
	 * 
	 * @return true if to show a selection marker. Default is true.
	 */
	public boolean drawSelectionMarkerAroundSelectedEvent();
	
	/**
	 * Whether checkpoints can be resized (assuming resizing is on {@link ISettings#enableResizing()}).
	 * 
	 * @return true if checkpoints can be resized. Default is false.	 
	 * @see ISettings#enableResizing()
	 */
	public boolean allowCheckpointResizing();
	
	/**
	 * Whether to show any menu items at all when right clicking an event or a chart.
	 * 
	 * @return true if to show menu items on right click. Default is true.
	 */
	public boolean showMenuItemsOnRightClick();
		
	/**
	 * Returns the space between the head of the dependency arrowhead and the event.
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
	 * If null, the current date will be used. Please remember to use a Locale when you create your Calendar object, ideally the same as used in the settings {@link ISettings#getDefaultLocale()}.
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
	 * Moves the calendar to start on the first day of the week of the either current date or the date set in {@link ISettings#getStartupCalendarDate()}
	 * 
	 * Please note, if the {@link ISettings#getCalendarStartupDateOffset()} is set to other than zero, these two methods will most likely clash.
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
	 * Returns the image used for displaying something as locked in the GANTT chart. 
	 * 
	 * @return Image or null. Default is the <code>lock_tiny.gif</code> image in the package.
	 */
	public Image getLockImage();
	
	/**
	 * Decides how the string behind the event is displayed. You may override this individually by setting the GanttEvent parameter by the same name.
	 * <ul>
	 * <li>#name# = Name of event
	 * <li>#pc# = Percentage complete
	 * <li>#sd# = Start date
	 * <li>#ed# = End date
	 * <li>#rs# = Revised start date
	 * <li>#re# = Revised end date 
	 * <li>#days# = Number of days event spans over
	 * <li>#reviseddays# = Number of revised days event spans over
	 * </ul>
	 * @return String format. Default is "#name# (#pc#%)"
	 * @see GanttEvent#setTextDisplayFormat(String)
	 */
	public String getTextDisplayFormat();
	
	/**
	 * The distance from the event top (and event bottom) that is used to draw the line portraying revised start and end dates. 
	 * 
	 * @return Pixel spacing. Default is 3.
	 */
	public int getRevisedLineSpacer();
	
	/*
	 * The number of work hours in a day. Must not be less than 0 or greater than 23.
	 * <br><br>
	 * <b><font color="red">THIS FEATURE IS CURRENTLY BEING WORKED ON AND NOT SUPPORTED, PLEASE DO NOT CHANGE!</font></b>
	 * 
	 * @return Work hours per day. Default is 0.
	 */
	int getWorkHoursPerDay();
	
	/*
	 * The hour when the work days start. A number between 0 and 24.
	 * <br><br>
	 * <b><font color="red">THIS FEATURE IS CURRENTLY BEING WORKED ON AND NOT SUPPORTED, PLEASE DO NOT CHANGE!</font></b>
	 * 
	 * @return Work day start hour. Default is 24.
	 */
	int getWorkDayStartHour();
	
	/**
	 * Whether to round off minutes on events to nearest hour.
	 * 
	 * @return true if yes. Default is false.
	 */
	public boolean roundHourlyEventsOffToNearestHour();
	
	/**
	 * The default help image used in the advanced tooltip. Null if none.
	 * 
	 * @return Image or null. Default is null.
	 */
	public Image getDefaultAdvandedTooltipHelpImage();
	
	/**
	 * The default image used in the advanced tooltip. Null if none.
	 * 
	 * @return Image or null. Default is null.
	 */
	public Image getDefaultAdvandedTooltipImage();
	
	/**
	 * The default help text shown in the advanced tooltip.
	 * 
	 * @return String or null. Default is null.
	 */
	public String getDefaultAdvancedTooltipHelpText();
	
	/**
	 * The default title text shown in the advanced tooltip.
	 * 
	 * @return String or null. Default is null.
	 */
	public String getDefaultAdvancedTooltipTitle();
	
	/**
	 * The default extended tooltip shown in the advanced tooltip. Extended tooltips are used when the GanttEvent has revised dates.
	 * 
	 * @return String or null. Default is a text showing the dates, revised dates and percentage complete.
	 */
	public String getDefaultAdvancedTooltipTextExtended();
	
	/**
	 * The default tooltip shown in the advanced tooltip. Normal tooltips are used when the GanttEvent has no revised dates and also for scopes, images and checkpoints.
	 * 
	 * @return String or null. Default is a text showing the dates and percentage complete.
	 */
	public String getDefaultAdvancedTooltipText();
	
	/**
	 * The width of the line showing where today's date is.
	 *  
	 * @return line width. Default is 2.
	 */
	public int getTodayLineWidth();
	
	/**
	 * The default line style of the line showing where today's date is.
	 * 
	 * @return SWT.LINE_ style. Default is SWT.LINE_SOLID.
	 */
	public int getTodayLineStyle();
	
	/**
	 * The vertical offset from the top for the today line calculated from the bottom part of the header. 
	 * 
	 * @return Vertical offset. Default is the height of the bottom header.
	 */
	public int getTodayLineVerticalOffset();
	
	/**
	 * The vertical offset from top for the tick marks in the top header. 
	 * 
	 * @return Default is the height of the top header minus 5.
	 */
	public int getVerticalTickMarkOffset();

	/**
	 * The SimpleDateFormat of the text shown in the top header for the day view. 
	 * 
	 * @return SimpleDateFormat string. May not be null.
	 */
	public String getDayHeaderTextDisplayFormatTop();
	
	/**
	 * The SimpleDateFormat of the text shown in the top header for the week view. 
	 * 
	 * @return SimpleDateFormat string. May not be null.
	 */
	public String getWeekHeaderTextDisplayFormatTop();
	
	/**
	 * The SimpleDateFormat of the text shown in the top header for the month view. 
	 * 
	 * @return SimpleDateFormat string. May not be null.
	 */
	public String getMonthHeaderTextDisplayFormatTop();
	
	/**
	 * The SimpleDateFormat of the text shown in the top header for the year view. 
	 * 
	 * @return SimpleDateFormat string. May not be null.
	 */
	public String getYearHeaderTextDisplayFormatTop();
	
	/**
	 * The SimpleDateFormat of the text shown in the bottom header for the day view. 
	 * 
	 * @return SimpleDateFormat string. May not be null.
	 */
	public String getDayHeaderTextDisplayFormatBottom();
	
	/**
	 * The SimpleDateFormat of the text shown in the bottom header for the week view. 
	 * 
	 * @return SimpleDateFormat string. May not be null.
	 */
	public String getWeekHeaderTextDisplayFormatBottom();
	
	/**
	 * The SimpleDateFormat of the text shown in the bottom header for the month view. 
	 * 
	 * @return SimpleDateFormat string. May not be null.
	 */
	public String getMonthHeaderTextDisplayFormatBottom();
	
	/**
	 * The SimpleDateFormat of the text shown in the bottom header for the year view. 
	 * 
	 * @return SimpleDateFormat string. May not be null.
	 */
	public String getYearHeaderTextDisplayFormatBottom();

	/**
	 * Whether to draw the header or not. If this returns false, all header-related settings will be ignored. 
	 * 
	 * @return true if to draw header. Default is true.
	 */
	public boolean drawHeader();
	
	/**
	 * Top spacer between the top pixel and the beginning of the first event. The top pixel will be either the height of the
	 * header (if drawn) or 0 if the header is not drawn.
	 * 
	 * @return top spacer value. Default is 12.
	 */
	public int getEventsTopSpacer();
	
	/**
	 * Bottom spacer between the bottom event and the section divider when using GanttSections. For non GanttSection usage, this setting is ignored. 
	 * 
	 * @return bottom spacer value. Default is 12.
	 */
	public int getEventsBottomSpacer();
	
	/**
	 * The height of the bar drawn between sections.
	 * 
	 * @return height. Default is 5.
	 */
	public int getSectionBarDividerHeight();
	
	/**
	 * Width of the section bar.
	 * 
	 * @return width. Default is 20.
	 */
	public int getSectionBarWidth();
	
	/**
	 * Minimum height of sections. Normally the minimum height is calculated by:
	 * <p>
	 * 1. Space the section name takes up vertically<br>
	 * 2. Space all contained events take up including various event spacers.
	 * <p>
	 * If the two calculations above are smaller than the value returned by this method, the height returned from this method will be used.
	 * 
	 * @return minimum section height (larger or equals to zero). Default is 80.
	 */
	public int getMinimumSectionHeight();
	
	/**
	 * Advanced tooltips are shells and by default they pop up where the mouse pointer is. To not block the pointer, an offset is used.
	 * 
	 * @return horizontal offset. Default is 15.
	 */
	public int getAdvancedTooltipXOffset();
	
	/**
	 * Whether to use Advanced Tooltips by default. 
	 * 
	 * @return True whether to use advanced tooltips. Default is true.
	 */
	public boolean getUseAdvancedTooltips();
	
	/**
	 * The keyboard modifier key (also known as a hint) used to determine when it's a drag-all-linked-events {@link ISettings#moveLinkedEventsWhenEventsAreMoved()} event or just a normal drag.
	 *  
	 * @return Keyboard modifier. Default is <code>SWT.SHIFT</code> (the shift key)
	 * @see ISettings#moveLinkedEventsWhenEventsAreMoved()
	 */
	public int getDragAllModifierKey();
	
	/**
	 * The keyboard modifier key combined with the scroll wheel to make the chart zoom. 
	 * 
	 * @return Keyboard modifier. Default is <code>SWT.MOD1</code> (usually the <code>CTRL</code> key)
	 */
	public int getZoomWheelModifierKey();
	
	/**
	 * Locale used for calendars and wherever needed. 
	 * 
	 * @return Locale. Default is Locale.getDefault().
	 */
	public Locale getDefaultLocale();
	
	/**
	 * See IGanttEventListener's lastDraw(GC gc) method method info. 
	 * 
	 * @return true whether to enable the last draw functionality. Default is false.
	 * @see IGanttEventListener#lastDraw(org.eclipse.swt.graphics.GC)
	 */
	public boolean enableLastDraw();
	
	/**
	 * If you plan on using reverse dependencies, you may want to flag this to true. This makes normal connections
	 * draw the arrow to the top left corner of the target event, and if it's a reverse dependency, it draw the arrow
	 * to the bottom left of target events. That way any overlapping event don't have arrows drawing in the exact same spot
	 * which makes it much easier to view the chart.
	 * 
	 * @return true to split arrows. Default is false.
	 */
	public boolean useSplitArrowConnections();
	
	/**
	 * If you plan on using reverse dependencies, this value will be interesting. If this value returns 0 and you have both
	 * a normal-direction dependency and a reverse one, their vertical lines will overlap. By setting this value to greater 
	 * than zero that vertical line will be spaced out for reverse dependencies. The value is how much extra it will be spaced. 
	 * 
	 * @return spacer value. Default is 2.
	 */
	public int getReverseDependencyLineHorizontalSpacer();
	
	/**
	 * The vertical lines are the lines that divide days and weeks etc. 
	 * 
	 * @see GanttComposite#setDrawVerticalLinesOverride(Boolean)
	 * @return true if to draw vertical lines. Default is true.
	 */
	public boolean drawVerticalLines();	
	
	/**
	 * Whether to draw horizontal lines between the events. Useful for where you have a tree/table on the left and wish events
	 * to be easier to see when they are lined up with the tree items.
	 * 
	 * @see GanttComposite#setDrawHorizontalLinesOverride(Boolean)
 	 * @return true to show horizontal lines. Default is false;
	 */
	public boolean drawHorizontalLines();
	
	/**
	 * This setting is a bit tricky. When this returns true, the chart will try to cache and minimize as many redraw events as possible,
	 * and try to cut corners where it can to speed it up. It's rather experimental and as such it is included here as a setting. However, it is on by default.
	 * Should you encounter strange drawing or items not updating correctly forcing you to either manually call redraw or to force an event change,
	 * it is suggested you turn this flag to off.
	 * 
	 * @return true whether to use "Fast drawing". Default is true.
	 */
	public boolean useFastDraw();
	
	/**
	 * Which side the section bar should be drawn on. 
	 * <br><br>
	 * You may use one of <code>SWT.LEFT</code> or <code>SWT.RIGHT</code>.
	 * 
	 * @return section bar side. Default is <code>SWT.LEFT</code>.
	 */
	public int getSectionSide();
	
	/**
	 * When dates are locked down to a certain day, the chart will draw a special marker on the lock start and end dates to point out that it's locked between certain
	 * constraints. 
	 * 
	 * @return Whether to draw markers for date-range-locked events. Default is true.
	 */
	public boolean drawLockedDateMarks();
	
	/**
	 * Whether to show a date tooltip when scrolling horizontally (changing dates) and vertically. 
	 * The tooltip will show just above the bottom horizontal toolbar. Note that if showDateTips()
	 * returns false, this tip will not show.
	 * 
	 * @return true whether to show date tooltips when scrolling through dates. Default is true.
	 * @see #showDateTips()
	 */
	public boolean showDateTipsOnScrolling();
	
	// fix to bugzilla #236852
	/**
	 * Whether the GanttSection bar should draw all the way down or not.
	 * 
	 * @return true to draw all the way down. Default is false.
	 */
	public boolean drawGanttSectionBarToBottom();
	
	// fix to bugzilla #236852
	/**
	 * Whether to draw fills and vertical lines etc to the bottom when GanttSections are used.
	 * 
	 * @return true to draw everything all the way down. Default is false.
	 */
	public boolean drawFillsToBottomWhenUsingGanttSections();
	
	/**
	 * Whether the header should always be visible regardless of vertical scroll position. Basically a "fixed header" feature.
	 * 
	 * @return true whether to lock the header. Default is false.
	 */
	public boolean lockHeaderOnVerticalScroll();
	
	/**
	 * Whether to show the default set of menu items on the right click menus of events. If false, the menu will be blank allowing you to set all items from scratch.
	 * 
	 * @return true to show default menu items along with custom ones on right click. Default is true.
	 * @see #showMenuItemsOnRightClick()
	 * @see #showPropertiesMenuOption()
	 * @see #showDeleteMenuOption()
	 * @see GanttEvent#getMenu()
	 */
	public boolean showDefaultMenuItemsOnEventRightClick();
	
	/**
	 * Whether scopes can show a right click menu or not. By default scopes are non-active objects that simply draw according to their children. By allowing a menu
	 * to be shown on the scope you can still perform custom events if you so wish (such as show/hide all children). 
	 *
	 * @return true to allow menus on scopes. Default is false.
	 * @see GanttEvent#getMenu()
	 * @see GanttEvent#showAllChildren()
	 * @see GanttEvent#hideAllChildren()
	 */
	public boolean allowScopeMenu();

	/**
	 * Whether selecting dates in the header is allowed by clicking the date. Events will be fired on selection events. Note that not all views have this feature,
	 * only those that actually show full dates, as that's where it makes most sense. 
	 *  
	 * @return true to allow header selection. Default is true.
	 */
	public boolean allowHeaderSelection(); 
	
	/*
	 * DO NOT USE - TESTING ONLY
	 * 
	 * @return
	 */
	//public List getHeaderLevels();
}
