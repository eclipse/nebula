/*******************************************************************************
 * Copyright (c) 2012-2022 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable;

import org.eclipse.swt.widgets.Composite;

/**
 * Classes which implement this interface are widgets that may compose a
 * PropertyTable (Table for Flat List, TableTree for Category)
 *
 */
public interface PTWidget {

	/**
	 * Build the widget (Table or TreeTable)
	 *
	 * @return the built widget
	 */
	PTWidget build();

	/**
	 * Dispose the previous widget and build a new one (when ones switch from
	 * Category View to Flat List view)
	 *
	 * @param table the PropertyTable to dispose
	 * @return the built widget
	 */
	PTWidget disposeAndBuild(PropertyTable table);

	/**
	 * Inject the parent property table in the widget
	 *
	 * @param table table to inject
	 */
	void setParentPropertyTable(final PropertyTable table);

	/**
	 * Clear all data and fill the widget
	 */
	void refillData();

	/**
	 * @return the underlying widget (Table or TableTree)
	 */
	Composite getWidget();

	/**
	 * Update the description panel (if it exists)
	 *
	 * @param selection selected property
	 */
	void updateDescriptionPanel(final Object selection);

}
