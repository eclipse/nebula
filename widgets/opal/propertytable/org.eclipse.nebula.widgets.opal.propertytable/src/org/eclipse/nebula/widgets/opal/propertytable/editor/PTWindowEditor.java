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
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.propertytable.PTProperty;
import org.eclipse.nebula.widgets.opal.propertytable.PTWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This abstract class contains common code for editors that open a window :
 * dimension editor, insets editor, rectangle editor
 *
 */
public abstract class PTWindowEditor extends PTChooserEditor {

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTChooserEditor#openWindow(org.eclipse.nebula.widgets.opal.propertytable.PTWidget,
	 *      org.eclipse.swt.widgets.Item, org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected void openWindow(final PTWidget widget, final Item item, final PTProperty property) {
		final Shell shell = new Shell(widget.getWidget().getShell(), SWT.DIALOG_TRIM);
		shell.setLayout(new GridLayout(2, false));
		shell.setText(ResourceManager.getLabel(ResourceManager.EDIT_PROPERTY));

		final Label title = new Label(shell, SWT.NONE);
		final GridData titleLayoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 2, 1);
		titleLayoutData.widthHint = 400;
		title.setLayoutData(titleLayoutData);
		final Font font = SWTGraphicUtil.buildFontFrom(title, SWT.BOLD, 16);
		title.setFont(font);
		title.setText(ResourceManager.getLabel(ResourceManager.EDIT_PROPERTY));
		SWTGraphicUtil.addDisposer(title, font);

		createContent(shell, property);

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		final GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
		composite.setLayout(gridLayout);

		final Button ok = new Button(composite, SWT.PUSH);
		final GridData okLayoutData = new GridData(GridData.END, GridData.BEGINNING, true, false);
		okLayoutData.widthHint = 150;
		ok.setLayoutData(okLayoutData);
		ok.setText(ResourceManager.getLabel(ResourceManager.OK));
		ok.addListener(SWT.Selection, event -> {
			fillProperty(item, property);
			shell.dispose();
		});

		final Button cancel = new Button(composite, SWT.PUSH);
		final GridData cancelLayoutData = new GridData(GridData.END, GridData.BEGINNING, false, false);
		cancelLayoutData.widthHint = 150;
		cancel.setLayoutData(cancelLayoutData);
		cancel.setText(ResourceManager.getLabel(ResourceManager.CANCEL));
		cancel.addListener(SWT.Selection, event -> {
			shell.dispose();
		});

		shell.setDefaultButton(ok);
		shell.pack();
		SWTGraphicUtil.centerShell(shell);
		shell.open();
	}

	/**
	 * Fill property when the ok button is pressed
	 *
	 * @param item item in which the string representation of property's value is
	 *            display
	 * @param property associated property
	 */
	protected abstract void fillProperty(Item item, PTProperty property);

	/**
	 * Create the content (text widgets)
	 *
	 * @param shell associated shell
	 * @param property associated property
	 */
	protected abstract void createContent(Shell shell, PTProperty property);

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTChooserEditor#getBackgroundColor(org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	protected Color getBackgroundColor(final PTProperty property) {
		return null;
	}

	/**
	 * @param text text widget
	 * @return the integer value stored in a text. If the text value is empty,
	 *         returns 0
	 */
	protected int getIntValue(final Text text) {
		if (text.getText().trim().equals("")) {
			return 0;
		}
		return Integer.parseInt(text.getText().trim());
	}

	/**
	 * Add a verify listener to a given text that accepts only integers
	 *
	 * @param text text widget
	 */
	protected void addVerifyListeners(final Text text) {
		text.addListener(SWT.Verify, e -> {
			final String string = e.text;
			final char[] chars = new char[string.length()];
			string.getChars(0, chars.length, chars, 0);
			for (int i = 0; i < chars.length; i++) {
				if (!('0' <= chars[i] && chars[i] <= '9') && e.keyCode != SWT.BS && e.keyCode != SWT.DEL) {
					e.doit = false;
					return;
				}
			}
		});
	}

}
