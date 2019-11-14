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

import java.awt.Dimension;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.propertytable.PTProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Editor for {@link Dimension} values
 */
public class PTDimensionEditor extends PTWindowEditor {

	private Text width;
	private Text height;

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTWindowEditor#createContent(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected void createContent(final Shell shell, final PTProperty property) {
		final Label widthLabel = new Label(shell, SWT.NONE);
		widthLabel.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		widthLabel.setText(ResourceManager.getLabel(ResourceManager.WIDTH));

		width = new Text(shell, SWT.BORDER);
		width.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		if (property.getValue() != null) {
			final Dimension d = (Dimension) property.getValue();
			width.setText(String.valueOf(d.width));
		}
		addVerifyListeners(width);

		final Label heightLabel = new Label(shell, SWT.NONE);
		heightLabel.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		heightLabel.setText(ResourceManager.getLabel(ResourceManager.HEIGHT));

		height = new Text(shell, SWT.BORDER);
		height.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		if (property.getValue() != null) {
			final Dimension d = (Dimension) property.getValue();
			height.setText(String.valueOf(d.height));
		}
		addVerifyListeners(height);

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTWindowEditor#fillProperty(org.eclipse.swt.widgets.Item,
	 *      org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected void fillProperty(final Item item, final PTProperty property) {
		final Dimension d = new Dimension();
		d.width = getIntValue(width);
		d.height = getIntValue(height);
		property.setValue(d);
		if (item instanceof TableItem) {
			((TableItem) item).setText(1, getTextFor(property));
		} else {
			((TreeItem) item).setText(1, getTextFor(property));
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTChooserEditor#getTextFor(org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected String getTextFor(final PTProperty property) {
		if (property.getValue() == null) {
			return "(null)";
		}
		final Dimension d = (Dimension) property.getValue();
		return "[" + d.width + "," + d.height + "]";
	}

}
