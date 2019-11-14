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
 * emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

class HorizontalScrollbarHandler implements Listener {

	private static final int _minScrollRange = 0;
	private static final int _maxScrollRange = 500;
	private static final int _center = (_maxScrollRange / 2) - 20;

	private GanttComposite _gc;
	private ScrollBar _scrollBar;
	private boolean _scrolling;
	private int _lastScrollbarPosition;

	private boolean _infinite;
	private boolean _none;
	private boolean _fixed;

	private int _lastDetailEvent;

	public HorizontalScrollbarHandler(GanttComposite parent, ScrollBar scrollBar, int style) {
		_gc = parent;
		_scrollBar = scrollBar;

		if ((style & GanttFlags.H_SCROLL_FIXED_RANGE) != 0) {
			_fixed = true;
		} else if ((style & GanttFlags.H_SCROLL_INFINITE) != 0) {
			_infinite = true;
		} else if ((style & GanttFlags.H_SCROLL_NONE) != 0) {
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

		if (e.detail == SWT.PAGE_UP || e.detail == SWT.PAGE_DOWN) {
			scrollViewportByPage(scrollDirectionForEventDetail(e.detail), diff);
		} else if (e.detail == SWT.ARROW_UP || e.detail == SWT.ARROW_DOWN) {
			scrollViewportByOffset(scrollDirectionForEventDetail(e.detail), diff);
		} else if (e.detail == SWT.DRAG) {
			scrollViewportTo(left ? SWT.LEFT : SWT.RIGHT, _scrollBar.getSelection(), diff);
		}

		if ((_infinite && e.detail == 0) ) {
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
		} else {
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
			int xStart = _gc.getXForDate(_gc.getRootStartCalendar());
			int xEnd = _gc.getXForDate(_gc.getRootEndCalendar());
			int xWidth = _gc.getVisibleBounds().width; 

			if (_gc.isShowingGanttSections()) {
				xWidth -= _gc.getSettings().getSectionBarWidth();
			}

			xWidth -= _gc.getVerticalBar().getSize().x;
			int dayWidth = _gc.getDayWidth();

			_scrollBar.setVisible(true);
			_scrollBar.setMaximum((xEnd - xStart) / dayWidth);
			_scrollBar.setThumb(xWidth / dayWidth);
			_scrollBar.setSelection(-xStart / dayWidth);

		}

		_lastScrollbarPosition = _scrollBar.getSelection();
	}

}