/*******************************************************************************
 * Copyright (c) 2005, 2007 eric.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eric Wuillai (eric@wdev91.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.datechooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * Theme for <code>DateChooser</code> widgets. Defines the GUI settings
 * (colors, font...) applied to the differents elements of the calendar.<p>
 * 
 * Some default themes are provided as constants of this class. The GRAY theme
 * is defined as the default for all new calendars. This can be changed with
 * the <code>setDefaultTheme()</code> method.<p>
 * 
 * To define a new theme, instantiate a new <code>DateChooserTheme</code>. It
 * takes by default the same settings as the GRAY theme. Each setting can then
 * be changed by the corresponding setter. For colors, setters exist under 2
 * forms:
 * <ul>
 *   <li>A setter taking a <code>Color</code> parameter</li>
 *   <li>A setter taking an <code>int</code> parameter, corresponding to the SWT
 *     colors code</li>
 * </ul>
 */
public class DateChooserTheme {
	/** GRAY theme. Default */
	public static final DateChooserTheme GRAY;
	/** BLUE theme */
	public static final DateChooserTheme BLUE;
	/** YELLOW theme */
	public static final DateChooserTheme YELLOW;

	/** Default theme */
	protected static DateChooserTheme defaultTheme;

	// ----- Colors -----
	/** Color for header background */
	Color headerBackground;
	/** Color for header foreground */
	Color headerForeground;
	/** Color for grid header background */
	Color gridHeaderBackground;
	/** Color for grid header foreground */
	Color gridHeaderForeground;
	/** Color for grid lines */
	Color gridLinesColor;

	/** Color for day cells background */
	Color dayCellBackground;
	/** Color for worked days cells foreground */
	Color dayCellForeground;
	/** Color for selected cell background */
	Color selectedBackground;
	/** Color for selected cell foreground */
	Color selectedForeground;
	/** Color for today cell background */
	Color todayBackground;
	/** Color for today cell foreground */
	Color todayForeground;
	/** Color for adjascent days foreground */
	Color extraMonthForeground;
	/** Color for week end foreground */
	Color weekendForeground;
	/** Color for focus box */
	Color focusColor;

	// ----- Other GUI settings -----
	/** Flag to set grid visible or not */
	boolean gridVisible = true;
	/** Horizontal cell padding */
	int cellPadding = 2;
	/** Font */
	Font font = null;

	static {
		GRAY	 = new DateChooserTheme();
		BLUE	 = createBlueTheme();
		YELLOW = createYellowTheme();
		defaultTheme = GRAY;
	}

	/**
	 * Constructs a new instance of this class. All colors elements are
	 * initialized with the default GRAY theme.
	 */
	public DateChooserTheme() {
		Display display = Display.getCurrent();
		this.headerBackground			= display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		this.headerForeground			= display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
		this.gridHeaderBackground = this.headerBackground;
		this.gridHeaderForeground = this.headerForeground;
		this.gridLinesColor				= display.getSystemColor(SWT.COLOR_GRAY);
		this.dayCellBackground		= display.getSystemColor(SWT.COLOR_WHITE);
		this.dayCellForeground		= this.headerForeground;
		this.selectedBackground		= display.getSystemColor(SWT.COLOR_YELLOW);
		this.selectedForeground		= this.headerForeground;
		this.todayBackground			= this.headerBackground;
		this.todayForeground			= this.headerForeground;
		this.extraMonthForeground	= this.gridLinesColor;
		this.weekendForeground		= display.getSystemColor(SWT.COLOR_DARK_RED);
		this.focusColor						= display.getSystemColor(SWT.COLOR_RED);
	}

	/**
	 * Creates the BLUE theme.
	 * 
	 * @return BLUE theme
	 */
	private static DateChooserTheme createBlueTheme() {
		Display display  = Display.getCurrent();
		DateChooserTheme theme = new DateChooserTheme();

		theme.headerBackground		 = new Color(display, 170, 190, 220);
		theme.gridHeaderBackground = theme.headerBackground;
		theme.dayCellBackground		 = new Color(display, 190, 220, 240);
		theme.extraMonthForeground = display.getSystemColor(SWT.COLOR_DARK_GRAY);
		theme.weekendForeground		 = display.getSystemColor(SWT.COLOR_RED);
		theme.todayBackground			 = display.getSystemColor(SWT.COLOR_WHITE);

		return theme;
	}
	/**
	 * Creates the YELLOW theme.
	 * 
	 * @return YELLOW theme
	 */
	private static DateChooserTheme createYellowTheme() {
		Display display  = Display.getCurrent();
		DateChooserTheme theme = new DateChooserTheme();

		theme.headerBackground		 = new Color(display, 190, 180, 60);
		theme.gridHeaderBackground = theme.headerBackground;
		theme.dayCellBackground		 = new Color(display, 255, 255, 170);
		theme.extraMonthForeground = display.getSystemColor(SWT.COLOR_DARK_GRAY);
		theme.weekendForeground		 = display.getSystemColor(SWT.COLOR_RED);
		theme.todayBackground			 = display.getSystemColor(SWT.COLOR_GRAY);
		theme.selectedBackground	 = display.getSystemColor(SWT.COLOR_DARK_GREEN);
		theme.selectedForeground	 = display.getSystemColor(SWT.COLOR_WHITE);

		return theme;
	}

