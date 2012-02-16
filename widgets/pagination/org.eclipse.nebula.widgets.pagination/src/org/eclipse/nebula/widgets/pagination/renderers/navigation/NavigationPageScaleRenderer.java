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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;

/**
 * This SWT {@link Composite} display a SWT {@link Scale} linked to the current
 * page of the pagination controller. When scale moves it update the current
 * page of the pagination controller.
 * 
 */
public class NavigationPageScaleRenderer extends
		AbstractPageControllerComposite implements
		SelectionListener {

	private Scale pageScale;

	public NavigationPageScaleRenderer(Composite parent, int style,
			PageableController controller) {
		super(parent, style, controller);
	}

	public void pageIndexChanged(int oldPageIndex, int newPageIndex,
			PageableController controller) {
		// Page index change, update the scale ranges.
		updateScaleRange(controller);
		// Update the scale selection with the new page index of the controller.
		pageScale.setSelection(newPageIndex);
	}

	public void totalElementsChanged(long oldTotalElements,
			long newTotalElements, PageableController controller) {
		// Do nothing
	}

	public void sortChanged(String oldPopertyName, String propertyName,
			int oldSortDirection, int sortDirection,
			PageableController paginationController) {
		// Do nothing
	}

	public void pageSizeChanged(int oldPageSize, int newPageSize,
			PageableController controller) {
		// Page size change, update the scale ranges.
		updateScaleRange(controller);
	}

	private void updateScaleRange(PageableController controller) {
		int totalPages = controller.getTotalPages();
		pageScale.setMinimum(0);
		if (totalPages > 1) {
			pageScale.setMaximum(totalPages - 1);
		} else {
			pageScale.setMaximum(1);
		}
		pageScale.setPageIncrement(1);
	}

	@Override
	protected void createUI(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		this.setLayout(layout);

		pageScale = new Scale(parent, SWT.READ_ONLY);
		pageScale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		pageScale.addSelectionListener(this);
	}

	@Override
	public void dispose() {
		pageScale.removeSelectionListener(this);
		super.dispose();
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void widgetSelected(SelectionEvent e) {
		int newCurrentPage = pageScale.getSelection();
		super.setCurrentPage(newCurrentPage);
	}
}
