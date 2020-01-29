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
 * emil.crumhorn@gmail.com - initial API and implementation
 * ziogiannigmail.com - Bug 462855 - Zoom Minimum Depth increased up to 6 levels deeper than initial implementation (-6,-5,-4,-3,-2,-1)
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * This interface lets you define various settings for the GanttChart. It's highly advisable that for implementation, {@link AbstractSettings} is extended
 * and methods needed to be changed from their defaults are overridden and changed. It would be quite a hassle to implement a full ISettings interface from scratch.
 * In order to preserve binary compatibility, after the MinuteView implementation, this interface has been extended by {@link ISettings2}
 * Please refer both to {@link ISettings} and {@link ISettings2} for any setting change.
 *
 * <pre>
 * class MySettings extends AbstractSettings {
 * 	// override your methods here
 * }
 * </pre>
 * <p />
 * Once you've overridden the settings you wish to change, simply pass an instance of your implementation class to the constructor of GanttChart: {@link GanttChart#GanttChart(org.eclipse.swt.widgets.Composite, int, ISettings)}
 *
 * @author Emil Crumhorn and Giovanni Cimmino
 *
 */
public interface ISettings {

	// connection types..
	// --| ---|
	// | <-- type 1 |--| <-- type 2 .. "type 3" is a mix of 1 and 2 with rounded corners, Project style
	// V |->
	/**
	 * A connecting line that starts at the right of an event and goes to the top of the connecting event
	 */
	static final int CONNECTION_ARROW_RIGHT_TO_TOP = 1; // TODO: Fix flawed drawing

	/**
	 * A connecting line that starts at the right of an event and goes to the left of the connecting event
	 */
	static final int CONNECTION_ARROW_RIGHT_TO_LEFT = 2;

	/**
	 * A MS Project style connection uses rounded corners and quite a bit of logic to make the line as nice as possible,
	 * and also supports reverse connection coloring. This is the suggested style and is also the default.
	 */
	static final int CONNECTION_MS_PROJECT_STYLE = 3;

	/**
	 * Birds flight path is an arrow-head-less line from the one event to another in the straightest line, also known as "how the crow flies".
	 */
	static final int CONNECTION_BIRDS_FLIGHT_PATH = 4;

	/**
	 * The default connection which is {@link #CONNECTION_ARROW_RIGHT_TO_LEFT}
	 */
	static final int DEFAULT_CONNECTION_ARROW = CONNECTION_ARROW_RIGHT_TO_LEFT;

	// gantt view mode

	static final int VIEW_MINUTE = 0;
	static final int VIEW_DAY = 1;
	static final int VIEW_WEEK = 2;
	static final int VIEW_MONTH = 3;
	static final int VIEW_YEAR = 4;
	static final int VIEW_D_DAY = 5;

	// zoom levels
	static final int MIN_ZOOM_LEVEL = -6;
	static final int ZOOM_SECONDS_MAX = -6;
	static final int ZOOM_SECONDS_MEDIUM = -5;
	static final int ZOOM_SECONDS_NORMAL = -4;
	static final int ZOOM_MINUTES_MAX = -3;
	static final int ZOOM_MINUTES_MEDIUM = -2;
	static final int ZOOM_MINUTES_NORMAL = -1;
	static final int ZOOM_HOURS_MAX = 0;
	static final int ZOOM_HOURS_MEDIUM = 1;
	static final int ZOOM_HOURS_NORMAL = 2;
	static final int ZOOM_DAY_MAX = 3;
	static final int ZOOM_DAY_MEDIUM = 4;
	static final int ZOOM_DAY_NORMAL = 5;
	static final int ZOOM_MONTH_MAX = 6;
	static final int ZOOM_MONTH_MEDIUM = 7;
	static final int ZOOM_MONTH_NORMAL = 8;
	static final int ZOOM_YEAR_MAX = 9;
	static final int ZOOM_YEAR_MEDIUM = 10;
	static final int ZOOM_YEAR_NORMAL = 11;
	static final int ZOOM_YEAR_SMALL = 12;
	static final int ZOOM_YEAR_VERY_SMALL = 13;
	// static final int ZOOM_YEAR_SMALLER = 14;
	// static final int ZOOM_YEAR_SMALLEST = 15;
	static final int MAX_ZOOM_LEVEL = 13;

	/**
	 * The date format to use when displaying dates in string format.
	 *
	 * @return Date format. Default is month/day/year.
	 * @see DateFormat
	 * @see DateFormatSymbols
	 */
	String getDateFormat();

	/**
	 * The date format to use when displaying dates in string format in the hours view.
	 *
	 * @return Date format. Default is month/day/year/ hh:mm.
	 * @see DateFormat
	 * @see DateFormatSymbols
	 */
	String getHourDateFormat();

	// TODO: Move these to color manager
	/**
	 * The default color for an event when there is none set on the event itself.
	 *
	 * @return Event color. Default is gray.
	 */
	Color getDefaultEventColor();

	/**
	 * The default gradient color for an event (if gradients are turned on).
	 *
	 * @return Event gradient color. Default is white-ish.
	 */
	Color getDefaultGradientEventColor();

	/**
	 * Whether to show the properties menu option in the right click menu. When clicked the event will be reported to
	 * IGanttEventListener.
	 *
	 * @return True if to show the menu item. Default is true.
	 * @see IGanttEventListener#eventPropertiesSelected(GanttEvent)
	 */
	boolean showPropertiesMenuOption();

