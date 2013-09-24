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
package org.eclipse.nebula.paperclips.core.border;

import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.border.internal.BorderIterator;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

/**
 * A decorator that draws a border around the target print.
 * 
 * @author Matthew Hall
 */
public class BorderPrint implements Print {
	final Print target;
	final Border border;

	/**
	 * Constructs a BorderPrint with the given target and border.
	 * 
	 * @param target
	 *            the print to decorate with a border.
	 * @param border
	 *            the border which will be drawn around the target.
	 */
	public BorderPrint(Print target, Border border) {
		Util.notNull(target, border);
		this.target = target;
		this.border = border;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((border == null) ? 0 : border.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BorderPrint other = (BorderPrint) obj;
		if (border == null) {
			if (other.border != null)
				return false;
		} else if (!border.equals(other.border))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	/**
	 * Returns the wrapped print to which the border is being applied.
	 * 
	 * @return the wrapped print to which the border is being applied.
	 */
	public Print getTarget() {
		return target;
	}

	/**
	 * Returns the border being applied to the target.
	 * 
	 * @return the border being applied to the target.
	 */
	public Border getBorder() {
		return border;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new BorderIterator(this, device, gc);
	}
}
