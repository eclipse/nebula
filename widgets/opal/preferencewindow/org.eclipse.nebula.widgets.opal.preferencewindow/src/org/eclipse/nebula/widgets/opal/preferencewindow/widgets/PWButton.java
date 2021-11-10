/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.preferencewindow.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Instances of this class are buttons
 * 
 */
public class PWButton extends PWWidget {
	private final SelectionListener listener;

	/**
	 * Constructor
	 * 
	 * @param label associated label
	 * @param listener selection listener
	 */
	public PWButton(final String label, final SelectionListener listener) {
		super(label, null, 1, true);
		this.listener = listener;
	}

	@Override
	public Control build(final Composite parent) {
		final Button button = new Button(parent, SWT.PUSH);
		addControl(button);
		if (getLabel() == null) {
			throw new UnsupportedOperationException("You need to set a label for a button");
		} else {
			button.setText(getLabel());
		}
		if (this.listener != null) {
			button.addSelectionListener(this.listener);
		}

		return button;

	}

	@Override
	public void check() {
	}
}
