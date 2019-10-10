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

import org.eclipse.nebula.widgets.opal.preferencewindow.enabler.Enabler;

/**
 * Abstract class for both row and groups
 */
public abstract class PWRowGroup extends PWContainer {

	protected int numberOfColumns;
	protected int parentNumberOfColums;
	protected PWContainer parent;
	protected Enabler enabler;

	/**
	 * Check if the parent is compatible with the object
	 * 
	 * @param parent parent to check
	 * @throws UnsupportedOperationException if the parent is not compatible
	 *             with the object
	 */
	protected abstract void checkParent(PWContainer parent);

	/**
	 * Enables or disables all elements stored in this group or row
	 */
	public abstract void enableOrDisable();

	/**
	 * Add a column to the current element
	 * 
	 * @param number number of column to add
	 */
	public void addColumn(final int number) {
		this.numberOfColumns += number;
	}

	/**
	 * @return the number of columns of this group or row
	 */
	public int getNumberOfColums() {
		return this.numberOfColumns;
	}

	/**
	 * @param enabler the enabler to set
	 */
	public PWRowGroup setEnabler(final Enabler enabler) {
		this.enabler = enabler;
		this.enabler.injectRowGroup(this);
		return this;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(final PWContainer parent) {
		checkParent(parent);
		this.parent = parent;
	}

	/**
	 * @param numberOfColumns the number of columns of the parent
	 */
	public void setParentNumberOfColumns(final int numberOfColumns) {
		this.parentNumberOfColums = numberOfColumns;
	}

}
