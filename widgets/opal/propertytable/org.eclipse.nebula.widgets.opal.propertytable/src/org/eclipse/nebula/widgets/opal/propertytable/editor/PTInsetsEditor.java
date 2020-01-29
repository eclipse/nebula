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

import java.awt.Insets;

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
 * Editor for {@link Insets} values
 */
public class PTInsetsEditor extends PTWindowEditor {

	private Text top;
	private Text left;
	private Text right;
	private Text bottom;

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTWindowEditor#createContent(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected void createContent(final Shell shell, final PTProperty property) {
		final Label topLabel = new Label(shell, SWT.NONE);
		topLabel.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		topLabel.setText(ResourceManager.getLabel(ResourceManager.TOP));

		top = new Text(shell, SWT.BORDER);
		top.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		if (property.getValue() != null) {
			final Insets insets = (Insets) property.getValue();
			top.setText(String.valueOf(insets.top));
		}
		addVerifyListeners(top);

		final Label heightLabel = new Label(shell, SWT.NONE);
		heightLabel.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		heightLabel.setText(ResourceManager.getLabel(ResourceManager.LEFT));

		left = new Text(shell, SWT.BORDER);
		left.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		if (property.getValue() != null) {
			final Insets insets = (Insets) property.getValue();
			left.setText(String.valueOf(insets.left));
		}
		addVerifyListeners(left);

		final Label bottomLabel = new Label(shell, SWT.NONE);
		bottomLabel.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		bottomLabel.setText(ResourceManager.getLabel(ResourceManager.BOTTOM));

		bottom = new Text(shell, SWT.BORDER);
		bottom.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		if (property.getValue() != null) {
			final Insets insets = (Insets) property.getValue();
			bottom.setText(String.valueOf(insets.bottom));
		}
		addVerifyListeners(bottom);

		final Label rightLabel = new Label(shell, SWT.NONE);
		rightLabel.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		rightLabel.setText(ResourceManager.getLabel(ResourceManager.RIGHT));

		right = new Text(shell, SWT.BORDER);
		right.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		if (property.getValue() != null) {
			final Insets insets = (Insets) property.getValue();
			right.setText(String.valueOf(insets.bottom));
		}
		addVerifyListeners(right);

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTWindowEditor#fillProperty(org.eclipse.swt.widgets.Item,
	 *      org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected void fillProperty(final Item item, final PTProperty property) {
		final Insets i = new Insets(getIntValue(top), getIntValue(left), getIntValue(bottom), getIntValue(right));
		property.setValue(i);
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
		final Insets insets = (Insets) property.getValue();
		return "[" + insets.top + "," + insets.left + "," + insets.bottom + "," + insets.right + "]";
	}

}
