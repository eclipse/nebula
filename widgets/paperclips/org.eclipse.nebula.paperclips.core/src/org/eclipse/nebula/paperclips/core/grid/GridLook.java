/*
 * Copyright (c) 2006 Matthew Hall and others.
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
package org.eclipse.nebula.paperclips.core.grid;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

/**
 * A pluggable "look" for a GridPrint.
 * 
 * @author Matthew Hall
 */
public interface GridLook {
	/**
	 * Returns a GridLookPainter for painting the GridLook.
	 * 
	 * @param device
	 *            the device to paint on.
	 * @param gc
	 *            the graphics context for painting.
	 * @return a GridLookPainter for painting the GridLook.
	 */
	public GridLookPainter getPainter(Device device, GC gc);
}