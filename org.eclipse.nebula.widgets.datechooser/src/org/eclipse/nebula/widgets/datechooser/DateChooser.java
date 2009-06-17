/*******************************************************************************
 * Copyright (c) 2005, 2009 Eric Wuillai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eric Wuillai (eric@wdev91.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.datechooser;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TypedListener;

/**
 * Calendar widget. Presents the monthly view of a calendar for date picking.<p>
 * 
 * Calendar is composed of a header and a grid for date selection. The header
 * display the current month and year, and the two buttons for navigation
 * (previous and next month). An optional footer display the today date.<p>
 * 
 * Features:
 * <ul>
 *   <li>Month names, weekday names and first day of week depend of the locale
 *     setted on the calendar</li>
 *   <li>GUI (colors, font...) are customizable through themes (3 provided)</li>
 *   <li>Shows days from adjacent months</li>
 *   <li>Optionnally shows weeks numbers</li>
 *   <li>Optionnel footer showing today date</li>
 *   <li>Multi selection and interval selection</li>
 *   <li>Keyboard support.</li>
 * </ul><p>
 * 
 * To know which dates have been selected in the calendar, there is two means :
 * <ul>
 *   <li>The <code>getSelectedDate()</code> method returns the currently
 *     selected date (single selection). The <code>getSelectedDates()</code>
 *     returns a list of all selected dates (multi selection).</li>
 *   <li>Add a <code>CalendarListener</code> to the calendar. This listener
 *     will be notified of selection. <code>event.data</code> contain the
 *     selection if in single selection mode.</li>
 * </ul><p>
 * 
 * Keyboard navigation :
 * <ul>
 * 	 <li>Arrows: Change the focus cell.</li>
 * 	 <li>Page UP / DOWN: Next / previous month.</li>
 * 	 <li>Ctrl-Page UP / DOWN: Next / previous year.</li>
 * 	 <li>SPACE: Select the cell having the focus. If in multi selection mode,
 *     all previous selected dates are cleared.</li>
 * 	 <li>Ctrl-SPACE: Add the cell having the focus to the selection (multi
 *     selection mode).</li>
 * 	 <li>Shift-SPACE: Select all dates in the interval between the current
 *     focus and the last selected date.</li>
 *   <li>HOME: Set the focus on the today date, and select it if
 *     autoselectOnFooter is true.</li>
 * </ul>
 */
public class DateChooser extends Composite {
	/** Bundle name constant */
	public static final String BUNDLE_NAME = "org.eclipse.nebula.widgets.datechooser.resources"; //$NON-NLS-1$
	/** Header spacing constant */
	protected static final int HEADER_SPACING = 3;
	/** Value to use when there is none internal widget having the focus */
	protected static final int NOFOCUS = -100;

	// ----- Selection -----
	/** Multi selection flag */
	protected boolean multi;
	/** Selection */
	protected List selection;
	/** Begin date of selection interval */
	protected Date beginInterval;
	/** End date of selection interval */
	protected Date endInterval;
	/** If true, the today date is automatically selected on footer selection event */
	protected boolean autoSelectOnFooter = false;

	// ----- Calendar header -----
	/** Header panel */
	protected Composite monthHeader;
	/** Navigation button for previous month */
	protected Button prevMonth;
	/** Label for the display of current month and year (corresponding to curDate) */
	protected Label currentMonth;
	/** Navigation button for next month */
	protected Button nextMonth;
	/** Popup menu for month selection */
	protected Menu monthsMenu;

	// Calendar grid -----
	/** Grid header panel */
	protected Composite gridHeader;
	/** Grid headers, displaying weekday names */
	protected Label[] headers;
	/** Grid panel */
	protected Composite grid;
	/** Grid cells */
	protected Cell[] days;
	/** Weeks number panels */
	protected Composite weeksPanel;
	/** Weeks numbers cells */
	protected Cell[] weeks;
	/** Index in the grid of the first day of displayed month */
	protected int firstDayIndex;

	// ----- Calendar footer -----
	/** Footer panel */
	protected Composite footer;
	/** Today label of the footer */
	protected Label todayLabel;

	// ---- Localization -----
	/** Locale used for localized names and formats */
	protected Locale locale;
	/** Format for the display of month and year in the header */
	protected SimpleDateFormat df1;
	/** Format for the today date in the footer */
	protected DateFormat df2;
	/** Index of the first day of week */
	protected int firstDayOfWeek;
	/** Minimal number of days in the first week */
	protected int minimalDaysInFirstWeek;
	/** Resources bundle */
	protected ResourceBundle resources;

	// ----- Dates -----
	/** Date of 1st day of the currently displayed month */
	protected Calendar currentMonthCal;
	/** The today date */
	protected Calendar todayCal;

	// ----- GUI settings -----
	/** Calendar theme */
	protected DateChooserTheme theme;
	/** Flag to set grid visible or not */
	protected boolean gridVisible = true;
	/** Flag to set weeks numbers visibles or not */
	protected boolean weeksVisible = false;
	/** Flag to set footer visible or not */
	protected boolean footerVisible = false;
	/** Flag to set navigation enabled or not */
	protected boolean navigationEnabled = true;
	/** If true, change the current month if an adjacent day is clicked */
	protected boolean autoChangeOnAdjacent = true;

	// ----- GUI interactions -----
	/** Listener for all internal events */
	protected Listener listener;
	/** Listener for external events */
	protected Listener filter;
	/** Flag indixating if the calendar has the focus */
	protected boolean hasFocus = false;
	/** Index of the focus in the grid */
	protected int focusIndex = NOFOCUS;

	private boolean resize = false;
	private int redraw = 0;

