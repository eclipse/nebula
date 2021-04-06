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
package org.eclipse.nebula.widgets.opal.propertytable.editor;

import org.eclipse.nebula.widgets.opal.propertytable.PTProperty;
import org.eclipse.nebula.widgets.opal.propertytable.PTWidget;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.widgets.Item;

/**
 * This abstract class represents a Property Table Editor. An editor is a widget
 * that allows one to modify a property
 * 
 */
public abstract class PTEditor {

	/**
	 * Renders an editor
	 * 
	 * @param parent the parent PTWidget (a table or a tree table)
	 * @param item the item on which the editor is displayed
	 * @param property the property associated to the editor
	 * @return a control editor
	 */
	public abstract ControlEditor render(PTWidget parent, Item item, PTProperty property);

}
