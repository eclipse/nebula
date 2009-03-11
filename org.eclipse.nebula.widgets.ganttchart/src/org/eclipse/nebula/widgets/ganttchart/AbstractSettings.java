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

import java.util.Calendar;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

abstract class AbstractSettings implements ISettings {

	public String getDateFormat() {
		return "MM/dd/yyyy";
	}

	public String getHourDateFormat() {
		return "MM/dd/yyyy HH:mm";
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

	public String getYearHeaderTextDisplayFormatTop() {
		return "yyyy";
	}
	
	public String getDayHeaderTextDisplayFormatBottom() {
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

	public boolean consumeEventWhenOutOfRange() {
		return false;
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
		StringBuffer buf = new StringBuffer();
		buf.append("\\ceRevised: #rs# - #re# (#reviseddays# days)\n");
		buf.append("\\c100100100Planned: #sd# - #ed# (#days# days)\n");
		buf.append("#pc#% complete");
		return buf.toString();//"\\ceStart Date: \\b#sd#\nEnd Date: \\b#ed#\nRevised Start: \\b#rs#\nRevised End: \\b#re#\nDay Span: \\b#days# days\nPercent Complete: \\b#pc#%";
	}

	public String getDefaultAdvancedTooltipText() {
		StringBuffer buf = new StringBuffer();
		buf.append("\\cePlanned: #sd# - #ed# (#days# days)\n");
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
		return (getHeaderMonthHeight()-5 > 0 ? getHeaderMonthHeight()-5 : 0);
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

	public boolean useFastDraw() {
		return true;
	}

	public int getSectionSide() {
		return SWT.LEFT;
	}

	public boolean drawLockedDateMarks() {
		return false;
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
		Calendar mDDayCalendar = Calendar.getInstance(getDefaultLocale());
		mDDayCalendar.set(Calendar.YEAR, 2000);
		mDDayCalendar.set(Calendar.MONTH, Calendar.JANUARY);
		mDDayCalendar.set(Calendar.DATE, 1);
		mDDayCalendar.set(Calendar.MINUTE, 0);
		mDDayCalendar.set(Calendar.SECOND, 0);
		mDDayCalendar.set(Calendar.MILLISECOND, 0);
		return mDDayCalendar;
	}

	public boolean drawEventsDownToTheHourAndMinute() {
		return false;
	}


	
/*	public List getHeaderLevels() {
		List l = new ArrayList();
		
		HeaderSet hs = new HeaderSet();
		HeaderLevel dayNormalTop = new HeaderLevel(Calendar.DATE, HeaderLevel.OFFSET_DATE, 1, 16*7, "MMM dd, ''yy", HeaderLevel.SEPARATOR_BOTTOM_TICK, Locale.getDefault());
		HeaderLevel dayNormalBottom = new HeaderLevel(Calendar.DATE, HeaderLevel.OFFSET_DATE, 1, 16, "E", HeaderLevel.SEPARATOR_FULL_LINE, Locale.getDefault());
		hs.addHeaderLevel(dayNormalTop);
		hs.addHeaderLevel(dayNormalBottom);
		l.add(hs);

		hs = new HeaderSet();
		HeaderLevel hoursNormalTop = new HeaderLevel(Calendar.HOUR_OF_DAY, 1, 32*24, "MMM dd, HH:mm", HeaderLevel.SEPARATOR_BOTTOM_TICK, Locale.getDefault());
		HeaderLevel hoursNormalBottom = new HeaderLevel(Calendar.HOUR_OF_DAY, 1, 32, "HH:mm", HeaderLevel.SEPARATOR_FULL_LINE, Locale.getDefault());
		hs.addHeaderLevel(hoursNormalTop);
		hs.addHeaderLevel(hoursNormalBottom);
		l.add(hs);
		
		return l;
	}
*/
	
}
