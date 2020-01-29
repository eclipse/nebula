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
 *    emil.crumhorn@gmail.com - initial API and implementation
 *    ziogiannigmail.com - Bug 464509 - Minute View Implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.util.Calendar;

import org.eclipse.swt.widgets.Display;

class ViewPortHandler implements IViewPortHandler2 {

	private GanttComposite _ganttComposite;
	
	public ViewPortHandler(GanttComposite parent) {
		_ganttComposite = parent;
	}
	
	public void scrollingLeft(final int diffCount) {
	    int diff = diffCount;
	    
		int mCurrentView = _ganttComposite.getCurrentView();
		_ganttComposite.showScrollDate();

		if (diff == 1) {
			if (mCurrentView == ISettings.VIEW_YEAR) {
				prevMonth();
				return;
			}
			else {
				if (mCurrentView == ISettings.VIEW_DAY) {
					prevHour();
					return;
				}else
				if (mCurrentView == ISettings.VIEW_MINUTE) {
					prevMinute();
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
					}else
					if (mCurrentView == ISettings.VIEW_MINUTE) {
						prevMinute();
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
					}else
					if (mCurrentView == ISettings.VIEW_MINUTE) {
						prevMinute();
						return;
					}
					else {
						prevDay();
					}
				}
			}
		}
	}

	public void scrollingRight(final int diffCount) {
	    int diff = diffCount;
		int mCurrentView = _ganttComposite.getCurrentView();
		_ganttComposite.showScrollDate();

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
				else
				if (mCurrentView == ISettings.VIEW_MINUTE) {
					nextMinute();
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
					}else
					if (mCurrentView == ISettings.VIEW_MINUTE) {
						nextMinute();
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
					}else
					if (mCurrentView == ISettings.VIEW_MINUTE) {
						nextMinute();
						return;
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
		Calendar mCalendar = _ganttComposite.getRootCalendar();
		mCalendar.set(Calendar.DATE, 1);
		mCalendar.add(Calendar.MONTH, 1);

		_ganttComposite.setNoRecalc();
		_ganttComposite.moveXBounds(false);

		_ganttComposite.redraw();
	}

	/**
	 * Jumps to the previous month.
	 */
	public void prevMonth() {
		Calendar mCalendar = _ganttComposite.getRootCalendar();
		mCalendar.set(Calendar.DATE, 1);
		mCalendar.add(Calendar.MONTH, -1);

		_ganttComposite.setNoRecalc();
		_ganttComposite.moveXBounds(true);

		_ganttComposite.redraw();
	}

	/**
	 * Jumps one week forward.
	 */
	public void nextWeek() {
		Calendar mCalendar = _ganttComposite.getRootCalendar();
		mCalendar.add(Calendar.DATE, 7);

		_ganttComposite.setNoRecalc();
		_ganttComposite.moveXBounds(false);
		_ganttComposite.redraw();
	}

	/**
	 * Jumps one week backwards.
	 */
	public void prevWeek() {
		Calendar mCalendar = _ganttComposite.getRootCalendar();
		mCalendar.add(Calendar.DATE, -7);

		_ganttComposite.setNoRecalc();
		_ganttComposite.moveXBounds(true);
		_ganttComposite.redraw();
	}

	/**
	 * Jumps to the next minute.
	 */
	public void nextMinute() {
		Display.getDefault().asyncExec(new Runnable(){
            public void run(){
		try {
		Calendar mCalendar = _ganttComposite.getRootCalendar();
		mCalendar.add(Calendar.MINUTE, 1);

        if (mCalendar.get(Calendar.MINUTE) >= 60) {
			mCalendar.add(Calendar.HOUR_OF_DAY, 1);
			mCalendar.set(Calendar.MINUTE, 0);
		}
        Thread.sleep(50);
        if (_ganttComposite != null && !_ganttComposite.isDisposed()){
		_ganttComposite.setNoRecalc();
		_ganttComposite.moveXBounds(false);
		_ganttComposite.redraw();
		} 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            }
        });
           }
            
            

	/**
	 * Jumps to the previous hour.
	 */
	public void prevMinute() {
		Display.getDefault().asyncExec(new Runnable(){
            public void run(){
            	try {
            		Calendar mCalendar = _ganttComposite.getRootCalendar();
            		mCalendar.add(Calendar.MINUTE, -1);
            		
            	   if (mCalendar.get(Calendar.MINUTE) < 0) {
            			mCalendar.add(Calendar.HOUR_OF_DAY, -1);
            			mCalendar.set(Calendar.MINUTE, 60 - 1);
            		}
            	   Thread.sleep(50);
            	   if (_ganttComposite != null && !_ganttComposite.isDisposed()){
            		_ganttComposite.setNoRecalc();
            		_ganttComposite.moveXBounds(true);
            		_ganttComposite.redraw();
            		} 
            	} catch (Exception e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		}
            }
        });
            }
	/**
	 * Jumps to the next hour.
	 */
	public void nextHour() {
		Calendar mCalendar = _ganttComposite.getRootCalendar();
		mCalendar.add(Calendar.HOUR_OF_DAY, 1);
		if (mCalendar.get(Calendar.HOUR_OF_DAY) >= 24) {
			mCalendar.add(Calendar.DATE, 1);
			mCalendar.set(Calendar.HOUR_OF_DAY, 0);
		}

		_ganttComposite.setNoRecalc();
		_ganttComposite.moveXBounds(false);
		_ganttComposite.redraw();
	}

	/**
	 * Jumps to the previous hour.
	 */
	public void prevHour() {

		Calendar mCalendar = _ganttComposite.getRootCalendar();
		mCalendar.add(Calendar.HOUR_OF_DAY, -1);

		if (mCalendar.get(Calendar.HOUR_OF_DAY) < 0) {
			mCalendar.add(Calendar.DATE, -1);
			// -1 !!
			mCalendar.set(Calendar.HOUR_OF_DAY, 24 - 1);
		}

		_ganttComposite.setNoRecalc();
		_ganttComposite.moveXBounds(true);
		_ganttComposite.redraw();
	}

	/**
	 * Jumps one day forward.
	 */
	public void nextDay() {
		Calendar mCalendar = _ganttComposite.getRootCalendar();
		mCalendar.add(Calendar.DATE, 1);
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);

		_ganttComposite.moveXBounds(false);
		_ganttComposite.setNoRecalc();
		_ganttComposite.redraw();

	}

	/**
	 * Jumps one day backwards.
	 */
	public void prevDay() {
		Calendar mCalendar = _ganttComposite.getRootCalendar();
		mCalendar.add(Calendar.DATE, -1);
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);

		_ganttComposite.moveXBounds(true);
		_ganttComposite.setNoRecalc();
		_ganttComposite.redraw();
	}
}
