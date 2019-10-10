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
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This editor is a spinner
 *
 */
public class PTSpinnerEditor extends PTEditor {
	private final int max;
	private final int min;

	/**
	 * Constructor
	 *
	 * @param min minimum value
	 * @param max maximum value
	 */
	public PTSpinnerEditor(final int min, final int max) {
		this.min = min;
		this.max = max;
	}

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

		final Spinner spinner = new Spinner(widget.getWidget(), SWT.HORIZONTAL);

		spinner.setMinimum(min);
		spinner.setMaximum(max);
		final Integer originalValue = (Integer) property.getValue();
		spinner.setSelection(originalValue == null ? min : originalValue.intValue());

		spinner.addListener(SWT.FocusIn, event -> {
			widget.updateDescriptionPanel(property);
		});

		spinner.addListener(SWT.Modify, event -> {
			property.setValue(Integer.valueOf(spinner.getSelection()));
		});

		editor.grabHorizontal = true;
		if (widget.getWidget() instanceof Table) {
			((TableEditor) editor).setEditor(spinner, (TableItem) item, 1);
		} else {
			((TreeEditor) editor).setEditor(spinner, (TreeItem) item, 1);
		}

		spinner.setEnabled(property.isEnabled());

		return editor;

	}

}
