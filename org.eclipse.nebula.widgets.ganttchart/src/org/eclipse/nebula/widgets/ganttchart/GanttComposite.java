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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class GanttComposite extends Canvas implements MouseListener, MouseMoveListener, MouseTrackListener, KeyListener {
	private static final Cursor CURSOR_NONE = CursorCache.getCursor(SWT.NONE);
	private static final Cursor CURSOR_SIZEE = CursorCache.getCursor(SWT.CURSOR_SIZEE);
	private static final Cursor CURSOR_SIZEW = CursorCache.getCursor(SWT.CURSOR_SIZEW);
	private static final Cursor CURSOR_SIZEALL = CursorCache.getCursor(SWT.CURSOR_SIZEALL);
	private static final Cursor CURSOR_HAND = CursorCache.getCursor(SWT.CURSOR_HAND);

	// arrow directions
	private static final int ARROW_UP = 1;
	private static final int ARROW_DOWN = 2;
	private static final int ARROW_LEFT = 3;
	private static final int ARROW_RIGHT = 4;

	// scrolling directions
	private static final int DIRECTION_LEFT = 1;
	private static final int DIRECTION_RIGHT = 2;

	private int mAutoScrollDirection = SWT.NULL;

    // keeps track of when to show or hide the zoom-helper area, go Clippy!
    private boolean mShowZoomHelper;
    private Rectangle mZoomLevelHelpArea;

	// start level
	private int mZoomLevel;

	// current view
	private int mCurrentView;

	// 3D looking events
	private boolean mThreeDee;

	// show a little plaque telling the days between start and end on the chart, only draws if it fits
	private boolean mShowNumberOfDaysOnChart;

	// draw the revised dates as a very thin small black |------| on the chart
	private boolean mShowRevisedDates;

	// how many days offset to start drawing the calendar at.. useful for not having the first day be today
	private int mStartCalendarAtOffset;

	// how many pixels from each event border to ignore as a move.. mostly to help resizing
	private int mMoveAreaInsets;

	// year stuff
	private int mYearDayWidth;

	private int mYearMonthWeekWidth;

	private int mYearMonthWidth;

	// month stuff
	private int mMonthDayWidth;

	private int mMonthWeekWidth;
	
	// day stuff
	// width of one day
	private int mDayWidth;

	// one week width, usually 7 days * width of one day
	private int mWeekWidth;

	private int mBottomMostY;

	// where the first event will always be drawn. The last number is just a spacer or it will be drawn directly below the horizontal lines
	private int mEventRoot;

	// various colors used.. all set in initColors()
	private Color mLineTodayColor;

	private Color mLineColor;

	private Color mLineWeekDividerColor;

	private Color mTextColor;

	private Color mDayBackgroundColor;

	private Color mSaturdayColor;
	
	private Color mSaturdayBackgroundColor;
	
	private Color mSundayColor;
	
	private Color mSundayBackgroundColor;
	
	private Color mWeekdayBackgroundColor;

	private Color mTextHeaderBackgroundColor;

	private Color mArrowColor;

	private Color mEventBorderColor;

	private Color mTodayBGColor;
		
	private Color mWhite;

	// currently selected event
	private GanttEvent mSelectedEvent;

	private static DateFormatSymbols mDFS = new DateFormatSymbols();

	// used in writing out the top month string
	private String[] mMonths = mDFS.getShortMonths();

	private String[] mFullMonths = mDFS.getMonths();

	// weekdates in java start at 1 end at 7.. so 0 is X.. should never show up
	private static String[] mDayLetters;

	// the calendar that the current view is on, will always be on the first date of the visible area (the date on the far left)
	// it should never change from internal functions. Where date changes, a clone is made.
	private Calendar mCalendar;

	//OLD CODE. From when vertical scrollbar was attached straight onto composite. Leaving in as it may be useful for adjusting total draw area vertically.
	private int mYScrollPosition;

	// the number of days that will be visible in the current area. Is set after we're done drawing the chart
	private int mDaysVisible;

	// all events
	private ArrayList mGanttEvents;

	// all connections between events
	private ArrayList mGanttConnections;

	// various variables for resize & drag n drop
	private boolean mDragging = false;

	private boolean mResizing = false;

	private int mLastX;

	private int mCursor;

	private ArrayList mDragEvents;

	private boolean mLastLeft = false;

	private Date mDragStartDate = null;

	private GanttMap mGmap = new GanttMap();

	private GanttMap mOGMap = new GanttMap();

	//  what operating system we're on
	public static int OS_OTHER = 0;

	public static int OS_WINDOWS = 1;

	public static int OS_MAC = 2;

	public static int OS_LINUX = 3;

	public static int osType = OS_OTHER;

	private ISettings mSettings;
	
	private IColorManager mColorManager;
	
	private IPaintManager mPaintManager;
	
	private ArrayList mEventListeners;
	
	private boolean mMouseIsDown;
	
	private Point mMouseDragStartLocation;
	
	// parent control
	private GanttChartScrolledWrapper mParent;
	
	private ArrayList mGanttGroups;
	
	// menu used at right click events
	private Menu mRightClickMenu;
	
	static {
		String[] weekdays = mDFS.getWeekdays();
		mDayLetters = new String[weekdays.length];
		for (int i = 0; i < weekdays.length; i++) {
			String weekday = weekdays[i];
			if (weekday.length() > 0) {
				mDayLetters[i] = weekday.substring(0, 1).toUpperCase();
			}
		}

		String osProperty = System.getProperty("os.name");
		if (osProperty != null) {
			String osName = osProperty.toUpperCase();

			if (osName.indexOf("WINDOWS") > -1)
				osType = OS_WINDOWS;
			else if (osName.indexOf("MAC") > -1)
				osType = OS_MAC;
			else if (osName.indexOf("NIX") > -1 || osName.indexOf("NUX") > -1)
				osType = OS_LINUX;
		}
	}

	public GanttComposite(GanttChartScrolledWrapper parent, int style, ISettings settings, IColorManager colorManager, IPaintManager paintManager) {
		super(parent, style | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
	
		mParent = parent;
		mSettings = settings;
		mColorManager = colorManager;
		mPaintManager = paintManager;
		
		mGanttConnections = new ArrayList();
		mDragEvents = new ArrayList();
		mEventListeners = new ArrayList();
		mGanttEvents = new ArrayList();
		mGanttGroups = new ArrayList();
		
		mCurrentView = mSettings.getInitialView();
		mZoomLevel = mSettings.getInitialZoomLevel();
		mShowNumberOfDaysOnChart = mSettings.showNumberOfDaysOnBars();
		mShowRevisedDates = mSettings.showRevisedDates();
		mThreeDee = mSettings.showBarsIn3D();
		mYearDayWidth = mSettings.getYearMonthDayWidth();
		mYearMonthWeekWidth = mYearDayWidth * 7;
		mYearMonthWidth = mYearMonthWeekWidth * 4;
		mMonthDayWidth = mSettings.getMonthDayWidth();
		mMonthWeekWidth = mMonthDayWidth * 7;
		mDayWidth = mSettings.getDayWidth();
		mWeekWidth = mDayWidth * 7;
		mEventRoot =  mSettings.getHeaderMonthHeight() + mSettings.getHeaderDayHeight() + (mSettings.getEventHeight() + 8) + 8;
		mStartCalendarAtOffset = mSettings.getCalendarStartupDateOffset();
		mCalendar = mSettings.getStartupCalendarDate();
		mMoveAreaInsets = mSettings.getMoveAreaNegativeSensitivity();
		
		// by default, set today's date
		setDate(mCalendar, true);

		addMouseListener(this);
		addMouseMoveListener(this);
		addMouseTrackListener(this);
		addKeyListener(this);
        
		Listener mMouseWheelListner = new Listener()
        {
            public void handleEvent(Event event)
            {
            	if (!mSettings.enableZooming())
            		return;
            		
                if (event.stateMask != 0)
                {
                	mShowZoomHelper = mSettings.showZoomLevelBox();
                	
                    if (event.count > 0)
                    {
                        zoomIn(mSettings.showZoomLevelBox());
                    }
                    else
                    {
                        zoomOut(mSettings.showZoomLevelBox());
                    }                   

                }
            }

        };       
        addListener(SWT.MouseWheel, mMouseWheelListner);
		// on Windows we want to kill any tooltip when alt is pressed, as it may be an indicator we're switching
		// programs, and tooltips usually die on MouseMove or MouseUp
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent event) {
				if (event.stateMask == SWT.ALT || event.keyCode == SWT.ALT) {
					GanttToolTip.kill();
					GanttDateTip.kill();
				}
			}

			public void keyReleased(KeyEvent event) {
				if (mShowZoomHelper)
                {
                    mShowZoomHelper = false;
                    // redraw the area where it was
                    if (mZoomLevelHelpArea != null)
                    {
                        redraw(mZoomLevelHelpArea.x, mZoomLevelHelpArea.y, mZoomLevelHelpArea.width + 1, mZoomLevelHelpArea.height + 1, false);
                    }
                }
			}
		});

		// without this there'd be nothing!
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				GanttComposite.this.repaint(event);
			}
		});

		setLayout(new FillLayout());

		initColors();
	}

	public void addGroup(GanttGroup group) {
		if (!mGanttGroups.contains(group))
			mGanttGroups.add(group);
	}
	
	public void removeGroup(GanttGroup group) {
		mGanttGroups.remove(group);
	}
	
	public void scrollingLeft(int diff) {
		checkWidget();
		if (diff == 1) {
			if (mCurrentView == ISettings.VIEW_YEAR) {
				prevMonth();
			}
			else {
				prevDay();
			}
		}
		else {
			if (diff == 0) {
				if (mCurrentView == ISettings.VIEW_YEAR) {
					prevWeek();
				}
				else {
					prevDay();
				}
				return;
			}

			for (int i = 0; i < diff; i++) {
				if (mCurrentView == ISettings.VIEW_YEAR) {
					prevWeek();
				}
				else {
					prevDay();
				}
			}
		}
	}

	public void scrollingRight(int diff) {
		checkWidget();
		if (diff == 1) {
			if (mCurrentView == ISettings.VIEW_YEAR) {
				nextMonth();
			}
			else {
				nextDay();
			}
		}
		else {
			// far right
			if (diff == 0) {
				if (mCurrentView == ISettings.VIEW_YEAR) {
					nextWeek();
				}
				else {
					nextDay();
				}
				return;
			}

			for (int i = 0; i < diff; i++) {
				if (mCurrentView == ISettings.VIEW_YEAR) {
					nextWeek();
				}
				else {
					nextDay();
				}
			}
		}
	}

	private void initColors() {
		mLineTodayColor = mColorManager.getLineTodayColor();
		mLineColor = mColorManager.getLineColor();
		mTextColor = mColorManager.getTextColor();
		mSaturdayColor = mColorManager.getSaturdayColor();
		mSaturdayBackgroundColor = mColorManager.getSaturdayBackgroundColor();
		mSundayColor = mColorManager.getSundayColor();
		mSundayBackgroundColor = mColorManager.getSundayBackgroundColor();
		mWeekdayBackgroundColor = mColorManager.getWeekdayBackgroundColor();
		mLineWeekDividerColor = mColorManager.getLineWeekDividerColor();
		mDayBackgroundColor = mColorManager.getDayBackgroundColor();
		mTextHeaderBackgroundColor = mColorManager.getTextHeaderBackgroundColor();
		mArrowColor = mColorManager.getArrowColor();
		mEventBorderColor = mColorManager.getEventBorderColor();
		mWhite = mColorManager.getWhite();
		mTodayBGColor = mColorManager.getTodayBackgroundColor();
	}

	// the repaint event, whenever the composite needs to refresh the contents
	private void repaint(PaintEvent event) {
		GC gc = event.gc;
		drawChartOntoGC(gc);
 	}
	
	private void showMenu(int x, int y, GanttEvent event, final MouseEvent me) {
		if (!mSettings.showMenuItemsOnRightClick())
			return;
		
		mRightClickMenu = new Menu(Display.getDefault().getActiveShell(), SWT.POP_UP);

		if (event != null) {
			// We can't use JFace actions.. so we need to make copies.. Dirty but at least not reinventing a wheel (as much)
			Menu eventMenu = event.getMenu();
			MenuItem[] items = eventMenu.getItems(); 
			
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					final MenuItem mItem = items[i];
				
					MenuItem copy = new MenuItem(mRightClickMenu, SWT.PUSH);
					copy.setText(mItem.getText());
					copy.setImage(mItem.getImage());
					copy.setEnabled(mItem.getEnabled());
					copy.setAccelerator(mItem.getAccelerator());
					copy.setData(mItem.getData());
					copy.setSelection(mItem.getSelection());
					copy.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event event) {
							// relay...
							mItem.notifyListeners(SWT.Selection, event);
						}
					});
				}
			}
			
			if (items != null && items.length > 0)
				new MenuItem(mRightClickMenu, SWT.SEPARATOR);
	}
		
		if (mSettings.enableZooming()) {
			//new MenuItem(menu, SWT.SEPARATOR);
			MenuItem zoomIn = new MenuItem(mRightClickMenu, SWT.PUSH);
			zoomIn.setText("Zoom In");
			MenuItem zoomOut = new MenuItem(mRightClickMenu, SWT.PUSH);
			zoomOut.setText("Zoom Out");
			MenuItem zoomReset = new MenuItem(mRightClickMenu, SWT.PUSH);
			zoomReset.setText("Reset Zoom Level");
			
			zoomIn.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					zoomIn(mSettings.showZoomLevelBox());
				}
			});
			zoomOut.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					zoomOut(mSettings.showZoomLevelBox());
				}
			});
			zoomReset.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					resetZoom();
				}
			});
		}

		final MenuItem showPlaque = new MenuItem(mRightClickMenu, SWT.CHECK);
		showPlaque.setText("Show Number of Days on Events");
		showPlaque.setSelection(mShowNumberOfDaysOnChart);
		showPlaque.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mShowNumberOfDaysOnChart = !mShowNumberOfDaysOnChart;
				showPlaque.setSelection(mShowNumberOfDaysOnChart);
				redraw();
			}
		});
		
		final MenuItem showEsts = new MenuItem(mRightClickMenu, SWT.CHECK);
		showEsts.setText("Show Revised Dates");
		showEsts.setSelection(mShowRevisedDates);
		showEsts.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mShowRevisedDates = !mShowRevisedDates;
				showEsts.setSelection(mShowRevisedDates);
				redraw();
			}
		});
		
		final MenuItem showThreeDee = new MenuItem(mRightClickMenu, SWT.CHECK);
		showThreeDee.setText("3D Bars");
		showThreeDee.setSelection(mThreeDee);
		showThreeDee.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mThreeDee = !mThreeDee;
				redraw();
			}
		});
	
		if (mSettings.showDeleteMenuOption() && event != null) {
			new MenuItem(mRightClickMenu, SWT.SEPARATOR);
	
			MenuItem delete = new MenuItem(mRightClickMenu, SWT.PUSH);
			delete.setText("Delete");
			delete.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					if (mSelectedEvent != null) {
						ArrayList events = new ArrayList();
						events.add(mSelectedEvent);
						for (int i = 0; i < mEventListeners.size(); i++) {
							IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);					
							listener.eventsDeleteRequest(events, me);
						}
					}
				}
			});
		}
		
		if (mSettings.showPropertiesMenuOption() && mSelectedEvent != null) {
			new MenuItem(mRightClickMenu, SWT.SEPARATOR);

			MenuItem properties = new MenuItem(mRightClickMenu, SWT.PUSH);
			properties.setText("Properties");
			properties.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if (mSelectedEvent != null) {
						for (int i = 0; i < mEventListeners.size(); i++ ) {
							IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);						
							listener.eventPropertiesSelected(mSelectedEvent);
						}
					}
				}
			});
		}

		mRightClickMenu.setLocation(x, y);
		mRightClickMenu.setVisible(true);
	}

	public GanttEvent getActiveEvent() {
		checkWidget();
		return mSelectedEvent;
	}

	// draws the actual chart.. seperated from the repaint event as the getImage() call uses it on a different GC
	// Note: things are in a certain order, as some stuff draws on top of others, so don't move them around unless you want a different effect
	private void drawChartOntoGC(GC gc) {
		gc.setBackground(ColorCache.getWhite());
		Rectangle bounds = super.getBounds();
		gc.fillRectangle(bounds);

		if (mCurrentView == ISettings.VIEW_WEEK) {
			// draw the week boxes
			drawWeekBoxes(gc, bounds);
			// draw the day boxes
			drawDayBoxes(gc, bounds);
		}
		else if (mCurrentView == ISettings.VIEW_MONTH) {
			// draws the month boxes at the top
			drawMonthBoxes(gc, bounds);
			// draws the monthly "week" boxes
			drawMonthWeekBoxes(gc, bounds);
		}
		else if (mCurrentView == ISettings.VIEW_YEAR) {
			// draws the years at the top
			drawYearBoxes(gc, bounds);
			// draws the month boxes
			drawYearMonthBoxes(gc, bounds);
		}

		// generic drawing for all views

		// draws vertical lines all over the chart
		drawVerticalLines(gc, bounds);
		// draws the 2 horizontal (usually black) lines at the top to make things stand out a little
		drawTopHorizontalLines(gc, bounds);
		// draw the events
		drawEvents(gc);
		// draw the connecting arrows
		drawConnections(gc);
		// zoom
        if (mShowZoomHelper && mSettings.showZoomLevelBox())
            drawZoomLevel(gc);         
	}

	private void drawZoomLevel(final GC gc) {
        final int helpHeight = 19;

        final int bottomLeftY = super.getBounds().height - helpHeight - 3;
        final int bottomLeftX = 3;

        final String qText = "Zoom Level: " + (mZoomLevel-1);
        final Point point = gc.stringExtent(qText);

        final int helpWidth = point.x + 5;
        
        gc.setBackground(mColorManager.getZoomBackgroundColor());
        gc.setForeground(mColorManager.getZoomForegroundColor());

        final Rectangle outer = new Rectangle(bottomLeftX, bottomLeftY, helpWidth, helpHeight);
        final Rectangle inner = new Rectangle(bottomLeftX + 1, bottomLeftY + 1, helpWidth - 1, helpHeight - 1);

        mZoomLevelHelpArea = outer;

        gc.drawRectangle(outer);
        gc.fillRectangle(inner);
        gc.setForeground(mTextColor);

        gc.drawString(qText, bottomLeftX + 4, bottomLeftY + 3, true);
    }

	// draws vertical bars for seperating days
	private void drawVerticalLines(GC gc, Rectangle bounds) {
		int xMax = bounds.width;
		int yStart = mSettings.getHeaderMonthHeight() + mSettings.getHeaderDayHeight() - mYScrollPosition;

		gc.drawLine(0, yStart, bounds.width, yStart);

		if (mCurrentView == ISettings.VIEW_WEEK) {
			int current = 0;

			int day = mCalendar.get(Calendar.DAY_OF_WEEK);

			Calendar temp = Calendar.getInstance(Locale.getDefault());
			temp.setTime(mCalendar.getTime());

			while (true) {
				// x1, y1, x2, y2
				if (day == mCalendar.getFirstDayOfWeek()) {
					gc.setForeground(mLineWeekDividerColor);
					gc.drawLine(current, 0, current, bounds.height);
				}
				else {
					gc.setForeground(mLineColor);
					gc.setBackground(mWhite);
					gc.drawLine(current, yStart, current, bounds.height);
				}

				if (DateHelper.isToday(temp)) {
					drawTodayLine(gc, bounds, current, day);
				}
				else {
					gc.drawLine(current, yStart, current, bounds.height);
				}

				current += mDayWidth;

				temp.add(Calendar.DATE, 1);

				day++;
				if (day > 7) {
					day = 1;
				}

				if (current > xMax) {
					break;
				}
			}
		}
		else if (mCurrentView == ISettings.VIEW_MONTH) {
			// drawing month view we still do it by days
			int day = mCalendar.get(Calendar.DAY_OF_WEEK);

			Calendar temp = Calendar.getInstance(Locale.getDefault());
			temp.setTime(mCalendar.getTime());

			int current = 0;

			while (true) {
				if (day == temp.getFirstDayOfWeek()) {
					gc.setForeground(mLineWeekDividerColor);
					gc.drawLine(current, yStart, current, bounds.height);
				}
				else {
					gc.setForeground(mLineColor);
					gc.setBackground(mWhite);
					gc.drawLine(current, yStart, current, bounds.height);
				}

				if (DateHelper.isToday(temp)) {
					drawTodayLine(gc, bounds, current, day);
				}

				temp.add(Calendar.DATE, 1);
				current += mMonthDayWidth;

				day++;
				if (day > 7) {
					day = 1;
				}

				if (current > xMax) {
					break;
				}
			}
		}
		else if (mCurrentView == ISettings.VIEW_YEAR) {
			Calendar temp = Calendar.getInstance(Locale.getDefault());
			temp.setTime(mCalendar.getTime());

			int current = 0;
			temp.set(Calendar.DAY_OF_MONTH, 1);

			while (true) {
				int day = temp.get(Calendar.DATE);

				if (day == 1) {
					gc.setForeground(mLineWeekDividerColor);

					int x = getXForDate(temp);

					gc.drawLine(x, yStart, x, bounds.height);
				}

				if (DateHelper.isToday(temp)) {
					drawTodayLine(gc, bounds, current, temp.get(Calendar.DAY_OF_WEEK));
				}

				temp.add(Calendar.MONTH, 1);

				current += mYearMonthWidth;

				if (current > xMax) {
					break;
				}
			}

			int today = getXForDate(Calendar.getInstance(Locale.getDefault()));
			drawTodayLine(gc, bounds, today, 1);
		}
	}

	// year
	private void drawYearMonthBoxes(GC gc, Rectangle bounds) {
		int xMax = bounds.width;
		int current = 0;
		int topY = mSettings.getHeaderMonthHeight() - mYScrollPosition;
		int bottomY = mSettings.getHeaderDayHeight();
		mDaysVisible = 0;

		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(mCalendar.getTime());

		temp.set(Calendar.DAY_OF_MONTH, 1);

		gc.setBackground(mTextHeaderBackgroundColor);
		gc.fillRectangle(0, topY, bounds.width, bottomY);

		while (true) {
			int curMonth = temp.get(Calendar.MONTH);

			if (temp.get(Calendar.DAY_OF_MONTH) == 1) {
				//gc.setForeground(lineColor);
				//gc.drawLine(current, topY, 10, bottomY);
				//gc.setBackground(weekBackgroundColor);
				//gc.fillRectangle(current + 1, topY + 1, yearDayWidth - 1, bottomY - 1);
				gc.setForeground(mTextColor);
				gc.drawString("" + mMonths[curMonth], current + 4, topY + 3, true);
			}

			gc.setBackground(mWeekdayBackgroundColor);
			gc.fillRectangle(current + 1, topY + mSettings.getHeaderMonthHeight(), mYearDayWidth - 1, bounds.height);
			mDaysVisible++;

			temp.add(Calendar.DATE, 1);

			current += mYearDayWidth;

			if (current > xMax) {
				break;
			}
		}
	}

	// year
	private void drawYearBoxes(GC gc, Rectangle bounds) {
		int xMax = bounds.width;
		int current = 0;
		int topY = 0 - mYScrollPosition;

		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(mCalendar.getTime());

		temp.set(Calendar.DAY_OF_MONTH, 1);

		int lastYear = -1;
		while (true) {
			// draw month at beginning of month
			if (temp.get(Calendar.YEAR) != lastYear) {
				gc.setForeground(mLineColor);
				gc.drawLine(current, topY, current, mSettings.getHeaderDayHeight());
				gc.setForeground(mTextColor);
				gc.drawString("" + temp.get(Calendar.YEAR), current + 4, topY + 3);
			}

			lastYear = temp.get(Calendar.YEAR);

			temp.add(Calendar.DATE, 1);

			current += mYearDayWidth;

			if (current > xMax) {
				break;
			}
		}
	}

	// month
	private void drawMonthWeekBoxes(GC gc, Rectangle bounds) {
		int xMax = bounds.width;
		int current = 0;
		int topY = mSettings.getHeaderMonthHeight() - mYScrollPosition;
		int bottomY = mSettings.getHeaderDayHeight();

		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(mCalendar.getTime());

		// if we're not on a sunday, the weekbox will be shorter, so check how much shorter
		int day = temp.get(Calendar.DAY_OF_WEEK);
		int dayOffset = temp.getFirstDayOfWeek() - day;
		int toTakeOff = (dayOffset * mMonthDayWidth);

		int wWidth = mMonthWeekWidth;
		current += toTakeOff;

		// move us to sunday, as the date shouldn't change when scrolling
		temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

		while (true) {
			int curDay = temp.get(Calendar.DAY_OF_WEEK);

			// only change dates when week changes, as we don't change date string for every different day
			if (curDay == temp.getFirstDayOfWeek()) {
				gc.setForeground(mLineColor);
				gc.drawRectangle(current, topY, wWidth, bottomY);
				gc.setBackground(mTextHeaderBackgroundColor);
				gc.fillRectangle(current + 1, topY + 1, wWidth - 1, bottomY - 1);
				gc.setForeground(mTextColor);

				gc.drawString(getShortDateString(temp), current + 4, topY + 3, true);
			}

			if (curDay == Calendar.SUNDAY || curDay == Calendar.SATURDAY) {
				// fill the whole thing all the way down
				gc.setBackground(mTextHeaderBackgroundColor);
				gc.fillRectangle(current + 1, topY + mSettings.getHeaderMonthHeight(), mMonthDayWidth - 1, bounds.height);
			}
			else {
				gc.setBackground(mWeekdayBackgroundColor);
				gc.fillRectangle(current + 1, topY + mSettings.getHeaderMonthHeight(), mMonthDayWidth - 1, bounds.height);
			}

			if (DateHelper.isToday(temp)) {
				gc.setBackground(mTodayBGColor);
				gc.fillRectangle(current + 1, topY + mSettings.getHeaderMonthHeight(), mMonthDayWidth - 1, bounds.height);
			}

			temp.add(Calendar.DATE, 1);

			current += mMonthDayWidth;

			if (current > xMax) {
				break;
			}
		}
	}

	// month
	private void drawMonthBoxes(GC gc, Rectangle bounds) {
		int xMax = bounds.width;
		int current = 0;
		int topY = 0 - mYScrollPosition;
		mDaysVisible = 0;

		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(mCalendar.getTime());

		int day = temp.get(Calendar.DAY_OF_WEEK);
		int dayOffset = temp.getFirstDayOfWeek() - day;

		current += (dayOffset * mMonthDayWidth);

		// move us to sunday, as the date shouldn't change when scrolling
		temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

		while (true) {
			// draw month at beginning of month
			if (temp.get(Calendar.DAY_OF_MONTH) == 1) {
				gc.setForeground(mLineColor);
				//gc.drawRectangle(current, topY, wWidth, bottomY);
				gc.drawLine(current, topY, current, mSettings.getHeaderDayHeight());
				//gc.setBackground(weekBackgroundColor);
				//gc.fillRectangle(current + 1, topY + 1, wWidth - 1, bottomY - 1);
				gc.setForeground(mTextColor);
				gc.drawString(getMonthString(temp), current + 4, topY + 3);
			}

			temp.add(Calendar.DATE, 1);
			mDaysVisible++;

			current += mMonthDayWidth;

			if (current > xMax) {
				break;
			}
		}
	}
		

	// days
	private void drawWeekBoxes(GC gc, Rectangle bounds) {
		int xMax = bounds.width;
		int current = 0;
		int topY = 0 - mYScrollPosition;
		int bottomY = mSettings.getHeaderMonthHeight();

		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(mCalendar.getTime());

		// if we're not on a sunday, the weekbox will be shorter, so check how much shorter		
		// sun = 1, mon = 2, tue = 3, wed = 4, thu = 5, fri = 6, sat = 7		
		int day = temp.get(Calendar.DAY_OF_WEEK);
		int firstDayOfWeek = temp.getFirstDayOfWeek();
				
		int dayOffset = firstDayOfWeek - day;

		// BUGFIX Sep 12/07:
		// On international dates that have getFirstDayOfWeek() on other days than Sundays, we need to do some magic to these
		// or the dates printed will be wrong. As such, if the offset is positive, we simply toss off 7 days to get the correct number
		// we want a negative value in the end.
		// --Emil
		if (dayOffset > 0) 
			dayOffset -= 7;		

		int toTakeOff = (dayOffset * mDayWidth);

		int wWidth = mWeekWidth;
		current += toTakeOff;

		// move us to the first day of the week, as the date shouldn't change when scrolling
		temp.set(Calendar.DAY_OF_WEEK, temp.getFirstDayOfWeek());

		while (true) {
			gc.setForeground(mLineColor);
			gc.drawRectangle(current, topY, wWidth, bottomY);
			gc.setBackground(mTextHeaderBackgroundColor);
			gc.fillRectangle(current + 1, topY + 1, wWidth - 1, bottomY - 1);

			gc.setForeground(mTextColor);
			gc.drawString(getDateString(temp), current + 4, topY + 3);

			// only change dates on the first day of the week, as we don't change date string for every different day
			if (temp.get(Calendar.DAY_OF_WEEK) == temp.getFirstDayOfWeek()) {
				temp.add(Calendar.DATE, 7);
			}

			current += mWeekWidth;

			if (current > xMax) {
				break;
			}
		}
	}

	// days
	private void drawDayBoxes(GC gc, Rectangle bounds) {
		int xMax = bounds.width;
		int current = 0;
		int topY = mSettings.getHeaderMonthHeight() - mYScrollPosition;
		int bottomY = mSettings.getHeaderDayHeight();

		int day = mCalendar.get(Calendar.DAY_OF_WEEK);
		mDaysVisible = 0;

		Calendar temp = Calendar.getInstance();
		temp.setTime(mCalendar.getTime());

		while (true) {
			gc.setBackground(mWhite);
			gc.setForeground(mLineColor);
			gc.drawRectangle(current, topY, mDayWidth, bottomY);
			gc.setBackground(mDayBackgroundColor);
			gc.fillRectangle(current + 1, topY + 1, mDayWidth - 1, bottomY - 1);

			int hSpacer = mSettings.getDayHorizontalSpacing();
			int vSpacer = mSettings.getDayVerticalSpacing();

			if (mSettings.adjustForLetters()) {
				switch (gc.stringExtent(mDayLetters[day]).x) {
					case 5:
						hSpacer += 3;
						break;
					case 6:
						hSpacer += 2;
						break;
					case 7:
						hSpacer += 1;
						break;
					case 9:
						hSpacer -= 1;
						break;
				}
			}

			if (day == Calendar.SATURDAY) {
				gc.setForeground(mSaturdayColor);
				gc.setBackground(mSaturdayBackgroundColor);
			}
			else if (day == Calendar.SUNDAY) {
				gc.setForeground(mSundayColor);
				gc.setBackground(mSundayBackgroundColor);				
			}
			else {
				gc.setForeground(mTextColor);
				gc.setBackground(mWeekdayBackgroundColor);
			}
			
			// fill the whole thing aaall the way down
			gc.drawString(mDayLetters[day], current + hSpacer, topY + vSpacer, true);
			gc.fillRectangle(current + 1, topY + mSettings.getHeaderMonthHeight(), mDayWidth - 1, bounds.height);
			
			if (DateHelper.isToday(temp)) {
				gc.setBackground(mTodayBGColor);
				gc.fillRectangle(current + 1, topY + mSettings.getHeaderMonthHeight(), mDayWidth - 1, bounds.height);
			}

			temp.add(Calendar.DATE, 1);
			// fixes some odd red line bug, not sure why
			gc.setForeground(mLineColor);

			current += mDayWidth;

			day++;
			if (day > 7) {
				day = 1;
			}

			mDaysVisible++;

			if (current > xMax) {
				break;
			}
		}
	}

	private void drawEvents(GC gc) {
		if (mGanttEvents.size() == 0) {
			return;
		}

		int yStart = mEventRoot - mYScrollPosition;
		
		ArrayList eventsAlreadyDrawn = new ArrayList();
		ArrayList allEventsInGroups = new ArrayList();
		for (int i = 0; i < mGanttGroups.size(); i++) 
			allEventsInGroups.addAll(((GanttGroup)mGanttGroups.get(i)).getEventMembers());
		
		boolean lastLoopWasGroup = false;
		GanttGroup lastGroup = null;

		for (int i = 0; i < mGanttEvents.size(); i++) {
			GanttEvent ge = (GanttEvent) mGanttEvents.get(i);
			
			if (ge.isHidden())
				continue;

			boolean groupedEvent = false;
			
			if (eventsAlreadyDrawn.contains(ge))
				continue;
			
			if (!isEventVisible(ge)) {
				yStart += mSettings.getEventHeight() + mSettings.getEventSpacer();
				mBottomMostY = yStart + mSettings.getEventHeight() + mSettings.getEventSpacer();
				continue;
			}
							
			//System.err.println(allEventsInGroups);
			// draw entire group if this element is part of a group
			if (allEventsInGroups.contains(ge)) {
				groupedEvent = true;
								
				ArrayList groupEvents = ge.getGanttGroup().getEventMembers();
				int yToUse = 0;
				for (int x = 0; x < groupEvents.size(); x++) {
					yToUse = ((GanttEvent)groupEvents.get(x)).getY();
					if (yToUse != 0)
						break;
				}
				
				if (yToUse == 0)
					yToUse = yStart;				
			}
			
			if (lastLoopWasGroup && !groupedEvent) {
				yStart += mSettings.getEventHeight() + mSettings.getEventSpacer();
				mBottomMostY = yStart + mSettings.getEventHeight() + mSettings.getEventSpacer();
			}
			else if (lastLoopWasGroup) {
				// check if it's the same group, if not, we need to increase
				if (lastGroup != null) {
					// different group? ok, increase spacer
					if (!lastGroup.equals(ge.getGanttGroup())) {
						yStart += mSettings.getEventHeight() + mSettings.getEventSpacer();
						mBottomMostY = yStart + mSettings.getEventHeight() + mSettings.getEventSpacer();						
					}
				}
			}
			
			// at this point it will be drawn
			eventsAlreadyDrawn.add(ge);

			if (ge.isScope()) {
				ge.calculateScope();
			}
			
			int xStart = getStartingXforEvent(ge);
			int xEventWidth = getXLengthForEvent(ge);

			Color cEvent = ge.getStatusColor();
			Color gradient = ge.getGradientStatusColor();

			if (cEvent == null)
				cEvent = mSettings.getDefaultEventColor();
			if (gradient == null)
				gradient = mSettings.getDefaultGradientEventColor();
			
			if (ge.isCheckpoint()) {
				mPaintManager.drawCheckpoint(this, mSettings, mColorManager, ge, gc, mThreeDee, getDayWidth(), xStart, yStart);
			}
			else if (ge.isImage()) {
				mPaintManager.drawImage(this, mSettings, mColorManager, ge, gc, ge.getPicture(), mThreeDee, getDayWidth(), xStart, yStart);
			}
			else if (ge.isScope()) {
				mPaintManager.drawScope(this, mSettings, mColorManager, ge, gc, mThreeDee, getDayWidth(), xStart, yStart, xEventWidth);
			}
			else {
				mPaintManager.drawEvent(this, mSettings, mColorManager, ge, gc, (mSelectedEvent != null && mSelectedEvent.equals(ge)), mThreeDee, getDayWidth(), xStart, yStart, xEventWidth);			
			}
			
			// set event bounds
			Rectangle eventBounds = new Rectangle(xStart, yStart, xEventWidth, mSettings.getEventHeight());
			ge.setBounds(eventBounds.x, eventBounds.y, eventBounds.width, eventBounds.height);
			
			// draws |---------| lines to show revised dates, if any
			if (mShowRevisedDates) {
				mPaintManager.drawRevisedDates(this, mSettings, mColorManager, ge, gc, mThreeDee, xStart, yStart, xEventWidth);
			}

			// draw a little plaque saying how many days that this event is long
			if (mShowNumberOfDaysOnChart) {
				long days = DateHelper.daysBetween(ge.getStartDate().getTime(), ge.getEndDate().getTime()) + 1;
				mPaintManager.drawDaysOnChart(this, mSettings, mColorManager, ge, gc, mThreeDee, xStart, yStart, xEventWidth, (int)days);
			}

			// fetch current font
			Font oldFont = gc.getFont();			
					
			// draw the text if any
			String toDraw = getStringForEvent(ge);
			if (toDraw != null) {
				mPaintManager.drawEventString(this, mSettings, mColorManager, ge, gc, toDraw, mThreeDee, xStart, yStart, xEventWidth);
			}
			
			if (!groupedEvent) {
				yStart += mSettings.getEventHeight() + mSettings.getEventSpacer(); // space them out
				mBottomMostY = yStart + mSettings.getEventHeight() + (i != mGanttEvents.size() - 1 ? mSettings.getEventSpacer() : 0);
			}
			else {
				lastLoopWasGroup = true;
				lastGroup = ge.getGanttGroup();
			}
			
			// reset font
			gc.setFont(oldFont);
		}
	}
	
	// string processing for display text beyond event
	private String getStringForEvent(GanttEvent ge) {
		String toUse = ge.getTextDisplayFormat();
		if (toUse == null)
			toUse = mSettings.getTextDisplayFormat();
		if (toUse == null)
			return "";		
		
		String toReturn = toUse;
		if (ge.getName() != null)
			toReturn = toReturn.replaceAll("#n#", ge.getName());
		else
			toReturn = toReturn.replaceAll("#n#", "");
		
		toReturn = toReturn.replaceAll("#p#", ""+ge.getPercentComplete());		
		if (ge.getStartDate() != null)
			toReturn = toReturn.replaceAll("#sd#", ""+DateHelper.getDate(ge.getStartDate(), mSettings.getDateFormat()));
		else
			toReturn = toReturn.replaceAll("#sd#", "");
		
		if (ge.getEndDate() != null)
			toReturn = toReturn.replaceAll("#ed#", ""+DateHelper.getDate(ge.getEndDate(), mSettings.getDateFormat()));
		else
			toReturn = toReturn.replaceAll("#ed", "");
			
		if (ge.getRevisedStart() != null)
			toReturn = toReturn.replaceAll("#rsd#", ""+DateHelper.getDate(ge.getRevisedStart(), mSettings.getDateFormat()));
		else
			toReturn = toReturn.replaceAll("#rsd#", "");
			
		if (ge.getRevisedEnd() != null)
			toReturn = toReturn.replaceAll("#red#", ""+DateHelper.getDate(ge.getRevisedEnd(), mSettings.getDateFormat()));
		else
			toReturn = toReturn.replaceAll("#red#", "");
		
		if (ge.getStartDate() != null && ge.getEndDate() != null) {
			long days = DateHelper.daysBetween(ge.getStartDate(), ge.getEndDate());
			toReturn = toReturn.replaceAll("#nd#", ""+days);
		}
		else 
			toReturn = toReturn.replaceAll("#nd#", "");
		
		return toReturn;
	}

	// draws the three top horizontal lines
	private void drawTopHorizontalLines(GC gc, Rectangle bounds) {
		gc.setForeground(mLineWeekDividerColor);

		int yStart = 0 - mYScrollPosition;
		
		gc.drawLine(0, yStart, bounds.width, 0);
		gc.drawLine(0, mSettings.getHeaderMonthHeight() - mYScrollPosition, bounds.width, mSettings.getHeaderMonthHeight() - mYScrollPosition);
		gc.drawLine(0, mSettings.getHeaderMonthHeight() - mYScrollPosition + mSettings.getHeaderDayHeight(), bounds.width, mSettings.getHeaderMonthHeight() - mYScrollPosition + mSettings.getHeaderDayHeight());
	}

	/**
	 * Adds a connection between two GanttEvents. ge1 will connect to ge2.
	 *
	 * @param source Source event
	 * @param target Target event
	 */
	public void addDependency(GanttEvent source, GanttEvent target) {
		checkWidget();
		mGmap.put(source, target);
		mGmap.put(target, source);
		mOGMap.put(source, target);
		Connection con = new Connection(source, target);
		mGanttConnections.add(con);
	}
	
	/**
	 * Same as addDependency().
	 * 
	 * @param source Source event
	 * @param target Target event
	 */
	public void addConnection(GanttEvent source, GanttEvent target) {
		checkWidget();
		addDependency(source, target);
	}

	// the tiny little string that goes out at the end of an event
	private Rectangle getFirstStub(Connection con) {
		GanttEvent ge1 = con.getGe1();

		// draw the few pixels right after the box, but don't draw over text
		int xStart = ge1.getX() + ge1.getWidth();
		int yStart = ge1.getY() + (mSettings.getEventHeight() / 2);

		return new Rectangle(xStart, yStart, mSettings.getTextSpacer() / 2, mSettings.getEventHeight());
	}

	// draws the lines and arrows between events
	private void drawConnections(GC gc) {
		gc.setForeground(mArrowColor);

		for (int i = 0; i < mGanttConnections.size(); i++) {
			Connection c = (Connection) mGanttConnections.get(i);

			if (c.getGe1().equals(c.getGe2())) {
				continue;
			}

			if (c.getGe1().getX() == 0 && c.getGe2().getY() == 0) {
				continue;
			}
			
			// don't draw hidden events
			if (c.getGe1().isHidden() || c.getGe2().isHidden())
				continue;

			if (mSettings.showOnlyDependenciesForSelectedItems()) {
				if (mSelectedEvent == null) {
					return;
				}

				if (!mSelectedEvent.equals(c.getGe1())) {
					continue;
				}
			}
			
			if (mSettings.getArrowConnectionType() != ISettings.CONNECTION_MS_PROJECT_STYLE) {
				// draw the stub.. [event]--  .. -- is the stub
				Rectangle rect = getFirstStub(c);
				gc.drawLine(rect.x + 1, rect.y, rect.x + rect.width - 1, rect.y);
	
				// draw down some, (start at the end of stub and draw down remaining height of event box, + half of the event spacer)
				// --
				//  | <-- that part
				Rectangle down = new Rectangle(rect.x + rect.width, rect.y, rect.width, (rect.height / 2) + (mSettings.getEventSpacer() / 2));
				gc.drawLine(down.x, down.y, rect.x + down.width, rect.y + down.height);
	
				// get the top left corner of the target area, then draw a line out to it, only along the x axis
				GanttEvent ge2 = c.getGe2();
				Rectangle rGe2 = new Rectangle(ge2.getX()-mSettings.getArrowHeadEventSpacer(), ge2.getY()+mSettings.getArrowHeadVerticalAdjuster(), ge2.getWidth(), ge2.getHeight());
	
				boolean goingUp = false;
				boolean goingLeft = false;
				if (rect.y > rGe2.y) {
					goingUp = true;
				}
				if (rect.x > rGe2.x) {
					goingLeft = true;
				}
	
				if (mSettings.getArrowConnectionType() == ISettings.CONNECTION_ARROW_RIGHT_TO_TOP) {
									
					// draw the line
					gc.drawLine(down.x, rect.y + down.height, rGe2.x, rect.y + down.height);
	
					// draw the last snippet
					if (goingLeft) {
						if (goingUp) {
							gc.drawLine(rGe2.x, rect.y + down.height, rGe2.x, rGe2.y + mSettings.getEventHeight() + 1);
						}
						else {
							gc.drawLine(rGe2.x, rect.y + down.height, rGe2.x, rGe2.y);
						}
					}
					else {
						gc.drawLine(rGe2.x, rect.y + down.height, rGe2.x, rGe2.y);
					}
	
					if (mSettings.showArrows()) {
						if (goingUp) {
							drawArrowHead(rGe2.x, rGe2.y + mSettings.getEventHeight() / 2 + 4, ARROW_UP, gc);
						}
						else {
							drawArrowHead(rGe2.x, rGe2.y - mSettings.getEventHeight() / 2 - 1, ARROW_DOWN, gc);
						}
					}
	
				}
				else if (mSettings.getArrowConnectionType() == ISettings.CONNECTION_ARROW_RIGHT_TO_LEFT) {
					int offset = 10;
	
					// first of all, draw a bit further of the line we just created
					gc.drawLine(down.x, rect.y + down.height, rGe2.x - offset, rect.y + down.height);
		
					gc.drawLine(rGe2.x - offset, rect.y + down.height, rGe2.x - offset, rGe2.y + mSettings.getEventHeight() / 2);
						
					// draw the last snippet
					gc.drawLine(rGe2.x - offset, rGe2.y + mSettings.getEventHeight() / 2, rGe2.x, rGe2.y + mSettings.getEventHeight() / 2);
	
					if (mSettings.showArrows()) {
						if (goingUp) {
							drawArrowHead(rGe2.x - 7, rGe2.y + mSettings.getEventHeight() / 2, ARROW_LEFT, gc);
						}
						else {
							drawArrowHead(rGe2.x - 7, rGe2.y + mSettings.getEventHeight() / 2, ARROW_RIGHT, gc);
						}
					}
				}
			}
			else {
				// MS Project style basically means as follows
				// 1. All corners are rounded
				// 2. Arrows go from event [above] to event [below] and connect to [below] on the left middle side if [above] comes after [below]
				// 3. Arrows connect from event [above] to event [below] to [below]'s top if [below] is far enough away right-side wise from [above]

				// draw first section that points out from right-side a few pixels and down a little to round it
				Rectangle rect = getFirstStub(c);
				rect.width = mSettings.getTextSpacer()-4; // we need a few pixels to draw the 1'st curve
				if (rect.width < 0)
					rect.width = 0;
				
				int x = rect.x;
				int y = rect.y;
				
				gc.drawLine(x, y, x + rect.width, y);
				x = rect.x + rect.width + 1;
				y += 1;
				gc.drawLine(x, y + 1, x, rect.y + 1);
				y += 1;
				
				// (2) if event is right below us and aligned in such a way that it's doable to draw straight down, we simply draw down
				int indexOfUs = mGanttEvents.indexOf(c.getGe1());
				int indexOfThem = mGanttEvents.indexOf(c.getGe2());
				boolean rightBelow = (indexOfThem == indexOfUs+1);
				int startXOfBelow = getXForDate(c.getGe2().getStartDate());
				int endXOfBelow = getXForDate(c.getGe2().getEndDate()) + getDayWidth();
				boolean inMiddle = (x >= startXOfBelow && x <= endXOfBelow);
				if (rightBelow && inMiddle) {
					x += 1;
					gc.drawLine(x, y, x, c.getGe2().getBounds().y-mSettings.getArrowHeadVerticalAdjuster()-1);
					y = c.getGe2().getBounds().y-mSettings.getArrowHeadVerticalAdjuster();
					y -= 7;
					if (mSettings.showArrows())
						drawArrowHead(x, c.getGe2().getY() - mSettings.getEventHeight() / 2 - 2, ARROW_DOWN, gc);
				}
				else {
					// otherwise, draw down to bottom of event first (but between spacer)
					x += 1;
					gc.drawLine(x, y, x, y + (rect.height / 2) + (mSettings.getEventSpacer() / 2) - 2); // -2 because we need 2 pixels to round corners again
					y += (rect.height / 2) + (mSettings.getEventSpacer() / 2) - 2;			
					
					// now draw left, or right
					int destination = c.getGe2().getX();// + c.getGe2().getWidth();
					int origin = x;
					
					// to the left
					if (destination < origin) {
						gc.drawLine(x-1, y+1, x-1, y+1);
						int neg = 7;
						y += 2;
						gc.drawLine(x-1, y, c.getGe2().getX()-neg, y);
						x = c.getGe2().getX()-neg;
						gc.drawLine(x-1, y+1, x-1, y+1);
						gc.drawLine(x-2, y+2, x-2, y+2);
						x -= 2;
						y += 2;
						gc.drawLine(x, y, x, c.getGe2().getY()+(mSettings.getEventHeight()/2)-2);
						y = c.getGe2().getY()+(mSettings.getEventHeight()/2)-2;
						gc.drawLine(x+1, y+1, x+1, y+1);
						gc.drawLine(x+2, y+2, x+2, y+2);
						gc.drawLine(x+2, y+2, c.getGe2().getX()-1, y+2);
						if (mSettings.showArrows())
							drawArrowHead(c.getGe2().getX()-1-7, y+2, ARROW_RIGHT, gc);
					}
					else {
						if (rightBelow) {
							gc.drawLine(x, y, x, c.getGe2().getY()+(mSettings.getEventHeight()/2)-2);
							y = c.getGe2().getY()+(mSettings.getEventHeight()/2)-2;
							gc.drawLine(x+1, y+1, x+1, y+1);
							gc.drawLine(x+2, y+2, destination-1-mSettings.getArrowHeadEventSpacer(), y+2);
							y += 2;
							if (mSettings.showArrows())
								drawArrowHead(destination-8-mSettings.getArrowHeadEventSpacer(), y, ARROW_RIGHT, gc);
						}
						else {
							gc.drawLine(x, y, x, c.getGe2().getY()+(mSettings.getEventHeight()/2)-2);
							y = c.getGe2().getY()+(mSettings.getEventHeight()/2)-2;
							gc.drawLine(x+1, y+1, x+1, y+1);
							gc.drawLine(x+2, y+2, destination-1-mSettings.getArrowHeadEventSpacer(), y+2);
							y += 2;
							if (mSettings.showArrows())
								drawArrowHead(destination-8-mSettings.getArrowHeadEventSpacer(), y, ARROW_RIGHT, gc);
						}
					}					
				}				
			}
		}
	}

	/**
	 * Draws an arrowhead in the middle of the line between x, y and x2, y2 in the given direction and facing either UP, DOWN, LEFT, RIGHT
	 *
	 * @param x    X coordinate
	 * @param y    Y coordinate
	 * @param face direction arrow should face
	 * @param gc   graphics object to draw on
	 */
	private void drawArrowHead(int x, int y, int face, GC gc) {
		switch (face) {
			case ARROW_UP:
				gc.drawLine(x, y + 3, x, y + 3);
				gc.drawLine(x - 1, y + 4, x + 1, y + 4);
				gc.drawLine(x - 2, y + 5, x + 2, y + 5);
				gc.drawLine(x - 3, y + 6, x + 3, y + 6);
				gc.drawLine(x - 4, y + 7, x + 4, y + 7);
				break;
			case ARROW_DOWN:
				gc.drawLine(x, y + 7, x, y + 7);
				gc.drawLine(x - 1, y + 6, x + 1, y + 6);
				gc.drawLine(x - 2, y + 5, x + 2, y + 5);
				gc.drawLine(x - 3, y + 4, x + 3, y + 4);
				gc.drawLine(x - 4, y + 3, x + 4, y + 3);
				break;
			case ARROW_RIGHT:
				// don't need 1 as a line will be on it
				gc.drawLine(x + 3, y - 4, x + 3, y + 4);
				gc.drawLine(x + 4, y - 3, x + 4, y + 3);
				gc.drawLine(x + 5, y - 2, x + 5, y + 2);
				gc.drawLine(x + 6, y - 1, x + 6, y + 1);
				break;
			case ARROW_LEFT:
				// don't need 1 as a line will be on it
				gc.drawLine(x + 3, y - 4, x + 3, y + 4);
				gc.drawLine(x + 4, y - 3, x + 4, y + 3);
				gc.drawLine(x + 5, y - 2, x + 5, y + 2);
				gc.drawLine(x + 6, y - 1, x + 6, y + 1);
				break;
		}
	}

	// draws the dotted line showing where today's date is
	private void drawTodayLine(GC gc, Rectangle bounds, int xStart, int dayOfWeek) {
		gc.setForeground(mLineTodayColor);
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_DOT);

		// black thick divider on sundays, so move dotted line 1 pixel left on those days for it to show up
		if (dayOfWeek == mCalendar.getFirstDayOfWeek()) {
			xStart--;
		}

		gc.drawLine(xStart, mSettings.getHeaderMonthHeight()-mYScrollPosition, xStart, bounds.height);
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);
	}

	private String getMonthString(Calendar cal) {
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		// show year as '05 etc
		if (year > 2000) {
			year -= 2000;
		}

		// lets support the ol days
		if (year > 1900) {
			year -= 1900;
		}

		StringBuffer buf = new StringBuffer();
		buf.append(mFullMonths[month]);
		buf.append(' ');
		buf.append(year < 10 ? "0" : "");
		buf.append(year);
		return buf.toString();
	}

	private String getShortDateString(Calendar cal) {
		int month = cal.get(Calendar.MONTH);
		int date = cal.get(Calendar.DATE);

		StringBuffer buf = new StringBuffer();
		buf.append(mMonths[month]);
		buf.append(' ');
		buf.append(date);

		return buf.toString();
	}

	// the Date string that is displayed at the very top
	private String getDateString(Calendar cal) {
		int date = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		// show year as '05 etc
		if (year > 2000) {
			year -= 2000;
		}

		// lets support the ol days
		if (year > 1900) {
			year -= 1900;
		}

		StringBuffer buf = new StringBuffer();
		buf.append(mMonths[month]);
		buf.append(' ');
		buf.append(date);
		buf.append(", '");
		buf.append(year < 10 ? "0" : "");
		buf.append(year);

		return buf.toString();
	}

	/**
	 * Sets the calendar date.
	 *
	 * @param date
	 */
	public void setDate(Calendar date) {
		checkWidget();
		setDate(date, false);
	}

	/**
	 * Moves calendar to the current date.
	 */
	public void jumpToToday() {
		checkWidget();
		setDate(Calendar.getInstance(Locale.getDefault()), false);
	}

	/**
	 * Sets the new date of the calendar and redraws.
	 *
	 * @param date
	 */
	public void setDate(Calendar date, boolean applyOffset) {
		checkWidget();
		if (mSettings.startCalendarOnFirstDayOfWeek())
			date.set(Calendar.DAY_OF_WEEK, date.getFirstDayOfWeek());
		
		date.set(Calendar.HOUR, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		
		if (applyOffset) {
			date.add(Calendar.DATE, mStartCalendarAtOffset);
		}

		if (date.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_YEAR)) {
			date.add(Calendar.DATE, -7);
		}

		mCalendar = date;
		redrawCalendar();
	}
	
	public void reindex(GanttEvent event, int newIndex) {
		mGanttEvents.remove(event);
		mGanttEvents.add(newIndex, event);
		redrawEventsArea();
	}
	
	public void addEvent(GanttEvent event) {
		checkWidget();
		addEvent(event, true);
	}
	
	public void addEvent(GanttEvent event, int index) {
		checkWidget();
		if (!mGanttEvents.contains(event)) {
			mGanttEvents.add(index, event);
		}
		
		// full redraw, as event hasn't been added yet so bounds will not return included new event
		redraw();
		
		// update v. scrollbar and such
		mParent.eventNumberModified();
	}

	/**
	 * Adds an event that will be drawn in the chart.
	 *
	 * @param event GanttEvent
	 */
	public void addEvent(GanttEvent event, boolean redraw) {
		checkWidget();
		if (!mGanttEvents.contains(event)) {
			mGanttEvents.add(event);
		}

		// full redraw, as event hasn't been added yet so bounds will not return included new event
		if (redraw)
			redraw();
		
		// update v. scrollbar and such
		mParent.eventNumberModified();
	}

	/**
	 * Removes an event.
	 *
	 * @param event GanttEvent
	 */
	public void removeEvent(GanttEvent event) {
		checkWidget();
		mGanttEvents.remove(event);
		ArrayList toRemove = new ArrayList();
		for (int i = 0; i < mGanttConnections.size(); i++) {
			Connection con = (Connection) mGanttConnections.get(i);

			if (con.getGe1().equals(event) || con.getGe2().equals(event)) {
				toRemove.add(con);
			}

		}
		for (int i = 0; i < toRemove.size(); i++) {
			mGanttConnections.remove(toRemove.get(i));
		}

		redrawEventsArea();
				
		// update v. scrollbar and such
		mParent.eventNumberModified();
	}

	public void clearEvents() {
		checkWidget();
		mGanttEvents.clear();
		mGanttConnections.clear();
		mGmap.clear();
		mOGMap.clear();
		redrawEventsArea();
		mParent.eventNumberModified();
	}

	public boolean hasEvent(GanttEvent event) {
		checkWidget();
		return mGanttEvents.contains(event);
	}

	public void nextMonth() {
		checkWidget();
		mCalendar.add(Calendar.MONTH, 1);
		redrawCalendar();
 	}

	public void prevMonth() {
		checkWidget();
		mCalendar.add(Calendar.MONTH, -1);
		redrawCalendar();
 	}

	/**
	 * Jumps one week forward.
	 */
	public void nextWeek() {
		checkWidget();
		mCalendar.add(Calendar.DATE, 7);
		redrawCalendar();
 	}

	/**
	 * Jumps one week backwards.
	 */
	public void prevWeek() {
		checkWidget();
		mCalendar.add(Calendar.DATE, -7);
		redrawCalendar();
	}

	/**
	 * Moves one day forward.
	 */
	public void nextDay() {
		checkWidget();
		mCalendar.add(Calendar.DATE, 1);
		redrawCalendar();
	}

	/**
	 * Moves one day backwards.
	 */
	public void prevDay() {
		checkWidget();
		mCalendar.add(Calendar.DATE, -1);
		redrawCalendar();
	}

	/**
	 * Redraws the calendar should some event not do it automatically.
	 */
	public void refresh() {
		checkWidget();
		redraw();
	}

	private void redrawCalendar() {
		redraw();
	}

	// redraws only the area where the events are, only call when dates aren't changing
	private void redrawEventsArea() {
		Rectangle rect = getEventBounds();
		redraw(rect.x, rect.y, rect.width, getBounds().height, false);
	}

	// checks whether an event is visible in the current date range that is displayed on the screen
	private boolean isEventVisible(GanttEvent event) {
		if (!mSettings.consumeEventWhenOutOfRange()) {
			return true;
		}

		Date eStart = event.getStartDate().getTime();
		Date eEnd = event.getEndDate().getTime();

		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(mCalendar.getTime());

		long calStart = temp.getTimeInMillis();
		temp.add(Calendar.DATE, mDaysVisible);
		long calEnd = temp.getTimeInMillis();

		if (eStart.getTime() >= calStart && eStart.getTime() <= calEnd) {
			return true;
		}

		return eEnd.getTime() >= calStart && eEnd.getTime() <= calEnd;
	}

	// gets the x position for where the event bar should start
	private int getStartingXforEvent(GanttEvent event) {
		return getStartingXfor(event.getStartDate());
	}
	
	public int getStartingXforEventDate(Calendar date) {
		return getStartingXfor(date);
	}

	public int getStartingXfor(Calendar date) {
		checkWidget();
		if (date == null)
			return 0;
		
		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(mCalendar.getTime());
		if (mCurrentView == ISettings.VIEW_YEAR) {
			temp.set(Calendar.DAY_OF_MONTH, 1);
		}

		long daysBetween = DateHelper.daysBetween(temp.getTime(), date.getTime());
		int dw = getDayWidth();
		return ((int) daysBetween * dw);
	}

	// gets the x position for where the event bar should end
	private int getXLengthForEvent(GanttEvent event) {
		Date eDate;

		if (event.getEndDate() == null)
			return 0;
		
		eDate = event.getEndDate().getTime();

		return getXLengthFor(event.getStartDate().getTime(), eDate);
	}

	private int getXLengthFor(Date start, Date end) {
		if (start == null || end == null)
			return 0;
		
		long daysBetween = DateHelper.daysBetween(start, end);
		daysBetween++;

		int dw = getDayWidth();

		return ((int) daysBetween * dw);
	}

	private int getDayWidth() {
		int dw = mDayWidth;
		if (mCurrentView == ISettings.VIEW_MONTH) {
			dw = mMonthDayWidth;
		}
		else if (mCurrentView == ISettings.VIEW_YEAR) {
			dw = mYearDayWidth;
		}

		return dw;
	}

	// checks whether an x/y position is inside the given rectangle
	private boolean isInside(int x, int y, Rectangle rect) {
		if (rect == null) {
			return false;
		}

		return x >= rect.x && y >= rect.y && x <= (rect.x + rect.width) && y <= (rect.y + rect.height);
	}

	// open edit dialogs
	public void mouseDoubleClick(MouseEvent me) {
		// if we only listen to selected events we won't catch locked or otherwise disabled events, up to the user in the end, not us.
		//if (mSelectedEvent != null) {
			GanttToolTip.kill();

			for (int i = 0; i < mGanttEvents.size(); i++) {
				GanttEvent event = (GanttEvent) mGanttEvents.get(i);
		
				if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {	
					for (int j = 0; j < mEventListeners.size(); j++) 
						((IGanttEventListener)mEventListeners.get(j)).eventDoubleClicked(event, me);
					
					return;										
				}
			}			
		//}
	}

	public void mouseDown(MouseEvent me) {
		if (me.button == 1)
			mMouseIsDown = true;
		
		Point ctrlPoint = toDisplay(new Point(me.x, me.y)); 

		// deal with selection
		for (int i = 0; i < mGanttEvents.size(); i++) {
			GanttEvent event = (GanttEvent) mGanttEvents.get(i);
			if (event.isScope())
				continue;
	
			if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {	
				GC gc = new GC(this);
				Rectangle bounds = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());

				// remove old dotted border
				if (mSelectedEvent != null && mSettings.drawSelectionMarkerAroundSelectedEvent()) {
					removeOldSelectedBorder();
				}

				// fire selection changed
				if ((mSelectedEvent != null && !mSelectedEvent.equals(event)) || mSelectedEvent == null) {
					ArrayList allSelectedEvents = new ArrayList();
					for (int x = 0; x < mEventListeners.size(); x++) {
						IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(x);					
						listener.eventSelected(event, allSelectedEvents, me);
					}
				}
				
				mSelectedEvent = event;

				if (!mSelectedEvent.isCheckpoint() && !mSelectedEvent.isImage() && mSettings.drawSelectionMarkerAroundSelectedEvent()) {
					gc.setLineStyle(SWT.LINE_DOT);
					gc.setForeground(ColorCache.getWhite());
					gc.drawRectangle(bounds.x, bounds.y, bounds.width, mSettings.getEventHeight());
					gc.setLineStyle(SWT.LINE_SOLID);
				}

				gc.dispose();

				if (mSettings.showOnlyDependenciesForSelectedItems()) {
					redrawEventsArea();
				}

				if (me.button == 3) {
					showMenu(ctrlPoint.x, ctrlPoint.y, mSelectedEvent, me);
				}
				
				return;
			}
		}
		
		removeOldSelectedBorder();
		mSelectedEvent = null;

		if (mSettings.showOnlyDependenciesForSelectedItems()) {
			redrawEventsArea();
		}
		
		if (mSettings.allowBlankAreaDragAndDropToMoveDates() && me.button == 1)
			setCursor(CURSOR_HAND);
		
		if (me.button == 3)
			showMenu(ctrlPoint.x, ctrlPoint.y, null, me);
	}

	// remove old dotted border
	private void removeOldSelectedBorder() {
		if (mSelectedEvent != null && !mSelectedEvent.isCheckpoint() && !mSelectedEvent.isImage()) {
			GC gc = new GC(this);
			Rectangle oldBounds = new Rectangle(mSelectedEvent.getX(), mSelectedEvent.getY(), mSelectedEvent.getWidth(), mSelectedEvent.getHeight());
			gc.setLineStyle(SWT.LINE_SOLID);
			gc.setForeground(mEventBorderColor);
			gc.drawRectangle(oldBounds.x, oldBounds.y, oldBounds.width, mSettings.getEventHeight());
			gc.dispose();
		}
	}

	// whenever the mouse button is let up, we reset all drag and resize things, as they only happen when the mouse
	// button is down and we resize or drag & drop
	public void mouseUp(MouseEvent event) {
		mMouseIsDown = false;
		endEverything();
	}

	public void keyPressed(KeyEvent e) {
		//todo: if escape is pressed while doing something, cancel moves and resizes in progress (just endEverything() is not strong enough)
	}

	public void keyReleased(KeyEvent e) {
		checkWidget();
		endEverything();
	}

	private void endEverything() {
		// oh, not quite yet
		if (mMouseIsDown)
			return;
		
		mMouseIsDown = false;
		mMouseDragStartLocation = null;
		// kill auto scroll
		endAutoScroll();

		mShowZoomHelper = false;
		if (mZoomLevelHelpArea != null)
			redraw(mZoomLevelHelpArea.x, mZoomLevelHelpArea.y, mZoomLevelHelpArea.width + 1, mZoomLevelHelpArea.height + 1, false);
		
		mDragging = false;
		mDragEvents.clear();
		mResizing = false;
		this.setCursor(CURSOR_NONE);
		mLastX = 0;
		mCursor = SWT.NONE;
		GanttDateTip.kill();
		GanttToolTip.kill();
		mDragStartDate = null;
	}

	public void mouseMove(final MouseEvent me) {
		try {
			if (mSettings.showToolTips()) {
				GanttToolTip.kill();
			}

			// if we moved mouse back in from out of bounds, kill auto scroll
			Rectangle area = super.getClientArea();
			if (me.x >= area.x && me.x < area.width) {
				endAutoScroll();
			}

			boolean dndOK = mSettings.enableDragAndDrop();
			boolean resizeOK = mSettings.enableResizing();

			// check if we need to auto-scroll
			if ((mDragging || mResizing) && mSettings.enableAutoScroll()) {
				if (me.x < super.getClientArea().x) {
					doAutoScroll(me);
				}

				if (me.x > super.getClientArea().width) {
					doAutoScroll(me);
				}
			}

			// move
			if (mDragging && dndOK) {
				handleMove(me, mSelectedEvent);
				return;
			}

			// resize
			if (mResizing && resizeOK && (mCursor == SWT.CURSOR_SIZEE || mCursor == SWT.CURSOR_SIZEW)) {
				handleResize(me, mSelectedEvent, mLastLeft);
				return;
			}
			
			// when we move a mouse really fast at the beginning of a move or a drag, we need to catch that seperately
			// as "isInside()" will not have a chance to see if it's true or not (below)
			if (!mResizing && !mDragging && dndOK && resizeOK && me.stateMask != 0 && mCursor != SWT.NONE) {
				if (mSelectedEvent != null) {
					if ((mCursor == SWT.CURSOR_SIZEE || mCursor == SWT.CURSOR_SIZEW)) {
						handleResize(me, mSelectedEvent, mCursor == SWT.CURSOR_SIZEW);
						return;
					}
					else if (mCursor == SWT.CURSOR_SIZEALL) {
						handleMove(me, mSelectedEvent);
						return;
					}
				}
			}

			if (me.stateMask == 0) {
				this.setCursor(CURSOR_NONE);
			}

			for (int i = 0; i < mGanttEvents.size(); i++) {
				GanttEvent event = (GanttEvent) mGanttEvents.get(i);
				if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
					if (event.isScope())
						continue;

					int x = me.x;
					int y = me.y;
					Rectangle rect = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());

					boolean onRightBorder = false;
					boolean onLeftBorder = false;

					// n pixels left or right of either border = show resize mouse cursor
					if (x >= (rect.x + rect.width - mSettings.getResizeBorderSensitivity()) && y >= rect.y && x <= (rect.x + rect.width + mSettings.getResizeBorderSensitivity()) && y <= (rect.y + rect.height)) {
						onRightBorder = true;
					}
					else if (x >= (rect.x - mSettings.getResizeBorderSensitivity()) && y >= rect.y && x <= (rect.x + mSettings.getResizeBorderSensitivity()) && y <= (rect.y + rect.height)) {
						onLeftBorder = true;
					}
					
					// right border resize cursor (not images!)
					if (me.stateMask == 0 || me.stateMask == SWT.CTRL) {
						boolean resizeArea = false;
						if (resizeOK && event.canResize()) {
							if ((event.isCheckpoint() && mSettings.allowCheckpointResizing()) || !event.isCheckpoint() && !event.isImage()) {
								if (onRightBorder) {
									this.setCursor(CURSOR_SIZEE);
									mCursor = SWT.CURSOR_SIZEE;
									return;
								}
								else if (onLeftBorder) {
									this.setCursor(CURSOR_SIZEW);
									mCursor = SWT.CURSOR_SIZEW;
									return;
								}
							}
						}
						// move cursor
						if ((dndOK && event.canMove()) && !resizeArea) {
							this.setCursor(CURSOR_SIZEALL);
							mCursor = SWT.CURSOR_SIZEALL;
							return;
						}
					}
					else {
						if ((dndOK || event.canMove()) && mCursor == SWT.CURSOR_SIZEALL) {
							if (isInMoveArea(event, me.x)) {
								handleMove(me, event);
								return;
							}
						}

						if (!event.isCheckpoint()) {
							if ((resizeOK || event.canResize()) && (mCursor == SWT.CURSOR_SIZEE || mCursor == SWT.CURSOR_SIZEW)) {
								handleResize(me, event, onLeftBorder);
								return;
							}
						}
					}
					break;
				}
			}
			
			if (mMouseIsDown && mSettings.allowBlankAreaDragAndDropToMoveDates()) {
				// blank area drag
				if (mMouseDragStartLocation == null)
					mMouseDragStartLocation = new Point(me.x, me.y);

				int diff = me.x - mMouseDragStartLocation.x;
				
				boolean left = false;
				if (diff < 0) 
					left = true;
				
				if (mSettings.flipBlankAreaDragDirection())
					left = !left;
				
				diff /= getDayWidth();
				for (int i = 0; i < Math.abs(diff); i++) {
					mMouseDragStartLocation = new Point(me.x, me.y);
					
					if (left)
						prevDay();
					else
						nextDay();
				}
				
				Point loc = new Point(mMouseDragStartLocation.x + 10, mMouseDragStartLocation.y-20);
				// don't show individual dates if we're on yearly view, they're correct, it's just that
				// we move horizontally month-wise, so it doesn't quite make sense.
				if (mZoomLevel >= ISettings.ZOOM_YEAR_MAX) {
					Calendar temp = Calendar.getInstance();
					temp.setTime(mCalendar.getTime());
					temp.set(Calendar.DAY_OF_MONTH, 1);
					GanttDateTip.makeDialog(mColorManager, DateHelper.getDate(temp, mSettings.getDateFormat()), toDisplay(loc), getBounds().y);
				}
				else
					GanttDateTip.makeDialog(mColorManager, DateHelper.getDate(mCalendar, mSettings.getDateFormat()), toDisplay(loc), getBounds().y);
			}
		}
		catch (Exception error) {
			error.printStackTrace();
		}
	}

	private boolean isInMoveArea(GanttEvent event, int x) {
		Rectangle bounds = new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight());
		return x > (bounds.x + mMoveAreaInsets) && x < (bounds.x + bounds.width - mMoveAreaInsets);
	}

	private void doAutoScroll(MouseEvent event) {
		Rectangle area = super.getClientArea();

		if (event.x < area.x) {
			doAutoScroll(DIRECTION_LEFT);
		}
		else if (event.x > area.width) {
			doAutoScroll(DIRECTION_RIGHT);
		}
		else {
			endAutoScroll();
		}
	}

	private void doAutoScroll(int direction) {
		Runnable timer = null;
		final int TIMER_INTERVAL = 25;

		// If we're already autoscrolling in the given direction do nothing
		if (mAutoScrollDirection == direction) {
			return;
		}

		final Display display = getDisplay();

		if (direction == DIRECTION_LEFT) {
			timer = new Runnable() {
				public void run() {
					if (mAutoScrollDirection == DIRECTION_LEFT) {
						prevDay();
						display.timerExec(TIMER_INTERVAL, this);
					}
				}
			};
		}
		else if (direction == DIRECTION_RIGHT) {
			timer = new Runnable() {
				public void run() {
					if (mAutoScrollDirection == DIRECTION_RIGHT) {
						nextDay();
						display.timerExec(TIMER_INTERVAL, this);
					}
				}
			};
		}
		if (timer != null) {
			mAutoScrollDirection = direction;
			display.timerExec(TIMER_INTERVAL, timer);
		}
	}

	private void endAutoScroll() {
		mAutoScrollDirection = SWT.NULL;
	}

	public int getXForDate(Date date) {
		checkWidget();
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTime(date);

		return getXForDate(cal);
	}

	/**
	 * Gets the x position where the given date starts. Time is ignored.
	 *
	 * @param cal Calendar
	 * @return -1 if date was not found
	 */
	public int getXForDate(Calendar cal) {
		checkWidget();
		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(mCalendar.getTime());

		Calendar temp2 = Calendar.getInstance(Locale.getDefault());
		temp2.setTime(cal.getTime());

		int dw = getDayWidth();
		if (mCurrentView == ISettings.VIEW_MONTH) {
			dw = mMonthDayWidth;
		}
		else if (mCurrentView == ISettings.VIEW_YEAR) {
			// we draw years starting on the left for simplicity's sake
			temp.set(Calendar.DAY_OF_MONTH, 1);
			dw = mYearDayWidth;
		}

		int x = 0;
		for (int i = 0; i < mDaysVisible; i++) {
			if (DateHelper.sameDate(temp, temp2)) {
				return x;
			}

			temp.add(Calendar.DATE, 1);
			x += dw;
		}

		return -1;
	}

	/*private int getPixelsOutOfBounds(boolean earliest, Calendar root) {
		boolean negative = true;
		
		GanttEvent ge = getEvent(earliest);
		Calendar cal;
		if (earliest)
			cal = ge.getStartDate();
		else
			cal = ge.getEndDate();
		
		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(root.getTime());

		Calendar temp2 = Calendar.getInstance(Locale.getDefault());
		temp2.setTime(cal.getTime());
		
		// infinite otherwise
		if (negative && temp.before(temp2))
			negative = !negative;
		
		if (!negative && temp.after(temp2))
			negative = !negative;

		int dw = getDayWidth();
		if (mCurrentView == ISettings.VIEW_MONTH) {
			dw = monthDayWidth;
		}
		else if (mCurrentView == ISettings.VIEW_YEAR) {
			// we draw years starting on the left for simplicity's sake
			temp.set(Calendar.DAY_OF_MONTH, 1);
			dw = yearDayWidth;
		}

		int x = 0;
		while (true) {
			if (DateHelper.sameDate(temp, temp2)) {
				if (!earliest)
					x += dw;
				break;
			}

			if (negative) {
				temp.add(Calendar.DATE, -1);
				x -= dw;
			}
			else {
				temp.add(Calendar.DATE, 1);
				x += dw;
			}
		}
		
		if (earliest) {
			if (x < 0)
				return x;
			else
				return 0;
		}
		else {
			if (x > getBounds().width) { 
				return x - getBounds().width;
			}
			else {
				return 0;
			}
		}
	}
	*/
	
	/*private int getXForDateBoundless(Calendar cal, Calendar root) {
		boolean negative = true;
		
		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(root.getTime());

		Calendar temp2 = Calendar.getInstance(Locale.getDefault());
		temp2.setTime(cal.getTime());
		
		// infinite otherwise
		if (negative && temp.before(temp2))
			negative = !negative;
		
		if (!negative && temp.after(temp2))
			negative = !negative;

		int dw = getDayWidth();
		if (mCurrentView == ISettings.VIEW_MONTH) {
			dw = monthDayWidth;
		}
		else if (mCurrentView == ISettings.VIEW_YEAR) {
			// we draw years starting on the left for simplicity's sake
			temp.set(Calendar.DAY_OF_MONTH, 1);
			dw = yearDayWidth;
		}

		int x = 0;
		while (true) {
			if (DateHelper.sameDate(temp, temp2)) {
				return x;
			}

			if (negative) {
				temp.add(Calendar.DATE, -1);
				x -= dw;
			}
			else {
				temp.add(Calendar.DATE, 1);
				x += dw;
			}
		}
	}*/
	
	/**
	 * Gets the date where the current x position is.
	 *
	 * @param xPosition
	 * @return Calendar of date
	 */
	public Calendar getDateAt(int xPosition) {
		checkWidget();
		Calendar temp = Calendar.getInstance(Locale.getDefault());
		temp.setTime(mCalendar.getTime());

		int dw = getDayWidth();
		if (mCurrentView == ISettings.VIEW_YEAR) {
			temp.set(Calendar.DAY_OF_MONTH, 1);
		}

		// loop from left to right
		int x = 0;
		while (true) {
			if (x > xPosition) {
				break;
			}

			if (x != 0) {
				temp.add(Calendar.DATE, 1);
			}

			x += dw;
		}

		return temp;
	}

	/**
	 * Gets the closest x latitude value to the given x position.
	 *
	 * @param xPosition
	 * @return x latitude
	 */
	public int getXAt(int xPosition) {
		checkWidget();
		// loop from left to right

		int dw = getDayWidth();

		int x = 0;
		while (true) {
			if (x > xPosition) {
				break;
			}

			x += dw;
		}

		return x;
	}

	/**
	 * Handles the resize event according to the following rules:
	 * <p/>
	 * - If we're on the right side of the event and drag right: Increase end date
	 * - If we're on the right side of the event and drag left: Decrease end date
	 * - If we're on the left side of the event and drag right: Increase start date
	 * - If we're on the left side of the event and drag left: Decrease start date
	 * <p/>
	 * Also tries to scroll left or right if we're at the edge
	 */
	private void handleResize(MouseEvent me, GanttEvent event, boolean leftSide) throws Exception {
		if (event == null || !mMouseIsDown) 
			return;		

		if (!mResizing) {
			mResizing = true;
			mLastX = me.x;
			mDragEvents.clear();
			mDragEvents.add(event);
			mLastLeft = leftSide;
		}

		Calendar dat = getDateAt(me.x);
		boolean modified = false;
		
		if (me.x > mLastX) {
			if (mLastLeft) {
				if (ensureNoOverlap(dat, event.getEndDate())) {
					event.setStartDate(dat);
					modified = true;
				}
			}
			else {
				if (ensureNoOverlap(event.getStartDate(), dat)) {
					event.setEndDate(dat);
					modified = true;
				}
			}

			mLastX = me.x;
		}

		if (me.x < mLastX) {
			if (!mLastLeft) {
				if (ensureNoOverlap(event.getStartDate(), dat)) {
					event.setEndDate(dat);
					modified = true;
				}
			}
			else {
				if (ensureNoOverlap(dat, event.getEndDate())) {
					event.setStartDate(dat);
					modified = true;
				}
			}

			mLastX = me.x;
		}

		if (mSettings.showDateTips()) {		
			int xx = me.x;
			
			if (mSettings.showResizeDateTipOnBorders()) {
				if (xx <= event.getX())
					xx = event.getX();
				if (xx >= event.getX() + event.getWidth())
					xx = event.getX() + event.getWidth();
				
				if (!leftSide)
					xx = getXAt(xx);
				else
					xx = event.getX();
			}
			
			Rectangle eBounds = new Rectangle(xx, event.getY()-25, event.getWidth(), event.getHeight());
			Point eventOnDisplay = toDisplay(xx, eBounds.y);
			GanttDateTip.makeDialog(mColorManager, mLastLeft ? DateHelper.getDate(event.getStartDate(), mSettings.getDateFormat()) : DateHelper.getDate(event.getEndDate(), mSettings.getDateFormat()), eventOnDisplay, getBounds().y);
		}
		
		if (modified) {
			ArrayList eventsResized = new ArrayList();
			eventsResized.add(event);
			for (int i = 0; i < mEventListeners.size(); i++) {
				IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);			
				listener.eventsResized(eventsResized, me);
			}
		}

		redrawCalendar();
	}

	/**
	 * Checks whether two dates would overlap or not, with a minimum span of 1 day.
	 *
	 * @param dat1 Date 1
	 * @param dat2 Date 2
	 * @return true if no overlap
	 */
	private boolean ensureNoOverlap(Calendar dat1, Calendar dat2) {
		long days = DateHelper.daysBetween(dat1.getTime(), dat2.getTime());
		return days >= 0;
	}

	/**
	 * Draws a dotted vertical marker at the given date. It will get removed on repaint, so make sure it's drawn as often
	 * as needed.
	 *
	 * @param date
	 */
	public void drawMarker(Date date) {
		checkWidget();
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTime(date);

		int x = getXForDate(cal);

		if (x == -1) {
			return;
		}

		GC gc = new GC(this);
		gc.setLineStyle(SWT.LINE_DOT);
		gc.drawRectangle(x, 0, x, getBounds().height);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.dispose();
	}

	/**
	 * Handles a move event when moving an event.. really.
	 */
	private void handleMove(MouseEvent me, GanttEvent event) throws Exception {
		if (!mSettings.enableDragAndDrop() || !mMouseIsDown)
			return;
		
		if (event == null) 
			return;

		Date mouseDate = getDateAt(me.x).getTime();

		// drag start event, show cross hair arrow cursor and keep going
		if ((me.stateMask & SWT.BUTTON1) != 0 && !mDragging) {
			mDragging = true;
			this.setCursor(CURSOR_SIZEALL);
			mDragEvents.clear();
			mDragEvents.add(event);
			mLastX = me.x;
			mDragStartDate = mouseDate;
		}

		// if we're dragging, with left mouse held down..
		if ((me.stateMask & SWT.BUTTON1) != 0 && mDragging) {
			// right drag

			// there's some math here to calculate how far between the drag start date and the current mouse x position date we are
			// we move the event the amount of days of difference there is. This way we get 2 wanted results:
			// 1. The position where we grabbed the event will remain the same throughout the move
			// 2. The mouse cursor will move with the event and not skip ahead or behind
			// it's important we set the dragStartDate to the current mouse x date once we're done with it, as well as set the last
			// x position to where the mouse was last
			//if (autoScrollDirection == SWT.NULL) {
			if (me.x > mLastX) {
				long diff = DateHelper.daysBetween(mDragStartDate, mouseDate);
				mDragStartDate = mouseDate;
				moveEvent(event, (int) (diff), me.stateMask, me);
			}

			// left drag
			if (me.x < mLastX) {
				long diff = DateHelper.daysBetween(mouseDate, mDragStartDate);
				mDragStartDate = mouseDate;
				moveEvent(event, (int) (-diff), me.stateMask, me);
			}
		}

		// set last x position to where mouse is
		mLastX = me.x;

		if (mSettings.showDateTips() && mDragging) {
			Rectangle eBounds = new Rectangle(event.getX(), event.getY()-25, event.getWidth(), event.getHeight());
			Point eventOnDisplay = toDisplay(me.x, eBounds.y);
			if (event.isCheckpoint()) {
				long days = DateHelper.daysBetween(event.getStartDate(), event.getEndDate());
				days++;

				if (days == 1) {
					GanttDateTip.makeDialog(mColorManager, DateHelper.getDate(event.getStartDate(), mSettings.getDateFormat()), eventOnDisplay, getBounds().y);
				}
				else {
					GanttDateTip.makeDialog(mColorManager, DateHelper.getDate(event.getStartDate(), mSettings.getDateFormat()) + " - " + DateHelper.getDate(event.getEndDate(), mSettings.getDateFormat()), eventOnDisplay, getBounds().y);
				}
			}
			else {
				GanttDateTip.makeDialog(mColorManager, DateHelper.getDate(event.getStartDate(), mSettings.getDateFormat()) + " - " + DateHelper.getDate(event.getEndDate(), mSettings.getDateFormat()), eventOnDisplay, getBounds().y);
			}
		}
	}

	public void mouseEnter(MouseEvent event) {
	}

	public void mouseExit(MouseEvent event) {
	}

	public void mouseHover(MouseEvent me) {
		if (mDragging || mResizing) {
			return;
		}

		// right now all we do here is tooltips
		if (!mSettings.showToolTips()) {
			return;
		}

		// possible fix for mac's over-and-over display of hover tip
		if (GanttToolTip.isActive()) {
			return;
		}

		if (me.stateMask != 0) {
			return;
		}

		for (int i = 0; i < mGanttEvents.size(); i++) {
			GanttEvent event = (GanttEvent) mGanttEvents.get(i);
			if (isInside(me.x, me.y, new Rectangle(event.getX(), event.getY(), event.getWidth(), event.getHeight()))) {
				long days = DateHelper.daysBetween(event.getStartDate(), event.getEndDate());
				days++;
		
				if (event.isCheckpoint() || event.isImage()) {
					if (DateHelper.sameDate(event.getStartDate(), event.getEndDate())) {
						GanttToolTip.makeDialog(mColorManager, event.getName(), DateHelper.getDate(event.getEndDate(), mSettings.getDateFormat()), event.getPercentComplete() + "% complete", super.toDisplay(new Point(me.x, me.y)));
					}
					else {
						GanttToolTip.makeDialog(mColorManager, event.getName(), DateHelper.getDate(event.getStartDate(), mSettings.getDateFormat()) + " - " + DateHelper.getDate(event.getEndDate(), mSettings.getDateFormat()) + " (" + days + " day" + (days == 1 || days == -1 ? ")" : "s)"), event.getPercentComplete() + "% complete", 
								super.toDisplay(new Point(me.x, me.y)));
					}
				}
				else {
					Calendar estiStart = event.getRevisedStart();
					Calendar estiEnd = event.getRevisedEnd();
					String extra = null;
					if (estiStart != null) {
						extra = "Planned: " + DateHelper.getDate(estiStart, mSettings.getDateFormat());

						if (estiEnd != null) {
							long cnt = DateHelper.daysBetween(estiStart, estiEnd);
							cnt++;
							extra += " - " + DateHelper.getDate(estiEnd, mSettings.getDateFormat()) + " (" + cnt + " day" + (cnt == 1 || cnt == -1 ? ")" : "s)");
						}
						else {
							extra += " - n/a";
						}
					}

					if (extra != null) {
						GanttToolTip.makeDialog(mColorManager, event.getName(), extra, "Revised: " + DateHelper.getDate(event.getStartDate(), mSettings.getDateFormat()) + " - " + DateHelper.getDate(event.getEndDate(), mSettings.getDateFormat()) + " (" + days + " day" + (days == 1 || days == -1 ? ")" : "s)"), event.getPercentComplete()
								+ "% complete", super.toDisplay(new Point(me.x, me.y)));
					}
					else {
						GanttToolTip.makeDialog(mColorManager, event.getName(), DateHelper.getDate(event.getStartDate(), mSettings.getDateFormat()) + " - " + DateHelper.getDate(event.getEndDate(), mSettings.getDateFormat()) + " (" + days + " day" + (days == 1 || days == -1 ? ")" : "s)"), event.getPercentComplete() + "% complete",
								super.toDisplay(new Point(me.x, me.y)));
					}
				}
				return;
			}
		}

	}

	// moves an event on the canvas. If SHIFT is held and linked events is true, it moves all events linked to the moved event
	private void moveEvent(GanttEvent ge, int i, int stateMask, MouseEvent me) throws Exception {
		if (i == 0) 
			return;		
		
		ArrayList eventsMoved = new ArrayList();

		if ((stateMask & SWT.SHIFT) == 0 || !mSettings.moveLinkedEventsWhenEventsAreMoved()) {
			Calendar cal1 = Calendar.getInstance(Locale.getDefault());
			Calendar cal2 = Calendar.getInstance(Locale.getDefault());
			cal1.setTime(ge.getStartDate().getTime());
			cal2.setTime(ge.getEndDate().getTime());

			cal1.add(Calendar.DATE, i);
			cal2.add(Calendar.DATE, i);

			ge.setStartDate(cal1);
			ge.setEndDate(cal2);
			
			eventsMoved.add(ge);
		}
		else {
			if ((stateMask & SWT.SHIFT) != 0) {
				ArrayList conns = getEventsDependingOn(ge, new ArrayList());

				ArrayList translated = new ArrayList();
				for (int x = 0; x < conns.size(); x++) {
					GanttEvent md = (GanttEvent) conns.get(x);
					translated.add(md);
				}

				if (!translated.contains(ge)) {
					translated.add(ge);
				}

				mDragEvents.clear();
				mDragEvents = translated;

				for (int x = 0; x < translated.size(); x++) {
					GanttEvent event = (GanttEvent) translated.get(x);

					Calendar cal1 = Calendar.getInstance(Locale.getDefault());
					Calendar cal2 = Calendar.getInstance(Locale.getDefault());
					cal1.setTime(event.getStartDate().getTime());
					cal2.setTime(event.getEndDate().getTime());

					cal1.add(Calendar.DATE, i);
					cal2.add(Calendar.DATE, i);

					event.setStartDate(cal1);
					event.setEndDate(cal2);
					
					if (!eventsMoved.contains(event))
						eventsMoved.add(event);
				}
				
				if (!eventsMoved.contains(ge))
					eventsMoved.add(ge);

			}
		}
		
		for (int x = 0; x < mEventListeners.size(); x++) {
			IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(x);		
			listener.eventsMoved(eventsMoved, me);
		}
		redrawEventsArea();
	}

	private Rectangle getEventBounds() {
		int yStart = mSettings.getHeaderDayHeight() + mSettings.getHeaderMonthHeight();
		int yEnd = yStart;
		// the last part --> + (eventSpacer/2+1) is to include any connecting lines drawn below the last event box
		yEnd += (mGanttEvents.size() * mSettings.getEventHeight()) + (mGanttEvents.size() * mSettings.getEventSpacer()) + (mSettings.getEventSpacer() / 2 + 1);

		return new Rectangle(0, yStart, super.getBounds().width, yEnd - yStart);
	}

	public Rectangle getVisibleBounds() {
		return new Rectangle(0, 0, super.getClientArea().width, mBottomMostY);
	}

	public int getView() {
		return mCurrentView;
	}

	public void setView(int view) {
		mCurrentView = view;
		// do stuff here
		redrawCalendar();
	}
	
	public ArrayList getEvents() {
		return mGanttEvents;
	}

	/**
	 * Fetches the current visible area as an image
	 *
	 * @return Image of the chart
	 */
	public Image getImage() {
		checkWidget();
		Image buffer = new Image(Display.getDefault(), super.getBounds());

		GC gc2 = new GC(buffer);
		drawChartOntoGC(gc2);
		gc2.dispose();

		return buffer;
	}

	private ArrayList getEventsDependingOn(GanttEvent ge, ArrayList ret) {
		ArrayList conns = (ArrayList) mGmap.get(ge);
		if (conns != null && conns.size() > 0) {
			for (int i = 0; i < conns.size(); i++) {
				GanttEvent event = (GanttEvent) conns.get(i);
				if (ret.contains(event))
					continue;
				else
					ret.add(event);

				ArrayList more = getEventsDependingOn(event, ret);
				if (more.size() > 0) {
					for (int x = 0; x < more.size(); x++) {
						if (!ret.contains(more.get(x))) {
							ret.add(more.get(x));
						}
					}
				}
			}
		}
		return ret;
	}

	public void zoomIn(boolean showHelper) {
		checkWidget();
		if (!mSettings.enableZooming())
			return;
		
		mZoomLevel--;
		if (mZoomLevel < 1) {
			mZoomLevel = 1;
		}

		updateZoomLevel();
		redraw();
		
		for (int i = 0; i < mEventListeners.size(); i++) {
			IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);		
			listener.zoomedOut(mZoomLevel);
		}
	}

	private void updateZoomLevel() {
		int originalDayWidth = mSettings.getDayWidth();
		int originalMonthWeekWidth = mSettings.getMonthDayWidth();
		int originalYearMonthDayWidth = mSettings.getYearMonthDayWidth();
		
		switch (mZoomLevel) {
			case ISettings.ZOOM_DAY_MAX:
				mCurrentView = ISettings.VIEW_WEEK;
				mDayWidth = originalDayWidth * 3;
				break;
			case ISettings.ZOOM_DAY_MEDIUM:
				mCurrentView = ISettings.VIEW_WEEK;
				mDayWidth = originalDayWidth * 2;
				break;
			case ISettings.ZOOM_DAY_NORMAL:
				mCurrentView = ISettings.VIEW_WEEK;
				mDayWidth = originalDayWidth;
				break;
			case ISettings.ZOOM_MONTH_MAX:
				mCurrentView = ISettings.VIEW_MONTH;
				mMonthDayWidth = originalMonthWeekWidth + 6;
				break;
			case ISettings.ZOOM_MONTH_MEDIUM:
				mCurrentView = ISettings.VIEW_MONTH;
				mMonthDayWidth = originalMonthWeekWidth + 3;
				break;
			case ISettings.ZOOM_MONTH_NORMAL:
				mCurrentView = ISettings.VIEW_MONTH;
				mMonthDayWidth = originalMonthWeekWidth;
				break;
			case ISettings.ZOOM_YEAR_MAX:
				mCurrentView = ISettings.VIEW_YEAR;
				mYearDayWidth = originalYearMonthDayWidth + 3;
				break;
			case ISettings.ZOOM_YEAR_MEDIUM:
				mCurrentView = ISettings.VIEW_YEAR;
				mYearDayWidth = originalYearMonthDayWidth + 2;
				break;
			case ISettings.ZOOM_YEAR_NORMAL:
				mCurrentView = ISettings.VIEW_YEAR;
				mYearDayWidth = originalYearMonthDayWidth;
				break;
			case ISettings.ZOOM_YEAR_SMALL:
				mCurrentView = ISettings.VIEW_YEAR;
				mYearDayWidth = originalYearMonthDayWidth - 1;
				break;
			case ISettings.ZOOM_YEAR_VERY_SMALL:
				mCurrentView = ISettings.VIEW_YEAR;
				mYearDayWidth = originalYearMonthDayWidth - 2;
				break;
		}

		mWeekWidth = mDayWidth * 7;
		mMonthWeekWidth = mMonthDayWidth * 7;

		mYearMonthWeekWidth = mYearDayWidth * 7;
		mYearMonthWidth = mYearMonthWeekWidth * 4;
	}

	public void zoomOut(boolean showHelper) {
		checkWidget();
		if (!mSettings.enableZooming())
			return;

		mZoomLevel++;
		if (mZoomLevel > ISettings.ZOOM_YEAR_VERY_SMALL) {
			mZoomLevel = ISettings.ZOOM_YEAR_VERY_SMALL;
		}

		updateZoomLevel();
		redraw();
		
		for (int i = 0; i < mEventListeners.size(); i++) {
			IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);		
			listener.zoomedOut(mZoomLevel);
		}
	}

	public void resetZoom() {
		checkWidget();
		if (!mSettings.enableZooming())
			return;

		mZoomLevel = mSettings.getInitialZoomLevel();
		updateZoomLevel();
		redraw();
		
		for (int i = 0; i < mEventListeners.size(); i++) {
			IGanttEventListener listener = (IGanttEventListener) mEventListeners.get(i);		
			listener.zoomReset();
		}
	}	
	
	public void addGanttEventListener(IGanttEventListener listener) {
		checkWidget();
		if (mEventListeners.contains(listener))
			return;
		
		mEventListeners.add(listener);
	}
	
	public void removeGanttEventListener(IGanttEventListener listener) {
		checkWidget();
		mEventListeners.remove(listener);
	}
	
	// one connection between 2 events
	class Connection {
		private GanttEvent ge1;

		private GanttEvent ge2;

		public Connection(GanttEvent ge1, GanttEvent ge2) {
			this.ge1 = ge1;
			this.ge2 = ge2;
		}

		public GanttEvent getGe1() {
			return ge1;
		}

		public GanttEvent getGe2() {
			return ge2;
		}
	}

}
