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

package org.eclipse.nebula.widgets.calendarcombo;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;

class CalendarComposite extends Canvas implements MouseListener, MouseMoveListener {

	private final int ARROW_LEFT = 1;

	private final int ARROW_RIGHT = 2;

	private Button mButtonToday;

	private Button mButtonNone;

	private Rectangle mLeftArrowBounds;

	private Rectangle mRightArrowBounds;

	private Rectangle mMonthNameBounds;

	private Calendar mCalendar = Calendar.getInstance(Locale.getDefault());

	private Calendar mToday = Calendar.getInstance(Locale.getDefault());

	private int mDatesTopY = 0;

	private int mDayXs[] = new int[7];

	private CalDay[] mDays = new CalDay[7 * 6];

	private Calendar mSelectedDay;

	private static DateFormatSymbols mDFS = new DateFormatSymbols(Locale.getDefault());

	private String mMonths[] = mDFS.getMonths();

	private static String[] mDayTitles = null;

	private boolean mMonthSelectorOpen;

	private ArrayList mListeners;

	private boolean mNoDayClicked;

	// this thread deals with holding down the mouse over the arrows to jump
	// months quickly

	// milliseconds
	private static final int ARROW_SLOW_TIME = 300;

	private static final int ARROW_FAST_TIME = 120;

	// after how many iterations do we switch speed? (after how many months flipped-by)
	private static final int ARROW_SPEED_SWITCH_COUNT = 10;

	// default speed
	private int mArrowSleepTime = ARROW_SLOW_TIME;

	// various variables used to keep track
	private int mArrowIterations = 0;

	private Thread mArrowThread;

	private boolean mArrowRun;

	private boolean mArrowPause;

	private int mArrowThreadDirection = 0;

	private IColorManager mColorManager;
	
	private ISettings mSettings;

	static {
		String[] weekdays = mDFS.getWeekdays();
		mDayTitles = new String[weekdays.length];
		for (int i = 0; i < weekdays.length; i++) {
			String weekday = weekdays[i];
			if (weekday.length() > 0) {
				mDayTitles[i] = weekday.substring(0, 1).toUpperCase();
			}
		}
	}

	public CalendarComposite(Composite parent, Calendar selectedDay, IColorManager colorManager, ISettings settings) {
		super(parent, SWT.NO_BACKGROUND | SWT.NO_FOCUS | SWT.DOUBLE_BUFFERED);
		this.mSelectedDay = selectedDay;
		this.mCalendar = selectedDay;
		this.mColorManager = colorManager;
		this.mSettings = settings;
		if (this.mCalendar == null)
			this.mCalendar = Calendar.getInstance(Locale.getDefault());
		build();
	}