	/**
	 * Whether to show the delete menu option in the right click menu. When clicked the event will be reported to
	 * IGanttEventListener.
	 *
	 * @return True if to show the menu item. Default is true.
	 * @see IGanttEventListener#eventsDeleteRequest(java.util.List, org.eclipse.swt.events.MouseEvent)
	 */
	boolean showDeleteMenuOption();

	/**
	 * What type of arrow connection to draw. There are three types:
	 * <ul>
	 * <li>{@link ISettings#CONNECTION_ARROW_RIGHT_TO_TOP} - Arrow line (and arrow head if turned on) will be drawn into the
	 * events top and bottom corners.
	 * <li>{@link ISettings#CONNECTION_ARROW_RIGHT_TO_LEFT} - Arrow line (and arrow head if turned on) will be drawn into the
	 * events middle left or right side.
	 * <li>{@link ISettings#CONNECTION_MS_PROJECT_STYLE} - Arrow line (and arrow head if turned on) will be drawn as
	 * logically as possible from above event to below. Lines are rounded in corners and arrows will go to middle to top
	 * of below event or to side depending on where event is situated.
	 * <li>{@link ISettings#CONNECTION_BIRDS_FLIGHT_PATH} - Straight "as the bird flies" line between events without any bends.
	 * </ul>
	 *
	 * @return Arrow head type. Default is <code>CONNECTION_MS_PROJECT_STYLE</code>.
	 * @see #CONNECTION_MS_PROJECT_STYLE
	 */
	int getArrowConnectionType();

	/**
	 * What view is used when the chart is initially drawn. Options are:
	 * <ul>
	 * <li>{@link #VIEW_DAY}
	 * <li>{@link #VIEW_WEEK}
	 * <li>{@link #VIEW_MONTH}
	 * <li>{@link #VIEW_YEAR}
	 * <li>{@link #VIEW_D_DAY}
	 * </ul>
	 *
	 * @return Initial view. Default is VIEW_WEEK.
	 */
	int getInitialView();

	/**
	 * What initial zoom level is used when the chart is initially drawn. Options are:
	 * <ul>
	 * <li>{@link #ZOOM_SECONDS_MAX}
	 * <li>{@link #ZOOM_SECONDS_MEDIUM}
	 * <li>{@link #ZOOM_SECONDS_NORMAL}
	 * <li>{@link #ZOOM_MINUTES_MAX}
	 * <li>{@link #ZOOM_MINUTES_MEDIUM}
	 * <li>{@link #ZOOM_MINUTES_NORMAL}
	 * <li>{@link #ZOOM_HOURS_MAX}
	 * <li>{@link #ZOOM_HOURS_MEDIUM}
	 * <li>{@link #ZOOM_HOURS_NORMAL}
	 * <li>{@link #ZOOM_DAY_MAX}
	 * <li>{@link #ZOOM_DAY_MEDIUM}
	 * <li>{@link #ZOOM_DAY_NORMAL}
	 * <li>{@link #ZOOM_MONTH_MAX}
	 * <li>{@link #ZOOM_MONTH_MEDIUM}
	 * <li>{@link #ZOOM_MONTH_NORMAL}
	 * <li>{@link #ZOOM_YEAR_MAX}
	 * <li>{@link #ZOOM_YEAR_MEDIUM}
	 * <li>{@link #ZOOM_YEAR_NORMAL}
	 * <li>{@link #ZOOM_YEAR_SMALL}
	 * <li>{@link #ZOOM_YEAR_VERY_SMALL}
	 * </ul>
	 *
	 * @return Zoom level. Default is {@link ISettings#ZOOM_DAY_NORMAL}
	 * @see ISettings#ZOOM_DAY_MAX
	 */
	int getInitialZoomLevel();

	/**
	 * If to draw arrows on connecting lines or not.
	 *
	 * @return true if to show arrowheads on connecting lines. Default is true.
	 */
	boolean showArrows();

	/**
	 * Whether to enable auto scroll which causes the chart to scroll either left or right when events are dragged or
	 * resized beyond the bounds of the chart.
	 *
	 * @return true if to enable. Default is true.
	 */
	boolean enableAutoScroll();

	/**
	 * Whether to show tooltips when mouse is lingering over events.
	 *
	 * @return true if to show tooltips. Default is true.
	 */
	boolean showToolTips();

	/**
	 * Returns the custom tool tip generator which generates the tooltip
	 * out of custom data for a GanttEvent.
	 *
	 * @return
	 */
	IToolTipContentReplacer getToolTipContentReplacer();

	/**
	 * Whether to show date tooltips when events are moved or resized.
	 *
	 * @return true if to show tooltips. Default is true.
	 */
	boolean showDateTips();

	/**
	 * Whether to show events in 3D (meaning, a drop shadow is drawn below and to the right of the event)
	 *
	 * @return true if to show events in 3D. Default is true.
	 */
	boolean showBarsIn3D();

	/**
	 * Whether to draw the color on the events using gradients. The colors are controlled by setting the following two
	 * variables on the GanttEvent. {@link GanttEvent#setGradientStatusColor(Color)} and {@link GanttEvent#setStatusColor(Color)}.
	 *
	 * @return true if to draw with gradients. Default is true.
	 * @see GanttEvent#setGradientStatusColor(Color)
	 * @see GanttEvent#setStatusColor(Color)
	 */
	boolean showGradientEventBars();

	/**
	 * Whether to only show the dependency connections only when an event is selected, as opposed to always showing the
	 * lines.
	 *
	 * @return true if to show them only on selections. Default is false.
	 */
	boolean showOnlyDependenciesForSelectedItems();

