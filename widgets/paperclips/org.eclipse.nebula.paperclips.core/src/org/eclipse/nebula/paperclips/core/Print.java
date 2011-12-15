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
package org.eclipse.nebula.paperclips.core;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

/**
 * Interface for printable elements.
 * 
 * @author Matthew Hall
 */
public interface Print {
	/**
	 * Returns a PrintIterator for laying out the contents of this Print. The
	 * iterator uses a snapshot of the print at the time this method is invoked,
	 * so subsequent changes to the Print will not affect the output of the
	 * iterator.
	 * 
	 * @param device
	 *            the graphics device this Print will be drawn onto.
	 * @param gc
	 *            the graphics context to be used for calculating layout and
	 *            drawing the Print's contents.
	 * @return a PrintIterator for laying out the contents of this Print.
	 */
	public PrintIterator iterator(Device device, GC gc);
}