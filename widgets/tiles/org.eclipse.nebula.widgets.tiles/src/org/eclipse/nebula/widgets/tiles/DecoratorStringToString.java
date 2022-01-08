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
 * A decorator that calls toString() on the element wrapped by a tile.
 * @author Fabian Prasser
 *
 * @param <T>
 */
public class DecoratorStringToString<T> extends DecoratorString<T> {

	@Override
	public String decorate(final T t) {
		return t.toString();
	}
}
