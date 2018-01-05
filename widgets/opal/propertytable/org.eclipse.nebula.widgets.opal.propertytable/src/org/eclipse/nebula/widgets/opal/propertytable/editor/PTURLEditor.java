/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable.editor;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.dialog.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * This editor is used to edit URL values
 */
public class PTURLEditor extends PTBaseTextEditor {

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTBaseTextEditor#addVerifyListeners()
	 */
	@Override
	public void addVerifyListeners() {
		text.addListener(SWT.FocusOut, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				try {
					new URL(PTURLEditor.this.text.getText());
				} catch (final MalformedURLException e) {
					Dialog.error(ResourceManager.getLabel(ResourceManager.APPLICATION_ERROR),
							ResourceManager.getLabel(ResourceManager.VALID_URL));
					event.doit = false;
					PTURLEditor.this.text.forceFocus();
				}

			}
		});

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTBaseTextEditor#convertValue()
	 */
	@Override
	public Object convertValue() {
		return text.getText();
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTBaseTextEditor#getStyle()
	 */
	@Override
	public int getStyle() {
		return SWT.NONE;
	}

}
