/*******************************************************************************
 * Copyright (c) 2012-2017 Laurent CARON
 * All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.calculator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Instances of this class are calculator.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * This component is inspired by Hermant
 * (http://www.javabeginner.com/java-swing/java-swing-calculator)
 */
public class Calculator extends Composite {

	private final Label displayArea;
	private final CalculatorButtonsComposite panel;

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
	 *                </ul>
	 *
	 */
	public Calculator(final Composite parent, final int style) {
		super(parent, style);
		setLayout(new GridLayout());
		displayArea = createTextArea();
		panel = new CalculatorButtonsComposite(this, SWT.NONE);
		panel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		panel.setDisplayArea(displayArea);
		displayArea.addListener(SWT.KeyDown,panel.getKeyListener());
	}

	/**
	 * Create the text area
	 */
	private Label createTextArea() {
		final Label text = new Label(this, SWT.BORDER | SWT.RIGHT);
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false);
		gd.widthHint = 150;
		text.setLayoutData(gd);
		text.setText("0");
		text.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		return text;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the receiver's text is modified, by sending it one of the messages defined in
	 * the <code>ModifyListener</code> interface.
	 *
	 * @param listener the listener which should be notified
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
	 * @see ModifyListener
	 * @see #removeModifyListener
	 */
	public void addModifyListener(final ModifyListener listener) {
		checkWidget();
		panel.addModifyListener(listener);
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		checkWidget();
		return displayArea.getText();
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the receiver's text is modified.
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
	 * @see ModifyListener
	 * @see #addModifyListener
	 */
	public void removeModifyListener(final ModifyListener listener) {
		checkWidget();
		panel.removeModifyListener(listener);
	}

	/**
	 * @param value new value
	 * @throws NumberFormatException if <code>value</code> is not a valid double
	 *             value
	 */
	public void setValue(final String value) {
		checkWidget();
		new Double(value);
		displayArea.setText(value);
	}

}
