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
package org.eclipse.nebula.widgets.opal.preferencewindow.enabler;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;

/**
 * This enabler is used to enable a widget if a property is not equal to a given
 * value
 */
public class EnabledIfNotEquals extends Enabler {
	private final Object value;

	/**
	 * Constructor
	 * 
	 * @param prop property to evaluate
	 * @param value condition value
	 */
	public EnabledIfNotEquals(final String prop, final Object value) {
		super(prop);
		this.value = value;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.enabler.Enabler#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		final Object propValue = PreferenceWindow.getInstance().getValueFor(this.prop);
		if (this.value == null) {
			return propValue == null;
		}
		return !this.value.equals(propValue);
	}
}
