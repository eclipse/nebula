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

import org.eclipse.nebula.widgets.opal.preferencewindow.PWRowGroup;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget;

/**
 * This is the abstract class of all Enablers. An enabler is an object used to
 * enable or disable a widget depending on a value of a stored property.
 */
public abstract class Enabler {

	protected String prop;

	/**
	 * Constructor
	 * 
	 * @param prop property linked to the enabler.
	 */
	public Enabler(final String prop) {
		this.prop = prop;
	}

	/**
	 * @return the evaluation condition
	 */
	public abstract boolean isEnabled();

	/**
	 * Link a widget to the enabler
	 * 
	 * @param widget widget to link
	 */
	public void injectWidget(final PWWidget widget) {
		PreferenceWindow.getInstance().addWidgetLinkedTo(this.prop, widget);
	}

	/**
	 * Link a row or a group to the enabler
	 * 
	 * @param rowGroup RowGroup to link
	 */
	public void injectRowGroup(final PWRowGroup rowGroup) {
		PreferenceWindow.getInstance().addRowGroupLinkedTo(this.prop, rowGroup);
	}

}
