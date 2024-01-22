/*******************************************************************************
 * Copyright (c) 2018-2024 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.nebulaslider;

import org.eclipse.nebula.widgets.opal.commons.SelectionListenerUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class are selectable user interface
 * objects that represent a range of positive, numeric values.
 * It is like an horizontal slider
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(None)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 */
public class NebulaSlider extends Canvas {

	private NebulaSliderGraphicConfiguration renderer;

	private int minimum;
	private int maximum;
	private int value;
	private int xPosition;
	private int mouseDeltaX;

	private boolean moving = false;

	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style the style of control to construct
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the parent</li>
	 *                <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed
	 *                subclass</li>
	 *                </ul>
	 *
	 * @see Widget#getStyle()
	 */
	public NebulaSlider(final Composite parent, final int style) {
		super(parent, checkStyle(style) | SWT.DOUBLE_BUFFERED);

		renderer = new NebularSliderDefaultConfiguration(this);

		minimum = Integer.MIN_VALUE;
		maximum = Integer.MAX_VALUE;
		value = 0;
		xPosition = -1;

		addPaintListener(e -> {
			paintControl(e.gc);
		});
		addMouseListeners();
	}

	private static int checkStyle(final int style) {
		if ((style & SWT.BORDER) != 0) {
			return style & ~SWT.BORDER;
		}
		return 0;
	}

	private void paintControl(final GC gc) {
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);

		if (xPosition < 0) {
			// Compute xPosition
			xPosition = computeXPosition();
		}

