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
package org.eclipse.nebula.paperclips.core;

/**
 * Instances of this class represent the page margins to follow when processing
 * a print job.
 * 
 * @author Matthew Hall
 */
public class Margins {
	/** The top margin. */
	public int top;

	/** The left margin. */
	public int left;

	/** The right margin. */
	public int right;

	/** The bottom margin. */
	public int bottom;

	/**
	 * Constructs a Margins with all sides set to 1" margins.
	 */
	public Margins() {
		this(72);
	}

	/**
	 * Constructs a Margins with all sides set to the argument.
	 * 
	 * @param margins
	 *            the page margins, expressed in points. 72 points = 1".
	 */
	public Margins(int margins) {
		top = left = right = bottom = margins;
	}

	/**
	 * Returns a Margins that is the result of rotating this Margins
	 * counter-clockwise 90 degrees. A job which is rotated 90 degrees (e.g. for
	 * landscape printing) needs to have its margins rotated to match. This is a
	 * convenience method for that purpose.
	 * 
	 * @return a Margins that is the result of rotating this Margins
	 *         counter-clockwise 90 degrees.
	 */
	public Margins rotate() {
		Margins result = new Margins();
		result.top = right;
		result.left = top;
		result.right = bottom;
		result.bottom = left;
		return result;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bottom;
		result = prime * result + left;
		result = prime * result + right;
		result = prime * result + top;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Margins other = (Margins) obj;
		if (bottom != other.bottom)
			return false;
		if (left != other.left)
			return false;
		if (right != other.right)
			return false;
		if (top != other.top)
			return false;
		return true;
	}
}