/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * {@link SelectionListener} implementation used to load data with lazy mode :
 * when an item (TableItem, TreeItem, etc) is selected and must load next page.
 * 
 */
public class LazyItemsSelectionListener extends
		AbstractPageControllerSelectionListener<PageableController> {

	public static final String LAST_ITEM_LOADED = "___LAST_ITEM_LOADED";

	/**
	 * Constructor with none pagination controller.
	 * 
	 * @param controller
	 */
	public LazyItemsSelectionListener() {
		super();
	}

	/**
	 * Constructor with pagination controller.
	 * 
	 * @param controller
	 */
	public LazyItemsSelectionListener(PageableController controller) {
		super(controller);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		// An item is selected
		Widget item = e.item;
		if (item.getData(LAST_ITEM_LOADED) != null) {
			// The selected item must load another page.
			PageableController controller = super.getController(e.widget);
			if (controller.hasNextPage()) {
				// There is next page, increment the current page of the
				// controller
				controller.setCurrentPage(controller.getCurrentPage() + 1);
			}
			// Set as null the LAST_ITEM_LOADED flag to avoid loading data when
			// the item is selected (data is already loaded).
			item.setData(LAST_ITEM_LOADED, null);
		}
	}
}
