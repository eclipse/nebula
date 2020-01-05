/*******************************************************************************
 * Copyright (c) 2019 Akuiteo (http://www.akuiteo.com).
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.stepbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * Instances of this class represent a collection of steps, indicating
 * progress/status in a linear order. Each step has a label. One can select the
 * current step, and possibly set an error state.
 *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.BORDER</dd>
 * <dd>SWT.TOP or SWT.BOTTOM (vertical location of the text)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 */
public class Stepbar extends Canvas {

	private static final int MINIMUM_LINE_WIDTH = 80;
	private static final int CIRCLE_DIAMETER = 24;
	private static final int VERTICAL_SPACE = 14;
	private static final int SPACING = 4;

	private Color unselectedColor;
	private Color textColor;
	private final Color errorColor;
	private List<String> steps = new ArrayList<>();
	private boolean bottom = false;
	private int currentStep = 0;
	private boolean onError = false;

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
	 * @param parent a composite control which will be the parent of the new instance (cannot be null)
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
	 */
	public Stepbar(final Composite parent, final int style) {
		super(parent, checkStyle(style));
		bottom = (getStyle() & SWT.BOTTOM) != 0;
		textColor = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		unselectedColor = SWTGraphicUtil.getColorSafely(217, 217, 217);
		setForeground(SWTGraphicUtil.getColorSafely(0, 147, 249));
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		errorColor = SWTGraphicUtil.getColorSafely(255, 64, 81);

		addListener(SWT.Paint, e -> {
			final GC gc = e.gc;
			gc.setFont(getFont());
			gc.setAdvanced(true);
			gc.setTextAntialias(SWT.ON);
			gc.setAntialias(SWT.ON);

			if (bottom) {
				drawSteps(SPACING, gc);
				drawTexts(SPACING + CIRCLE_DIAMETER + VERTICAL_SPACE, gc);
			} else {
				drawTexts(SPACING, gc);
				drawSteps(SPACING + CIRCLE_DIAMETER / 2 + VERTICAL_SPACE, gc);
			}
		});
	}

	private static int checkStyle(final int style) {
		final int mask = SWT.BORDER | SWT.BOTTOM | SWT.TOP;
		int newStyle = style & mask;
		newStyle |= SWT.DOUBLE_BUFFERED;
		return newStyle;
	}