	/**
	 * Whether to show a "plaque" on the event bars that displays a number of how many days the event spans over.
	 *
	 * @return true if to show the plaque. Default is false.
	 */
	boolean showNumberOfDaysOnBars();

	/**
	 * Whether to draw lines that show where the revised dates would have been on the chart (assuming revised dates are
	 * set).
	 *
	 * @return true if to draw lines for the revised dates. Default is false.
	 */
	boolean showPlannedDates();

	/**
	 * Returns the height of the header section that displays the month names.
	 *
	 * @return Pixel value. Default is 18.
	 */
	int getHeaderMonthHeight();

	/**
	 * Returns the height of the header section that displays the days.
	 *
	 * @return Pixel value. Default is 18.
	 */
	int getHeaderDayHeight();

	/**
	 * Returns the width for each day that is drawn. Zoom levels use multipliers with these numbers.
	 *
	 * @return Pixel value. Default is 16.
	 */
	int getDayWidth();

	/**
	 * Returns the width for each day that is drawn in the monthly view. Zoom levels use multipliers with these numbers.
	 *
	 * @return Pixel value. Default is 6.
	 */
	int getMonthDayWidth();

	/**
	 * Returns the width for each day that is drawn in the yearly view. Zoom levels use multipliers with these numbers.
	 * <p>
	 * <font color="red"><b>WARNING: Setting this value below 3 will cause an infinite loop and probable application
	 * crash. This happens as internal size calculations end up on 0 width (rounded) values.</b></font>
	 *
	 * @return Pixel value. Default is 3.
	 */
	int getYearMonthDayWidth();

	/**
	 * Returns the height for each event, including checkpoints.
	 *
	 * @return Pixel value. Default is 12.
	 */
	int getEventHeight();

	/**
	 * Returns the height of the bar that is drawn inside the events that represents the percentage done.
	 * <p>
	 * Note: If the event height is an even number, this number does best being odd, and vice versa.
	 *
	 * @return Pixel value. Default is 3.
	 */
	int getEventPercentageBarHeight();

	/**
	 * The percentage bar always draws as a line in the center of the event but normally it only draws the part that
	 * displays the % complete. If this returns true it will draw that part, but also the remainder. You can set the
	 * color for the remainder different than that from the actual percentage complete as well.
	 *
	 * @return true if to draw the entire bar. Default is true.
	 */
	boolean drawFullPercentageBar();

	/**
	 * The Alpha level of the percentage complete bar. Only draws if Alpha is turned on.
	 *
	 * @return Alpha value. Default is 255.
	 */
	int getPercentageBarAlpha();

	/**
	 * The Alpha level of the remaining percentage complete bar. Only draws if Alpha is turned on.
	 *
	 * @return Alpha value. Default is 70.
	 */
	int getRemainderPercentageBarAlpha();

	/**
	 * Returns the number of pixels off each side of an event that is the area for which the resize cursor is shown. If
	 * people find it hard to grab events, increase this number.
	 *
	 * @return Pixel value. Default is 3.
	 */
	int getResizeBorderSensitivity();

	/**
	 * Returns the number of pixels off each side of an event towards the center of it that is ignored when trying to
	 * move an event. This is to help resize distinguish from a move cursor and to help users find the two different
	 * areas.
	 *
	 * @return Pixel value. Default is 6. Remember that this value is handled in a negative calculation, but it should
	 *         be a positive value.
	 */
	int getMoveAreaNegativeSensitivity();

	/**
	 * Returns the space between the actual event and the text that is written after the event when the event has a
	 * connecting line extending from it.
	 *
	 * @return Pixel value. Default is 9.
	 */
	int getTextSpacerConnected();

	/**
	 * Returns the space between the actual event and the text that is written after the event when the event has no
	 * connecting line extending from it.
	 *
	 * @return Pixel value. Default is 9.
	 */
	int getTextSpacerNonConnected();

	/**
	 * Returns the space from the left of the border where the day letter is printed.
	 *
	 * @return Pixel value. Default is 3.
	 */
	int getDayVerticalSpacing();

	/**
	 * Returns the spacing from the top where the day letter is printed.
	 *
	 * @return Pixel value. Default is 4.
	 */
	int getDayHorizontalSpacing();

	/**
	 * Returns the vertical space between each event.
	 *
	 * @return Pixel value. Default is 12.
	 */
	int getEventSpacer();

	/**
	 * Whether the name of the scope is drawn in bold or plain.
	 *
	 * @return true if bold. Default is true.
	 */
	boolean showBoldScopeText();

	/**
	 * Letters have different width. If turned on, this will try to make all day letters appear centered, whereas if
	 * turned off, letter width will be ignored.
	 *
	 * @return true if to adjust for letter widths. Default is true.
	 */
	boolean adjustForLetters();

	/**
	 * Whether event-resizing is turned on or off.
	 *
	 * @return true if turned on. If turned off, event resizing is not possible. Default is true.
	 */
	boolean enableResizing();

	/**
	 * Whether users can hold down <code>SHIFT</code> (or whatever the settings say) to move an event, and all dependent
	 * events will move with the selected event.
	 *
	 * @return true if to move all linked events. Default is true.
	 * @see ISettings#getDragAllModifierKey()
	 */
	boolean moveLinkedEventsWhenEventsAreMoved();

	/**
	 * Whether event drag-and-drop is turned on or off.
	 *
	 * @return true if turned on. If turned off, drag and drop is not possible. Default is true.
	 */
	boolean enableDragAndDrop();

