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
package org.eclipse.nebula.widgets.opal.preferencewindow.enabler;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;

/**
 * This enabler is used to enable a widget if a property is equal to a given
 * value
 */
public class EnabledIfEquals extends Enabler {
	private final Object value;

	/**
	 * Constructor
	 * 
	 * @param prop property to evaluate
	 * @param value condition value
	 */
	public EnabledIfEquals(final String prop, final Object value) {
		super(prop);
		this.value = value;
	}

	@Override
	public boolean isEnabled() {
		final Object propValue = PreferenceWindow.getInstance().getValueFor(this.prop);
		if (this.value == null) {
			return propValue == null;
		}
		return this.value.equals(propValue);
	}
}
