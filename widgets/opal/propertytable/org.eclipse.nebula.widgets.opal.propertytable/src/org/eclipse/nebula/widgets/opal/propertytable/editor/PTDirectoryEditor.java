/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable.editor;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.commons.StringUtil;
import org.eclipse.nebula.widgets.opal.propertytable.PTProperty;
import org.eclipse.nebula.widgets.opal.propertytable.PTWidget;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This editor allows user to select a directory
 */
public class PTDirectoryEditor extends PTChooserEditor {

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTChooserEditor#openWindow(org.eclipse.nebula.widgets.opal.propertytable.PTWidget,
	 *      org.eclipse.swt.widgets.Item, org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected void openWindow(final PTWidget widget, final Item item, final PTProperty property) {
		final DirectoryDialog dialog = new DirectoryDialog(widget.getWidget().getShell());
		dialog.setMessage(ResourceManager.getLabel(ResourceManager.CHOOSE_DIRECTORY));
		final String result = dialog.open();
		if (result != null) {
			if (item instanceof TableItem) {
				((TableItem) item).setText(1, result);
			} else {
				((TreeItem) item).setText(1, result);
			}
			property.setValue(result);
		}

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTChooserEditor#getTextFor(org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected String getTextFor(final PTProperty property) {
		return StringUtil.safeToString(property.getValue());
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTChooserEditor#getBackgroundColor(org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected Color getBackgroundColor(final PTProperty property) {
		return null;
	}

}
