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

import org.eclipse.swt.graphics.Color;


/**
 * Decorator that returns a constant color
 *
 * @author Fabian Prasser
 *
 * @param <T>
 */
public class DecoratorColorConstant<T> extends DecoratorColor<T> {

	/** The color*/
	private final Color color;

	/**
	 * Constructor
	 * @param color
	 */
	public DecoratorColorConstant(final Color color){
		this.color = color;
	}

	@Override
	public Color decorate(final T t) {
		return color;
	}
}
