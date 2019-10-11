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
import org.eclipse.nebula.widgets.opal.propertytable.PTProperty;
import org.eclipse.nebula.widgets.opal.propertytable.PTWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This editor is a font editor
 */
public class PTFontEditor extends PTChooserEditor {

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTChooserEditor#openWindow(org.eclipse.nebula.widgets.opal.propertytable.PTWidget,
	 *      org.eclipse.swt.widgets.Item, org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected void openWindow(final PTWidget widget, final Item item, final PTProperty property) {
		final FontDialog dialog = new FontDialog(widget.getWidget().getShell());
		final FontData result = dialog.open();
		if (result != null && result.getName() != null && !"".equals(result.getName().trim())) {
			property.setValue(result);
			if (item instanceof TableItem) {
				((TableItem) item).setText(1, getTextFor(property));
			} else {
				((TreeItem) item).setText(1, getTextFor(property));
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTChooserEditor#getTextFor(org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected String getTextFor(final PTProperty property) {
		if (property.getValue() == null) {
			return "";
		}

		final FontData fontData = (FontData) property.getValue();

		final StringBuilder sb = new StringBuilder();
		if (fontData != null) {
			sb.append(fontData.getName()).append(",").append(fontData.getHeight()).append(" pt");
			if ((fontData.getStyle() & SWT.BOLD) == SWT.BOLD) {
				sb.append(", ").append(ResourceManager.getLabel(ResourceManager.BOLD));
			}
			if ((fontData.getStyle() & SWT.ITALIC) == SWT.ITALIC) {
				sb.append(", ").append(ResourceManager.getLabel(ResourceManager.ITALIC));
			}
		}
		return sb.toString();
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTChooserEditor#getBackgroundColor(org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected Color getBackgroundColor(final PTProperty property) {
		return null;
	}

}