	/**
	 * Returns the default theme.
	 * 
	 * @return default theme
	 */
	public static DateChooserTheme getDefaultTheme() {
		return defaultTheme;
	}

	/**
	 * Sets a new default theme for all new <code>DateChooser</code> widgets.
	 * 
	 * @param defaultTheme new default theme
	 */
	public static void setDefaultTheme(DateChooserTheme defaultTheme) {
		DateChooserTheme.defaultTheme = defaultTheme;
	}

	public void setDayCellBackground(Color dayCellBackground) {
		this.dayCellBackground = dayCellBackground;
	}

	public void setDayCellBackground(int dayCellBackground) {
		this.dayCellBackground = Display.getCurrent().getSystemColor(dayCellBackground);
	}

	public void setDayCellForeground(Color dayCellForeground) {
		this.dayCellForeground = dayCellForeground;
	}

	public void setDayCellForeground(int dayCellForeground) {
		this.dayCellForeground = Display.getCurrent().getSystemColor(dayCellForeground);
	}

	public void setGridHeaderBackground(Color gridHeaderBackground) {
		this.gridHeaderBackground = gridHeaderBackground;
	}

	public void setGridHeaderBackground(int gridHeaderBackground) {
		this.gridHeaderBackground = Display.getCurrent().getSystemColor(gridHeaderBackground);
	}

	public void setGridHeaderForeground(Color gridHeaderForeground) {
		this.gridHeaderForeground = gridHeaderForeground;
	}

	public void setGridHeaderForeground(int gridHeaderForeground) {
		this.gridHeaderForeground = Display.getCurrent().getSystemColor(gridHeaderForeground);
	}

	public void setGridLinesColor(Color gridLinesColor) {
		this.gridLinesColor = gridLinesColor;
	}

	public void setGridLinesColor(int gridLinesColor) {
		this.gridLinesColor = Display.getCurrent().getSystemColor(gridLinesColor);
	}

	public void setHeaderBack(Color headerBackground) {
		this.headerBackground = headerBackground;
	}

	public void setHeaderBack(int headerBackground) {
		this.headerBackground = Display.getCurrent().getSystemColor(headerBackground);
	}

	public void setHeaderForg(Color headerForeground) {
		this.headerForeground = headerForeground;
	}

	public void setHeaderForg(int headerForeground) {
		this.headerForeground = Display.getCurrent().getSystemColor(headerForeground);
	}

	public void setSelectedBackground(Color selectedBackground) {
		this.selectedBackground = selectedBackground;
	}

	public void setSelectedBackground(int selectedBackground) {
		this.selectedBackground = Display.getCurrent().getSystemColor(selectedBackground);
	}

	public void setSelectedForeground(Color selectedForeground) {
		this.selectedForeground = selectedForeground;
	}

	public void setSelectedForeground(int selectedForeground) {
		this.selectedForeground = Display.getCurrent().getSystemColor(selectedForeground);
	}

	public void setTodayBackground(Color todayBackground) {
		this.todayBackground = todayBackground;
	}

	public void setTodayBackground(int todayBackground) {
		this.todayBackground = Display.getCurrent().getSystemColor(todayBackground);
	}

	public void setTodayForeground(Color todayForeground) {
		this.todayForeground = todayForeground;
	}

	public void setTodayForeground(int todayForeground) {
		this.todayForeground = Display.getCurrent().getSystemColor(todayForeground);
	}

	public void setExtraMonthForeground(Color extraMonthForeground) {
		this.extraMonthForeground = extraMonthForeground;
	}

	public void setAdjascentForeground(int extraMonthForeground) {
		this.extraMonthForeground = Display.getCurrent().getSystemColor(extraMonthForeground);
	}

	public void setWeekendForeground(Color weekendForeground) {
		this.weekendForeground = weekendForeground;
	}

	public void setWeekendForeground(int weekendForeground) {
		this.weekendForeground = Display.getCurrent().getSystemColor(weekendForeground);
	}

	public void setFocusColor(Color focusColor) {
		this.focusColor = focusColor;
	}

	public void setFocusColor(int focusColor) {
		this.focusColor = Display.getCurrent().getSystemColor(focusColor);
	}

	public void setGridVisible(boolean gridVisible) {
		this.gridVisible = gridVisible;
	}

	public void setCellPadding(int cellPadding) {
		this.cellPadding = cellPadding;
	}

	public void setFont(Font font) {
		this.font = font;
	}
}
