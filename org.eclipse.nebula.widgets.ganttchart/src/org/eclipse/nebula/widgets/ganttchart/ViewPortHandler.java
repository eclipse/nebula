package org.eclipse.nebula.widgets.ganttchart;

import java.util.Calendar;

class ViewPortHandler {

	private GanttComposite _gc;
	
	public ViewPortHandler(GanttComposite parent) {
		_gc = parent;
	}
	
	public void scrollingLeft(int diff) {
		int mCurrentView = _gc.getCurrentView();
		_gc.showScrollDate();

		if (diff == 1) {
			if (mCurrentView == ISettings.VIEW_YEAR) {
				prevMonth();
				return;
			}
			else {
				if (mCurrentView == ISettings.VIEW_DAY) {
					prevHour();
					return;
				}
				else {
					prevDay();
					return;
				}
			}
		}
		else {
			if (diff == 0) {
				if (mCurrentView == ISettings.VIEW_YEAR) {
					prevWeek();
					return;
				}
				else {
					if (mCurrentView == ISettings.VIEW_DAY) {
						prevHour();
						return;
					}
					else {
						prevDay();
						return;
					}
				}
			}

			// year scrolls so fast that the events actually lose position, this is no good, so we need to break the speed
			if (mCurrentView == ISettings.VIEW_YEAR) {
				if (diff > 7)
					diff = 7;
			}

			for (int i = 0; i < diff; i++) {
				if (mCurrentView == ISettings.VIEW_YEAR) {
					prevMonth();
				}
				else {
					if (mCurrentView == ISettings.VIEW_DAY) {
						prevHour();
					}
					else {
						prevDay();
					}
				}
			}
		}
	}

	public void scrollingRight(int diff) {
		int mCurrentView = _gc.getCurrentView();
		_gc.showScrollDate();

		if (diff == 1) {
			if (mCurrentView == ISettings.VIEW_YEAR) {
				nextMonth();
				return;
			}
			else {
				if (mCurrentView == ISettings.VIEW_DAY) {
					nextHour();
					return;
				}
				else {
					nextDay();
					return;
				}
			}
		}
		else {
			// far right
			if (diff == 0) {
				if (mCurrentView == ISettings.VIEW_YEAR) {
					nextWeek();
					return;
				}
				else {
					if (mCurrentView == ISettings.VIEW_DAY) {
						nextHour();
						return;
					}
					else {
						nextDay();
						return;
					}
				}
			}

			// year scrolls so fast that the events actually lose position, this is no good, so we need to break the speed
			// TODO: look into this a bit more, as it's quite odd (same for scrollingLeft)
			if (mCurrentView == ISettings.VIEW_YEAR) {
				if (diff > 7)
					diff = 7;
			}

			for (int i = 0; i < diff; i++) {
				if (mCurrentView == ISettings.VIEW_YEAR) {
					nextMonth();
				}
				else {
					if (mCurrentView == ISettings.VIEW_DAY) {
						nextHour();
					}
					else {
						nextDay();
					}
				}
			}
		}
	}

	/**
	 * Jumps to the next month.
	 */
	public void nextMonth() {
		Calendar mCalendar = _gc.getRootCalendar();
		mCalendar.set(Calendar.DATE, 1);
		mCalendar.add(Calendar.MONTH, 1);

		_gc.setNoRecalc();
		_gc.moveXBounds(false);

		_gc.redraw();
	}

	/**
	 * Jumps to the previous month.
	 */
	public void prevMonth() {
		Calendar mCalendar = _gc.getRootCalendar();
		mCalendar.set(Calendar.DATE, 1);
		mCalendar.add(Calendar.MONTH, -1);

		_gc.setNoRecalc();
		_gc.moveXBounds(true);

		_gc.redraw();
	}

	/**
	 * Jumps one week forward.
	 */
	public void nextWeek() {
		Calendar mCalendar = _gc.getRootCalendar();
		mCalendar.add(Calendar.DATE, 7);

		_gc.setNoRecalc();
		_gc.moveXBounds(false);
		_gc.redraw();
	}

	/**
	 * Jumps one week backwards.
	 */
	public void prevWeek() {
		Calendar mCalendar = _gc.getRootCalendar();
		mCalendar.add(Calendar.DATE, -7);

		_gc.setNoRecalc();
		_gc.moveXBounds(true);
		_gc.redraw();
	}

	/**
	 * Jumps to the next hour.
	 */
	public void nextHour() {
		Calendar mCalendar = _gc.getRootCalendar();
		mCalendar.add(Calendar.HOUR_OF_DAY, 1);
		if (mCalendar.get(Calendar.HOUR_OF_DAY) >= 24) {
			mCalendar.add(Calendar.DATE, 1);
			mCalendar.set(Calendar.HOUR_OF_DAY, 0);
		}

		_gc.setNoRecalc();
		_gc.moveXBounds(false);
		_gc.redraw();
	}

	/**
	 * Jumps to the previous hour.
	 */
	public void prevHour() {
		Calendar mCalendar = _gc.getRootCalendar();
		mCalendar.add(Calendar.HOUR_OF_DAY, -1);

		if (mCalendar.get(Calendar.HOUR_OF_DAY) < 0) {
			mCalendar.add(Calendar.DATE, -1);
			// -1 !!
			mCalendar.set(Calendar.HOUR_OF_DAY, 24 - 1);
		}

		_gc.setNoRecalc();
		_gc.moveXBounds(true);
		_gc.redraw();
	}

	/**
	 * Jumps one day forward.
	 */
	public void nextDay() {
		Calendar mCalendar = _gc.getRootCalendar();
		mCalendar.add(Calendar.DATE, 1);
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);

		_gc.moveXBounds(false);
		_gc.setNoRecalc();
		_gc.redraw();

	}

	/**
	 * Jumps one day backwards.
	 */
	public void prevDay() {
		Calendar mCalendar = _gc.getRootCalendar();
		mCalendar.add(Calendar.DATE, -1);
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);

		_gc.moveXBounds(true);
		_gc.setNoRecalc();
		_gc.redraw();
	}
}
