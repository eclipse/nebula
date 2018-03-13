/*******************************************************************************
 * Copyright (c) 2018 Laurent CARON. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.progresscircle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class represents a progress circle, which displays a percentage in a "donut".
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BORDER</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 *
 */
public class ProgressCircle extends Canvas {
	private int percentage;
	private int circleSize;
	private int thickness;
	private boolean showPercentage;
	private Color highlightColor;
	private static int MARGIN = 2;
	private boolean firstDisplay = true;

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
	public ProgressCircle(Composite parent, int style) {
		super(parent, checkStyle(style) | SWT.DOUBLE_BUFFERED);
		percentage = 0;
		circleSize = 100;
		thickness = 10;
		highlightColor = getAndDisposeColor(119, 167, 251);
		setForeground(getAndDisposeColor(232, 232, 232));
		setFont(createDefaultFont());
		addPaintListener(e -> {
			paintControl(e);
		});
	}

	private static int checkStyle(final int style) {
		if ((style & SWT.BORDER) != 0) {
			return SWT.BORDER;
		}
		return 0;
	}

	private Color getAndDisposeColor(int r, int g, int b) {
		final Color color = new Color(getDisplay(), r, g, b);
		addDisposeListener(e -> {
			if (!color.isDisposed()) {
				color.dispose();
			}
		});
		return color;
	}

	private Font createDefaultFont() {
		final FontData fontData = getFont().getFontData()[0];
		final Font newFont = new Font(getDisplay(), fontData.getName(), Math.max(fontData.getHeight(), 20), fontData.getStyle());
		addDisposeListener(e -> {
			if (!newFont.isDisposed()) {
				newFont.dispose();
			}
		});
		return newFont;
	}

	private void paintControl(final PaintEvent e) {
		firstDisplay = false;
		final GC gc = e.gc;
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);

		// Draw the selected part
		final Path pathHighlight = new Path(getDisplay());
		final float angle = percentage * 3.6f;

		pathHighlight.addArc(MARGIN, MARGIN, circleSize, circleSize, 90, -angle);
		pathHighlight.lineTo((MARGIN + circleSize) / 2, (MARGIN + circleSize) / 2);
		pathHighlight.close();

		gc.setBackground(getHighlightColor());
		gc.fillPath(pathHighlight);
		pathHighlight.dispose();

		// Draw the unselected part
		final Path path = new Path(getDisplay());
		final float unselectedAngle = (100 - percentage) * 3.6f;

		path.addArc(MARGIN, MARGIN, circleSize, circleSize, 90 - angle, -unselectedAngle);
		path.lineTo((MARGIN + circleSize) / 2, (MARGIN + circleSize) / 2);
		path.close();

		gc.setBackground(getForeground());
		gc.fillPath(path);
		pathHighlight.dispose();

		// Draw the hole
		gc.setBackground(getBackground());
		gc.fillOval(MARGIN + thickness, MARGIN + thickness, circleSize - thickness * 2, circleSize - thickness * 2);

		if (showPercentage) {
			gc.setForeground(getHighlightColor());
			final String text = percentage + "%";
			final Point textSize = gc.stringExtent(text);
			final int x = MARGIN + (circleSize - textSize.x) / 2;
			final int y = (circleSize - textSize.y) / 2;
			gc.drawText(text, x, y, true);
		}

	}

	// ----------------------------- Getters and Setters

	/**
	 * @return the percentage value
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getPercentage() {
		checkWidget();
		return percentage;
	}

	/**
	 * Set the percentage value of the circle
	 *
	 * @param percentage the new percentage value
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setPercentage(int percentage) {
		checkWidget();
		if (percentage < 0 || percentage > 100) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT, null, String.format("Value %d is out of range [0-100]", percentage));
		}
		final int previousValue = this.percentage;
		this.percentage = percentage;
		if (firstDisplay || previousValue == percentage) {
			return;
		}
		startAnimation(previousValue);
	}

	private void startAnimation(int startValue) {
		final int endValue = percentage;
		final float delta = (endValue - startValue) / 10f;
		percentage = startValue;
		redraw();
		update();
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				percentage = (int) (percentage + delta);
				if (isDisposed()) {
					return;
				}
				redraw();
				update();

				if (delta > 0 && percentage >= endValue || delta < 0 && percentage <= endValue) {
					percentage = endValue;
					return;
				}
				getDisplay().timerExec(50, this);
			}
		});

	}

	/**
	 * @return the size of the circle
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getCircleSize() {
		checkWidget();
		return circleSize;
	}

	/**
	 * Set the size of the circle
	 *
	 * @param circleSize the new size of the circle
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setCircleSize(int circleSize) {
		checkWidget();
		if (this.circleSize == circleSize) {
			return;
		}
		this.circleSize = circleSize;
		getParent().layout(new Control[] { this });
	}

	/**
	 * @return the thickness of the circle
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getThickness() {
		checkWidget();
		return thickness;
	}

	/**
	 * Set the thickness value
	 *
	 * @param thickness the new value
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setThickness(int thickness) {
		checkWidget();
		this.thickness = thickness;
		redraw();
		update();
	}

	/**
	 * @return <code>true</code> if the percentage text is displayed, false otherwise
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean isShowPercentage() {
		checkWidget();
		return showPercentage;
	}

	/**
	 * Displays or not the percentage label
	 *
	 * @param showPercentage if <code>true</code>, displays the percentage label
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setShowPercentage(boolean showPercentage) {
		checkWidget();
		this.showPercentage = showPercentage;
		redraw();
		update();
	}

	/**
	 * @return the highlight color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getHighlightColor() {
		checkWidget();
		return highlightColor;
	}

	/**
	 * Set the highlight color of the circle
	 *
	 * @param highlightColor the new highlight color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setHighlightColor(Color highlightColor) {
		checkWidget();
		this.highlightColor = highlightColor;
		redraw();
		update();
	}

	// ----------- Overriden methods

	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(Math.max(circleSize + 2 * MARGIN, wHint), Math.max(circleSize + 2 * MARGIN, hHint));
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setForeground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		redraw();
		update();
	}

	/**
	 * @see org.eclipse.swt.widgets.Canvas#setFont(org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		redraw();
		update();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		redraw();
		update();
	}

}