	private void build() {
		// button height & width and spacers
		int bheight = mSettings.getButtonHeight();
		int bwidth = mSettings.getButtonWidth();
		int buttonStyle = SWT.PUSH;
		mListeners = new ArrayList();

		// Mac buttons need different flag to ook normal
		if (CalendarCombo.OS_CARBON) {
			buttonStyle = SWT.FLAT;
		}
		
		setLayout(new ButtonSectionLayout());
						
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				paint(event);
			}
		});

		addMouseListener(this);
		addMouseMoveListener(this);
					
		mButtonToday = new Button(this, buttonStyle);
		mButtonToday.setText(mSettings.getTodayText());
		mButtonToday.setLayoutData(new GridData(bwidth, bheight));
		mButtonToday.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				clickedTodayButton();
			}
		});

		mButtonNone = new Button(this, buttonStyle);
		mButtonNone.setText(mSettings.getNoneText());
		mButtonNone.setLayoutData(new GridData(bwidth, bheight));
		mButtonNone.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				clickedNoneButton();
			}
		});
	}

	private void clickedTodayButton() {
		setDate(Calendar.getInstance(Locale.getDefault()));
		mSelectedDay = Calendar.getInstance(Locale.getDefault());
		notifyListeners();
	}

	private void clickedNoneButton() {
		notifyListeners(null);
		notifyClose();
	}

	private void paint(PaintEvent event) {
		GC gc = event.gc;
		drawChartOntoGC(gc);
	}

	private void drawChartOntoGC(GC gc) {
		Rectangle bounds = super.getBounds();
		gc.setBackground(mColorManager.getCalendarBackgroundColor());
		gc.fillRectangle(bounds);
		// header
		drawHeader(gc);
		// day titles
		drawTitleDays(gc);
		// days
		drawDays(gc);
		// 1 pixel border
		drawBorder(gc);
		gc.dispose();
	}

	public void setDate(Calendar date) {
		date.set(Calendar.DAY_OF_MONTH, 1);
		this.mCalendar = date;
		redraw();
	}

	public void nextMonth() {
		mSelectedDay = null;
		this.mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		this.mCalendar.add(Calendar.MONTH, 1);
		redraw();
	}

	public void prevMonth() {
		mSelectedDay = null;
		this.mCalendar.add(Calendar.MONTH, -1);
		this.mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		redraw();
	}

	public void goToToday() {
		mSelectedDay = null;
		this.mCalendar = Calendar.getInstance(Locale.getDefault());
		redraw();
	}

	// draws 1 pixel border around entire calendar
	private void drawBorder(GC gc) {
		Rectangle bounds = super.getBounds();

		gc.setForeground(mColorManager.getCalendarBorderColor());
		gc.drawRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
	}

	private void drawHeader(GC gc) {
		Rectangle bounds = super.getBounds();
		Rectangle bgRect = new Rectangle(bounds.x + mSettings.getHeaderLeftMargin(), bounds.y + mSettings.getHeaderTopMargin(), mSettings.getCalendarWidth()-13, bounds.y + mSettings.getHeaderHeight());
		gc.setBackground(mColorManager.getCalendarHeaderColor());
		gc.fillRectangle(bgRect);
		drawArrow(gc, bounds.x + mSettings.getHeaderLeftMargin() + mSettings.getArrowLeftSpacing() + 1, bounds.y + mSettings.getHeaderTopMargin() + mSettings.getArrowTopSpacing() + 4, ARROW_LEFT);
		drawArrow(gc, bounds.x + mSettings.getCalendarWidth()-13 - mSettings.getHeaderRightMargin(), bounds.y + mSettings.getHeaderTopMargin() + mSettings.getArrowTopSpacing() + 4, ARROW_RIGHT);

		String toDraw = mMonths[mCalendar.get(Calendar.MONTH)] + " " + mCalendar.get(Calendar.YEAR);
		Point strWidth = gc.stringExtent(toDraw);

		int avail = mSettings.getCalendarWidth()-13 - strWidth.x;
		avail /= 2;

		mMonthNameBounds = new Rectangle(bounds.x + mSettings.getHeaderLeftMargin() + avail, bounds.y + mSettings.getHeaderTopMargin() + 1, strWidth.x, strWidth.y);

		gc.drawString(toDraw, bounds.x + mSettings.getHeaderLeftMargin() + avail, bounds.y + mSettings.getHeaderTopMargin() + 1, true);
	}

	private void drawTitleDays(GC gc) {
		Calendar temp = Calendar.getInstance(Locale.getDefault());
		// fetch the first day of the week, and draw starting on that day
		int fdow = temp.getFirstDayOfWeek();

		Rectangle bounds = super.getBounds();
		int xStart = mSettings.getDatesLeftMargin() + 5;
		int yStart = bounds.y + mSettings.getHeaderTopMargin() + mSettings.getHeaderHeight() + 1;

		int spacer = 0;
		int letterHeight = 0;

		for (int i = 0; i < 7; i++) {
			Point strWidth = gc.stringExtent(mDayTitles[fdow]);

			int x = xStart + mSettings.getOneDateBoxSize() + spacer - strWidth.x;
			// don't add the string width, as our string width later when
			// drawing days will differ
			mDayXs[i] = xStart + mSettings.getOneDateBoxSize() + spacer;

			gc.drawString(mDayTitles[fdow], x, yStart, true);

			letterHeight = strWidth.y;
			spacer += mSettings.getOneDateBoxSize() + mSettings.getBoxSpacer();

			fdow++;
			if (fdow > 7) {
				fdow = 1;
			}
		}

		int lineStart = yStart + 1 + letterHeight;
		gc.setForeground(mColorManager.getLineColor());
		gc.drawLine(mSettings.getDatesLeftMargin() + 1, lineStart, bounds.width - mSettings.getDatesRightMargin() - 3, lineStart);

		mDatesTopY = lineStart + 3;
	}

	private void drawDays(GC gc) {
		gc.setBackground(mColorManager.getCalendarBackgroundColor());
		gc.setForeground(mColorManager.getTextColor());

		Rectangle bounds = super.getBounds();
		int spacer = 0;

		Calendar temp = (Calendar) mCalendar.clone();
		temp.set(Calendar.DAY_OF_MONTH, 1);

		int monthToShow = temp.get(Calendar.MONTH);

		int firstDayOfWeek = temp.getFirstDayOfWeek();
		int firstDay = temp.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek;
		if (firstDay < 0) {
			firstDay += 7;
		}

		temp.add(Calendar.DATE, -firstDay);
		int col = 0;
		int colCount = 0;

		String typicalLetter = "8";
		int strHeight = gc.stringExtent(typicalLetter).y;

		gc.setForeground(mColorManager.getLineColor());

		int lastY = 0;

		for (int y = 0; y < 42; y++) {
			// new row
			if (y % 7 == 0 && y != 0) {
				spacer += strHeight + 2;
			}

			if (colCount > 6) {
				colCount = 0;
				col = 0;
			}

			int curMonth = temp.get(Calendar.MONTH);

			if (curMonth == monthToShow) {
				gc.setForeground(mColorManager.getTextColor());
			}
			else {
				gc.setForeground(mColorManager.getLineColor());
			}

			String dateStr = "" + temp.get(Calendar.DATE);
			Point width = gc.stringExtent(dateStr);

			if (mSelectedDay != null) {
				if (DateHelper.sameDate(mSelectedDay, temp)) {
					gc.setBackground(mColorManager.getSelectedDayColor());
					if (CalendarCombo.OS_CARBON) {
						gc.fillRectangle(mDayXs[col] - mSettings.getOneDateBoxSize() - 4, mDatesTopY + spacer + 1, mSettings.getOneDateBoxSize() + 5, 14);
					}
					else {
						gc.fillRectangle(mDayXs[col] - mSettings.getOneDateBoxSize() - 4, mDatesTopY + spacer - 1, mSettings.getOneDateBoxSize() + 5, 14);
					}
					gc.setBackground(mColorManager.getCalendarBackgroundColor());
				}
			}

			Rectangle dayBounds = new Rectangle(mDayXs[col] - mSettings.getOneDateBoxSize() - 4, mDatesTopY + spacer - 1, mSettings.getOneDateBoxSize() + 5, 14);

			mDays[y] = new CalDay(y, temp, dayBounds);

			gc.drawString(dateStr, mDayXs[col] - width.x, mDatesTopY + spacer, true);

			if (DateHelper.sameDate(mToday, temp)) {
				Color old = gc.getForeground();
				gc.setForeground(mColorManager.getSelectedDayBorderColor());
				if (CalendarCombo.OS_CARBON) {
					gc.drawRectangle(mDayXs[col] - mSettings.getOneDateBoxSize() - 4, mDatesTopY + spacer + 1, mSettings.getOneDateBoxSize() + 5, 14);
				}
				else {
					gc.drawRectangle(mDayXs[col] - mSettings.getOneDateBoxSize() - 4, mDatesTopY + spacer - 1, mSettings.getOneDateBoxSize() + 5, 14);
				}
				gc.setForeground(old);
			}

			temp.add(Calendar.DATE, 1);
			col++;
			colCount++;
			lastY = mDatesTopY + spacer;
		}

		lastY += strHeight + 1;

		gc.setForeground(mColorManager.getLineColor());
		gc.drawLine(mSettings.getDatesLeftMargin() + 1, lastY, bounds.width - mSettings.getDatesRightMargin() - 3, lastY);
	}

	private void drawArrow(GC gc, int x, int y, int style) {
		gc.setForeground(mColorManager.getArrowColor());
		switch (style) {
			case ARROW_RIGHT:
				gc.drawLine(x, y - 4, x, y + 4);
				gc.drawLine(x + 1, y - 3, x + 1, y + 3);
				gc.drawLine(x + 2, y - 2, x + 2, y + 2);
				gc.drawLine(x + 3, y - 1, x + 3, y + 1);
				gc.drawLine(x + 4, y, x + 4, y);
				mRightArrowBounds = new Rectangle(x - 4, y - 4, x + 8, y);
				break;
			case ARROW_LEFT:
				gc.drawLine(x - 1, y, x - 1, y);
				gc.drawLine(x, y - 1, x, y + 1);
				gc.drawLine(x + 1, y - 2, x + 1, y + 2);
				gc.drawLine(x + 2, y - 3, x + 2, y + 3);
				gc.drawLine(x + 3, y - 4, x + 3, y + 4);
				mLeftArrowBounds = new Rectangle(x - 4, y - 4, x + 8, y);
				break;
		}
	}

	private boolean isInside(int x, int y, Rectangle rect) {
		if (rect == null)
			return false;

		if (x >= rect.x && y >= rect.y && x <= (rect.x + rect.width) && y <= (rect.y + rect.height))
			return true;

		return false;
	}

	public void mouseMove(MouseEvent e) {
		if (mMonthSelectorOpen)
			return;

		// "dragging" the day, just paint it
		if (e.stateMask != 0)
			doDaySelection(e.x, e.y);

		if (mArrowRun && mArrowThread != null) {
			if (!isInside(e.x, e.y, mLeftArrowBounds) && !isInside(e.x, e.y, mRightArrowBounds)) {
				mArrowPause = true;
				// also pause the speed
				mArrowIterations = 0;
				mArrowSleepTime = ARROW_SLOW_TIME;
			}
			else {
				if (isInside(e.x, e.y, mLeftArrowBounds))
					mArrowThreadDirection = ARROW_LEFT;
				else
					mArrowThreadDirection = ARROW_RIGHT;

				mArrowPause = false;
			}
		}
	}

	public void mouseDoubleClick(MouseEvent event) {
	}

	// draw the date selection on mouse down to give that flicker of
	// "response"
	// to the user
	public void mouseDown(MouseEvent event) {
		if (isInside(event.x, event.y, mLeftArrowBounds)) {
			prevMonth();

			runArrowThread(ARROW_LEFT);

			return;
		}

		if (isInside(event.x, event.y, mRightArrowBounds)) {
			nextMonth();

			runArrowThread(ARROW_RIGHT);

			return;
		}

		if (mSettings.showMonthPickerOnMonthNameMousePress() && isInside(event.x, event.y, mMonthNameBounds)) {
			MonthPick mp = new MonthPick(this, SWT.NONE, mCalendar, this);
			mp.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					mMonthSelectorOpen = false;
				}
			});

			// figure out where to put it
			Rectangle bounds = getBounds();
			int xSpacer = (bounds.width - mp.getSize().x) / 2;
			int ySpacer = getLocation().y;
			mp.setLocation(xSpacer, ySpacer);

			mp.setVisible(true);
			mMonthSelectorOpen = true;
			return;
		}

		doDaySelection(event.x, event.y);
	}

	private void killArrowThread() {
		if (mArrowThread != null) {
			mArrowPause = true;
			mArrowThreadDirection = 0;
			mArrowRun = false;
			mArrowThread = null;
		}
	}

	private void runArrowThread(int direction) {
		this.mArrowThreadDirection = direction;
		mArrowIterations = 0;
		mArrowSleepTime = ARROW_SLOW_TIME;
		mArrowRun = false;
		mArrowPause = false;
		mArrowThread = new Thread() {
			public void run() {
				while (mArrowRun) {
					try {
						sleep(mArrowSleepTime);

						if (!mArrowPause)
							mArrowIterations++;

						if (mArrowIterations > ARROW_SPEED_SWITCH_COUNT && mArrowSleepTime != ARROW_FAST_TIME)
							mArrowSleepTime = ARROW_FAST_TIME;
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!mArrowPause) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								if (isDisposed()) {
									mArrowRun = false;
									mArrowThread = null;
								}
								else {
									if (mArrowThreadDirection == ARROW_LEFT)
										prevMonth();
									else if (mArrowThreadDirection == ARROW_RIGHT)
										nextMonth();
								}
							}
						});
					}
				}
			}
		};
		mArrowPause = false;
		mArrowRun = true;
		mArrowThread.start();
	}

	// selects a day at x, y if there is a day there to select
	private void doDaySelection(int x, int y) {
		int curYear = mCalendar.get(Calendar.YEAR);
		int curMonth = mCalendar.get(Calendar.MONTH);
		mNoDayClicked = false;

		for (int i = 0; i < mDays.length; i++) {
			if (isInside(x, y, mDays[i].getBounds())) {

				int dayYear = mDays[i].getDate().get(Calendar.YEAR);
				int dayMonth = mDays[i].getDate().get(Calendar.MONTH);

				if (dayYear == curYear) {
					if (dayMonth < curMonth) {
						prevMonth();
						return;
					}
					else if (dayMonth > curMonth) {
						nextMonth();
						return;
					}
				}
				else {
					if (dayYear < curYear) {
						prevMonth();
						return;
					}
					else if (dayYear > curYear) {
						nextMonth();
						return;
					}
				}

				mSelectedDay = mDays[i].getDate();
				redraw();
				return;
			}
		}

		mNoDayClicked = true;
	}

	public void mouseUp(MouseEvent event) {
		killArrowThread();

		if (mNoDayClicked) {
			mNoDayClicked = false;
			return;
		}

		if (mSelectedDay != null) {
			notifyListeners();
			notifyClose();
		}
	}

	private void notifyClose() {
		for (int i = 0; i < mListeners.size(); i++) {
			ICalendarListener l = (ICalendarListener) mListeners.get(i);
			l.popupClosed();
		}
	}

	private void notifyListeners(Calendar date) {
		for (int i = 0; i < mListeners.size(); i++) {
			ICalendarListener l = (ICalendarListener) mListeners.get(i);
			l.dateChanged(date);
		}
	}

	private void notifyListeners() {
		for (int i = 0; i < mListeners.size(); i++) {
			ICalendarListener l = (ICalendarListener) mListeners.get(i);
			l.dateChanged(mSelectedDay);
		}
	}

	public void addCalendarListener(ICalendarListener listener) {
		if (!mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}

	public void removeCalendarListener(ICalendarListener listener) {
		mListeners.remove(listener);
	}

	public boolean externalClick(Point p) {
		Point loc = mButtonToday.getLocation();
		p = toControl(p);
		int width = mButtonToday.getSize().x;
		int height = mButtonToday.getSize().y;
		Rectangle area = new Rectangle(loc.x, loc.y, width, height);

		if (isInside(p.x, p.y, area)) {
			clickedTodayButton();
			return true;
		}

		loc = mButtonNone.getLocation();
		width = mButtonNone.getSize().x;
		height = mButtonNone.getSize().y;
		area = new Rectangle(loc.x, loc.y, width, height);

		if (isInside(p.x, p.y, area))
			clickedNoneButton();

		return true;
	}

	public boolean isMonthPopupActive() {
		return mMonthSelectorOpen;
	}
	
	class ButtonSectionLayout extends Layout {

		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			return new Point(SWT.DEFAULT, SWT.DEFAULT);
		}

		protected void layout(Composite composite, boolean flushCache) {
			int bheight = mSettings.getButtonHeight();
			int bwidth = mSettings.getButtonWidth();
			int vspacer = mSettings.getButtonVerticalSpace();
			int bspacer = mSettings.getButtonsHorizontalSpace();
			
			// see how much space we put on the left and right sides of the buttons
			int width = mSettings.getCalendarWidth() - (bwidth*2) - bspacer;
			width /= 2;

			int button1Left = width;
			int button2Left = mSettings.getCalendarWidth() - width - bwidth; 
			
			Control [] children = composite.getChildren();
			for (int i = 0; i < children.length; i++) {
				switch (i) {
					case 0:
						children[i].setBounds(button1Left, vspacer, bwidth, bheight);						
						break;
					case 1:
						children[i].setBounds(button2Left, vspacer, bwidth, bheight);
						break;
				}
				
			}
		}
		
	}
	
	class CalDay {
		private Calendar date;

		private int number;

		private Rectangle bounds;

		public CalDay(int number, Calendar date, Rectangle bounds) {
			this.date = (Calendar) date.clone();
			this.bounds = bounds;
			this.number = number;
		}

		public Calendar getDate() {
			return date;
		}

		public int getNumber() {
			return number;
		}

		public Rectangle getBounds() {
			return bounds;
		}
	}
}
