/*******************************************************************************
 * Copyright (c) 2019 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.grid;

/**
 * Selection type for the grid object
 */
public enum GridSelectionType {
	/**
	 * Only one row or cell can be selected
	 */
	SINGLE,
	/**
	 * Allow multiple selections
	 */
	MULTI
}
