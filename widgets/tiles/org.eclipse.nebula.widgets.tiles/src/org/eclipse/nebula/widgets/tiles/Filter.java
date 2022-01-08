/*****************************************************************************
 * Copyright (c) 2014, 2021 Fabian Prasser, Laurent Caron
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Fabian Prasser - Initial API and implementation
 * Laurent Caron <laurent dot caron at gmail dot com> - Integration into the Nebula Project
 *****************************************************************************/

package org.eclipse.nebula.widgets.tiles;

/**
 * Interface for filters. Implement this to filter sets of elements to be displayed by the widget.
 *
 * @author Fabian Prasser
 *
 * @param <T>
 */
public interface Filter<T> {

	/**
	 * Returns whether the element is accepted
	 * @param t
	 * @return
	 */
	public boolean accepts(T t);
}
