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
package org.eclipse.nebula.widgets.pagination;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * Abstract class {@link SelectionListener} implementation for {@link Widget}
 * which needs update pagination controller.
 * 
 * @param <T>
 *            pagination controller.
 */
public class AbstractPageControllerSelectionListener<T extends PageableController>
		extends SelectionAdapter {

	private final T controller;

	/**
	 * Constructor with none pagination controller.
	 * 
	 * @param controller
	 */
	public AbstractPageControllerSelectionListener() {
		this(null);
	}

	/**
	 * Constructor with pagination controller.
	 * 
	 * @param controller
	 */
	public AbstractPageControllerSelectionListener(T controller) {
		this.controller = controller;
	}

	/**
	 * Returns the attached pagination controller of the given widget.
	 * 
	 * @param widget
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T getController(Widget widget) {
		if (controller != null) {
			// Controller is defined, return it.
			return controller;
		}
		// Get the pagination controller attached to this widget
		return (T) PaginationHelper.getController(widget);
	}

}
