/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.preferencewindow;

import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * Abstract class for "Containers" (row, group and tab)
 * 
 */
public abstract class PWContainer {

	/**
	 * Add a container to the current element
	 * 
	 * @param element element to add
	 * @return the container
	 */
	public abstract PWContainer add(final PWContainer element);

	/**
	 * Add a widget to the current element
	 * 
	 * @param widget widget to add
	 * @return the container
	 */
	public abstract PWContainer add(final PWWidget widget);

	/**
	 * Build the content of the container
	 * 
	 * @param parent parent composite
	 */
	public abstract void build(final Composite parent);

}
