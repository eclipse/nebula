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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

class MonthPick extends Canvas implements MouseListener, MouseMoveListener {
	// months to show in the popup
	private int mMonthsToShow = 7;

	private Calendar mStart;

	private Rectangle mBounds;

	private int mTopDateSpacer = 2;

	private int mInnerWidth = 99;

	private DateFormatSymbols mDFS = new DateFormatSymbols(Locale.getDefault());

	private String[] mMonths = mDFS.getMonths();

	private ArrayList mMonthEntries = new ArrayList();

	private MonthEntry mHoverEntry;

	private boolean mEnableDoubleBuffering = true;

	private boolean mCreated;

	private Thread mAboveThread = null;

	private Thread mBelowThread = null;

	private boolean mAboveRun = false;

	private boolean mBelowRun = false;

	private CalendarComposite mCalendarComposite = null;

	private Calendar mSelectedMonth = null;
	
	private Locale mLocale;

	public MonthPick(Composite parent, int style, Calendar start, CalendarComposite cc, Locale locale) {
		super(parent, style | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.ON_TOP);
		mStart = start;
		mCalendarComposite = cc;
		mLocale = locale;
		
		setSize(101, 106);
		setCapture(true);

		addMouseListener(this);
		addMouseMoveListener(this);

		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				setCapture(false);
			}
		});

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				paint(event);
			}
		});

	}

	private void paint(PaintEvent event) {
		GC gc = event.gc;

		// this.globalGC = gc;

		// double buffering. this could be triple buffering if the platform does
		// it automatically, windows XP does not seem to however
		// basically, we draw all the updates onto an Image in memory, then we
		// transfer the contents thereof onto the canvas.
		// that way there is 0 flicker, which is the desired effect.
		if (mCreated && mEnableDoubleBuffering) {
			try {
				Image buffer = new Image(Display.getDefault(), super.getBounds());
				GC gc2 = new GC(buffer);
				drawOntoGC(gc2);

				// transfer the image buffer onto this canvas
				// just drawImage(buffer, w, h) didn't work, so we do the whole
				// source transfer call
				Rectangle b = getBounds();
				gc.drawImage(buffer, 0, 0, b.width, b.height, 0, 0, b.width, b.height);

				// dispose the buffer, very important or we'll run out of
				// address space for buffered images
				buffer.dispose();
				gc2.dispose();
			}
			catch (IllegalArgumentException iea) {
				// seems to come here for some reason when we switch phases
				// while the gantt chart is being viewed, I'm not sure why
				// but no time to figure it out for the demo.. so instead of
				// buffering, just draw it onto the GC
				drawOntoGC(gc);
			}
		}
		else {
			drawOntoGC(gc);
			mCreated = true;
		}
	}

	private void drawOntoGC(GC gc) {
		mBounds = super.getBounds();

		drawBG(gc);
		drawDates(gc);
		drawBorder(gc);
	}

	private void drawBG(GC gc) {
		gc.setForeground(ColorCache.getBlack());
		gc.setBackground(ColorCache.getWhite());
		gc.fillRectangle(mBounds.x - 50, mBounds.y, mBounds.width + 50, mBounds.height);
	}

	private void drawBorder(GC gc) {
		Rectangle bounds = getClientArea();
		gc.setForeground(ColorCache.getBlack());
		gc.drawRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
	}

	private void drawDates(GC gc) {
		int y = mTopDateSpacer;
		int spacer = 2;

		Calendar temp = Calendar.getInstance(mLocale);
		temp.setTime(mStart.getTime());

		temp.add(Calendar.MONTH, -3);

		FontData[] old = getFont().getFontData();
		old[0].setHeight(8);
		Font f = new Font(Display.getDefault(), old);
		gc.setFont(f);
		// MUST DISPOSE FONT or we'll run out of handles (win XP)
		f.dispose();

		gc.setForeground(ColorCache.getBlack());

		mMonthEntries.clear();

		// gc.setBackground(ColorCache.getWhite());
		// gc.fillRectangle(bounds);

		for (int i = 0; i < mMonthsToShow; i++) {
			int mo = temp.get(Calendar.MONTH);
			String toDraw = mMonths[mo] + " " + temp.get(Calendar.YEAR);
			Point p = gc.stringExtent(toDraw);

			// center the text in the available space
			int rest = mInnerWidth - p.x;
			rest /= 2;

			Rectangle rect = new Rectangle(0, y - 1, mBounds.width, p.y + 1);

			if (mHoverEntry != null) {
				// draw hover entry
				if (mHoverEntry.getRect().equals(rect)) {
					gc.setBackground(ColorCache.getBlack());
					gc.fillRectangle(mHoverEntry.getRect());
					gc.setBackground(ColorCache.getWhite());
					gc.setForeground(ColorCache.getWhite());
					// lastHoverRect = hoverEntry.getRect();
					mSelectedMonth = mHoverEntry.getCalendar();
				}
			}
			else {
				// draw today
				if (i == 3) {
					gc.setBackground(ColorCache.getBlack());
					gc.fillRectangle(rect);
					gc.setBackground(ColorCache.getWhite());
					gc.setForeground(ColorCache.getWhite());
				}
			}

			gc.drawString(toDraw, rest, y, true);
			gc.setForeground(ColorCache.getBlack());

			MonthEntry me = new MonthEntry(temp);
			me.setRect(rect);
			me.setyPos(y);
			me.setText(toDraw);
			mMonthEntries.add(me);

			temp.add(Calendar.MONTH, 1);
			y += p.y + spacer;
		}
	}

	public void mouseDoubleClick(MouseEvent event) {

	}

	public void mouseDown(MouseEvent event) {

	}

	public void mouseUp(MouseEvent event) {
		mCalendarComposite.mouseUp(event);
		if (mSelectedMonth != null)
			mCalendarComposite.setDate(mSelectedMonth);

		dispose();
	}

	public void mouseMove(final MouseEvent me) {
		for (int i = 0; i < mMonthEntries.size(); i++) {
			MonthEntry mEntry = (MonthEntry) mMonthEntries.get(i);
			if (isInside(me.x, me.y, mEntry.getRect())) {
				mHoverEntry = mEntry;
				mAboveRun = false;
				mBelowRun = false;
				redraw();
				return;
			}
		}
		mHoverEntry = null;

		if (me.y < getLocation().y) {
			if (mAboveThread != null && mAboveThread.isAlive() && mAboveRun) {
				return;
			}

			mBelowRun = false;

			mAboveThread = new Thread() {
				public void run() {
					while (mAboveRun) {
						try {
							sleep(100);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								if (isDisposed())
									mAboveThread = null;
								else
									scrollOneMonth(true);
							}
						});
					}
				}
			};
			mAboveRun = true;
			mAboveThread.start();
		}
		else {
			if (mBelowThread != null && mBelowThread.isAlive() && mBelowRun) {
				return;
			}

			mBelowRun = false;

			mBelowThread = new Thread() {
				public void run() {
					while (mBelowRun) {
						try {
							sleep(100);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								if (isDisposed())
									mBelowThread = null;
								else
									scrollOneMonth(false);
							}
						});
					}
				}
			};
			mBelowRun = true;
			mBelowThread.start();
		}
	}

	private void scrollOneMonth(boolean up) {
		if (up) {
			mStart.add(Calendar.MONTH, -1);
			redraw();
		}
		else {
			mStart.add(Calendar.MONTH, 1);
			redraw();
		}
	}

	private boolean isInside(int x, int y, Rectangle rect) {
		if (rect == null) {
			return false;
		}

		if (x >= rect.x && y >= rect.y && x <= (rect.x + rect.width) && y <= (rect.y + rect.height)) {
			return true;
		}

		return false;
	}

	class MonthEntry {

		private Calendar mCal;

		private Rectangle mRect;

		private int mYPos;

		private String text;

		public MonthEntry(Calendar cal) {
			this.mCal = (Calendar) cal.clone();
		}

		public Calendar getCalendar() {
			return mCal;
		}

		public Rectangle getRect() {
			return mRect;
		}

		public void setRect(Rectangle rect) {
			this.mRect = rect;
		}

		public int getyPos() {
			return mYPos;
		}

		public void setyPos(int yPos) {
			this.mYPos = yPos;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}
}
