/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.preferencewindow.widgets;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Instances of this class are used to select a file
 */
public class PWFileChooser extends PWChooser {

	/**
	 * Constructor
	 * 
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWFileChooser(final String label, final String propertyKey) {
		super(label, propertyKey);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWChooser#setButtonAction(org.eclipse.swt.widgets.Text,
	 *      org.eclipse.swt.widgets.Button)
	 */
	@Override
	protected void setButtonAction(final Text text, final Button button) {
		final String originalFile = (String) PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		text.setText(originalFile);

		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				final FileDialog dialog = new FileDialog(text.getShell(), SWT.OPEN);
				final String result = dialog.open();
				if (result != null) {
					text.setText(result);
					PreferenceWindow.getInstance().setValue(getPropertyKey(), result);
				}

			}
		});

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), "");
		} else {
			if (!(value instanceof String)) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has to be a String because it is associated to a file chooser");
			}
		}
	}

}
