/*
 * Copyright (c) 2005 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.border;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

/**
 * Interface for drawing borders, used by BorderPaint and GridPrint for drawing
 * borders a child print and grid cells, respectively.
 * 
 * @author Matthew Hall
 */
public interface Border {
	/**
	 * Creates a BorderPainter which uses the given Device and GC.
	 * 
	 * @param device
	 *            the print device.
	 * @param gc
	 *            a GC for drawing to the print device.
	 * @return a BorderPainter for painting the border on the given Device and
	 *         GC.
	 */
	public BorderPainter createPainter(Device device, GC gc);
}
