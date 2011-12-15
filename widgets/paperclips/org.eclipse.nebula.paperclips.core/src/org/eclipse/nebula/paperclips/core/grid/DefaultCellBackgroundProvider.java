/*
 * Copyright (c) 2006 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.grid;

import org.eclipse.swt.graphics.RGB;

/**
 * Default implementation of the CellBackgroundProvider interface.
 * 
 * @author Matthew Hall
 */
public class DefaultCellBackgroundProvider implements CellBackgroundProvider {
	private final CellBackgroundProvider chain;

	private RGB background;

	/**
	 * Constructs a DefaultGridBackgroundProvider with a null background.
	 */
	public DefaultCellBackgroundProvider() {
		this.chain = null;

		this.background = null;
	}

	/**
	 * Constructs a DefaultGridBackgroundProvider which chains to the argument
	 * if this instance has a null background color. (DefaultGridLook uses this
	 * constructor to cause header and footer background colors to default to
	 * the body background color.)
	 * 
	 * @param chain
	 *            the provider to chain a getCellBackground(...) call to if this
	 *            instance would return null. Ignored if null.
	 */
	public DefaultCellBackgroundProvider(CellBackgroundProvider chain) {
		this.chain = chain;
		this.background = null;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((background == null) ? 0 : background.hashCode());
		result = prime * result + ((chain == null) ? 0 : chain.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultCellBackgroundProvider other = (DefaultCellBackgroundProvider) obj;
		if (background == null) {
			if (other.background != null)
				return false;
		} else if (!background.equals(other.background))
			return false;
		if (chain == null) {
			if (other.chain != null)
				return false;
		} else if (!chain.equals(other.chain))
			return false;
		return true;
	}

	/**
	 * Returns the value in the background property. If the background property
	 * is null, the chained provider will be consulted to obtain a background
	 * color.
	 */
	public RGB getCellBackground(int row, int column, int colspan) {
		RGB result = getBackground();
		if (result == null && chain != null)
			result = chain.getCellBackground(row, column, colspan);
		return result;
	}

	/**
	 * Returns the background color.
	 * 
	 * @return the background color.
	 */
	public RGB getBackground() {
		return background;
	}

	/**
	 * Sets the background color to the argument.
	 * 
	 * @param background
	 *            the new background color.
	 */
	public void setBackground(RGB background) {
		this.background = background;
	}
}
