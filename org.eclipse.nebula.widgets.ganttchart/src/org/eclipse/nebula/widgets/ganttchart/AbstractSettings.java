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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

abstract class AbstractSettings implements ISettings {

	public String getDateFormat() {
		return "MM/dd/yyyy";
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
		return 4;
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
		return 20;
	}

	public int getHeaderDayHeight() {
		return 20;
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

	public int getTextSpacer() {
		return 8;
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

	public boolean showRevisedDates() {
		return false;
	}

	public boolean showGradientBars() {
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
		return "#n# (#p#%)";
	}

	public int getRevisedLineSpacer() {
		return 3;
	}
	
	
}