	/**
	 * Whether when a user zooms in or out (only via <code>CTRL</code> (or whatever the settings say) +
	 * <code>Scroll Wheel</code>) to display a box in the bottom left corner that shows the zoom level.
	 *
	 * @return true if to show a box when zooming. Default is true.
	 * @see ISettings#getZoomWheelModifierKey()
	 */
	boolean showZoomLevelBox();

	/**
	 * Whether to mimic Microsoft &copy; Project's infinite scrollbar which lets users scroll into the future or past indefinitely. If
	 * turned off, the scrollbar will reflect the range of the oldest event's start date, to the latest event end date.
	 *
	 * @return true if to turn on infinite scrollbar. Default is true.
	 */
	boolean allowInfiniteHorizontalScrollBar();

	/**
	 * Whether the date tooltip box (if turned on) when resizing should hug the border of the event on the left or right
	 * side or if it should be simply stick somewhat to the event resize location.
	 *
	 * @return true if to stick to the resize sides. Default is true.
	 */
	boolean showResizeDateTipOnBorders();

	/**
	 * If true, it lets the move forwards or backwards in time by clicking in a "blank" area and dragging the mouse.
	 * Similar to the hand tool in Photoshop or Acrobat.
	 *
	 * @return true if to allow clear-area drag. Default is true.
	 */
	boolean allowBlankAreaDragAndDropToMoveDates();

	/**
	 * Relies on {@link #allowBlankAreaDragAndDropToMoveDates()} being true. If so, this will additionally determine if the user
	 * can blank-area drag the chart in a vertical manner to move the chart in that direction as well.
	 * <p />
	 * Holding down the shift key will double the speed of the vertical drag
	 *
	 * @return true to allow clear-area vertical drag. Default is false (as it can be confusing at first try).
	 */
	boolean allowBlankAreaVerticalDragAndDropToMoveChart();

	/**
	 * If for some reason the drag left vs. drag right directions feel reversed, simply flip this to switch them around.
	 * Only active if {@link #allowBlankAreaDragAndDropToMoveDates()} is active.
	 *
	 * @return true if to flip them around. Default is false.
	 * @see ISettings#allowBlankAreaDragAndDropToMoveDates()
	 */
	boolean flipBlankAreaDragDirection();

	/**
	 * If true, draws a dotted selection marker around the currently selected event to visualize the selection.
	 *
	 * @return true if to show a selection marker. Default is true.
	 */
	boolean drawSelectionMarkerAroundSelectedEvent();

	/**
	 * Whether checkpoints can be resized (assuming resizing is on {@link ISettings#enableResizing()}).
	 *
	 * @return true if checkpoints can be resized. Default is false.
	 * @see ISettings#enableResizing()
	 */
	boolean allowCheckpointResizing();

	/**
	 * Whether to show any menu items at all when right clicking an event or a chart.
	 *
	 * @return true if to show menu items on right click. Default is true.
	 */
	boolean showMenuItemsOnRightClick();

	/**
	 * Returns the space between the head of the dependency arrowhead and the event.
	 *
	 * @return pixel space. Default is 1.
	 */
	int getArrowHeadEventSpacer();

	/**
	 * If you for some reason wish to move the arrow head up or down vertically, setting a number here will modify the
	 * vertical "extra".
	 *
	 * @return pixel length. Default is 0.
	 */
	int getArrowHeadVerticalAdjuster();

	/**
	 * Returns the position that the calendar should start on when first created. If null, the current date will be
	 * used. Please remember to use a Locale when you create your Calendar object, ideally the same as used in the
	 * settings {@link ISettings#getDefaultLocale()}.
	 *
	 * @return Calendar, or null.
	 */
	Calendar getStartupCalendarDate();

	/**
	 * Date offset of the startup date that the calendar should start at. By default, you probably do not want this to
	 * be 0 as that will hug the leftmost side of the widget. It's suggested to set a negative value.
	 *
	 * @return date offset in number of days. Default is -4.
	 */
	int getCalendarStartupDateOffset();

	/**
	 * Moves the calendar to start on the first day of the week of the either current date or the date set in
	 * {@link ISettings#getStartupCalendarDate()}
	 * <p />
	 * Please note, if the
	 * {@link ISettings#getCalendarStartupDateOffset()} is set to other than zero, these two methods will most likely
	 * clash.
	 * <p />
	 * This setting has no effect on D-Day charts.
	 *
	 * @return true whether calendar should start on the first day of the week. Default is false.
	 * @see #getCalendarStartupDateOffset()
	 * @see #getStartupCalendarDate()
	 */
	boolean startCalendarOnFirstDayOfWeek();

	/**
	 * If zooming in/out should be enabled or disabled.
	 *
	 * @return true if enabled. Default is true.
	 */
	boolean enableZooming();

	/**
	 * Returns the image used for displaying something as locked in the GANTT chart.
	 *
	 * @return Image or null. Default is the <code>lock_tiny.gif</code> image in the package.
	 */
	Image getLockImage();

	/**
	 * Decides how the string behind the event is displayed. You may override this individually by setting the
	 * GanttEvent parameter by the same name.
	 * <ul>
	 * <li>#name# = Name of event
	 * <li>#pc# = Percentage complete
	 * <li>#sd# = Start date
	 * <li>#ed# = End date
	 * <li>#rs# =
	 * Revised start date
	 * <li>#re# = Revised end date
	 * <li>#days# = Number of days event spans over
	 * <li>#reviseddays# =
	 * Number of revised days event spans over
	 * </ul>
	 *
	 * @return String format. Default is "#name# (#pc#%)"
	 * @see GanttEvent#setTextDisplayFormat(String)
	 */
	String getTextDisplayFormat();

