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

import org.eclipse.nebula.widgets.opal.titledseparator.TitledSeparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Instances of this class are separators
 *
 */
public class PWSeparator extends PWWidget {

	private final Image image;

	/**
	 * Constructor
	 *
	 */
	public PWSeparator() {
		this(null, null);
	}

	/**
	 * Constructor
	 *
	 * @param label associated label
	 */
	public PWSeparator(final String label) {
		this(label, null);
	}

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param image associated image
	 */
	public PWSeparator(final String label, final Image image) {
		super(label, null, 1, true);
		this.image = image;
		setAlignment(GridData.FILL);
		setGrabExcessSpace(true);
		setHeight(20);
	}

	@Override
	public Control build(final Composite parent) {
		final TitledSeparator sep = new TitledSeparator(parent, SWT.NONE);
		addControl(sep);
		sep.setText(getLabel());
		sep.setImage(image);
		return sep;
	}

	@Override
	public void check() {
	}

}
