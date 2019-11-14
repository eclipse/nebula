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
 * ziogiannigmail.com - Bug 464509 - Minute View Implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.util.Calendar;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public abstract class AbstractSettings implements ISettings2 {

	public String getDateFormat() {
		return "MM/dd/yyyy";
	}

	public String getHourDateFormat() {
		return "MM/dd/yyyy HH:mm";
	}

	public String getMinuteDateFormat() {
		return "MM/dd/yyyy HH:mm:ss";
	}

	public String getWeekHeaderTextDisplayFormatTop() {
		return "MMM dd, ''yy";
	}

	public String getMonthHeaderTextDisplayFormatTop() {
		return "MMMMM ''yy";
	}

	public String getDayHeaderTextDisplayFormatTop() {
		return "MMM dd, HH:mm";
	}

	public String getMinuteHeaderTextDisplayFormatTop() {
		return "MMM dd, HH:mm";
	}

	public String getYearHeaderTextDisplayFormatTop() {
		return "yyyy";
	}

	public String getDayHeaderTextDisplayFormatBottom() {
		return "HH:mm";
	}

	public String getMinuteHeaderTextDisplayFormatBottom() {
		return "HH:mm";
	}

	public String getMonthHeaderTextDisplayFormatBottom() {
		return "MMM dd";
	}

	public String getWeekHeaderTextDisplayFormatBottom() {
		return "E";
	}

	public String getYearHeaderTextDisplayFormatBottom() {
		return "MMM";
	}

	public Color getDefaultEventColor() {
		return ColorCache.getColor(181, 180, 181);
	}

	public Color getDefaultGradientEventColor() {
		return ColorCache.getColor(235, 235, 235);
	}

	public boolean showPropertiesMenuOption() {
		return true;
	}

	public boolean showDeleteMenuOption() {
		return true;
	}

	public boolean adjustForLetters() {
		return true;
	}

	public boolean enableAutoScroll() {
		return true;
	}

	public boolean enableResizing() {
		return true;
	}

	public int getArrowConnectionType() {
		return CONNECTION_MS_PROJECT_STYLE;
	}

	public int getDayHorizontalSpacing() {
		return 3;
	}

	public int getDayVerticalSpacing() {
		return 3;
	}

	public int getDayWidth() {
		return 16;
	}

	public int getEventHeight() {
		return 12;
	}

	public int getEventPercentageBarHeight() {
		return 3;
	}

	public int getHeaderMonthHeight() {
		return 18;
	}

	public int getHeaderDayHeight() {
		return 18;
	}

	public int getInitialView() {
		return VIEW_WEEK;
	}

	public int getInitialZoomLevel() {
		return ZOOM_DAY_NORMAL;
	}

	public int getMonthDayWidth() {
		return 6;
	}

	public int getResizeBorderSensitivity() {
		return 3;
	}

	public int getTextSpacerConnected() {
		return 9;
	}

	public int getTextSpacerNonConnected() {
		return 9;
	}

	public int getYearMonthDayWidth() {
		return 3;
	}

	public boolean moveLinkedEventsWhenEventsAreMoved() {
		return true;
	}

	public boolean showArrows() {
		return true;
	}

	public boolean showBarsIn3D() {
		return true;
	}

	public boolean showBoldScopeText() {
		return true;
	}

	public boolean showDateTips() {
		return true;
	}

	public boolean showPlannedDates() {
		return false;
	}

	public boolean showGradientEventBars() {
		return true;
	}

	public boolean showNumberOfDaysOnBars() {
		return false;
	}

	public boolean showOnlyDependenciesForSelectedItems() {
		return false;
	}

	public boolean showToolTips() {
		return true;
	}

	public int getEventSpacer() {
		return 12;
	}

	public boolean enableDragAndDrop() {
		return true;
	}

	public boolean showZoomLevelBox() {
		return true;
	}

	public boolean allowInfiniteHorizontalScrollBar() {
		return true;
	}

	public boolean showResizeDateTipOnBorders() {
		return true;
	}

	public boolean allowBlankAreaDragAndDropToMoveDates() {
		return true;
	}

	public boolean allowBlankAreaVerticalDragAndDropToMoveChart() {
		return false;
	}

	public boolean flipBlankAreaDragDirection() {
		return true;
	}

	public boolean drawSelectionMarkerAroundSelectedEvent() {
		return true;
	}

	public boolean allowCheckpointResizing() {
		return false;
	}

	public boolean showMenuItemsOnRightClick() {
		return true;
	}

	public int getArrowHeadEventSpacer() {
		return 1;
	}

	public int getArrowHeadVerticalAdjuster() {
		return 0;
	}

	public Calendar getStartupCalendarDate() {
		return Calendar.getInstance(Locale.getDefault());
	}

	public int getCalendarStartupDateOffset() {
		return -4;
	}

	public boolean startCalendarOnFirstDayOfWeek() {
		return false;
	}

	public int getMoveAreaNegativeSensitivity() {
		return 6;
	}

	public boolean enableZooming() {
		return true;
	}

	public Image getLockImage() {
		return ImageCache.getImage("icons/lock_tiny.gif");
	}

	public String getTextDisplayFormat() {
		return "#name# (#pc#%)";
	}

	public int getRevisedLineSpacer() {
		return 3;
	}

	public Image getDefaultAdvandedTooltipHelpImage() {
		return null;
	}

	public Image getDefaultAdvandedTooltipImage() {
		return null;
	}

	public boolean roundHourlyEventsOffToNearestHour() {
		return false;
	}

	public String getDefaultAdvancedTooltipHelpText() {
		return null;
	}

	public String getDefaultAdvancedTooltipTitle() {
		return "\\b\\c027050082#name#";
	}

	public String getDefaultAdvancedTooltipTextExtended() {
		final StringBuffer buf = new StringBuffer();
		buf.append("\\ceRevised: #rs# - #re# (#reviseddays# day(s))\n");
		buf.append("\\c100100100Planned: #sd# - #ed# (#days# day(s))\n");
		buf.append("#pc#% complete");
		return buf.toString();// "\\ceStart Date: \\b#sd#\nEnd Date: \\b#ed#\nRevised Start: \\b#rs#\nRevised End: \\b#re#\nDay Span: \\b#days# days\nPercent Complete: \\b#pc#%";
	}

	public String getDefaultAdvancedTooltipText() {
		final StringBuffer buf = new StringBuffer();
		buf.append("\\cePlanned: #sd# - #ed# (#days# day(s))\n");
		buf.append("\\c100100100#pc#% complete");
		return buf.toString();
	}

	public int getTodayLineStyle() {
		return SWT.LINE_SOLID;
	}

	public int getTodayLineWidth() {
		return 2;
	}

	public int getTodayLineVerticalOffset() {
		return getHeaderMonthHeight();
	}

	public int getVerticalTickMarkOffset() {
		return getHeaderMonthHeight() - 5 > 0 ? getHeaderMonthHeight() - 5 : 0;
	}

	public boolean drawHeader() {
		return true;
	}

	public int getEventsTopSpacer() {
		return 12;
	}

	public int getEventsBottomSpacer() {
		return 12;
	}

	public int getSectionBarDividerHeight() {
		return 5;
	}

	public int getSectionBarWidth() {
		return 20;
	}

	public int getMinimumSectionHeight() {
		return 80;
	}

	public boolean drawFullPercentageBar() {
		return true;
	}

	public int getPercentageBarAlpha() {
		return 255;
	}

	public int getRemainderPercentageBarAlpha() {
		return 70;
	}

	public int getAdvancedTooltipXOffset() {
		return 15;
	}

	public int getDragAllModifierKey() {
		return SWT.SHIFT;
	}

	public int getZoomWheelModifierKey() {
		return SWT.MOD1;
	}

	public Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	public boolean getUseAdvancedTooltips() {
		return true;
	}

	public boolean enableLastDraw() {
		return false;
	}

	public boolean useSplitArrowConnections() {
		return true;
	}

	public int getReverseDependencyLineHorizontalSpacer() {
		return 2;
	}

	public boolean drawVerticalLines() {
		return true;
	}

	public boolean drawHorizontalLines() {
		return false;
	}

	public int getSectionSide() {
		return SWT.LEFT;
	}

	public boolean drawLockedDateMarks() {
		return true;
	}

	public boolean showDateTipsOnScrolling() {
		return true;
	}

	public boolean drawFillsToBottomWhenUsingGanttSections() {
		return false;
	}

	public boolean drawGanttSectionBarToBottom() {
		return false;
	}

	public boolean lockHeaderOnVerticalScroll() {
		return false;
	}

	public boolean showDefaultMenuItemsOnEventRightClick() {
		return true;
	}

	public boolean allowScopeMenu() {
		return false;
	}

	public boolean allowHeaderSelection() {
		return true;
	}

	public boolean zoomToMousePointerDateOnWheelZooming() {
		return true;
	}

	public Calendar getDDayRootCalendar() {
		final Calendar mDDayCalendar = Calendar.getInstance(getDefaultLocale());
		mDDayCalendar.set(Calendar.YEAR, mDDayCalendar.get(Calendar.YEAR));
		mDDayCalendar.set(Calendar.MONTH, Calendar.JANUARY);
		mDDayCalendar.set(Calendar.DATE, 1);
		mDDayCalendar.set(Calendar.HOUR, 0);
		mDDayCalendar.set(Calendar.MINUTE, 0);
		mDDayCalendar.set(Calendar.SECOND, 0);
		mDDayCalendar.set(Calendar.MILLISECOND, 0);
		return mDDayCalendar;
	}

	public int getDDaySplitCount() {
		return 10;
	}

	public boolean drawEventsDownToTheHourAndMinute() {
		return false;
	}

	public boolean moveAndResizeOnlyDependentEventsThatAreLaterThanLinkedMoveEvent() {
		return false;
	}

	public boolean forceMouseWheelVerticalScroll() {
		return false;
	}

	public int getSectionTextSpacer() {
		return 30;
	}

	public int getPhasesHeaderHeight() {
		return 18;
	}

	public boolean allowPhaseOverlap() {
		return false;
	}

	public int getVerticalEventDragging() {
		return VerticalDragModes.NO_VERTICAL_DRAG;
	}

	public int getVerticalDragResistance() {
		return 15;
	}

	public boolean onVerticalDragDropShowInsertMarker() {
		return true;
	}

	public boolean scaleImageToDayWidth() {
		return true;
	}

	public boolean allowArrowKeysToScrollChart() {
		return false;
	}

	public int getNumberOfDaysToAppendForEndOfDay() {
		return 1;
	}

	public boolean scrollChartVerticallyOnMouseWheel() {
		return true;
	}

	public IToolTipContentReplacer getToolTipContentReplacer() {
		return null;
	}

	public int getMinZoomLevel() {
		return ISettings.MIN_ZOOM_LEVEL;
	}

	public Calendar getPeriodStart() {
		return null;
	}

	public Calendar getPeriodEnd() {
		return null;
	}

	public boolean shiftHorizontalCenteredEventString() {
		return false;
	}

	public boolean enableAddEvent() {
		return false;
	}

	public boolean drawEventString() {
		return true;
	}

	public boolean alwaysDragAllEvents() {
		return false;
	}

	public boolean printSelectedVerticallyComplete() {
		return false;
	}

	public boolean printFooter() {
		return true;
	}

	public boolean drawSectionBar() {
		return true;
	}

	public boolean drawSectionDetails() {
		return false;
	}

	public int getSectionDetailWidth() {
		return 100;
	}

	public String getSectionDetailTitle() {
		return "\\b\\s8\\ce#name#";
	}

	public String getSectionDetailText() {
		return "\\ceEvents: #ne#";
	}

	public ISectionDetailContentReplacer getSectionDetailContentReplacer() {
		return null;
	}

	public boolean showSectionDetailMore() {
		return false;
	}

	public boolean showHolidayToolTips() {
		return false;
	}

	public boolean enableTodayLineUpdater() {
		return true;
	}

	public boolean fireEmptyEventSelection() {
		return false;
	}
}
