/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation 
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * This editor is used to edit integer values
 */
public class PTIntegerEditor extends PTBaseTextEditor {

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTBaseTextEditor#addVerifyListeners()
	 */
	@Override
	public void addVerifyListeners() {
		this.text.addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				final String string = e.text;
				final char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9') && e.keyCode != SWT.BS && e.keyCode != SWT.DEL) {
						e.doit = false;
						return;
					}
				}
			}
		});
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTBaseTextEditor#convertValue()
	 */
	@Override
	public Object convertValue() {
		int ret = 0;
		try {
			ret = Integer.parseInt(this.text.getText());
		} catch (final NumberFormatException e) {
			ret = 0;
			this.text.setText("0");
		}
		return ret;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTBaseTextEditor#getStyle()
	 */
	@Override
	public int getStyle() {
		return SWT.NONE;
	}

}
