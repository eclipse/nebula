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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

class HorizontalScrollbarHandler implements Listener {

	private static final int	_minScrollRange	= 0;
	private static final int	_maxScrollRange	= 500;
	private static final int	_center			= (_maxScrollRange / 2) - 20;

	private GanttComposite		_gc;
	private ScrollBar			_scrollBar;
	private boolean				_scrolling;
	private int					_lastScrollbarPosition;

	private boolean				_infinite;
	private boolean				_none;
	private boolean				_fixed;

	private int					_lastDetailEvent;

	public HorizontalScrollbarHandler(GanttComposite parent, ScrollBar scrollBar, int style) {
		_gc = parent;
		_scrollBar = scrollBar;

		if ((style & GanttFlags.H_SCROLL_FIXED_RANGE) != 0) {
			_fixed = true;
		}
		else if ((style & GanttFlags.H_SCROLL_INFINITE) != 0) {
			_infinite = true;
		}
		else if ((style & GanttFlags.H_SCROLL_NONE) != 0) {
			_none = true;
		}

		_scrollBar.addListener(SWT.Selection, this);

		if (_infinite) {
			_scrollBar.setVisible(true);
			_scrollBar.setMinimum(_minScrollRange);
			_scrollBar.setMaximum(_maxScrollRange);
			_scrollBar.setSelection(_center);
			_scrollBar.setThumb(20);
			return;
		}
		
		if (_fixed) {
			_scrollBar.setPageIncrement(7);
		}
		
		_scrollBar.setMinimum(0);
		_scrollBar.setIncrement(1);

		if (_none || _fixed) {
			_scrollBar.setVisible(false);
		}
	}

	public void resetScrollPosition() {
		_lastScrollbarPosition = getScrollBarPosition();
	}

	public void handleEvent(Event e) {
		_scrolling = true;

		int cur = getScrollBarPosition();

		// same as last (happens when user clicks the thumb without dragging)
		if (_lastScrollbarPosition == cur) {
			// if the mouse lets go of a scrollbar drag, update the scrollbars again
			if (e.detail == 0 && _lastDetailEvent != 0 && _fixed) {
				recalculate();
			}

			if (_infinite && e.detail == 0) {
				_scrollBar.setSelection(_center);
				_lastScrollbarPosition = _center;
			}

			_gc.killDialogs();

			return;
		}

		_lastDetailEvent = e.detail;

		boolean left = false;
		int diff = cur - _lastScrollbarPosition;
		if (diff < 0) {
			left = true;
			diff = _lastScrollbarPosition - cur;
		}

		boolean moveToCenter = false;
		
		if (e.detail == SWT.PAGE_UP || e.detail == SWT.PAGE_DOWN) {
			scrollViewportByPage(scrollDirectionForEventDetail(e.detail), diff);
			moveToCenter = true;
		}
		else if (e.detail == SWT.ARROW_UP || e.detail == SWT.ARROW_DOWN) {
			scrollViewportByOffset(scrollDirectionForEventDetail(e.detail), diff);
		}
		else if (e.detail == SWT.DRAG) {
			scrollViewportTo(left ? SWT.LEFT : SWT.RIGHT, _scrollBar.getSelection(), diff);
		}

		if ((_infinite && e.detail == 0) || moveToCenter) {
			_scrollBar.setSelection(_center);
			_lastScrollbarPosition = _center;
			_gc.killDialogs();
			return;
		}

		_lastScrollbarPosition = getScrollBarPosition();

		_scrolling = false;

	}

	public int getScrollBarPosition() {
		return _scrollBar.getSelection();
	}

	private void scrollViewportByPage(int direction, int diff) {
		scrollViewportByOffset(direction, diff);
	}

	private void scrollViewportByOffset(int direction, int diff) {
		if (direction == SWT.LEFT) {
			_gc.getViewPortHandler().scrollingLeft(diff);
		}
		else {
			_gc.getViewPortHandler().scrollingRight(diff);
		}
	}

	private void scrollViewportTo(int direction, int position, int diff) {
		scrollViewportByOffset(direction, diff);
	}

	private int scrollDirectionForEventDetail(int eventDetail) {
		return (eventDetail == SWT.PAGE_UP || eventDetail == SWT.ARROW_UP) ? SWT.LEFT : SWT.RIGHT;
	}

	public boolean isScrolling() {
		return _scrolling;
	}

