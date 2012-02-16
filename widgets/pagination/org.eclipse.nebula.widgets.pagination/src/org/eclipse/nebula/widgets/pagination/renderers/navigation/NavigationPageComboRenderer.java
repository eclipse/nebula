/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.renderers.navigation;

import org.eclipse.nebula.widgets.pagination.AbstractPageControllerComposite;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.PaginationHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * This SWT {@link Composite} display a SWT {@link Combo} linked to the current
 * page of the pagination controller. When combo item is selected it update the
 * current page of the pagination controller.
 * 
 */
public class NavigationPageComboRenderer extends
		AbstractPageControllerComposite implements SelectionListener {

	private Combo pageCombo;

	public NavigationPageComboRenderer(Composite parent, int style,
			PageableController controller) {
		super(parent, style, controller);
	}

	public void pageIndexChanged(int oldPageIndex, int newPageIndex,
			PageableController controller) {
		populateCombo(controller);
	}

	public void totalElementsChanged(long oldTotalElements,
			long newTotalElements, PageableController controller) {

	}

	public void sortChanged(String oldPopertyName, String propertyName,
			int oldSortDirection, int sortDirection,
			PageableController paginationController) {

	}

	public void pageSizeChanged(int oldPageSize, int newPageSize,
			PageableController controller) {
		populateCombo(controller);
	}

	/**
	 * Populate the combo with list of available pages.
	 * 
	 * @param controller
	 */
	private void populateCombo(PageableController controller) {
		int totalPages = controller.getTotalPages();
		String[] items = new String[totalPages];
		for (int i = 0; i < items.length; i++) {
			items[i] = PaginationHelper.getPageText((i + 1), totalPages,
					getLocale());
		}
		pageCombo.setItems(items);
		if (pageCombo.getItemCount() > 0) {
			pageCombo.select(controller.getCurrentPage());
		}
	}

	@Override
	protected void createUI(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		this.setLayout(layout);

		pageCombo = new Combo(parent, SWT.READ_ONLY);
		pageCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		pageCombo.addSelectionListener(this);
	}

	@Override
	public void dispose() {
		pageCombo.removeSelectionListener(this);
		super.dispose();
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void widgetSelected(SelectionEvent e) {
		int newCurrentPage = pageCombo.getSelectionIndex();
		super.setCurrentPage(newCurrentPage);
	}
}
