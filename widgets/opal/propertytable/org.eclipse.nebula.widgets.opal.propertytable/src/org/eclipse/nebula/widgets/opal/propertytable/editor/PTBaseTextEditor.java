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

import org.eclipse.nebula.widgets.opal.commons.StringUtil;
import org.eclipse.nebula.widgets.opal.propertytable.PTProperty;
import org.eclipse.nebula.widgets.opal.propertytable.PTWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This abstract class represents all text-based editors (float editor, integer
 * editor, password editor, String editor, URL editor)
 *
 */
public abstract class PTBaseTextEditor extends PTEditor {

	protected Text text;

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTEditor#render(org.eclipse.nebula.widgets.opal.propertytable.PTWidget,
	 *      org.eclipse.swt.widgets.Item, org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	public ControlEditor render(final PTWidget widget, final Item item, final PTProperty property) {

		ControlEditor editor;
		if (widget.getWidget() instanceof Table) {
			editor = new TableEditor((Table) widget.getWidget());
		} else {
			editor = new TreeEditor((Tree) widget.getWidget());
		}

		widget.updateDescriptionPanel(property);

		text = new Text(widget.getWidget(), getStyle());

		addVerifyListeners();

		text.setText(StringUtil.safeToString(property.getValue()));
		text.addListener(SWT.Modify, event -> {
			property.setValue(convertValue());
		});

		text.addListener(SWT.FocusIn, event -> {
			widget.updateDescriptionPanel(property);
		});

		editor.grabHorizontal = true;
		if (widget.getWidget() instanceof Table) {
			((TableEditor) editor).setEditor(text, (TableItem) item, 1);
		} else {
			((TreeEditor) editor).setEditor(text, (TreeItem) item, 1);
		}

		text.setEnabled(property.isEnabled());

		return editor;
	}

	/**
	 * Add the verify listeners
	 */
	public abstract void addVerifyListeners();

	/**
	 * @return the value of the data typed by the user in the correct format
	 */
	public abstract Object convertValue();

	/**
	 * @return the style (SWT.NONE or SWT.PASSWORD)
	 */
	public abstract int getStyle();
}
