/*
 * Copyright (c) 2005 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.internal.util;

import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.swt.graphics.Point;

/**
 * The static instance members of this class aid in the calculation of prints
 * and help abstract out the minimum/preferred size concepts to simplify
 * algorithms.
 * 
 * @author Matthew Hall
 */
public abstract class PrintSizeStrategy {
	/** Compute the minimum size */
	public static final PrintSizeStrategy MINIMUM = new PrintSizeStrategy() {
		public Point computeSize(PrintIterator iter) {
			return iter.minimumSize();
		}
	};

	/** Compute the preferred size. */
	public static final PrintSizeStrategy PREFERRED = new PrintSizeStrategy() {
		public Point computeSize(PrintIterator iter) {
			return iter.preferredSize();
		}
	};

	private PrintSizeStrategy() {
	}

	/**
	 * Computes the size of the PrintIterator.
	 * 
	 * @param print
	 *            the iterator
	 * @return the computed size of the PrintIterator.
	 */
	public abstract Point computeSize(PrintIterator print);
}