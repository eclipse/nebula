/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation 
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.propertytable.editor;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.nebula.widgets.opal.propertytable.PTProperty;
import org.eclipse.nebula.widgets.opal.propertytable.PTWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This editor allows the user to enter dates
 * 
 */
public class PTDateEditor extends PTEditor {

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

		final DateTime dateEditor = new DateTime(widget.getWidget(), SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN);
		final Date date = (Date) property.getValue();
		final Calendar c = Calendar.getInstance();

		if (date != null) {
			c.setTime(date);
			dateEditor.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		}

		dateEditor.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				c.setTime(new Date());
				c.clear();
				c.set(dateEditor.getYear(), dateEditor.getMonth(), dateEditor.getDay());
				property.setValue(c.getTime());
			}
		});

		dateEditor.addListener(SWT.FocusIn, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				widget.updateDescriptionPanel(property);
			}
		});

		editor.grabHorizontal = true;
		editor.horizontalAlignment = SWT.LEFT;

		if (widget.getWidget() instanceof Table) {
			((TableEditor) editor).setEditor(dateEditor, (TableItem) item, 1);
		} else {
			((TreeEditor) editor).setEditor(dateEditor, (TreeItem) item, 1);
		}

		dateEditor.setEnabled(property.isEnabled());

		return editor;
	}

}