	private void drawSteps(final int y, final GC gc) {
		final List<Point> textSizes = computeTextSizes();
		final int leftSpacing = Math.max(SPACING, textSizes.get(0).x / 2);
		final int rightSpacing = Math.max(SPACING, textSizes.get(textSizes.size() - 1).x / 2);

		int x = leftSpacing;
		final int count = steps.size();
		final int lineWidth = (getSize().x - x - rightSpacing - count * CIRCLE_DIAMETER) / (count - 1);

		for (int i = 0; i < count; i++) {
			// Draw circle
			gc.setLineWidth(3);
			if (i < currentStep) {
				// Filled circle
				gc.setBackground(getForeground());
				gc.fillOval(x, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
				gc.setForeground(getBackground());
				gc.setBackground(getForeground());
				gc.setLineWidth(2);
				final int centerX = CIRCLE_DIAMETER / 2;
				final int centerY = CIRCLE_DIAMETER / 2;
				gc.drawLine(x + 6, y + centerY, x + 9, y + centerY + 4);
				gc.drawLine(x + 9, y + centerY + 4, x + centerX + 6, y + centerY - 3);
			} else {
				if (i == currentStep && onError) {
					// Error circle
					gc.setBackground(errorColor);
					gc.fillOval(x, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
					gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
					// 14
					final int centerX = x + CIRCLE_DIAMETER / 2;
					gc.fillRectangle(centerX - 2, y + 5, 4, 9);
					gc.fillRectangle(centerX - 2, y + 16, 4, 4);
				} else {
					// Empty circle
					if (i == currentStep) {
						gc.setForeground(getForeground());
					} else {
						gc.setForeground(unselectedColor);
					}
					gc.drawOval(x, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
				}
			}
			gc.setLineWidth(1);
			x += CIRCLE_DIAMETER;

			// Draw rectangle
			if (i != count - 1) {
				final int middleY = CIRCLE_DIAMETER / 2 - 2;
				if (i >= currentStep) {
					gc.setBackground(unselectedColor);
				} else {
					gc.setBackground(getForeground());
				}
				gc.fillRectangle(x + (i >= currentStep && !onError ? 2 : 0), y + middleY, lineWidth, 4);
				x += lineWidth;
			}
		}

	}

	private void drawTexts(final int y, final GC gc) {
		final List<Point> textSizes = computeTextSizes();
		final int leftSpacing = Math.max(SPACING, textSizes.get(0).x / 2);
		final int rightSpacing = Math.max(SPACING, textSizes.get(textSizes.size() - 1).x / 2);

		int x = leftSpacing;
		final int count = steps.size();
		final int lineWidth = (getSize().x - x - rightSpacing - count * CIRCLE_DIAMETER) / (count - 1);

		gc.setForeground(textColor);
		gc.setBackground(getBackground());
		for (int i = 0; i < count; i++) {
			gc.drawText(steps.get(i), x + CIRCLE_DIAMETER / 2 - textSizes.get(i).x / 2, y);
			x += CIRCLE_DIAMETER + lineWidth;
		}
	}

	/**
	 * Append a step to the existing ones
	 *
	 * @param stepText the text associated to the step the user wants to add
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the stepText is null or
	 *                empty</li>
	 *                </ul>
	 */
	public void addStep(final String stepText) {
		checkWidget();
		if (stepText == null || stepText.trim().equals("")) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		steps.add(stepText);
		layout();
		redraw();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		checkWidget();
		final int count = steps.size();
		final int circleSizes = CIRCLE_DIAMETER * count;
		final List<Point> textSizes = computeTextSizes();

		int width = circleSizes + 2 * SPACING;
		int height = CIRCLE_DIAMETER + 2 * SPACING;
		final int maxHeightText = textSizes.size() == 0 ? 0 : //
				textSizes.stream().max(Comparator.comparing(p -> p.x)).get().y;

		// Size for text
		height += maxHeightText + VERTICAL_SPACE;
		width += textSizes.get(textSizes.size() - 1).x / 2;
		width += textSizes.get(0).x / 2;

		// Space for lines
		width += count - 1 * MINIMUM_LINE_WIDTH;

		return new Point(Math.max(width, wHint), Math.max(height, hHint));
	}

	private List<Point> computeTextSizes() {
		final int count = steps.size();
		final List<Point> list = new ArrayList<>(count);
		final GC gc = new GC(this);
		gc.setTextAntialias(SWT.ON);
		gc.setFont(getFont());
		for (int i = 0; i < count; i++) {
			final Point textSize = gc.textExtent(steps.get(i), SWT.DRAW_TRANSPARENT);
			list.add(textSize);
		}

		gc.dispose();
		return list;
	}

	/**
	 * Returns the receiver's current step.
	 *
	 * @return the current step (starting index is 0)
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public int getCurrentStep() {
		checkWidget();
		return currentStep;
	}

	/**
	 * Returns the receiver's list of steps
	 *
	 * @return the color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public List<String> getSteps() {
		checkWidget();
		return steps;
	}

	/**
	 * Returns the receiver's color of the text
	 *
	 * @return the color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getTextColor() {
		checkWidget();
		return textColor;
	}

	/**
	 * Returns the receiver's color used when the step is not reached
	 *
	 * @return the color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public Color getUnselectedColor() {
		checkWidget();
		return unselectedColor;
	}

	/**
	 * Remove a step from the list of the existing ones
	 *
	 * @param stepText
	 *            the text associated to the removed step
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the stepText is null or
	 *                empty</li>
	 *                </ul>
	 */
	public void removeStep(final String stepText) {
		checkWidget();
		if (stepText == null || stepText.trim().equals("")) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		steps.remove(stepText);
		layout();
		redraw();
	}

	/**
	 * Remove a step from the list of the existing ones
	 *
	 * @param stepText the text associated to the removed step
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the stepText is null or
	 *                empty</li>
	 *                </ul>
	 */
	public void setCurrentStep(final int currentStep) {
		checkWidget();
		if (currentStep < 0 || currentStep > steps.size() - 1) {
			SWT.error(SWT.ERROR_INVALID_RANGE);
		}
		this.currentStep = currentStep;
		redraw();
	}

	/**
	 * Set the color used to draw bars and circles for steps that have not been reached
	 *
	 * @param unselectedColor the new color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setUnselectedColor(final Color unselectedColor) {
		checkWidget();
		this.unselectedColor = unselectedColor;
		redraw();
	}

	/**
	 * Set the error state (on/off) of the selected step
	 *
	 * @param errorState the new error state
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setErrorState(final boolean errorState) {
		checkWidget();
		onError = errorState;
		redraw();
	}

	/**
	 * Set the steps
	 *
	 * @param steps an array of steps
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the array of steps is null</li>
	 *                </ul>
	 */
	public void setSteps(final String[] steps) {
		checkWidget();
		if (steps == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.steps = Arrays.asList(steps);
		layout();
		redraw();
	}

	/**
	 * Set the steps
	 *
	 * @param steps a list of steps
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the list of steps is null</li>
	 *                </ul>
	 */
	public void setSteps(final List<String> steps) {
		checkWidget();
		if (steps == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		this.steps = steps;
		layout();
		redraw();
	}

	/**
	 * Set the color of the text
	 *
	 * @param textColor the new color
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setTextColor(final Color textColor) {
		checkWidget();
		this.textColor = textColor;
		redraw();
	}

}
