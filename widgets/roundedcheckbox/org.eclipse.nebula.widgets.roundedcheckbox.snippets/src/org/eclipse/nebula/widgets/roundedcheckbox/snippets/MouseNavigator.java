/*******************************************************************************
 * Copyright (c) 2018 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.roundedcheckbox.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * This class add the following behaviour to <code>StyledText</code> widgets.<br/>
 * When the user clicks on the wheel, a circle with arrows appears. When the user mooves the mouse,
 * the StyledText content is scrolled (on the right or on the left for horizontal movements, up or down for vertical movements).<br/>
 * 
 */
class MouseNavigator {
	private final StyledText parent;
	private boolean buttonPressed = false;
	private GC gc;
	private static final int CIRCLE_RADIUS = 15;
	private static final int CENTRAL_POINT_RADIUS = 2;
	private Point originalMouseLocation;
	private final Listener mouseDownListener, mouseUpListener, paintListener, mouseMoveListener;

	MouseNavigator(final StyledText styledText) {
		if (styledText == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (styledText.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		parent = styledText;

		mouseDownListener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				onMouseDown(e);
			}
		};
		parent.addListener(SWT.MouseDown, mouseDownListener);

		mouseUpListener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				onMouseUp(e);
			}
		};
		parent.addListener(SWT.MouseUp, mouseUpListener);

		paintListener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				onPaint(e);
			}
		};
		parent.addListener(SWT.Paint, paintListener);

		mouseMoveListener = new Listener() {
			@Override
			public void handleEvent(Event e) {
				onMouseMove(e);
			}
		};
		parent.addListener(SWT.MouseMove, mouseMoveListener);

	}

	private void onMouseDown(Event e) {
		if (e.button != 2) {
			return;
		}

		if (!parent.isVisible() || !parent.getEnabled()) {
			return;
		}

		// Widget has no bar or bars are not enabled
		if (!hasHorizontalBar() && !hasVerticalBar()) {
			return;
		}

		buttonPressed = true;
		parent.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
		originalMouseLocation = getMouseLocation();
		parent.redraw();
	}

	private boolean hasHorizontalBar() {
		final ScrollBar horizontalBar = parent.getHorizontalBar();
		final boolean hasHorizontalBar = horizontalBar != null && horizontalBar.isVisible();
		final boolean exceedHorizontalSpace = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT).x > parent.getSize().x;
		return hasHorizontalBar && exceedHorizontalSpace;
	}

	private boolean hasVerticalBar() {
		final ScrollBar verticalBar = parent.getVerticalBar();
		final boolean hasVerticalBar = verticalBar != null && verticalBar.isEnabled();
		final boolean exceedVerticalSpace = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT).y > parent.getSize().y;
		return hasVerticalBar && exceedVerticalSpace;
	}

	private void onMouseUp(Event e) {
		buttonPressed = false;
		originalMouseLocation = null;
		parent.redraw();
	}

	private void onMouseMove(Event e) {
		if (!buttonPressed) {
			return;
		}

		final Point mouseLocation = getMouseLocation();
		final int deltaX = originalMouseLocation.x - mouseLocation.x;
		final int deltaY = originalMouseLocation.y - mouseLocation.y;
		final int dist = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		if (dist < CIRCLE_RADIUS) {
			return;
		}

		parent.setRedraw(false);
		if (hasHorizontalBar()) {
			final ScrollBar bar = parent.getHorizontalBar();
			bar.setSelection((int) (bar.getSelection() - deltaX * .1));
			fireSelectionEvent(e, bar);
		}

		if (hasVerticalBar()) {
			final ScrollBar bar = parent.getVerticalBar();
			bar.setSelection((int) (bar.getSelection() - deltaY * .1));
			fireSelectionEvent(e, bar);
		}
		parent.setRedraw(true);
		parent.redraw();
	}

	private void fireSelectionEvent(final Event e, final ScrollBar bar) {
		final Event event = new Event();
		event.widget = bar;
		event.display = parent.getDisplay();
		event.type = SWT.Selection;
		event.time = e.time;

		for (final Listener selectionListener : bar.getListeners(SWT.Selection)) {
			selectionListener.handleEvent(event);
		}
	}

	private Point getMouseLocation() {
		final Point cursorLocation = Display.getCurrent().getCursorLocation();
		final Point relativeCursorLocation = parent.toControl(cursorLocation);
		return relativeCursorLocation;
	}

	private void onPaint(final Event e) {
		if (!buttonPressed) {
			return;
		}

		final Rectangle rect = parent.getClientArea();
		if (rect.width == 0 || rect.height == 0) {
			return;
		}
		gc = e.gc;
		gc.setAntialias(SWT.ON);
		gc.setAdvanced(true);

		final Color oldForegroundColor = gc.getForeground();
		final Color oldBackgroundColor = gc.getForeground();
		gc.setBackground(parent.getForeground());
		drawCircle();
		drawCentralPoint();

		drawArrows();

		gc.setForeground(oldForegroundColor);
		gc.setBackground(oldBackgroundColor);
	}

	private void drawCircle() {
		gc.setBackground(parent.getBackground());
		gc.setForeground(parent.getForeground());
		gc.setAlpha(200);
		gc.fillOval(originalMouseLocation.x - CIRCLE_RADIUS, originalMouseLocation.y - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
		gc.setBackground(parent.getForeground());
		gc.setAlpha(255);
		gc.drawOval(originalMouseLocation.x - CIRCLE_RADIUS, originalMouseLocation.y - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
	}

	private void drawCentralPoint() {
		gc.fillOval(originalMouseLocation.x - CENTRAL_POINT_RADIUS, originalMouseLocation.y - CENTRAL_POINT_RADIUS, CENTRAL_POINT_RADIUS * 2, CENTRAL_POINT_RADIUS * 2);
	}

	private void drawArrows() {
		gc.setLineWidth(2);
		if (hasHorizontalBar()) {
			drawHorizontalArrows();
		}
		if (hasVerticalBar()) {
			drawVerticalArrows();
		}
	}

	private void drawHorizontalArrows() {
		final int[] points = new int[6];
		// Left
		points[0] = originalMouseLocation.x - 6;
		points[1] = originalMouseLocation.y + 3;
		points[2] = originalMouseLocation.x - 9;
		points[3] = originalMouseLocation.y;
		points[4] = originalMouseLocation.x - 6;
		points[5] = originalMouseLocation.y - 3;
		gc.drawPolyline(points);

		// Right
		points[0] = originalMouseLocation.x + 7;
		points[1] = originalMouseLocation.y + 3;
		points[2] = originalMouseLocation.x + 10;
		points[3] = originalMouseLocation.y;
		points[4] = originalMouseLocation.x + 7;
		points[5] = originalMouseLocation.y - 3;
		gc.drawPolyline(points);
	}

	private void drawVerticalArrows() {
		final int[] points = new int[6];
		// Upper
		points[0] = originalMouseLocation.x - 3;
		points[1] = originalMouseLocation.y - 6;
		points[2] = originalMouseLocation.x;
		points[3] = originalMouseLocation.y - 10;
		points[4] = originalMouseLocation.x + 3;
		points[5] = originalMouseLocation.y - 6;
		gc.drawPolyline(points);

		// Lower
		points[0] = originalMouseLocation.x - 3;
		points[1] = originalMouseLocation.y + 7;
		points[2] = originalMouseLocation.x;
		points[3] = originalMouseLocation.y + 11;
		points[4] = originalMouseLocation.x + 3;
		points[5] = originalMouseLocation.y + 7;
		gc.drawPolyline(points);

	}

	void dispose() {
		if (parent.isDisposed()) {
			return;
		}
		parent.removeListener(SWT.MouseDown, mouseDownListener);
		parent.removeListener(SWT.MouseUp, mouseUpListener);
		parent.removeListener(SWT.Paint, paintListener);
		parent.removeListener(SWT.MouseMove, mouseMoveListener);
	}
}