	/**
	 * The distance from the event top (and event bottom) that is used to draw the line portraying revised start and end
	 * dates.
	 *
	 * @return Pixel spacing. Default is 3.
	 */
	int getRevisedLineSpacer();

	/**
	 * Whether to round off minutes on events to nearest hour.
	 *
	 * @return true if yes. Default is false.
	 */
	boolean roundHourlyEventsOffToNearestHour();

	/**
	 * The default help image used in the advanced tooltip. Null if none.
	 *
	 * @return Image or null. Default is null.
	 */
	Image getDefaultAdvandedTooltipHelpImage();

	/**
	 * The default image used in the advanced tooltip. Null if none.
	 *
	 * @return Image or null. Default is null.
	 */
	Image getDefaultAdvandedTooltipImage();

	/**
	 * The default help text shown in the advanced tooltip.
	 *
	 * @return String or null. Default is null.
	 */
	String getDefaultAdvancedTooltipHelpText();

	/**
	 * The default title text shown in the advanced tooltip.
	 *
	 * @return String or null. Default is null.
	 */
	String getDefaultAdvancedTooltipTitle();

	/**
	 * The default extended tooltip shown in the advanced tooltip. Extended tooltips are used when the GanttEvent has
	 * revised dates.
	 *
	 * @return String or null. Default is a text showing the dates, revised dates and percentage complete.
	 */
	String getDefaultAdvancedTooltipTextExtended();

	/**
	 * The default tooltip shown in the advanced tooltip. Normal tooltips are used when the GanttEvent has no revised
	 * dates and also for scopes, images and checkpoints.
	 *
	 * @return String or null. Default is a text showing the dates and percentage complete.
	 */
	String getDefaultAdvancedTooltipText();

	/**
	 * The width of the line showing where today's date is.
	 *
	 * @return line width. Default is 2.
	 */
	int getTodayLineWidth();

	/**
	 * The default line style of the line showing where today's date is.
	 *
	 * @return SWT.LINE_ style. Default is SWT.LINE_SOLID.
	 */
	int getTodayLineStyle();

	/**
	 * The vertical offset from the top for the today line calculated from the bottom part of the header.
	 *
	 * @return Vertical offset. Default is the height of the bottom header.
	 */
	int getTodayLineVerticalOffset();

	/**
	 * The vertical offset from top for the tick marks in the top header.
	 *
	 * @return Default is the height of the top header minus 5.
	 */
	int getVerticalTickMarkOffset();

	/**
	 * The SimpleDateFormat of the text shown in the top header for the day view.
	 *
	 * @return {@link SimpleDateFormat} string. May not be null.
	 */
	String getDayHeaderTextDisplayFormatTop();

	/**
	 * The SimpleDateFormat of the text shown in the top header for the week view.
	 *
	 * @return SimpleDateFormat string. May not be null.
	 */
	String getWeekHeaderTextDisplayFormatTop();

	/**
	 * The SimpleDateFormat of the text shown in the top header for the month view.
	 *
	 * @return SimpleDateFormat string. May not be null.
	 */
	String getMonthHeaderTextDisplayFormatTop();

	/**
	 * The SimpleDateFormat of the text shown in the top header for the year view.
	 *
	 * @return SimpleDateFormat string. May not be null.
	 */
	String getYearHeaderTextDisplayFormatTop();

	/**
	 * The SimpleDateFormat of the text shown in the bottom header for the day view.
	 *
	 * @return SimpleDateFormat string. May not be null.
	 */
	String getDayHeaderTextDisplayFormatBottom();

	/**
	 * The SimpleDateFormat of the text shown in the bottom header for the week view.
	 *
	 * @return SimpleDateFormat string. May not be null.
	 */
	String getWeekHeaderTextDisplayFormatBottom();

	/**
	 * The SimpleDateFormat of the text shown in the bottom header for the month view.
	 *
	 * @return SimpleDateFormat string. May not be null.
	 */
	String getMonthHeaderTextDisplayFormatBottom();

	/**
	 * The SimpleDateFormat of the text shown in the bottom header for the year view.
	 *
	 * @return SimpleDateFormat string. May not be null.
	 */
	String getYearHeaderTextDisplayFormatBottom();

	/**
	 * Whether to draw the header or not. If this returns false, all header-related settings will be ignored.
	 *
	 * @return true if to draw header. Default is true.
	 */
	boolean drawHeader();

	/**
	 * Top spacer between the top pixel and the beginning of the first event. The top pixel will be either the height of
	 * the header (if drawn) or 0 if the header is not drawn.
	 *
	 * @return top spacer value. Default is 12.
	 */
	int getEventsTopSpacer();

	/**
	 * Bottom spacer between the bottom event and the section divider when using GanttSections. For non GanttSection
	 * usage, this setting is ignored.
	 *
	 * @return bottom spacer value. Default is 12.
	 */
	int getEventsBottomSpacer();

	/**
	 * The height of the bar drawn between sections.
	 *
	 * @return height. Default is 5.
	 */
	int getSectionBarDividerHeight();

	/**
	 * Width of the section bar.
	 *
	 * @return width. Default is 20.
	 */
	int getSectionBarWidth();

	/**
	 * Minimum height of sections. Normally the minimum height is calculated by:
	 * <p>
	 * 1. Space the section name takes up vertically<br>
	 * 2. Space all contained events take up including various event spacers.
	 * <p>
	 * If the two calculations above are smaller than the value returned by this method, the height returned from this
	 * method will be used.
	 *
	 * @return minimum section height (larger or equals to zero). Default is 80.
	 */
	int getMinimumSectionHeight();

