/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.commons;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.widgets.Composite;

/**
 * Instances of this class are StyledText that are read-only, that means that we
 * use it only as a renderer
 *
 * @see StyledText
 */
public class ReadOnlyStyledText extends StyledText {

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must
	 * be built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code>
	 * style constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a widget which will be the parent of the new instance
	 *            (cannot be null)
	 * @param style the style of widget to construct
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
	 * @see SWT#FULL_SELECTION
	 * @see SWT#MULTI
	 * @see SWT#SINGLE
	 * @see #getStyle
	 * @see StyledText
	 */
	public ReadOnlyStyledText(final Composite parent, final int style) {
		super(parent, style | SWT.WRAP | SWT.READ_ONLY);
		addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(final MouseEvent e) {
				setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
			}
		});
		setCaret(null);
		addListener(SWT.Selection, e -> {
			setSelection(0, 0);
		});
	}
}
