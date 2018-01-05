/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