	/**
	 * Advanced tooltips are shells and by default they pop up where the mouse pointer is. To not block the pointer, an
	 * offset is used.
	 *
	 * @return horizontal offset. Default is 15.
	 */
	int getAdvancedTooltipXOffset();

	/**
	 * Whether to use Advanced Tooltips by default.
	 *
	 * @return True whether to use advanced tooltips. Default is true.
	 */
	boolean getUseAdvancedTooltips();

	/**
	 * The keyboard modifier key (also known as a hint) used to determine when it's a drag-all-linked-events
	 * {@link ISettings#moveLinkedEventsWhenEventsAreMoved()} event or just a normal drag.
	 *
	 * @return Keyboard modifier. Default is <code>SWT.SHIFT</code> (the shift key)
	 * @see ISettings#moveLinkedEventsWhenEventsAreMoved()
	 */
	int getDragAllModifierKey();

	/**
	 * The keyboard modifier key combined with the scroll wheel to make the chart zoom.
	 *
	 * @return Keyboard modifier. Default is <code>SWT.MOD1</code> (usually the <code>CTRL</code> key)
	 */
	int getZoomWheelModifierKey();

	/**
	 * Locale used for calendars and wherever needed.
	 *
	 * @return Locale. Default is Locale.getDefault().
	 */
	Locale getDefaultLocale();

	/**
	 * See IGanttEventListener's lastDraw(GC gc) method method info.
	 *
	 * @return true whether to enable the last draw functionality. Default is false.
	 * @see IGanttEventListener#lastDraw(org.eclipse.swt.graphics.GC)
	 */
	boolean enableLastDraw();

	/**
	 * If you plan on using reverse dependencies, you may want to flag this to true. This makes normal connections draw
	 * the arrow to the top left corner of the target event, and if it's a reverse dependency, it draw the arrow to the
	 * bottom left of target events. That way any overlapping event don't have arrows drawing in the exact same spot
	 * which makes it much easier to view the chart.
	 *
	 * @return true to split arrows. Default is false.
	 */
	boolean useSplitArrowConnections();

	/**
	 * If you plan on using reverse dependencies, this value will be interesting. If this value returns 0 and you have
	 * both a normal-direction dependency and a reverse one, their vertical lines will overlap. By setting this value to
	 * greater than zero that vertical line will be spaced out for reverse dependencies. The value is how much extra it
	 * will be spaced.
	 *
	 * @return spacer value. Default is 2.
	 */
	int getReverseDependencyLineHorizontalSpacer();

	/**
	 * The vertical lines are the lines that divide days and weeks etc.
	 *
	 * @see GanttComposite#setDrawVerticalLinesOverride(Boolean)
	 * @return true if to draw vertical lines. Default is true.
	 */
	boolean drawVerticalLines();

	/**
	 * Whether to draw horizontal lines between the events. Useful for where you have a tree/table on the left and wish
	 * events to be easier to see when they are lined up with the tree items.
	 *
	 * @see GanttComposite#setDrawHorizontalLinesOverride(Boolean)
	 * @return true to show horizontal lines. Default is false;
	 */
	boolean drawHorizontalLines();

	/**
	 * Which side the section bar should be drawn on. <br>
	 * <br>
	 * You may use one of <code>SWT.LEFT</code> or <code>SWT.RIGHT</code>.
	 *
	 * @return section bar side. Default is <code>SWT.LEFT</code>.
	 */
	int getSectionSide();

	/**
	 * When dates are locked down to a certain day, the chart will draw a special marker on the lock start and end dates
	 * to point out that it's locked between certain constraints.
	 *
	 * @return Whether to draw markers for date-range-locked events. Default is true.
	 */
	boolean drawLockedDateMarks();

	/**
	 * Whether to show a date tooltip when scrolling horizontally (changing dates) and vertically. The tooltip will show
	 * just above the bottom horizontal toolbar. Note that if showDateTips() returns false, this tip will not show.
	 *
	 * @return true whether to show date tooltips when scrolling through dates. Default is true.
	 * @see #showDateTips()
	 */
	boolean showDateTipsOnScrolling();

	// fix to bugzilla #236852
	/**
	 * Whether the GanttSection bar should draw all the way down or not.
	 *
	 * @return true to draw all the way down. Default is false.
	 */
	boolean drawGanttSectionBarToBottom();

	// fix to bugzilla #236852
	/**
	 * Whether to draw fills and vertical lines etc to the bottom when GanttSections are used.
	 *
	 * @return true to draw everything all the way down. Default is false.
	 */
	boolean drawFillsToBottomWhenUsingGanttSections();

	/**
	 * Whether the header should always be visible regardless of vertical scroll position. Basically a "fixed header"
	 * feature.
	 *
	 * @return true whether to lock the header. Default is false.
	 */
	boolean lockHeaderOnVerticalScroll();

	/**
	 * Whether to show the default set of menu items on the right click menus of events. If false, the menu will be
	 * blank allowing you to set all items from scratch.
	 *
	 * @return true to show default menu items along with custom ones on right click. Default is true.
	 * @see #showMenuItemsOnRightClick()
	 * @see #showPropertiesMenuOption()
	 * @see #showDeleteMenuOption()
	 * @see GanttEvent#getMenu()
	 */
	boolean showDefaultMenuItemsOnEventRightClick();

	/**
	 * Whether scopes can show a right click menu or not. By default scopes are non-active objects that simply draw
	 * according to their children. By allowing a menu to be shown on the scope you can still perform custom events if
	 * you so wish (such as show/hide all children).
	 *
	 * @return true to allow menus on scopes. Default is false.
	 * @see GanttEvent#getMenu()
	 * @see GanttEvent#showAllChildren()
	 * @see GanttEvent#hideAllChildren()
	 */
	boolean allowScopeMenu();

