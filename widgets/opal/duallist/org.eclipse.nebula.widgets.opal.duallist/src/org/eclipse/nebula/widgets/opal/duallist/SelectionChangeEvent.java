/*******************************************************************************
 * Copyright (c) 2014-2021 Laurent CARON
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
package org.eclipse.nebula.widgets.opal.duallist;

import java.util.List;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Event;

/**
 * Instances of this class are sent as a result of
 * DualList being selected.
 *
 * @see SelectionChangeListener
 */
public class SelectionChangeEvent extends TypedEvent {

	private static final long serialVersionUID = -7164363995093867940L;

	/**
	 * The items that were selected.
	 */
	private List<DLItem> items;

	/**
	 * A flag indicating whether the operation should be allowed.
	 * Setting this field to <code>false</code> will cancel the
	 * operation, depending on the widget.
	 */
	public boolean doit;

	/**
	 * Constructs a new instance of this class based on the
	 * information in the given untyped event.
	 *
	 * @param e the untyped event containing the information
	 */
	public SelectionChangeEvent(final Event e) {
		super(e);
	}

	/**
	 * @return the list of items
	 */
	public List<DLItem> getItems() {
		return items;
	}

	/**
	 * @param items the list of items to set
	 */
	public void setItems(final List<DLItem> items) {
		this.items = items;
	}
}
