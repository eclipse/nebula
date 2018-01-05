/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.preferencewindow.widgets;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Instances of this class are labels, that could contain some HTML tags (B,I,U)
 */
public class PWLabel extends PWWidget {

	private StyledText labelWidget;

	/**
	 * Constructor
	 *
	 * @param label associated label
	 */
	public PWLabel(final String label) {
		super(label, null, 1, true);
		setAlignment(GridData.FILL);
		setGrabExcessSpace(true);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		if (getLabel() == null) {
			throw new UnsupportedOperationException("You need to set a description for a PWLabel object");
		}
		labelWidget = new StyledText(parent, SWT.WRAP | SWT.READ_ONLY);
		labelWidget.setEnabled(false);
		labelWidget.setBackground(parent.getBackground());
		labelWidget.setText(getLabel());
		SWTGraphicUtil.applyHTMLFormating(labelWidget);
		return labelWidget;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#enableOrDisable()
	 */
	@Override
	public boolean enableOrDisable() {
		if (enabler == null) {
			return true;
		}

		final boolean enabled = enabler.isEnabled();
		if (!labelWidget.isDisposed()) {
			if (enabled) {
				labelWidget.setForeground(labelWidget.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			} else {
				labelWidget.setForeground(labelWidget.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			}
		}
		return enabled;
	}

}
