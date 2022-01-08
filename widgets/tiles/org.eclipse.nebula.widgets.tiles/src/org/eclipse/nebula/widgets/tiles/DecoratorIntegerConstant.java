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
 *
 * A decorator that returns a constant integer
 *
 * @author Fabian Prasser
 *
 * @param <T>
 */
public class DecoratorIntegerConstant<T> extends DecoratorInteger<T> {

	private final int value;

	public DecoratorIntegerConstant(final int value){
		this.value = value;
	}

	@Override
	public Integer decorate(final T t) {
		return value;
	}
}