	/**
	 * Whether selecting dates in the header is allowed by clicking the date. Events will be fired on selection events.
	 * Note that not all views have this feature, only those that actually show full dates, as that's where it makes
	 * most sense.
	 *
	 * @return true to allow header selection. Default is true.
	 */
	boolean allowHeaderSelection();

	/**
	 * When you zoom in with the mouse, it can either act as a normal zoom (uses leftmost date as start date) or it can
	 * zoom in where the mouse pointer is at the time of the zoom in. For some the first make more sense than the other
	 * and vice versa. Default is that the zoom in is where the mouse pointer is (true).
	 */
	boolean zoomToMousePointerDateOnWheelZooming();

	/**
	 * It's highly suggested you use the default implementation of this method. Basically, this method needs to return a
	 * zeroed-down calendar (hours, minutes etc) that will be used as the D-day root calendar. As D-days aren't dates
	 * (except internally), this Calendar simply needs to be any "stable" date. By default it uses January 1st, [current
	 * year], 0h 0m 0s 0ms. (it uses the current year as days-between calculations otherwise get very large).
	 *
	 * @return Calendar
	 */
	Calendar getDDayRootCalendar();

	/**
	 * The value where D-days have their "weeks" so to speak, this is used to calculate the numbers shown in both
	 * headers, where the top header is displaying each "set" whereas the bottom one shows numbers from 0 to the number
	 * returned by this method.
	 *
	 * @return Split count number. Default is 10 which shows sets from 0 to 9.
	 */
	int getDDaySplitCount();

	/**
	 * If this returns true, events are never rounded up to their nearest hour/minute when shown in the chart, but will
	 * always show down to the minute even in any mid-zoomed and fully-zoomed out view (this does not do anything to the
	 * normal minute view). Default is false.
	 *
	 * @return true to draw event location down to the hour and minute
	 */
	boolean drawEventsDownToTheHourAndMinute();

	/**
	 * If this returns true, only linked events that come after the source drag event (time/date-wise) will be
	 * moved/resized (normally all linked events are moved/resized regardless of time/date).
	 *
	 * @return true to only move/resize "later" events on dependent linked event moves/resizes. Default is false.
	 */
	boolean moveAndResizeOnlyDependentEventsThatAreLaterThanLinkedMoveEvent();

	/**
	 * In SWT 3.5 it seems the mousewheel is not interpreted the same as in previous versions. If you notice that when
	 * scrolling the mousewheel the chart does not actually scroll vertically, flag this to true to force it to scroll
	 * the chart. This is forced to true on *NIX machines (not Mac).
	 *
	 * @return true to force the mousewheel to scroll the chart. Default is false.
	 * @deprecated By default mousewheel now scrolls chart vertically on all platforms. To turn off, flag scrollChartVerticallyOnMouseWheel()
	 * @see #scrollChartVerticallyOnMouseWheel()
	 */
	@Deprecated
	boolean forceMouseWheelVerticalScroll();

	/**
	 * If the name of any section is so large that it basically defines the size of the section, this value is used to
	 * space out the text slightly so that the section borders don't hug the text. If you want to save on some real-estate
	 * set this value to 0.
	 *
	 * @return Default is 30.
	 */
	int getSectionTextSpacer();

	/**
	 * When drawing {@link GanttPhase}s this is the header height used for displaying the names of these phases.
	 *
	 * @return Default is 18
	 */
	int getPhasesHeaderHeight();

	/**
	 * When phases are resized or moved, whether an overlap resize/move should be accepted or not.
	 * If false (default), a resize will simply stop at the next phase border, whereas a move that is dropped
	 * on top of a different phase will be undone (no event is fired for this).
	 *
	 * @return Default is false
	 */
	boolean allowPhaseOverlap();

	/**
	 * What style of vertical event dragging that is enabled. For the "resistance" before a vertical drag takes
	 * place you can change this with {@link ISettings#getVerticalDragResistance()}.
	 *
	 * @return One of the options in {@link VerticalDragModes}. Default is {@link VerticalDragModes#NO_VERTICAL_DRAG}
	 */
	int getVerticalEventDragging();

	/**
	 * For events that can be dragged vertically, this is the "resistance" in pixels before the event "lets go"
	 * of it's horizontal location. Once it's let go it will stick to the mouse cursor.
	 *
	 * @return A pixel range of resistance. Default is 15px.
	 */
	int getVerticalDragResistance();

	/**
	 * Whether an insert marker should be shown for where the dragged event will end up when a vertical drag/drop
	 * is in progress.
	 *
	 * @return true to show a marker. Default is true.
	 */
	boolean onVerticalDragDropShowInsertMarker();

	// bugzilla feature request #309808
	/**
	 * Whether to allow an image to exceed the width of one day when zooming in / out.
	 *
	 * @return true to keep within day width. Default is true.
	 */
	boolean scaleImageToDayWidth();

	/**
	 * Whether arrow keys are enabled to scroll chart left/right/up/down.
	 *
	 * @return true to allow arrow keys to move the chart. Default is false.
	 */
	boolean allowArrowKeysToScrollChart();

	/**
	 * Normally a day is calculated as day_starts->day_ends, which means that to make an event that starts and ends on the same day count as "anything", +1 is
	 * added by default to make the event actually show up on the chart. Should you for some reason need to override this, change this number to 0 or whatever you may need.
	 *
	 * @return Number of days to count for a start and end date that is the same date. Default is 1.
	 */
	int getNumberOfDaysToAppendForEndOfDay();

