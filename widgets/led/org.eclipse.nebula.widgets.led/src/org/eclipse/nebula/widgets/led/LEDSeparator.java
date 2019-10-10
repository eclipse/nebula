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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

/**
 * Instances of this class represent a non-selectable user interface object that
 * displays a separator (minus sign) like it was displayed on a LED screen.
 *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.BORDER</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 */
public class LEDSeparator extends Canvas {

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
	public LEDSeparator(Composite parent, int style) {
		super(parent, checkStyle(style));
		setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		addListener(SWT.Paint, e -> onPaint(e));
	}

	private static int checkStyle(final int style) {
		final int mask = SWT.BORDER;
		int newStyle = style & mask;
		newStyle |= SWT.DOUBLE_BUFFERED;
		return newStyle;
	}

	private void onPaint(Event e) {
		GC gc = e.gc;
		gc.setBackground(getBackground());
		gc.fillRectangle(getClientArea());
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		gc.setForeground(getForeground());
		gc.setLineWidth(1);
		gc.drawLine(0, getClientArea().height / 2, getClientArea().width, getClientArea().height / 2);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int, boolean)
	 */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int x = (wHint == SWT.DEFAULT) ? BaseLED.THIN_DEFAULT_WIDTH : wHint;
		int y = (hHint == SWT.DEFAULT) ? BaseLED.DEFAULT_HEIGHT : hHint;
		return new Point(x, y);
	}

}
