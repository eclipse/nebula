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
package org.eclipse.nebula.widgets.opal.preferencewindow;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget;

/**
 * This POJO contains a value a list of widgets that depends on a given property
 * 
 */
class ValueAndAssociatedWidgets {
	private Object value;
	private final List<PWWidget> widgets;
	private final List<PWRowGroup> rowGroups;

	/**
	 * Constructor
	 * 
	 * @param value associated value
	 */
	ValueAndAssociatedWidgets(final Object value) {
		this.value = value;
		this.widgets = new ArrayList<PWWidget>();
		this.rowGroups = new ArrayList<PWRowGroup>();
	}

	/**
	 * @param widget dependant widget
	 */
	void addWidget(final PWWidget widget) {
		this.widgets.add(widget);
	}

	/**
	 * @param rowGroup dependant row or group
	 */
	void addRowGroup(final PWRowGroup rowGroup) {
		this.rowGroups.add(rowGroup);
	}

	/**
	 * @return the value stored in the instance
	 */
	Object getValue() {
		return this.value;
	}

	/**
	 * @param value new value stored in this instance
	 */
	void setValue(final Object value) {
		this.value = value;
		fireValueChanged();
	}

	/**
	 * Fire events when the value has changed
	 */
	void fireValueChanged() {
		for (final PWRowGroup rowGroup : this.rowGroups) {
			rowGroup.enableOrDisable();
		}

		for (final PWWidget widget : this.widgets) {
			widget.enableOrDisable();
		}
	}
}