	/**
	 * Whether the chart should scroll vertically when the mouse wheel is used. If you notice excessive scrolling on SWT versions earlier than 3.5, you may want to turn this off
	 *
	 * @return true to scroll chart vertically. Default is true.
	 */
	boolean scrollChartVerticallyOnMouseWheel();

	/**
	 * Returns the minimum zoom level.
	 * Default should return {@link ISettings#MIN_ZOOM_LEVEL}
	 *
	 * @return
	 */
	int getMinZoomLevel();

	/**
	 * Specify a period start. Returning another value than <code>null</code> will result in rendering
	 * an additional line to the chart indicating a period start in the gantt itself.
	 *
	 * @return
	 */
	Calendar getPeriodStart();

	/**
	 * Specify a period end. Returning another value than <code>null</code> will result in rendering
	 * an additional line to the chart indicating a period end in the gantt itself.
	 *
	 * @return
	 */
	Calendar getPeriodEnd();

	/**
	 * If you want to show the event String within the event rectangle by setting the horizontalTextLocation of
	 * the GanttEvent to SWT.CENTER, there are cases that break the visualization. If your event String is longer
	 * than the event in the Gantt, it will look quite strange.
	 * <p>
	 * If this method returns <code>true</code>, the AbstractPaintManager will shift the rendering of the event
	 * String to the right if the String length is greater than the event rectangle.
	 *
	 * @return <code>true</code> if the event String should be shifted, <code>false</code> if not
	 */
	boolean shiftHorizontalCenteredEventString();

	/**
	 * @return <code>true</code> to enable the menu action for adding an event to the GanttChart,
	 *         <code>false</code> if this action should not be available to the user.
	 */
	boolean enableAddEvent();

	/**
	 * Global configuration to specify if the text of GanttEvents should be rendered or not.
	 * It is also possible to configure this per GanttEvent via _showText property.
	 *
	 * @return <code>true</code> if the event text should be rendered, <code>false</code>
	 *         if not.
	 */
	boolean drawEventString();

	/**
	 * The default behaviour in GanttChart on moving an event is that only the current dragged
	 * event is moved unless you press the SHIFT key. If there are more events selected, still
	 * only the dragged one is moved if the SHIFT key is not pressed.
	 * With this configuration it is possible to specify if the old default behavior should be
	 * used or if all currently selected events should be moved even if the SHIFT key is not pressed.
	 *
	 * @return <code>true</code> if all selected events should be moved by dragging on of them
	 *         <code>false</code> if only the current dragged event should be moved.
	 */
	boolean alwaysDragAllEvents();

	/**
	 * On printing a GanttChart it is possible to select to print only the selected area.
	 * For GanttChart this means to print the currently visible area. By default currently
	 * visible means vertically AND horizontally visible. This configuration allows to
	 * specify whether the vertical part should extend to the whole chart and only the
	 * horizontal area should be limited for the visible part.
	 *
	 * @return <code>true</code> if the printed chart should contain the whole chart
	 *         vertically but only the horizontal visible part of the chart,
	 *         <code>false</code> if only the visible part of the chart should be printed,
	 *         horizontally AND vertically
	 */
	boolean printSelectedVerticallyComplete();

	/**
	 * Configure whether a footer should be added to the print pages or not.
	 * The footer contains the page number and the date when the print was
	 * requested.
	 *
	 * @return <code>true</code> if a footer should be added to the print pages,
	 *         <code>false</code> if not
	 */
	boolean printFooter();

	/**
	 * Configure whether the section bar should be rendered. It only makes sense
	 * to not render the section bar if the section details are enabled.
	 *
	 * @return <code>true</code> if the section bar should
	 *         be rendered, <code>false</code> if not
	 */
	boolean drawSectionBar();

	/**
	 * Configure whether there should be an additional area to the section bar
	 * that shows additional section detail information.
	 *
	 * @return <code>true</code> if additional section detail information should
	 *         be rendered, <code>false</code> if not
	 */
	boolean drawSectionDetails();

	/**
	 * @return The width of the section detail area.
	 */
	int getSectionDetailWidth();

	/**
	 * The section detail area title that should be rendered in the section detail area.
	 *
	 * @return String or null. Default is a bold black text showing the section name.
	 */
	String getSectionDetailTitle();

	/**
	 * The detail information that should be rendered in the section detail area.
	 *
	 * @return String or null. Default is to show the number of events in that section.
	 */
	String getSectionDetailText();

	/**
	 * Returns the custom section detail generator which generates the section detail
	 * out of custom data for a GanttSection.
	 *
	 * @return
	 */
	ISectionDetailContentReplacer getSectionDetailContentReplacer();

	/**
	 *
	 * @return <code>true</code> if there should be a "more [+]" shown in the section
	 *         detail area which allows to register a listener against to show more
	 *         informations by e.g. opening a dialog.
	 */
	boolean showSectionDetailMore();

	/**
	 * Configure whether a tooltip pops up when hovering the mouse over a holiday
	 *
	 * @return <code>true</code> to show a "holiday" popup with the configured name of the holiday,
	 *         <code>false</code> if not (default)
	 */
	boolean showHolidayToolTips();

	/**
	 * whether {@link IGanttEventListener#eventSelected(GanttEvent, java.util.List, org.eclipse.swt.events.MouseEvent)} should also
	 * be called on empty selection
	 *
	 * @return true to have the event fired. Default is false
	 */
	boolean fireEmptyEventSelection();
}