		drawBar(gc);
		drawSelectionPart(gc);
		drawSelector(gc);
	}

	private void drawBar(final GC gc) {
		final Rectangle rect = getClientArea();
		gc.setForeground(renderer.getBarBorderColor());
		gc.setBackground(renderer.getBarInsideColor());

		final int hMargin = renderer.getHorizontalMargin();
		final int selectorWidth = renderer.getSelectorWidth();
		final int barHeight = renderer.getBarHeight();

		final int x = hMargin + selectorWidth / 2;
		final int y = (rect.height - barHeight) / 2;
		final int width = rect.width - hMargin * 2 - selectorWidth;

		gc.fillRoundRectangle(x, y, width, barHeight, barHeight, barHeight);
		gc.drawRoundRectangle(x, y, width, barHeight, barHeight, barHeight);
	}

	private void drawSelectionPart(final GC gc) {
		final Rectangle rect = getClientArea();
		gc.setForeground(renderer.getBarBorderColor());
		gc.setBackground(renderer.getBarSelectionColor());

		final int barHeight = renderer.getBarHeight();

		final int x = renderer.getHorizontalMargin() + renderer.getSelectorWidth() / 2;
		final int y = (rect.height - barHeight) / 2;

		gc.fillRoundRectangle(x, y, xPosition, barHeight, barHeight, barHeight);
		gc.drawRoundRectangle(x, y, xPosition, barHeight, barHeight, barHeight);
	}

	private int computeXPosition() {
		final int originalWidth = getClientArea().width - renderer.getHorizontalMargin() * 2 - renderer.getSelectorWidth();
		final float coeff = value * 1f / (maximum - minimum);
		final int position = (int) (coeff * originalWidth);
		return position;
	}

	private void drawSelector(final GC gc) {
		final Rectangle rect = getClientArea();
		gc.setForeground(renderer.getSelectorColorBorder());
		gc.setBackground(renderer.getSelectorColor());

		final int hMargin = renderer.getHorizontalMargin();

		final int selectorWidth = renderer.getSelectorWidth();
		final int selectorHeight = renderer.getSelectorHeight();

		final int y = (rect.height - selectorHeight) / 2;

		// Draw the body
		gc.fillRoundRectangle(hMargin + xPosition, y, selectorWidth, selectorHeight, selectorHeight, selectorHeight);
		gc.drawRoundRectangle(hMargin + xPosition, y, selectorWidth, selectorHeight, selectorHeight, selectorHeight);

		// Draw the arrows
		gc.setForeground(renderer.getArrowColor());
		gc.setLineWidth(renderer.getArrowLineWidth());
		final int baseY = y + selectorHeight / 2;
		gc.drawLine(hMargin + xPosition + 10, baseY, hMargin + xPosition + 17, baseY - 7);
		gc.drawLine(hMargin + xPosition + 10, baseY, hMargin + xPosition + 17, baseY + 7);

		gc.drawLine(hMargin + xPosition + selectorWidth - 10, baseY, hMargin + xPosition + selectorWidth - 17, baseY - 7);
		gc.drawLine(hMargin + xPosition + selectorWidth - 10, baseY, hMargin + xPosition + selectorWidth - 17, baseY + 7);

		// And the value
		gc.setForeground(renderer.getSelectorTextColor());
		gc.setFont(renderer.getTextFont());
		final String valueAsString = String.valueOf(value);
		final Point textSize = gc.textExtent(valueAsString);

		final int xText = hMargin + xPosition + selectorWidth / 2;
		final int yText = y + selectorHeight / 2;

		gc.drawText(valueAsString, xText - textSize.x / 2, yText - textSize.y / 2, true);
	}

	private void addMouseListeners() {

		addListener(SWT.MouseDown, e -> {
			final int selectorWidth = renderer.getSelectorWidth();
			final int selectorHeight = renderer.getSelectorHeight();

			final int y = (getClientArea().height - selectorHeight) / 2;
			final Rectangle rect = new Rectangle(xPosition + renderer.getHorizontalMargin(), y, selectorWidth, selectorHeight);
			if (!rect.contains(e.x, e.y)) {
				return;
			}
			moving = true;
			mouseDeltaX = xPosition - e.x;
		});

		addListener(SWT.MouseUp, e -> {
			moving = false;
			mouseDeltaX = 0;
			redraw();
		});

		addListener(SWT.MouseMove, e -> {
			if (!moving) {
				return;
			}

			xPosition = e.x + mouseDeltaX;
			if (xPosition < 0) {
				xPosition = 0;
			}
			final int originalWidth = getClientArea().width - renderer.getHorizontalMargin() * 2 - renderer.getSelectorWidth();

			if (xPosition > originalWidth) {
				xPosition = originalWidth;
			}

			// Update value
			final float ratio = (float) xPosition / originalWidth;
			value = (int) Math.floor(ratio * (maximum - minimum));

			SelectionListenerUtil.fireSelectionListeners(this,e);
			redraw();
		});
	}



	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the control is selected by the user, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the control is
	 *            selected by the user,
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(final SelectionListener listener) {
		checkWidget();
		SelectionListenerUtil.addSelectionListener(this, listener);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		return new Point(Math.max(300, wHint), Math.max(40, hHint));
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the control is selected by the user.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 *
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(final SelectionListener listener) {
		checkWidget();
		SelectionListenerUtil.removeSelectionListener(this, listener);
	}

	// ----------------------- Getters & Setters

	/**
	 * Returns the minimum value which the receiver will allow.
	 *
	 * @return the minimum
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getMinimum() {
		checkWidget();
		return minimum;
	}

	/**
	 * Sets the minimum value. If this value is greater than the maximum, an exception is thrown
	 *
	 * @param value the new minimum
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setMinimum(final int minimum) {
		checkWidget();
		if (minimum > maximum) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT, new IllegalArgumentException(String.format("Value %d is greater than the maximum value (%d)", minimum, maximum)));
		}
		this.minimum = minimum;
		redraw();
		update();
	}

	/**
	 * Returns the maximum value which the receiver will allow.
	 *
	 * @return the maximum
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getMaximum() {
		checkWidget();
		return maximum;
	}

	/**
	 * Sets the maximum value. If this value is lower than the minimum, an exception is thrown
	 *
	 * @param value the new minimum
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setMaximum(final int maximum) {
		checkWidget();
		if (maximum < minimum) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT, new IllegalArgumentException(String.format("Value %d is lower than the minimum value (%d)", maximum, minimum)));
		}
		this.maximum = maximum;
		redraw();
		update();
	}

	/**
	 * Returns the receiver's value.
	 *
	 * @return the selection
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public int getValue() {
		checkWidget();
		return value;
	}

	/**
	 * Sets the receiver's value. If the value is lower to minimum or greater than the maximum, an exception is thrown
	 *
	 * @param value the new selection (must be zero or greater)
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setValue(final int value) {
		checkWidget();
		if (value < minimum || value > maximum) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT, new IllegalArgumentException(String.format("Value %d is not int the range [%d - %d]", value, minimum, maximum)));
		}
		this.value = value;
		xPosition = -1;
		redraw();
		update();
	}

	/**
	 * Return the current renderer for this widget
	 *
	 * @return the renderer
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public NebulaSliderGraphicConfiguration getRenderer() {
		checkWidget();
		return renderer;
	}

	/**
	 * Sets the renderer for this widget
	 *
	 * @param renderer the new renderer
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 */
	public void setRenderer(final NebulaSliderGraphicConfiguration renderer) {
		checkWidget();
		this.renderer = renderer;
		redraw();
	}

}
