/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation 
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable;

/**
 * This class is a factory that builds the widget that is part of a property
 * table
 */
class PTWidgetFactory {

	/**
	 * Build the widget displayed in a property table
	 * 
	 * @param table property table
	 * @return the widget
	 */
	static PTWidget build(final PropertyTable table) {
		PTWidget widget;
		if (table.styleOfView == PropertyTable.VIEW_AS_FLAT_LIST) {
			widget = new PTWidgetTable();
		} else {
			widget = new PTWidgetTree();
		}
		widget.setParentPropertyTable(table);
		return widget;
	}
}
