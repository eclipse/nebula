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

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.commons.StringUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This abstract class contains all common methods for widgets that are part of
 * a property table
 *
 */
public abstract class AbstractPTWidget implements PTWidget {

	private PropertyTable parentPropertyTable;
	protected StyledText descriptionLabel;

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.PTWidget#refillData()
	 */
	@Override
	public abstract void refillData();

	/**
	 * Build the widget itself
	 *
	 * @param parent
	 */
	protected abstract void buildWidget(final Composite parent);

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.PTWidget#build()
	 */
	@Override
	public PTWidget build() {
		Composite parent;
		SashForm form = null;
		if (parentPropertyTable.showDescription) {
			form = new SashForm(parentPropertyTable, SWT.VERTICAL | SWT.BORDER);
			form.setSashWidth(3);
			form.setLayout(new GridLayout());

			parent = new Composite(form, SWT.NONE);
			parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		} else {
			parent = parentPropertyTable;
		}
		parent.setLayout(new GridLayout(3, false));

		if (parentPropertyTable.showButtons) {
			buildButtons(parent, parentPropertyTable.sorted, //
					parentPropertyTable.styleOfView == PropertyTable.VIEW_AS_CATEGORIES, //
					parentPropertyTable.showDescription);
		}

		buildWidget(parent);

		if (parentPropertyTable.showDescription) {
			buildDescriptionPanel(form);
			form.setWeights(new int[] { 90, 10 });
		}

		return this;
	}

	/**
	 * Build the buttons (Sort, switch category/flat list, show/hide description)
	 *
	 * @param parent parent composite
	 * @param sorted if <code>true</code>, the sort button is pushed
	 * @param showAsCategory if <code>true</code>, the "show as category" button is
	 *            pushed
	 * @param showDescription if <code>true</code>, the "description" button is
	 *            pushed
	 */
	private void buildButtons(final Composite parent, final boolean sorted, final boolean showAsCategory, final boolean showDescription) {
		buildSortButton(parent, sorted);
		buildCategoryButton(parent, showAsCategory);
		buildDescriptionButton(parent, showDescription);
	}

	/**
	 * @param parent parent composite
	 * @param sorted if <code>true</code>, the sort button is pushed
	 */
	private void buildSortButton(final Composite parent, final boolean sorted) {
		final Button sortButton = new Button(parent, SWT.FLAT | SWT.TOGGLE);
		sortButton.setImage(SWTGraphicUtil.createImageFromFile("images/sort.png"));
		sortButton.setSelection(sorted);
		sortButton.setToolTipText(ResourceManager.getLabel(ResourceManager.SORT_SHORT_DESCRIPTION));
		sortButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		sortButton.addListener(SWT.Selection, event -> {
			getParentPropertyTable().sorted = !getParentPropertyTable().sorted;
			refillData();
		});

	}

	/**
	 * @param parent parent composite
	 * @param showAsCategory if <code>true</code>, the "show as category" button is
	 *            pushed
	 */
	private void buildCategoryButton(final Composite parent, final boolean showAsCategory) {
		final Button categoryButton = new Button(parent, SWT.FLAT | SWT.TOGGLE);
		categoryButton.setImage(SWTGraphicUtil.createImageFromFile("images/category.png"));
		categoryButton.setSelection(showAsCategory);
		categoryButton.setToolTipText(ResourceManager.getLabel(ResourceManager.CATEGORY_SHORT_DESCRIPTION));
		categoryButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		categoryButton.addListener(SWT.Selection, event -> {
			if (getParentPropertyTable().styleOfView == PropertyTable.VIEW_AS_CATEGORIES) {
				getParentPropertyTable().viewAsFlatList();
			} else {
				getParentPropertyTable().viewAsCategories();
			}
		});

	}

	/**
	 * @param parent parent composite
	 * @param showDescription if <code>true</code>, the "description" button is
	 *            pushed
	 */
	private void buildDescriptionButton(final Composite parent, final boolean showDescription) {
		final Button descriptionButton = new Button(parent, SWT.FLAT | SWT.TOGGLE);
		descriptionButton.setImage(SWTGraphicUtil.createImageFromFile("images/description.png"));
		descriptionButton.setSelection(showDescription);
		descriptionButton.setToolTipText(ResourceManager.getLabel(ResourceManager.DESCRIPTION_SHORT_DESCRIPTION));
		descriptionButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, true, false, 1, 1));

		descriptionButton.addListener(SWT.Selection, event -> {
			if (getParentPropertyTable().showDescription) {
				getParentPropertyTable().hideDescription();
			} else {
				getParentPropertyTable().showDescription();
			}
		});

	}

	/**
	 * Build the description panel
	 *
	 * @param parent parent composite
	 */
	private void buildDescriptionPanel(final Composite parent) {
		descriptionLabel = new StyledText(parent, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		descriptionLabel.setText("");
		descriptionLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.PTWidget#disposeAndBuild(org.eclipse.nebula.widgets.opal.propertytable.PropertyTable)
	 */
	@Override
	public PTWidget disposeAndBuild(final PropertyTable table) {
		dispose();
		return PTWidgetFactory.build(table);
	}

	/**
	 * Dispose the previous widget
	 */
	private void dispose() {
		if (parentPropertyTable == null || parentPropertyTable.getChildren() == null) {
			return;
		}

		for (final Control c : parentPropertyTable.getChildren()) {
			c.dispose();
		}
		return;
	}

	/**
	 * @return the parent PropertyTable
	 */
	protected PropertyTable getParentPropertyTable() {
		return parentPropertyTable;
	}

	/**
	 * @param parentPropertyTable the parent PropertyTable to set
	 */
	@Override
	public void setParentPropertyTable(final PropertyTable table) {
		parentPropertyTable = table;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.propertytable.PTWidget#updateDescriptionPanel(java.lang.Object)
	 */
	@Override
	public void updateDescriptionPanel(final Object selection) {
		if (selection == null || descriptionLabel == null) {
			return;
		}

		final PTProperty selectedProperty = (PTProperty) selection;
		descriptionLabel.setText(StringUtil.safeToString(selectedProperty.getDescription()));
		SWTGraphicUtil.applyHTMLFormating(descriptionLabel);
		descriptionLabel.update();
	}

}
