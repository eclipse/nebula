/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
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
package org.eclipse.nebula.widgets.opal.preferencewindow.widgets;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

/**
 * Instances of this class are used to select a directory
 */
public class PWDirectoryChooser extends PWChooser {

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWDirectoryChooser(final String label, final String propertyKey) {
		super(label, propertyKey);
	}

	@Override
	protected void setButtonAction(final Text text, final Button button) {

		final String originalDirectory = (String) PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		text.setText(originalDirectory);

		button.addListener(SWT.Selection, event -> {
			final DirectoryDialog dialog = new DirectoryDialog(text.getShell());
			dialog.setMessage(ResourceManager.getLabel(ResourceManager.CHOOSE_DIRECTORY));
			final String result = dialog.open();
			if (result != null) {
				text.setText(result);
				PreferenceWindow.getInstance().setValue(getPropertyKey(), result);
			}
		});
	}

	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), "");
		} else {
			if (!(value instanceof String)) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has to be a String because it is associated to a directory chooser");
			}
		}
	}

}
