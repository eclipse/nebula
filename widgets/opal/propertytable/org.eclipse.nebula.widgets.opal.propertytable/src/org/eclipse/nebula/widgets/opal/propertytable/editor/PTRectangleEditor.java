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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Editor for {@link Rectangle} property
 */
public class PTRectangleEditor extends PTWindowEditor {

	private Text x;
	private Text y;
	private Text width;
	private Text height;

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTWindowEditor#createContent(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected void createContent(final Shell shell, final PTProperty property) {
		final Label xLabel = new Label(shell, SWT.NONE);
		xLabel.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		xLabel.setText("X");

		x = new Text(shell, SWT.BORDER);
		x.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		if (property.getValue() != null) {
			final Rectangle rect = (Rectangle) property.getValue();
			x.setText(String.valueOf(rect.x));
		}
		addVerifyListeners(x);

		final Label yLabel = new Label(shell, SWT.NONE);
		yLabel.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		yLabel.setText("Y");

		y = new Text(shell, SWT.BORDER);
		y.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		if (property.getValue() != null) {
			final Rectangle rect = (Rectangle) property.getValue();
			y.setText(String.valueOf(rect.y));
		}
		addVerifyListeners(y);

		final Label widthLabel = new Label(shell, SWT.NONE);
		widthLabel.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		widthLabel.setText(ResourceManager.getLabel(ResourceManager.WIDTH));

		width = new Text(shell, SWT.BORDER);
		width.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		if (property.getValue() != null) {
			final Rectangle rect = (Rectangle) property.getValue();
			x.setText(String.valueOf(rect.width));
		}
		addVerifyListeners(width);

		final Label heightLabel = new Label(shell, SWT.NONE);
		heightLabel.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		heightLabel.setText(ResourceManager.getLabel(ResourceManager.HEIGHT));

		height = new Text(shell, SWT.BORDER);
		height.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		if (property.getValue() != null) {
			final Rectangle rect = (Rectangle) property.getValue();
			y.setText(String.valueOf(rect.height));
		}
		addVerifyListeners(height);

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTWindowEditor#fillProperty(org.eclipse.swt.widgets.Item,
	 *      org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected void fillProperty(final Item item, final PTProperty property) {
		final Rectangle r = new Rectangle(getIntValue(x), getIntValue(y), getIntValue(width), getIntValue(height));
		property.setValue(r);
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
		final Rectangle rect = (Rectangle) property.getValue();
		return "[" + rect.x + "," + rect.y + "," + rect.width + "," + rect.height + "]";
	}

}
