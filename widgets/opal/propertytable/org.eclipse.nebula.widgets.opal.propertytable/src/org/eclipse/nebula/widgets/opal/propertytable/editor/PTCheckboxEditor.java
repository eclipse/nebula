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
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable.editor;

import org.eclipse.nebula.widgets.opal.propertytable.PTProperty;
import org.eclipse.nebula.widgets.opal.propertytable.PTWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This editor is a check box for boolean values
 *
 */
public class PTCheckboxEditor extends PTEditor {

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTEditor#render(org.eclipse.nebula.widgets.opal.propertytable.PTWidget,
	 *      org.eclipse.swt.widgets.Item,
	 *      org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	public ControlEditor render(final PTWidget widget, final Item item, final PTProperty property) {
		ControlEditor editor;
		if (widget.getWidget() instanceof Table) {
			editor = new TableEditor((Table) widget.getWidget());
		} else {
			editor = new TreeEditor((Tree) widget.getWidget());
		}

		final Button button = new Button(widget.getWidget(), SWT.CHECK);

		if (property.getValue() == null) {
			button.setSelection(false);
		} else {
			button.setSelection(((Boolean) property.getValue()).booleanValue());
		}

		button.addListener(SWT.Selection, event -> {
			property.setValue(Boolean.valueOf(button.getSelection()));
		});

		button.addListener(SWT.FocusIn, event -> {
			widget.updateDescriptionPanel(property);
		});

		button.pack();
		editor.minimumWidth = button.getSize().x;
		editor.horizontalAlignment = SWT.LEFT;
		if (widget.getWidget() instanceof Table) {
			((TableEditor) editor).setEditor(button, (TableItem) item, 1);
		} else {
			((TreeEditor) editor).setEditor(button, (TreeItem) item, 1);
		}

		button.setEnabled(property.isEnabled());

		return editor;

	}

}
