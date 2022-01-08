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
 * A decorator that uses a color gradient
 * @author Fabian Prasser
 *
 * @param <T>
 */
public abstract class DecoratorColorGradient<T> extends DecoratorColor<T>{

	/** The gradient*/
	protected final Gradient gradient;

	/**
	 * Creates a new instance
	 * @param tiles
	 */
	public DecoratorColorGradient(final Gradient gradient){
		this.gradient = gradient;
		addDecoratorListener(() -> gradient.dispose());
	}

	@Override
	public Color decorate(final T element){
		return gradient.getColor(getValue(element));
	}

	/**
	 * Returns a value in [0, 1]
	 * @param value
	 * @return
	 */
	protected abstract double getValue(T element);
}