	/**
	 * Defines a grid cell. Each cell displays a day or week number.
	 */
	protected class Cell {
		Label label;
		int index;
		Calendar cal;
		boolean weekend;
		int adjacent;
		boolean selected;
		boolean today;
		Cell(Composite parent, int idx) {
			label = new Label(parent, SWT.CENTER);
			index = idx;
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			label.addListener(SWT.MouseDown, listener);
			label.setData(this);
		}
	}

	/**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance.<p>
   * The calendar is initialized by default with the default Locale, and the
   * current date for today and selected date attributes.
   * 
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   */
	public DateChooser(Composite parent, int style) {
		super(parent, style);
		multi			= (style & SWT.MULTI) > 0;
		selection = new ArrayList();
		initialize();
		setLocale(Locale.getDefault());
		setTheme(DateChooserTheme.getDefaultTheme());
		setTodayDate(new Date());
		setCurrentMonth(todayCal.getTime());
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's selection changes, by sending
	 * it one of the messages defined in the <code>SelectionListener</code>
	 * interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the dates selection changes.
	 * </p>
	 *
	 * @param lsnr the listener which should be notified
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 */
	public void addSelectionListener(SelectionListener lsnr) {
		checkWidget();
		if ( lsnr == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(lsnr);
		addListener(SWT.Selection, typedListener);
	}

	/**
	 * Manages navigation buttons events.
	 * 
	 * @param event event
	 */
	protected void buttonsEvent(Event event) {
		switch ( event.type ) {
			case SWT.MouseUp :
				Rectangle r = ((Control) event.widget).getBounds();
				if ( event.x < 0 || event.x >= r.width || event.y < 0
						 || event.y >= r.height ) return;
				boolean ctrl = (event.stateMask & SWT.CTRL) != 0;
				if ( event.widget == prevMonth ) {
					changeCurrentMonth(ctrl ? -12 : -1);
				} else if ( event.widget == nextMonth ) {
					changeCurrentMonth(ctrl ? 12 : 1);
				}
				break;

			case SWT.FocusIn :
				handleFocus(event.type);
				break;
		}
	}

	/**
	 * Manages event at the calendar level.
	 * 
	 * @param event event
	 */
	protected void calendarEvent(Event event) {
		switch ( event.type ) {
			case SWT.Traverse :
				switch (event.detail) {
					case SWT.TRAVERSE_ARROW_NEXT :
					case SWT.TRAVERSE_ARROW_PREVIOUS :
					case SWT.TRAVERSE_PAGE_NEXT :
					case SWT.TRAVERSE_PAGE_PREVIOUS :
						event.doit = false;
						break;
					default :
						event.doit = true;
				}
				break;

			case SWT.FocusIn :
				handleFocus(event.type);
				break;

			case SWT.KeyDown :
				boolean ctrl = (event.stateMask & SWT.CTRL) != 0;
				switch ( event.keyCode ) {
					case SWT.ARROW_LEFT :
					  if ( event.stateMask != 0 ) return;
						setFocus(focusIndex - 1);
						break;
					case SWT.ARROW_RIGHT :
            if ( event.stateMask != 0 ) return;
						setFocus(focusIndex + 1);
						break;
					case SWT.ARROW_UP :
            if ( event.stateMask != 0 ) return;
						setFocus(focusIndex - 7);
						break;
					case SWT.ARROW_DOWN :
            if ( event.stateMask != 0 ) return;
						setFocus(focusIndex + 7);
						break;
					case SWT.PAGE_DOWN :
						if ( event.stateMask != 0 || ! navigationEnabled ) return;
						changeCurrentMonth(ctrl ? 12 : 1);
						break;
					case SWT.PAGE_UP :
						if ( event.stateMask != 0 || ! navigationEnabled ) return;
						changeCurrentMonth(ctrl ? -12 : -1);
						break;
					case ' ' :
						select(focusIndex, event.stateMask);
						break;
					case SWT.HOME :
            if ( event.stateMask != 0 ) return;
						setFocusOnToday(autoSelectOnFooter);
						break;
					default :
					  return;
				}
				if ( hasFocus ) {
					grid.redraw();
				}
				break;

			case SWT.Dispose :
				Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				display.removeFilter(SWT.KeyDown, filter);
				hasFocus = false;
				break;
		}
	}

	/**
	 * Displays a new month in the grid. The new month is specified by delta from
	 * the currently displayed one.
	 * 
	 * @param add delta from the current month
	 */
	protected void changeCurrentMonth(int add) {
		if ( add == 0 ) {
			return;
		}
		currentMonthCal.add(Calendar.MONTH, add);
		refreshDisplay();
	}

	/**
	 * Clears the selection.
	 */
	public void clearSelection() {
		clearSelection(true);
	}

	/**
	 * Clears the selection. The refresh flag allowes to indicate must be
	 * refreshed or not.
	 * 
	 * @param refresh true to refresh display, else false
	 */
	protected void clearSelection(boolean refresh) {
		selection.clear();
		for (int i = 0; i < days.length; i++) {
			days[i].selected = false;
		}
		beginInterval = null;
		if ( refresh ) refreshDisplay();
	}

	/**
	 * Returns the preferred size of the receiver.
	 * 
	 * @param wHint the width hint (can be SWT.DEFAULT)
	 * @param hHint the height hint (can be SWT.DEFAULT)
	 * @param changed <code>true</code> if the control's contents have changed, and <code>false</code> otherwise
	 * @return the preferred size of the control.
	 */
	public Point computeSize(int wHint, int hHint, boolean changed) {
		if ( resize ) {
			GC gc = new GC(currentMonth);
			int width = 0;
			int colCount = weeksVisible ? 8 : 7;
			int linesCount = colCount + 1;
			String[] months = df1.getDateFormatSymbols().getMonths();
			for (int i = 0; i < months.length; i++) {
				width = Math.max(width, gc.textExtent(months[i]).x);
			}
			width += prevMonth.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed).x * 2
			 + HEADER_SPACING * 4 + gc.textExtent(" 9999").x; //$NON-NLS-1$
			width = Math.max(width, (gc.textExtent("99").x + theme.cellPadding * 2)
			                 				* colCount + linesCount); //$NON-NLS-1$
			int cw = (width - linesCount) / colCount + 1;
			width = cw * colCount + linesCount;
			((GridData) monthHeader.getLayoutData()).widthHint = width;
			((GridData) footer.getLayoutData()).widthHint = width;

			for (int i = 0; i < 7; i++) {
				((GridData) headers[i].getLayoutData()).widthHint = cw;
				((GridData) days[i].label.getLayoutData()).widthHint = cw;
			}
			((GridData) weeks[0].label.getLayoutData()).widthHint = cw;

			gc.dispose();
			resize = false;
			changed = true;
		}
		return super.computeSize(wHint, hHint, changed);
	}

	/**
	 * Creates the footer of the calendar. The footer contains the label displaying
	 * the today date. It is not visible by default and can be made visible with
	 * setFooterVisible(true).
	 */
	private void createFooter() {
		footer = new Composite(this, SWT.NONE);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		footer.setLayout(layout);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.exclude = true;
		footer.setLayoutData(data);

		todayLabel = new Label(footer, SWT.CENTER);
		todayLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		todayLabel.addListener(SWT.MouseDoubleClick, listener);
		todayLabel.addListener(SWT.MouseDown, listener);
	}

	/**
	 * Creates the grid of labels displying the days numbers. The grid is composed
	 * of a header row, displaying days initiales, and 6 row for the days numbers.
	 * All labels are empty (no text displayed), the content depending of both the
	 * locale (for first day of week) and the current displayed month.
	 */
	private void createGrid() {
		// Weeks numbers panel
		weeksPanel = new Composite(this, SWT.NONE);
		GridLayout layout1 = new GridLayout(1, false);
		layout1.horizontalSpacing = 1;
		layout1.marginWidth = 1;
		layout1.verticalSpacing = 1;
		layout1.marginTop = 1;
		layout1.marginBottom = 1;
		layout1.marginLeft = 1;
		layout1.marginHeight = 0;
		layout1.marginWidth = 0;
		weeksPanel.setLayout(layout1);
		weeks = new Cell[7];
		for (int i = 0; i < 7; i++) {
			weeks[i] = new Cell(weeksPanel, i);
		}
		GridData data1 = new GridData();
		data1.verticalSpan = 2;
		data1.exclude = true;
		weeksPanel.setLayoutData(data1);

		// Grid header panel
		gridHeader = new Composite(this, SWT.NONE);
		GridLayout layout2 = new GridLayout(7, true);
		layout2.horizontalSpacing = 1;
		layout2.marginWidth = 1;
		layout2.verticalSpacing = 1;
		layout2.marginTop = 1;
		layout2.marginHeight = 0;
		gridHeader.setLayout(layout2);
		headers = new Label[7];
		for (int i = 0; i < 7; i++) {
			headers[i] = new Label(gridHeader, SWT.CENTER);
			headers[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			headers[i].addListener(SWT.MouseDown, listener);
		}
		GridData data2 = new GridData();
		data2.horizontalSpan = 2;
		gridHeader.setLayoutData(data2);

		// Grid panel
		grid = new Composite(this, SWT.NONE);
		GridLayout layout3 = new GridLayout(7, true);
		layout3.horizontalSpacing = 1;
		layout3.marginWidth = 1;
		layout3.verticalSpacing = 1;
		layout3.marginHeight = 1;
		grid.setLayout(layout3);
		days = new Cell[42];
		for (int i = 0; i < 42; i++) {
			days[i] = new Cell(grid, i);
			days[i].label.addListener(SWT.MouseUp, listener);
		}
		GridData data3 = new GridData();
		data3.horizontalSpan = 2;
		grid.setLayoutData(data3);
		grid.addListener(SWT.Paint, listener);
	}

	/**
	 * Creates the header of the calendar. The header contains the label
	 * displaying the current month and year, and the two buttons for navigation :
	 * previous and next month.
	 */
	private void createHeader() {
		monthHeader = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = HEADER_SPACING;
		layout.marginWidth = HEADER_SPACING;
		layout.verticalSpacing = 0;
		layout.marginHeight = 3;
		monthHeader.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		monthHeader.setLayoutData(data);
		monthHeader.addListener(SWT.MouseDown, listener);

		prevMonth = new Button(monthHeader, SWT.ARROW | SWT.LEFT | SWT.FLAT);
		prevMonth.addListener(SWT.MouseUp, listener);
		prevMonth.addListener(SWT.FocusIn, listener);

		currentMonth = new Label(monthHeader, SWT.CENTER);
		currentMonth.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		currentMonth.addListener(SWT.MouseDown, listener);

		nextMonth = new Button(monthHeader, SWT.ARROW | SWT.RIGHT | SWT.FLAT);
		nextMonth.addListener(SWT.MouseUp, listener);
		nextMonth.addListener(SWT.FocusIn, listener);

		monthsMenu = new Menu(getShell(), SWT.POP_UP);
		currentMonth.setMenu(monthsMenu);
		for (int i = 0; i < 12; i++) {
			MenuItem item = new MenuItem(monthsMenu, SWT.PUSH);
			item.addListener(SWT.Selection, listener);
			item.setData(new Integer(i));
		}
		monthsMenu.addListener(SWT.Show, listener);
	}

	/**
	 * Disposes of the operating system resources associated with the receiver
	 * and all its descendents.
	 * 
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		getDisplay().removeFilter(SWT.KeyDown, filter);
		getDisplay().removeFilter(SWT.FocusIn, filter);
	  super.dispose();
  }

	/**
	 * Manages events on the footer label.
	 * 
	 * @param event event
	 */
	protected void footerEvent(Event event) {
		switch ( event.type ) {
			case SWT.MouseDoubleClick :
				setFocusOnToday(autoSelectOnFooter);
				break;
		}
	}

	/**
	 * Forces the receiver to have the keyboard focus, causing all keyboard events
	 * to be delivered to it.
	 * 
	 * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
	 */
	public boolean forceFocus() {
		checkWidget();
		if ( super.forceFocus() ) {
			handleFocus(SWT.FocusIn);
			return true;
		}
		return false;
  }

	/**
	 * Returns the cell index corresponding to the given label.
	 * 
	 * @param label label
	 * @return cell index
	 */
	private int getCellIndex(Label label) {
		for (int i = 0; i < days.length; i++) {
			if ( days[i].label == label ) return i;
		}
		return -1;
	}

	/**
	 * Returns the current displayed month.
	 * 
	 * @return Date representing current month.
	 */
	public Date getCurrentMonth() {
		checkWidget();
		return currentMonthCal.getTime();
	}

	/**
	 * Gets what the first day of the week is.
	 * 
	 * @return the first day of the week.
	 */
	public int getFirstDayOfWeek() {
		return this.firstDayOfWeek;
	}

	/**
	 * Gets what the minimal days required in the first week of the year are.
	 * 
	 * @return the minimal days required in the first week of the year.
	 */
	public int getMinimalDaysInFirstWeek() {
		return this.minimalDaysInFirstWeek;
	}

	/**
	 * Returns the selected date. If calendar is in multi selection mode, the
	 * first item of selection list is returned, with no garanty of the selection
	 * order by the user.
	 * If no selection, return <code>null</code>.
	 * 
	 * @return selected date
	 */
	public Date getSelectedDate() {
		checkWidget();
		return selection.isEmpty() ? null : (Date) selection.get(0);
	}

	/**
	 * Returns all the selected dates. The collection returned is a copy of the
	 * internal selection list.<p>
	 * 
	 * If the calendar is in single selection mode, it is preferable to use
	 * <code>getSelectedDate</code> that returns a Date value.
	 * 
	 * @return Collection of selected dates
	 */
	public Collection getSelectedDates() {
		checkWidget();
		List returnSelection = new ArrayList(selection.size());
		for (Iterator it = selection.iterator(); it.hasNext();) {
			Date d = (Date) it.next();
			if ( ! returnSelection.contains(d) ) {
				returnSelection.add(d);
			}
		}
		return returnSelection;
	}

	/**
	 * Returns the today date.
	 * 
	 * @return today date
	 */
	public Date getTodayDate() {
		checkWidget();
		return todayCal.getTime();
	}

	/**
	 * Manages events at the grid level.
	 * 
	 * @param event event
	 */
	protected void gridEvent(Event event) {
		switch ( event.type ) {
			case SWT.MouseUp : {
				Rectangle r = ((Control) event.widget).getBounds();
				if ( event.x < 0 || event.x >= r.width
						 || event.y < 0 || event.y >= r.height ) return;
				setFocus(getCellIndex((Label) event.widget));
				select(focusIndex, event.stateMask);
				break;
			}
			case SWT.Paint : {
				if ( hasFocus ) {
					if ( focusIndex < 0 ) setFocus(-1);
					Rectangle r = days[focusIndex].label.getBounds();
					event.gc.setLineWidth (1);
					event.gc.setForeground(theme.focusColor);
					event.gc.drawRectangle (r.x - 1, r.y - 1, r.width + 1, r.height + 1);
				}
				break;
			}
		}
	}

	/**
	 * Handles the focus.
	 * 
	 * @param mode SWT.FocusIn or SWT.FocusOut
	 */
	private void handleFocus(int mode) {
		switch ( mode ) {
			case SWT.FocusIn :
				if ( hasFocus ) return;
				hasFocus = true;
				Display display = getDisplay ();
				display.removeFilter(SWT.KeyDown, filter);
				display.removeFilter(SWT.FocusIn, filter);
				if ( focusIndex < 0 ) {
					setFocus(NOFOCUS);
				}
				notifyListeners(SWT.FocusIn, new Event());
				display.addFilter(SWT.FocusIn, filter);
				display.addFilter(SWT.KeyDown, filter);
				break;
			case SWT.FocusOut :
				if ( ! hasFocus ) return;
				Control focusControl = getDisplay().getFocusControl();
				if ( focusControl == DateChooser.this
						 || focusControl == nextMonth
						 || focusControl == prevMonth ) return;
				hasFocus = false;
				getDisplay().removeFilter(SWT.KeyDown, filter);
				getDisplay().removeFilter(SWT.FocusIn, filter);
				notifyListeners(SWT.FocusOut, new Event());
				break;
		}
		grid.redraw();
	}

	/**
	 * Constructs and initializes all the GUI of the calendar.
	 */
	private void initialize() {
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		super.setLayout(layout);

		listener = new Listener() {
			public void handleEvent(Event event) {
				if ( event.type == SWT.MouseDown && ! hasFocus ) {
					setFocus();
					return;
				}
				if ( DateChooser.this == event.widget && event.type != SWT.KeyDown ) {
					calendarEvent(event);
				} else if ( prevMonth == event.widget || nextMonth == event.widget ) {
					buttonsEvent(event);
				} else if ( todayLabel == event.widget ) {
					footerEvent(event);
				} else if ( grid == event.widget || event.widget instanceof Label ) {
					gridEvent(event);
				} else if ( monthsMenu == event.widget || event.widget instanceof MenuItem ) {
					menuEvent(event);
				}
			}
		};
		filter = new Listener() {
			public void handleEvent(Event event) {
				switch ( event.type ) {
					case SWT.FocusIn :
						handleFocus(SWT.FocusOut);
						break;
					case SWT.KeyDown :
						calendarEvent(event);
						break;
				}
			}
		};

		createHeader();
		createGrid();
		createFooter();

		addListener(SWT.Dispose, listener);
		addListener(SWT.Traverse, listener);
		addListener(SWT.KeyDown, listener);
		addListener(SWT.FocusIn, listener);

		resize = true;
	}

	/**
	 * Returns the autoChangeOnAdjacent mode.
	 * 
	 * @return true / false
	 */
	public boolean isAutoChangeOnAdjacent() {
		return this.autoChangeOnAdjacent;
	}

	/**
	 * Returns the autoSelectOnFooter mode.
	 * 
	 * @return true / false
	 */
	public boolean isAutoSelectOnFooter() {
		checkWidget();
		return autoSelectOnFooter;
	}

	/**
	 * Returns <code>true</code> if the given date is selected, else returns
	 * <code>false</code>.
	 * 
	 * @param date
	 * @return <code>true</code> if selected, else <code>false</code>.
	 */
	public boolean isDateSelected(Date date) {
		checkWidget();
		for (Iterator it = selection.iterator(); it.hasNext();) {
			if ( ((Date) it.next()).equals(date) ) return true;
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if the receiver has the user-interface focus,
	 * and <code>false</code> otherwise.
	 * 
	 * @return the receiver's focus state
	 * @see org.eclipse.swt.widgets.Control#isFocusControl()
	 */
	public boolean isFocusControl() {
		return hasFocus;
	}

	/**
	 * Returns true if footer is visible.
	 * 
	 * @return <code>true</code> if footer visible, else <code>false</code>
	 */
	public boolean isFooterVisible() {
		checkWidget();
		return footerVisible;
	}

	/**
	 * Returns true if grid is visible.
	 * 
	 * @return Returns the grid visible status.
	 */
	public boolean isGridVisible() {
		checkWidget();
		return gridVisible;
	}

	/**
	 * Returns true if navigation is enabled. If false, buttons are not visibles.
	 * 
	 * @return Returns the navigation status.
	 */
	public boolean isNavigationEnabled() {
		checkWidget();
		return navigationEnabled;
	}

	/**
	 * Returns true if weeks numbers are visibles.
	 * 
	 * @return Returns the weeks numbers visible status.
	 */
	public boolean isWeeksVisible() {
		checkWidget();
		return weeksVisible;
	}

	/**
	 * Manages all events of the contextual menu on the month label of the header.
	 * 
	 * @param event event
	 */
	protected void menuEvent(Event event) {
		switch ( event.type ) {
			case SWT.Show :
				monthsMenu.setDefaultItem(monthsMenu.getItems()[currentMonthCal.get(Calendar.MONTH)]);
				break;
			case SWT.Selection :
				currentMonthCal.set(Calendar.MONTH, ((Integer) event.widget.getData()).intValue());
				refreshDisplay();
				break;
		}
	}

	/**
	 * Sends selection event to the listeners.
	 */
	protected void notifySelection() {
		Event event	= new Event();
		if ( ! multi ) event.data = getSelectedDate();
		notifyListeners(SWT.Selection, event);
	}

	private void redrawDec() {
		redraw--;
		if ( redraw == 0 ) setRedraw(true);
	}

	private void redrawInc() {
		if ( redraw == 0 ) setRedraw(false);
		redraw++;
	}

	/**
	 * Refreshes the display of the grid and header. This can be need because of
	 * a month display, a locale or color model change.
	 */
	private void refreshDisplay() {
		if ( currentMonthCal == null || theme == null ) return;
		redrawInc();
		currentMonth.setText(df1.format(currentMonthCal.getTime()));

		int maxDay = currentMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		Calendar cal = (Calendar) currentMonthCal.clone();
		int delta = -((cal.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7);
		cal.add(Calendar.DAY_OF_MONTH, delta);
		for (int i = 0; i < 42; i++) {
			if ( i % 7 == 0 ) {
				int w = cal.get(Calendar.WEEK_OF_YEAR);
				weeks[i / 7 + 1].label.setText(w < 10 ? "0" + w : "" + w);
			}
			if ( delta == 0 ) {
				firstDayIndex = i;
			}
			int weekDay = cal.get(Calendar.DAY_OF_WEEK);
			days[i].weekend	 = weekDay == 1 || weekDay == 7;
			days[i].adjacent = delta < 0 ? -1 : (delta >= maxDay ? 1 : 0);
			days[i].today		 = cal.equals(todayCal);
			if ( days[i].cal == null ) {
				days[i].cal = (Calendar) cal.clone();
			} else {
				days[i].cal.setTimeInMillis(cal.getTimeInMillis());
			}
			days[i].label.setText("" + days[i].cal.get(Calendar.DAY_OF_MONTH)); //$NON-NLS-1$
			days[i].selected = isDateSelected(cal.getTime());
			setCellColors(days[i]);

			cal.add(Calendar.DAY_OF_MONTH, 1);
			delta++;
		}
		redrawDec();
	}

	/**
	 * Removes the given date from the selection.
	 * 
	 * @param d date to remove
	 */
	public void removeSelectedDate(Date d) {
		checkWidget();
		removeSelectedDate(d, true);
	}

	/**
	 * Removes the given date from the selection. The refresh flag allowes to
	 * indicate must be refreshed or not.
	 * 
	 * @param d date to remove
	 * @param refresh true to refresh display, else false
	 */
	private void removeSelectedDate(Date d, boolean refresh) {
		for (Iterator it = selection.iterator(); it.hasNext();) {
			Date itDate = (Date) it.next();
			if ( itDate.equals(d) ) {
				selection.remove(itDate);
				break;
			}
		}
		if ( refresh ) refreshDisplay();
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's selection changes.
	 *
	 * @param lsnr the listener which should no longer be notified
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(SelectionListener lsnr) {
		checkWidget();
		if ( lsnr == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		removeListener(SWT.Selection, lsnr);
	}

	/**
	 * Manages the selection based on the current selected cell, specified by
	 * index, and the keyboard mask.
	 * 
	 * @param index index of selcted cell
	 * @param stateMask keyboard state
	 */
	private void select(int index, int stateMask) {
		Cell cell = days[index];
		if ( ! navigationEnabled && cell.adjacent != 0  ) return;
		boolean ctrl  = (stateMask & SWT.CTRL) != 0;
		boolean shift = (stateMask & SWT.SHIFT) != 0;
		if ( shift && beginInterval == null ) {
			ctrl  = true;
			shift = false;
		}
		if ( ! multi || (! ctrl && ! shift) ) clearSelection(false);
		Date selectedDate = cell.cal.getTime();
		if ( multi && ctrl && cell.selected ) {
			// Remove the selection on a single cell
			removeSelectedDate(selectedDate, false);
			beginInterval = null;
			endInterval   = null;
		} else {
			if ( multi && shift ) {
				// Interval selection
				Calendar c = (Calendar) cell.cal.clone();
				Date d;
				int delta;
				// Clear the previous interval
				if ( endInterval != null ) {
					delta = endInterval.after(beginInterval) ? -1 : 1;
					c.setTime(endInterval);
					d = c.getTime();
					while ( d.compareTo(beginInterval) != 0 ) {
						removeSelectedDate(d, false);
						c.add(Calendar.DAY_OF_MONTH, delta);
						d = c.getTime();
					}
				}
				// Select the new interval
				endInterval = selectedDate;
				delta = endInterval.after(beginInterval) ? -1 : 1;
				c.setTime(endInterval);
				d = c.getTime();
				while ( d.compareTo(beginInterval) != 0 ) {
					selection.add(d);
					c.add(Calendar.DAY_OF_MONTH, delta);
					d = c.getTime();
				}
			} else {
				// Single selection
				selection.add(selectedDate);
				beginInterval = cell.cal.getTime();
				endInterval   = null;
			}
		}
		// Changes the displayed month if an adjacent day has been selected
		if ( cell.adjacent != 0 && autoChangeOnAdjacent ) {
			changeCurrentMonth(cell.adjacent);
		} else {
			refreshDisplay();
		}
		notifySelection();
	}

	/**
	 * Sets to <code>true</code> to enable the automatic change of current month
	 * when an adjacent day is clicked in the grid.<p>
	 * This mode is <code>true</code> by default.
	 * 
	 * @param autoChangeOnAdjacent true / false
	 */
	public void setAutoChangeOnAdjacent(boolean autoChangeOnAdjacent) {
		this.autoChangeOnAdjacent = autoChangeOnAdjacent;
	}

	/**
	 * Set the autoSelectOnFooter mode. If true, the today date is automatically
	 * selected on the footer selection event.
	 * This mode is <code>false</code> by default.
	 * 
	 * @param autoselectOnFooter true /false
	 */
	public void setAutoSelectOnFooter(boolean autoselectOnFooter) {
		checkWidget();
		this.autoSelectOnFooter = autoselectOnFooter;
	}

	/**
	 * Sets the colors of a grid cell in function of its current state.
	 * 
	 * @param cell grid cell
	 */
	private void setCellColors(Cell cell) {
		if ( cell.selected ) {
			cell.label.setBackground(theme.selectedBackground);
			cell.label.setForeground(theme.selectedForeground);
		} else if ( cell.today ) {
			cell.label.setBackground(theme.todayBackground);
			cell.label.setForeground(theme.todayForeground);
		} else {
			cell.label.setBackground(theme.dayCellBackground);
			cell.label.setForeground(cell.adjacent != 0
			                         ? theme.extraMonthForeground
			                         : (cell.weekend
			                        		? theme.weekendForeground
			                        		: theme.dayCellForeground));
		}
	}

	/**
	 * Sets a new month to display.
	 * 
	 * @param month New month
	 */
	public void setCurrentMonth(Date month) {
		checkWidget();
		if ( month == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if ( currentMonthCal == null ) {
			currentMonthCal = Calendar.getInstance(locale);
			currentMonthCal.setFirstDayOfWeek(firstDayOfWeek);
			currentMonthCal.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
		}
		currentMonthCal.setTime(month);
		trunc(currentMonthCal);
		currentMonthCal.set(Calendar.DAY_OF_MONTH, 1);
		refreshDisplay();
	}

	/**
	 * Sets what the first day of the week is.<p>
	 * This method allowes to change the default first day of the week setted
	 * from the locale. It must be called after <code>setLocale()</code>.
	 * 
	 * @param firstDayOfWeek the given first day of the week.
	 */
	public void setFirstDayOfWeek(int firstDayOfWeek) {
		this.firstDayOfWeek = firstDayOfWeek;
		currentMonthCal.setFirstDayOfWeek(firstDayOfWeek);
		todayCal.setFirstDayOfWeek(firstDayOfWeek);
	}

	/**
	 * Causes the receiver to have the <em>keyboard focus</em>, 
	 * such that all keyboard events will be delivered to it.
	 *
	 * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
	 */
	public boolean setFocus() {
		checkWidget();
		if ( super.setFocus() ) {
			handleFocus(SWT.FocusIn);
			return true;
		}
		return false;
	}

	/**
	 * Sets the focus on the given cell, specified by index.
	 * 
	 * @param index index of cell taking the focus
	 */
	private void setFocus(int index) {
		if ( index == NOFOCUS ) {
			if ( todayCal.get(Calendar.MONTH) == currentMonthCal.get(Calendar.MONTH)
					 && todayCal.get(Calendar.YEAR) == currentMonthCal.get(Calendar.YEAR) ) {
				for (int i = 0; i < days.length; i++) {
					if ( days[i].today ) {
						focusIndex = i;
						return;
					}
				}
			}
			for (int i = 0; i < days.length; i++) {
				if ( days[i].cal.get(Calendar.DAY_OF_MONTH) == 1 ) {
					focusIndex = i;
					return;
				}
			}
		}
		if ( index < 0 ) {
			if ( ! navigationEnabled ) return;
			changeCurrentMonth(-1);
			focusIndex = index + 42;
		} else if ( index >= 42 ) {
			if ( ! navigationEnabled ) return;
			changeCurrentMonth(1);
			focusIndex = index - 42;
		} else {
			focusIndex = index;
		}
	}

	/**
	 * Sets the focus on the given date. The current displayed month is changed
	 * if necessary.
	 * 
	 * @param date date to set the focus on
	 */
	public void setFocusOnDate(Date date) {
		Calendar dateCal = (Calendar) currentMonthCal.clone();
		dateCal.setTime(date);
		if ( dateCal.get(Calendar.MONTH) != currentMonthCal.get(Calendar.MONTH)
				 || dateCal.get(Calendar.YEAR) != currentMonthCal.get(Calendar.YEAR) ) {
			setCurrentMonth(date);
		}
		focusIndex = firstDayIndex + dateCal.get(Calendar.DAY_OF_MONTH) - 1;
	}

	/**
	 * Sets the focus on the today date. If autoselect is true, the today date
	 * is selected.
	 * 
	 * @param autoselect true to select automatically the today date, else false
	 */
	public void setFocusOnToday(boolean autoselect) {
		checkWidget();
		redrawInc();
		if ( currentMonthCal.get(Calendar.MONTH) != todayCal.get(Calendar.MONTH)
				 || currentMonthCal.get(Calendar.YEAR) != todayCal.get(Calendar.YEAR) ) {
			setCurrentMonth(todayCal.getTime());
		}
		setFocus(NOFOCUS);
		if ( autoselect ) select(focusIndex, 0);
		refreshDisplay();
		redrawDec();
	}

	/**
	 * Sets the font that the receiver will use to paint textual information to
	 * the font specified by the argument, or to the default font for that kind
	 * of control if the argument is null.<p>
	 * 
	 * The new font is applied to all elements (labels) composing the calendar.
	 * The width of cells is adjusted.
	 * 
	 * @param font the new font (or null)
	 */
	public void setFont(Font font) {
		checkWidget();
		redrawInc();
		super.setFont(font);
		currentMonth.setFont(font);
		for (int i = 0; i < headers.length; i++) {
			headers[i].setFont(font);
		}
		for (int i = 0; i < days.length; i++) {
			days[i].label.setFont(font);
		}
		for (int i = 0; i < weeks.length; i++) {
			weeks[i].label.setFont(font);
		}
		resize = true;
		redrawDec();
	}

	/**
	 * Sets the footer visible or not. The footer displays the today date. It is
	 * not visible by default.
	 * 
	 * @param footerVisible <code>true</code> to set footer visible, else <code>false</code>
	 */
	public void setFooterVisible(boolean footerVisible) {
		checkWidget();
		if ( footerVisible != this.footerVisible ) {
			this.footerVisible = footerVisible;
			GridData data = (GridData) footer.getLayoutData();
			data.exclude = ! footerVisible;
			layout(true);
		}
	}

	/**
	 * Sets the grid visible or not. By default, the grid is visible.
	 * 
	 * @param gridVisible <code>true</code> to set grid visible, else <code>false</code>
	 */
	public void setGridVisible(boolean gridVisible) {
		checkWidget();
		if ( gridVisible != this.gridVisible ) {
			this.gridVisible = gridVisible;
			gridHeader.setBackground(gridVisible ? theme.gridLinesColor : theme.gridHeaderBackground);
			grid.setBackground(gridVisible ? theme.gridLinesColor : theme.dayCellBackground);
		}
	}

	/**
	 * Sets the layout which is associated with the receiver to be
	 * the argument which may be null.
	 * <p>
	 * Note : No Layout can be set on this Control because it already
	 * manages the size and position of its children.
	 * </p>
	 *
	 * @param layout the receiver's new layout or null
	 */
	public void setLayout(Layout layout) {
		checkWidget();
		return;
	}

	/**
	 * Sets a new locale to use for calendar. Locale will define the names of
	 * months and days, and the first day of week.
	 * 
	 * @param locale new locale (must not be null)
	 */
	public void setLocale(Locale locale) {
		checkWidget();
		if ( locale == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);

		this.locale = locale;

		// Loads the ressources
		resources = ResourceBundle.getBundle(BUNDLE_NAME, locale);
		prevMonth.setToolTipText(resources.getString("DateChooser.previousButton")); //$NON-NLS-1$
		nextMonth.setToolTipText(resources.getString("DateChooser.nextButton")); //$NON-NLS-1$

		// Defines formats
		df1 = new SimpleDateFormat("MMMM yyyy", locale); //$NON-NLS-1$
		df2 = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		Calendar c = Calendar.getInstance(TimeZone.getDefault(), locale);
		firstDayOfWeek = c.getFirstDayOfWeek();
		minimalDaysInFirstWeek = Integer.parseInt(resources.getString("minimalDaysInFirstWeek"));
		if ( currentMonthCal != null ) {
			currentMonthCal.setFirstDayOfWeek(firstDayOfWeek);
			currentMonthCal.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
		}
		if ( todayCal != null ) {
			todayCal.setFirstDayOfWeek(firstDayOfWeek);
			todayCal.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
		}

		// Sets the header menu items
		String[] months = df1.getDateFormatSymbols().getMonths();
		MenuItem[] items = monthsMenu.getItems();
		for (int i = 0; i < 12; i++) {
			items[i].setText(months[i]);
		}

		// Sets the grid header initiales
		redrawInc();
		DateFormatSymbols symboles = df1.getDateFormatSymbols();
		String[] sn = symboles.getShortWeekdays();
		String[] ln = symboles.getWeekdays();
		int f = firstDayOfWeek;
		for (int i = 0; i < headers.length; i++) {
			headers[i].setText(sn[f].substring(0, 1).toUpperCase());
			headers[i].setToolTipText(ln[f]);
			f = (f % 7) + 1;
		}

		// Updates the footer
		updateTodayLabel();

		refreshDisplay();
		redrawDec();
	}

	/**
	 * Sets what the minimal days required in the first week of the year are; For
	 * example, if the first week is defined as one that contains the first day
	 * of the first month of a year, call this method with value 1. If it must be
	 * a full week, use value 7.<p>
	 * This method allowes to change the default value setted from the locale.
	 * It must be called after <code>setLocale()</code>.
	 * 
	 * @param minimalDaysInFirstWeek the given minimal days required in the first week of the year.
	 */
	public void setMinimalDaysInFirstWeek(int minimalDaysInFirstWeek) {
		this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
		currentMonthCal.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
		todayCal.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
	}

	/**
	 * Sets the header's navigation buttons visibles or not.
	 * 
	 * @param navigationEnabled true if enabled, false else
	 */
	public void setNavigationEnabled(boolean navigationEnabled) {
		checkWidget();
		if ( navigationEnabled != this.navigationEnabled ) {
			this.navigationEnabled = navigationEnabled;
			prevMonth.setVisible(navigationEnabled);
			nextMonth.setVisible(navigationEnabled);
			if ( navigationEnabled ) {
				currentMonth.setMenu(monthsMenu);
			} else {
				currentMonth.setMenu(null);
			}
		}
	}

	/**
	 * Sets the selected date. The grid is refreshed to display the corresponding
	 * month.
	 * 
	 * @param date new selected date (must not be null)
	 */
	public void setSelectedDate(Date date) {
		checkWidget();
		if ( date == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);

		Calendar c = (Calendar) currentMonthCal.clone();
		c.setTime(date);
		trunc(c);
		Date d = c.getTime();
		if ( ! selection.contains(d) ) {
			if ( ! multi ) clearSelection(false);
			selection.add(d);
		}
		setCurrentMonth(d);
	}

	/**
	 * Sets the theme to apply to the calendar.
	 * 
	 * @param theme new theme (must not be null)
	 */
	public void setTheme(DateChooserTheme theme) {
		checkWidget();
		if ( theme == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		this.theme			 = theme;
		this.gridVisible = theme.gridVisible;
		redrawInc();

		// Header settings
		monthHeader.setBackground(theme.headerBackground);
		currentMonth.setBackground(theme.headerBackground);
		currentMonth.setForeground(theme.headerForeground);

		// Footer settings
		footer.setBackground(theme.headerBackground);
		todayLabel.setBackground(theme.headerBackground);
		todayLabel.setForeground(theme.headerForeground);

		// Grid headers settings
		gridHeader.setBackground(gridVisible ? theme.gridLinesColor : theme.gridHeaderBackground);
		for (int i = 0; i < headers.length; i++) {
			headers[i].setBackground(theme.gridHeaderBackground);
			headers[i].setForeground(theme.gridHeaderForeground);
		}

		// Grid day cells settings
		grid.setBackground(gridVisible ? theme.gridLinesColor : theme.dayCellBackground);
		for (int i = 0; i < days.length; i++) {
			days[i].label.setBackground(theme.dayCellBackground);
		}

		// Weeks cells settings
		weeksPanel.setBackground(gridVisible ? theme.gridLinesColor : theme.gridHeaderBackground);
		for (int i = 0; i < weeks.length; i++) {
			weeks[i].label.setBackground(theme.gridHeaderBackground);
			weeks[i].label.setForeground(theme.gridHeaderForeground);
		}

		// Font
		setFont(theme.font);

		refreshDisplay();
		redrawDec();
	}

	/**
	 * Sets the today date.<p>
	 * 
	 * By default the today date is initialized to the current system date. But it
	 * can be needed to ajust it for specifics needs.
	 * 
	 * @param today today date (must not be null)
	 */
	public void setTodayDate(Date today) {
		checkWidget();
		if ( today == null ) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if ( todayCal == null ) {
			todayCal = Calendar.getInstance(locale);
			todayCal.setFirstDayOfWeek(firstDayOfWeek);
			todayCal.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
		}
		todayCal.setTime(today);
		trunc(todayCal);
		updateTodayLabel();
		refreshDisplay();
	}

	/**
	 * Sets the weeks numbers visible or not. By default, the weeks are NOT
	 * visibles.
	 * 
	 * @param weeksVisible <code>true</code> to set weeks visibles, else <code>false</code>
	 */
	public void setWeeksVisible(boolean weeksVisible) {
		checkWidget();
		if ( weeksVisible != this.weeksVisible ) {
			this.weeksVisible = weeksVisible;
			GridData data = (GridData) weeksPanel.getLayoutData();
			data.exclude = ! weeksVisible;
			data = (GridData) gridHeader.getLayoutData();
			data.horizontalSpan = weeksVisible ? 1 : 2;
			data = (GridData) grid.getLayoutData();
			data.horizontalSpan = weeksVisible ? 1 : 2;
			layout(true);
		}
	}

	/**
	 * Trunc a given <code>Calendar</code>. The time fields are all setted to 0.
	 * 
	 * @param cal Calendar
	 */
	private void trunc(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * Updates the today label in the footer. Called when the today date or the
	 * locale is changed. 
	 */
	private void updateTodayLabel() {
		if ( todayCal != null ) {
			todayLabel.setText(resources.getString("DateChooser.today")
					 + " " + df2.format(todayCal.getTime()));
		}
	}
}
