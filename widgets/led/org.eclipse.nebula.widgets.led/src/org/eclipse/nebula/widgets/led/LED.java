/*******************************************************************************
 * Copyright (c) 2019 Akuiteo (http://www.akuiteo.com).
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 * Frank DELPORTE - inspiration for his LED Number Display JavaFX widget
 * (https://webtechie.be/2019/10/02/led-number-display-javafx-library-published-to-maven)
 *******************************************************************************/
package org.eclipse.nebula.widgets.led;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * Instances of this class represent a non-selectable user interface object that
 * displays a character like it was displayed on a LED screen.
 *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.BORDER</dd>
 * <dd>SWT.ICON if the user wants to add a dot
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 */
public class LED extends BaseLED {

	private static final int DEFAULT_WIDTH = 60;

	private LEDLine[] lines;
	private boolean showDot, hasDot;

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
	 * @param parent
	 *            a composite control which will be the parent of the new instance
	 *            (cannot be null)
	 * @param style
	 *            the style of control to construct
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
	public LED(Composite parent, int style) {
		super(parent, checkStyle(style) | SWT.DOUBLE_BUFFERED);
		initLinesConfiguration();
		hasDot = (getStyle() & SWT.ICON) == SWT.ICON;
	}

	private static int checkStyle(final int style) {
		final int mask = SWT.BORDER | SWT.ICON;
		int newStyle = style & mask;
		newStyle |= SWT.DOUBLE_BUFFERED;
		return newStyle;
	}

	private void initLinesConfiguration() {
		lines = new LEDLine[] { new LEDLine(new int[] { 0, 5, 10, 0, 50, 0, 60, 5, 50, 10, 10, 10, 0, 5 }), //
				new LEDLine(new int[] { 60, 7, 60, 48, 50, 43, 50, 12, 60, 7 }), //
				new LEDLine(new int[] { 60, 52, 60, 93, 50, 88, 50, 57, 60, 52 }), //
				new LEDLine(new int[] { 0, 95, 10, 90, 50, 90, 60, 95, 50, 100, 10, 100, 0, 95 }), //
				new LEDLine(new int[] { 0, 52, 10, 57, 10, 88, 0, 93, 0, 52 }), //
				new LEDLine(new int[] { 0, 7, 10, 12, 10, 43, 0, 48, 0, 7 }), //
				new LEDLine(new int[] { 0, 50, 10, 45, 50, 45, 60, 50, 50, 55, 10, 55, 0, 50 }) };
	}

	@Override
	protected void paintInternal() {
		for (int i = 0; i < 7; i++) {
			lines[i].paint(this);
		}
		if (hasDot && showDot) {
			gc.setBackground(selectedColor);
			Rectangle clientArea = getClientArea();
			gc.fillOval((int) (clientArea.width - DOT_DIAMETER * 1.5f) + 1, //
					clientArea.height - DOT_DIAMETER, DOT_DIAMETER, DOT_DIAMETER);
		}
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int x = (wHint == SWT.DEFAULT) ? DEFAULT_WIDTH : wHint;
		if (hasDot) {
			x += (int) (1.5f * DOT_DIAMETER);
		}
		int y = (hHint == SWT.DEFAULT) ? DEFAULT_HEIGHT : hHint;
		return new Point(x, y);
	}

	/**
	 * Returns the flag which indicates if the dot pixel in the right bottom corner
	 * of the widget is switched on
	 *
	 * @return <code>true</code> if the dot is displayed, <code>false</code>
	 *         otherwise
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public boolean isShowDot() {
		checkWidget();
		return showDot;
	}

	/**
	 * Sets the flag allowing the widget to display a dot in the right bottom corner
	 * of the widget
	 *
	 * @param showDot
	 *            the new value
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setShowDot(boolean showDot) {
		checkWidget();
		this.showDot = showDot;
		redraw();
	}

	/**
	 * Sets the character to display
	 *
	 * @param character
	 *            the character to display
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the argument has been
	 *                disposed</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the argument is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setCharacter(LEDCharacter character) {
		checkWidget();
		if (character == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		for (int i = 0; i < 7; i++) {
			lines[i].setSwitechOnFlag(character.isSwitchedOn(i));
		}
		redraw();
	}

}
