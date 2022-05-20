/*******************************************************************************
 * Copyright (c) 2012-2022 Laurent CARON
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Instances of this class are table that are displayed in a PropertyTable when
 * the type of view is "Category"
 */
public class PTWidgetTree extends AbstractPTWidget {

	private Tree tree;

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.AbstractPTWidget#buildWidget(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void buildWidget(final Composite parent) {
		tree = new Tree(parent, SWT.FULL_SELECTION);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));

		final TreeColumn propertyColumn = new TreeColumn(tree, SWT.NONE);
		propertyColumn.setText(ResourceManager.getLabel(ResourceManager.PROPERTY));

		final TreeColumn valueColumn = new TreeColumn(tree, SWT.NONE);
		valueColumn.setText(ResourceManager.getLabel(ResourceManager.VALUE));

		fillData();
		tree.addControlListener(new ControlAdapter() {

			/**
			 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
			 */
			@Override
			public void controlResized(final ControlEvent e) {
				final Rectangle area = tree.getParent().getClientArea();
				final Point size = tree.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				final ScrollBar vBar = tree.getVerticalBar();
				int width = area.width - tree.computeTrim(0, 0, 0, 0).width - vBar.getSize().x;
				if (size.y > area.height + tree.getHeaderHeight()) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					final Point vBarSize = vBar.getSize();
					width -= vBarSize.x;
				}
				propertyColumn.pack();
				valueColumn.setWidth(width - propertyColumn.getWidth());
				tree.removeControlListener(this);
			}

		});

		tree.addListener(SWT.Selection, event -> {
			if (tree.getSelectionCount() == 0 || tree.getSelection()[0] == null) {
				return;
			}
			updateDescriptionPanel(tree.getSelection()[0].getData());
		});

	}

	/**
	 * Fill the data in the widget
	 */
	private void fillData() {
		final Map<String, List<PTProperty>> data;

		if (getParentPropertyTable().sorted) {
			data = new TreeMap<>();
		} else {
			data = new LinkedHashMap<>();
		}

		for (final PTProperty p : getParentPropertyTable().getPropertiesAsList()) {
			final String category = StringUtil.safeToString(p.getCategory());
			if (!data.containsKey(category)) {
				data.put(category, new ArrayList<PTProperty>());
			}
			data.get(category).add(p);
		}

		for (final Entry<String, List<PTProperty>> entry : data.entrySet()) {

			if (entry.getValue() == null || entry.getValue().isEmpty()) {
				continue;
			}

			final TreeItem root = new TreeItem(tree, SWT.NONE);
			root.setText(0, entry.getKey());
			root.setBackground(root.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			root.setForeground(root.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			root.setExpanded(true);

			for (final PTProperty p : entry.getValue()) {
				final TreeItem item = new TreeItem(root, SWT.NONE);
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
					item.setForeground(item.getDisplay().getSystemColor(SWT.COLOR_GRAY));
				}
				item.setExpanded(true);
				p.setAssociatedItem(item);
			}
			root.setExpanded(true);

		}

	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.AbstractPTWidget#refillData()
	 */
	@Override
	public void refillData() {
		try {
			if (tree != null) {
				tree.setRedraw(false);
				for (final TreeItem treeItem : tree.getItems()) {
					treeItem.dispose();
				}
			}
			fillData();
		} finally {
			if (tree != null) {
				tree.setRedraw(true);
				tree.redraw();
				tree.update();
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.PTWidget#getWidget()
	 */
	@Override
	public Composite getWidget() {
		return tree;
	}

}
