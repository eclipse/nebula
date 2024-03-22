/*******************************************************************************
 * Copyright (c) 2019 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
