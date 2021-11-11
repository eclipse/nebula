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
 * This enabler is used to enable a widget if a boolean property is true
 */
public class EnabledIfTrue extends Enabler {

	/**
	 * Constructor
	 * 
	 * @param prop boolean property
	 */
	public EnabledIfTrue(final String prop) {
		super(prop);
	}

	@Override
	public boolean isEnabled() {
		final Object value = PreferenceWindow.getInstance().getValueFor(this.prop);

		if (value != null && !(value instanceof Boolean)) {
			throw new UnsupportedOperationException("Impossible to evaluate [" + this.prop + "] because it is not a Boolean !");
		}

		if (value == null) {
			return true;
		}

		return ((Boolean) value).booleanValue();
	}
}
