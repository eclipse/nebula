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

import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.graphics.Color;

/**
 * Abstract base class for frames rendered by the widget
 * @author Fabian Prasser
 *
 * @param <T>
 */
abstract class Frame<T> {

	/** The rendered tiles*/
	protected Tiles<T> tiles;

	/**
	 * Constructor
	 * @param tiles
	 */
	protected Frame(final Tiles<T> tiles){
		this.tiles = tiles;
	}

	/** Disposes the frame */
	protected abstract void dispose();

	/** Returns the background color*/
	protected abstract Color getBackground();

	/** Returns the comparator*/
	protected abstract Comparator<T> getComparator();

	/** Returns a decorator*/
	protected abstract DecoratorColor<T> getDecoratorBackgroundColor();

	/** Returns a decorator*/
	protected abstract DecoratorColor<T> getDecoratorForegroundColor();

	/** Returns a decorator*/
	protected abstract DecoratorString<T> getDecoratorLabel();

	/** Returns a decorator*/
	protected abstract DecoratorColor<T> getDecoratorLineColor();

	/** Returns a decorator*/
	protected abstract DecoratorInteger<T> getDecoratorLineStyle();

	/** Returns a decorator*/
	protected abstract DecoratorInteger<T> getDecoratorLineWidth();

	/** Returns the filter*/
	protected abstract Filter<T> getFilter();

	/** Returns the elements*/
	protected abstract List<T> getItems();

	/** Returns the rendered tiles*/
	protected abstract List<Tile<T>> getTiles();

	/** Updates the frame*/
	protected abstract void update();
}
