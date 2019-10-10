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
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This abstract class represents a chooser. A chooser is composed of :
 * <ul>
 * <li>a displayed value (text)
 * <li>a "X" button to erase the value (set to null)
 * <li>a "..." button to open an extra window to set up the value
 * </ul>
 *
 */
public abstract class PTChooserEditor extends PTEditor {

	private PTWidget widget;
	private Item item;
	private PTProperty property;

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.editor.PTEditor#render(org.eclipse.nebula.widgets.opal.propertytable.PTWidget,
	 *      org.eclipse.swt.widgets.Item, org.eclipse.nebula.widgets.opal.propertytable.PTProperty)
	 */
	@Override
	public ControlEditor render(final PTWidget widget, final Item item, final PTProperty property) {
		this.widget = widget;
		this.item = item;
		this.property = property;

		if (item instanceof TableItem) {
			((TableItem) item).setText(1, getTextFor(property));
		} else {
			((TreeItem) item).setText(1, getTextFor(property));
		}

		final Color bgColor = getBackgroundColor(property);
		if (bgColor != null) {
			if (item instanceof TableItem) {
				((TableItem) item).setBackground(1, bgColor);
			}
			if (item instanceof TreeItem) {
				((TreeItem) item).setBackground(1, bgColor);
			}
			SWTGraphicUtil.addDisposer(item, bgColor);
		}

		ControlEditor editor;
		if (widget.getWidget() instanceof Table) {
			editor = new TableEditor((Table) widget.getWidget());
		} else {
			editor = new TreeEditor((Tree) widget.getWidget());
		}

		final Composite buttonHolder = new Composite(widget.getWidget(), SWT.NONE);
		final FillLayout buttonHolderLayout = new FillLayout(SWT.HORIZONTAL);
		buttonHolderLayout.marginWidth = buttonHolderLayout.marginHeight = 0;
		buttonHolder.setLayout(buttonHolderLayout);

		createEraseButton(buttonHolder);
		createPlusButton(buttonHolder);

		buttonHolder.pack();
		editor.minimumWidth = buttonHolder.getSize().x;
		editor.horizontalAlignment = SWT.RIGHT;

		if (widget.getWidget() instanceof Table) {
			((TableEditor) editor).setEditor(buttonHolder, (TableItem) item, 1);
		} else {
			((TreeEditor) editor).setEditor(buttonHolder, (TreeItem) item, 1);
		}

		return editor;
	}

	/**
	 * Creates the "erase" button
	 *
	 * @param buttonHolder parent composite
	 */
	private void createEraseButton(final Composite buttonHolder) {
		final Button eraseButton = new Button(buttonHolder, SWT.PUSH);
		eraseButton.setText(" X ");
		eraseButton.setToolTipText(ResourceManager.getLabel(ResourceManager.ERASE_PROPERTY));
		eraseButton.setEnabled(property.isEnabled());
		eraseButton.pack();

		eraseButton.addListener(SWT.Selection, event -> {
			property.setValue(null);
			if (item instanceof TableItem) {
				((TableItem) item).setBackground(1, Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
				((TableItem) item).setText(1, getTextFor(property));
			}
			if (item instanceof TreeItem) {
				((TreeItem) item).setBackground(1, Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
				((TreeItem) item).setText(1, getTextFor(property));
			}
		});

		eraseButton.addListener(SWT.FocusIn, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				widget.updateDescriptionPanel(property);
			}
		});
	}

	/**
	 * Creates the "plus" button
	 *
	 * @param buttonHolder aprent composite
	 */
	private void createPlusButton(final Composite buttonHolder) {
		final Button plusButton = new Button(buttonHolder, SWT.PUSH);
		plusButton.setText("...");
		plusButton.setToolTipText(ResourceManager.getLabel(ResourceManager.EDIT_PROPERTY));
		plusButton.setEnabled(property.isEnabled());

		plusButton.addListener(SWT.Selection, event -> {
			openWindow(widget, item, property);
		});

		plusButton.addListener(SWT.FocusIn, event -> {
			widget.updateDescriptionPanel(property);
		});

		plusButton.pack();
	}

	/**
	 * Open the window to edit the property
	 *
	 * @param widget parent widget
	 * @param item item
	 * @param property edited property
	 */
	protected abstract void openWindow(PTWidget widget, Item item, PTProperty property);

	/**
	 * @param property property
	 * @return the string representation of the value stored in the property
	 */
	protected abstract String getTextFor(PTProperty property);

	/**
	 * Get the background color of an item
	 *
	 * @param property property
	 * @return a background color (for the PTColorEditor) or null (for other
	 *         editors).
	 */
	protected abstract Color getBackgroundColor(PTProperty property);

}
