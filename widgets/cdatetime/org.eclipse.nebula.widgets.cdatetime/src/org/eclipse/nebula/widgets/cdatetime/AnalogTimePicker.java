/****************************************************************************
 * Copyright (c) 2007-2008 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.nebula.cwt.v.VButton;
import org.eclipse.nebula.cwt.v.VLayout;
import org.eclipse.nebula.cwt.v.VPanel;
import org.eclipse.nebula.cwt.v.VTracker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Listener;

class AnalogTimePicker extends VPanel {

	class BaseLayout extends VLayout {

		@Override
		protected Point computeSize(VPanel panel, int wHint, int hHint,
				boolean flushCache) {
			Point size = dialPanel.computeSize(wHint, hHint, flushCache);
			if (digitalClock != null) {
				size.y += digitalClock.computeSize(wHint, hHint, flushCache).y;
			}
			return size;
		}

		@Override
		protected void layout(VPanel panel, boolean flushCache) {
			Rectangle clientArea = panel.getClientArea();
			Point dclockSize = digitalClock != null
					? digitalClock.computeSize(SWT.DEFAULT, SWT.DEFAULT,
							flushCache)
					: new Point(0, 0);

			int dwidth = clientArea.width;
			int dheight = Math.min(dwidth, clientArea.height - dclockSize.y);
			if (dheight < dwidth) {
				dwidth = dheight;
			}

			int dx = clientArea.x + (clientArea.width - dwidth) / 2;
			int dy = clientArea.y
					+ (clientArea.height - dheight - dclockSize.y) / 2;

			dialPanel.setBounds(dx, dy, dwidth, dheight);

			if (digitalClock != null) {
				digitalClock.setBounds(
						clientArea.x + (clientArea.width - dclockSize.x) / 2,
						dy + dheight, dclockSize.x, dclockSize.y);
			}
		}
	}

	class DialLayout extends VLayout {

		@Override
		protected Point computeSize(VPanel panel, int wHint, int hHint,
				boolean flushCache) {
			return new Point(200, 200);
		}

		@Override
		protected void layout(VPanel panel, boolean flushCache) {
			Rectangle r = panel.getClientArea();
			dialRadius = (Math.min(r.width, r.height) - 10) / 2;
			dialCenter.x = r.x + r.width / 2;
			dialCenter.y = r.y + r.height / 2;

			if (timeNow != null) {
				timeNow.setBounds(dialCenter.x - 11, dialCenter.y - 11, 22, 22);
			}

			if (timeAmPm != null) {
				Point size = timeAmPm.computeSize(-1, -1);
				timeAmPm.setBounds(dialCenter.x - size.x / 2,
						dialCenter.y + dialRadius / 3 - size.y / 4, size.x,
						size.y);
			}
		}
	}

	VPanel dialPanel;

	VButton timeNow;
	VButton timeAmPm;
	CDateTime digitalClock;
	int dialRadius;
	Point dialCenter = new Point(0, 0);

	boolean setH = false;
	boolean setM = false;
	boolean setS = false;
	boolean overHour = false;
	boolean overMin = false;
	boolean overSec = false;
	boolean is24Hour;
	boolean hourHand;
	boolean minHand;
	boolean secHand;
	boolean am_pm;
	boolean compact;
	private int[] snap = { 1, 1 };
	long increment = 300000; // 5 minutes

	private CDateTime cdt;
	String pattern;

	private Listener tapl;

	public AnalogTimePicker(CDateTime parent) {
		super(parent.pickerPanel, parent.style);
		cdt = parent;
		compact = (cdt.style & CDT.COMPACT) != 0;
		createContents();
	}

	public AnalogTimePicker(CDateTime cdt, DatePicker parent) {
		super(parent, 0);
		this.cdt = cdt;
		compact = (cdt.style & CDT.COMPACT) != 0;
		createContents();
	}

	protected void createContents() {
		setLayout(new BaseLayout());

		dialPanel = new VPanel(this, SWT.NONE);
		dialPanel.setLayout(new DialLayout());

		timeAmPm = new VButton(dialPanel, SWT.NO_FOCUS);
		timeAmPm.setText("PM"); //$NON-NLS-1$
		timeAmPm.setForeground(
				getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		timeAmPm.setMargins(4, 4);
		timeAmPm.setEnabled(!dialPanel.hasStyle(CDT.READ_ONLY));
		tapl = event -> {
			if (event.widget == null) {
				Calendar tmpcal = cdt.getCalendarInstance();
				tmpcal.set(Calendar.AM_PM,
						tmpcal.get(Calendar.AM_PM) == 0 ? 1 : 0);
				setSelection(tmpcal.getTime());
				cdt.fireSelectionChanged(Calendar.AM_PM);
			}
		};
		timeAmPm.addListener(SWT.Selection, tapl);
		timeAmPm.addListener(SWT.MouseWheel, tapl);

		Listener listener = event -> {
			if (cdt.getEditable()) {
				switch (event.type) {
				case SWT.Deactivate:
					if (VTracker.isMouseDown()) {
						handleMouseUp();
						overHour = overMin = overSec = false;
						redraw();
					}
					break;
				case SWT.MouseDown:
					handleMouseDown();
					break;
				case SWT.MouseMove:
					handleMouseMove(event.x, event.y);
					break;
				case SWT.MouseUp:
					handleMouseUp();
					break;
				case SWT.MouseWheel:
					handleMouseWheel(event.count);
					break;
				}
			}
		};

		dialPanel.addListener(SWT.Deactivate, listener);
		dialPanel.addListener(SWT.MouseDown, listener);
		dialPanel.addListener(SWT.MouseMove, listener);
		dialPanel.addListener(SWT.MouseUp, listener);
		dialPanel.addListener(SWT.MouseWheel, listener);

		dialPanel.setPainter(new AnalogClockPainter(cdt, this));
	}

	public int[] getFields() {
		return new int[] { Calendar.HOUR_OF_DAY, Calendar.HOUR, Calendar.MINUTE,
				Calendar.SECOND, Calendar.AM_PM };
	}

	long getIncrement() {
		return increment;
	}

	/**
	 * Get the snap intervals used when setting the minutes and seconds.
	 * 
	 * @return an int[2] where int[0] is the minutes snap, and int[1] is the
	 *         seconds snap
	 * @see #setTimeSnap(int, int)
	 */
	int[] getSnap() {
		return snap;
	}

	private void handleMouseDown() {
		if (overHour) {
			setH = true;
		} else if (overMin) {
			setM = true;
		} else if (overSec) {
			setS = true;
		}
		if (setH || setM || setS) {
			dialPanel.getComposite().setCursor(
					getDisplay().getSystemCursor(SWT.CURSOR_SIZEALL));
			if (timeAmPm != null) {
				timeAmPm.setEnabled(false);
			}
		}
	}

	private void handleMouseMove(int x, int y) {
		Calendar tmpcal = cdt.getCalendarInstance();
		int dx = x - dialCenter.x;
		int dy = y - dialCenter.y;
		double val;
		if (dx == 0) {
			if (dy > 0) {
				val = 30;
			} else {
				val = 0;
			}
		} else if (dy == 0) {
			if (dx > 0) {
				val = 15;
			} else {
				val = 45;
			}
		} else {
			val = 30 * Math.atan((double) dy / (double) dx) / Math.PI + 15;
			if (dx < 0) {
				val += 30;
			}
		}
		if (setH) {
			val = is24Hour ? val / 2.5 : val / 5;
			int v = (int) (val - (double) tmpcal.get(Calendar.MINUTE) / 60
					+ .5);
			if (is24Hour && v > 23) {
				v = 23;
			}
			if (!is24Hour && v > 11) {
				v = 11;
			}
			int field = is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR;
			tmpcal.set(field, v);
			setSelection(tmpcal.getTime());
		} else if (setM) {
			int v = (int) (val + 0.5);
			if (v > 59) {
				v = 59;
			}
			tmpcal.set(Calendar.MINUTE, v);
			setSelection(tmpcal.getTime());
		} else if (setS) {
			int v = (int) (val + 0.5);
			if (v > 59) {
				v = 59;
			}
			tmpcal.set(Calendar.SECOND, v);
			setSelection(tmpcal.getTime());
		} else {
			boolean rd = false;
			if (overHour || overMin || overSec) {
				rd = true;
			}
			overHour = false;
			overMin = false;
			overSec = false;
			if (Math.sqrt(dx * dx + dy * dy) < dialRadius) {
				double h = (tmpcal
						.get(is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR)
						+ (double) tmpcal.get(Calendar.MINUTE) / 60)
						* (is24Hour ? 2.5 : 5);
				int m = tmpcal.get(Calendar.MINUTE);
				int s = tmpcal.get(Calendar.SECOND);
				if (hourHand && val - 1 < h && h <= val + 1) {
					overHour = true;
					rd = true;
				} else if (minHand && val - 1 < m && m <= val + 1) {
					overMin = true;
					rd = true;
				} else if (secHand && val - 1 < s && s <= val + 1) {
					overSec = true;
					rd = true;
				}
			}
			if (rd) {
				dialPanel.redraw();
			}
		}
	}

	private void handleMouseUp() {
		dialPanel.getComposite()
				.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
		if (timeAmPm != null) {
			timeAmPm.setEnabled(true);
		}
		if (setH) {
			int field = is24Hour ? Calendar.HOUR_OF_DAY : Calendar.HOUR;
			cdt.fireSelectionChanged(field);
		} else if (setM) {
			cdt.fireSelectionChanged(Calendar.MINUTE);
		} else if (setS) {
			cdt.fireSelectionChanged(Calendar.SECOND);
		}
		setH = setM = setS = false;
	}

	private void handleMouseWheel(int count) {
		long time = cdt.getCalendarTimeInMillis();
		time += count > 0 ? increment : -increment;
		setSelection(snap(new Date(time), true));
		cdt.fireSelectionChanged();
	}

	public void setFields(int[] calendarFields) {
		is24Hour = false;
		hourHand = false;
		minHand = false;
		secHand = false;
		am_pm = false;
		for (int field : calendarFields) {
			if (field == Calendar.HOUR_OF_DAY) {
				is24Hour = true;
			} else if (field == Calendar.HOUR) {
				hourHand = true;
			} else if (field == Calendar.MINUTE) {
				minHand = true;
			} else if (field == Calendar.SECOND) {
				secHand = true;
			} else if (field == Calendar.AM_PM) {
				am_pm = true;
			}
		}
		if ((cdt.style & CDT.CLOCK_12_HOUR) != 0) {
			is24Hour = false;
			hourHand = true;
			am_pm = true;
		} else if ((cdt.style & CDT.CLOCK_24_HOUR) != 0) {
			is24Hour = true;
		}
		if (is24Hour) {
			hourHand = true;
			am_pm = false;
		}
		timeAmPm.setVisible(am_pm);
		boolean sepOK = false;
		pattern = ""; //$NON-NLS-1$
		String cdtPattern = cdt.getPattern();
		for (int i = 0; i < cdtPattern.length(); i++) {
			char c = cdtPattern.charAt(i);
			if ("Hhmsa".indexOf(c) > -1) { //$NON-NLS-1$
				pattern += c;
				sepOK = true;
			} else {
				if (sepOK && ":., ".indexOf(c) > -1) { //$NON-NLS-1$
					pattern += c;
				}
				sepOK = false;
			}
		}
		if (digitalClock != null) {
			digitalClock.setPattern(pattern);
		}
		updateLabels();
	}

	@Override
	public boolean setFocus() {
		if (timeNow != null) {
			return timeNow.setFocus();
		} else {
			return getComposite().forceFocus();
		}
	}

	void setIncrement(long millis) {
		increment = millis;
	}

	private void setSelection(Date date) {
		cdt.setSelection(snap(date));
	}

	/**
	 * Set the snap for the minutes and seconds. If the value given for either
	 * parameter is less than or equal to zero then its corresponding snap will
	 * be set to its default of one (1).
	 * 
	 * @param min
	 *            the snap interval for the minutes
	 * @param sec
	 *            the snap interval for the seconds
	 * @see #getSnap()
	 */
	void setSnap(int min, int sec) {
		snap[0] = min < 0 ? 1 : min;
		snap[1] = sec < 0 ? 1 : sec;
	}

	@Override
	public boolean setStyle(int style, boolean set) {
		if ((style & SWT.READ_ONLY) != 0) {
			if (timeAmPm != null && !timeAmPm.isDisposed()) {
				timeAmPm.setEnabled(!set);
			}
			if (timeNow != null && !timeNow.isDisposed()) {
				timeNow.setEnabled(!set);
			}
		}
		return super.setStyle(style, set);
	}

	/**
	 * perform the snap and return a new "snapped" Date object
	 */
	private Date snap(Date date) {
		return snap(date, false);
	}

	/**
	 * perform the snap and return a new "snapped" Date object
	 */
	private Date snap(Date date, boolean toIncrement) {
		Calendar tmpcal = cdt.getCalendarInstance();
		tmpcal.setTime(date);

		int msnap = toIncrement ? (int) (increment / 60000) : snap[0];

		int v = tmpcal.get(Calendar.MINUTE);
		int m = v % msnap;
		if (m != 0) {
			v += m > msnap / 2 ? msnap - m : -m;
			if (v > 59) {
				v = 0;
			}
			tmpcal.set(Calendar.MINUTE, v);
		}

		if (!toIncrement) {
			v = tmpcal.get(Calendar.SECOND);
			m = v % snap[1];
			if (m != 0) {
				v += m > snap[1] / 2 ? snap[1] - m : -m;
				if (v > 59) {
					v = 0;
				}
				tmpcal.set(Calendar.SECOND, v);
			}
		}

		return tmpcal.getTime();
	}

	void updateLabels() {
		if (timeNow != null) {
			timeNow.setToolTipText(
					Resources.getString("nav_current_time", cdt.getLocale())); //$NON-NLS-1$
		}
	}

	void updateView() {
		if (digitalClock != null) {
			digitalClock.setSelection(new Date(cdt.getCalendarTimeInMillis()));
		}
		if (timeAmPm != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("a"); //$NON-NLS-1$
			sdf.setTimeZone(cdt.getTimeZone());
			timeAmPm.setText(sdf.format(cdt.getCalendarTime()));
		}
		dialPanel.redraw();
		dialPanel.update();
	}

}