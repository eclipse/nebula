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
package org.eclipse.nebula.widgets.opal.propertytable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.commons.StringUtil;
import org.eclipse.nebula.widgets.opal.propertytable.editor.PTStringEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Instances of this class are table that are displayed in a PropertyTable when
 * the type of view is "Flat List"
 */
class PTWidgetTable extends AbstractPTWidget {

	private Table table;

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.AbstractPTWidget#buildWidget(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void buildWidget(final Composite parent) {
		table = new Table(parent, SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));

		final TableColumn propertyColumn = new TableColumn(table, SWT.NONE);
		propertyColumn.setText(ResourceManager.getLabel(ResourceManager.PROPERTY));

		final TableColumn valueColumn = new TableColumn(table, SWT.NONE);
		valueColumn.setText(ResourceManager.getLabel(ResourceManager.VALUE));

		fillData();

		table.addControlListener(new ControlAdapter() {

			/**
			 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
			 */
			@Override
			public void controlResized(final ControlEvent e) {
				final Rectangle area = table.getParent().getClientArea();
				final Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				final ScrollBar vBar = table.getVerticalBar();
				int width = area.width - table.computeTrim(0, 0, 0, 0).width - vBar.getSize().x;
				if (size.y > area.height + table.getHeaderHeight()) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					final Point vBarSize = vBar.getSize();
					width -= vBarSize.x;
				}
				propertyColumn.pack();
				valueColumn.setWidth(width - propertyColumn.getWidth());
				table.removeControlListener(this);
			}

		});

		table.addListener(SWT.Selection, event -> {
			if (table.getSelectionCount() == 0 || table.getSelection()[0] == null) {
				return;
			}
			updateDescriptionPanel(table.getSelection()[0].getData());
		});

	}

	/**
	 * Fill Data in the widget
	 */
	private void fillData() {
		List<PTProperty> props;
		if (getParentPropertyTable().sorted) {
			props = new ArrayList<>(getParentPropertyTable().getPropertiesAsList());
			Collections.sort(props, (o1, o2) -> {
				if (o1 == null && o2 == null) {
					return 0;
				}

				if (o1.getName() == null && o2.getName() != null) {
					return -1;
				}

				if (o1.getName() != null && o2.getName() == null) {
					return 1;
				}

				return o1.getName().compareTo(o2.getName());
			});
		} else {
			props = new ArrayList<>(getParentPropertyTable().getPropertiesAsList());
		}

		final List<ControlEditor> editors = new ArrayList<>();
		for (final PTProperty p : props) {
			final TableItem item = new TableItem(table, SWT.NONE);
			item.setData(p);
			item.setText(0, StringUtil.safeToString(p.getDisplayName()));
			if (p.getEditor() == null) {
				p.setEditor(new PTStringEditor());
			}

			final ControlEditor editor = p.getEditor().render(this, item, p);
			item.addListener(SWT.Dispose, event -> {
				if (editor.getEditor() != null) {
					editor.getEditor().dispose();
				}
				editor.dispose();
			});
			if (!p.isEnabled()) {
				item.setForeground(table.getDisplay().getSystemColor(SWT.COLOR_GRAY));
			}
			p.setAssociatedItem(item);
		}

		table.setData(editors);

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.AbstractPTWidget#refillData()
	 */
	@Override
	public void refillData() {
		try {
			table.setRedraw(false);
			for (final TableItem item : table.getItems()) {
				item.dispose();
			}

			if (table.getData() != null) {
				@SuppressWarnings("unchecked")
				final List<ControlEditor> list = (List<ControlEditor>) table.getData();
				for (final ControlEditor c : list) {
					c.dispose();
				}
				list.clear();
				table.setData(null);
			}

			fillData();
		} finally {
			table.setRedraw(true);
			table.redraw();
			table.update();
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.PTWidget#getWidget()
	 */
	@Override
	public Composite getWidget() {
		return table;
	}

}