	public void recalculate() {
		if (_gc.isDisposed() || !_gc.isChartReady()) {
			return;
		}

		// no scrollbar? nothing to recalculate
		if (_none) {
			return;
		}

		// infinite scrollbar is reset to center
		if (_infinite) {
			_scrollBar.setSelection(_center);
			_lastScrollbarPosition = _center;
			return;
		}

		// deal with fixed horizontal scrollbar
		if (_fixed) {
			float dayWidth = (float) _gc.getDayWidth();

			int xLeftMostPixel = _gc.getLeftMostPixel();
			int xRightMostPixel = _gc.getRightMostPixel();

			//System.err.println(xLeftMostPixel + " " + xRightMostPixel);
			
			int xStart = 0; //_gc.getXForDate(_gc.getRootCalendar());			
			int xEnd = _gc.getVisibleBounds().width; //_gc.getXForDate(_gc.getRootEndCalendar());
			int rangeBonus = 0;

			// take sections into account
			int gSectionWidth = 0;
			if (_gc.getSettings().drawSectionBar()) {
				gSectionWidth += _gc.getSettings().getSectionBarWidth();
			}
			if (_gc.getSettings().drawSectionDetails()) {
				gSectionWidth += _gc.getSettings().getSectionDetailWidth();
			}
			if (_gc.isShowingGanttSections()) {			
				if (_gc.getSettings().getSectionSide() == SWT.LEFT) {
					xStart = gSectionWidth;
				}
				else {
					xEnd -= gSectionWidth;
				}
				
				// add it to the range too
				rangeBonus += gSectionWidth;
			}
			
			//System.err.println(xStart + " : " + xEnd + " " + lastEvent.getActualBounds() + " " + _gc.getVisibleBounds().width + " " + xRightMostPixel);

			int vScrollSize = 0;
			if (_gc.getVerticalBar().isVisible()) {
				vScrollSize = _gc.getVerticalBar().getSize().x;
				xEnd -= vScrollSize;
			}

			int extraLeft = xLeftMostPixel - xStart;
			int extraRight = xEnd - xRightMostPixel;
			
			if (extraLeft > 0) {
				rangeBonus += extraLeft;
			}
			if (extraRight > 0) {
				rangeBonus += extraRight;
			}
						
			// positive extraLeft means we're manually further to the left of the start event
			// negative extraLeft means we're to the right of it 
			// positive extraRight means we're to the right of the last event
			// negative extraRight means we're to the left of it
			// thus, the "range" to display is the diff of the two divided by the day width

			// we don't need a scrollbar if all events are in the visible area
			if (extraLeft >= 0 && extraRight >= 0 || extraLeft == Integer.MAX_VALUE || extraRight == Integer.MIN_VALUE) {
				_scrollBar.setMaximum(0);
				_scrollBar.setSelection(0);
				_scrollBar.setThumb(1000000);
				if (_scrollBar.isVisible()) {
					// redraw chart as there's now a new area that is not drawn (where the scrollbar was before)
					_gc.redraw();
				}
				_scrollBar.setVisible(false);
				return;
			}

			// always add on a day as text might be cut on half a day
			rangeBonus += dayWidth;

			float pixelRange = (float) (xRightMostPixel - xLeftMostPixel + rangeBonus);
			pixelRange -= _gc.getVisibleBounds().width;
			
			if (_gc.getCurrentView() != ISettings.VIEW_YEAR) {
				pixelRange /= dayWidth;
			}			
			else {
				pixelRange /= dayWidth;
				// avg month width is ~30 days, over time it's an ok number, year is rather zoomed out anyway
				pixelRange /= 30;
				pixelRange += 1;
			}

			_scrollBar.setVisible(true);

			//System.err.println("RANGE: " + Math.ceil(pixelRange));// + " (debug1: " + debug1 + ", debug2: " + debug2 + "). Bonus: " + rangeBonus);

			_scrollBar.setMaximum((int) Math.ceil(pixelRange));

//			System.err.println("extra left: " + extraLeft + " extra right: " + extraRight + " range: " + pixelRange + " " + dayWidth);

			_scrollBar.setThumb(1);

			boolean setMax = false;
			boolean setMin = false;

			int scrollLocation = 0;
			// all the way to the right
			if (extraRight > 0 && extraLeft < 0) {
				setMax = true;
			}
			// all the way left
			if (extraLeft > 0 && extraRight < 0) {
				setMin = true;
			}

			// middle somewhere
			if (!setMax && !setMin) {
				if (extraLeft > 0) {
					scrollLocation -= extraLeft;
				}
				else {
					scrollLocation += Math.abs(extraLeft);
				}

				if (extraRight > 0) {
					scrollLocation -= extraRight;
				}
			}

			if (setMax) {
				_scrollBar.setSelection(_scrollBar.getMaximum());
			}
			else if (setMin) {
				_scrollBar.setSelection(0);
			}
			else {
				_scrollBar.setSelection(scrollLocation / (int)dayWidth);
			}

			_lastScrollbarPosition = _scrollBar.getSelection();
		}
	}

}