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

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * An abstract PrintPiece class.
 * 
 * @author Matthew Hall
 */
public abstract class AbstractPiece implements PrintPiece {
	/**
	 * The device being printed to.
	 */
	protected final Device device;

	private final Point size;

	/**
	 * Constructs an AbstractPiece.
	 * 
	 * @param device
	 *            the device being printed to.
	 * @param gc
	 *            a GC for drawing on the print device.
	 * @param size
	 *            the value to be returned by getSize().
	 */
	protected AbstractPiece(Device device, GC gc, Point size) {
		Util.notNull(device, gc, size);
		this.device = device;
		this.size = size;
	}

	/**
	 * Constructos an AbstractPiece.
	 * 
	 * @param iter
	 *            an AbstractIterator containing references to a Device and GC
	 *            which will be used for printing.
	 * @param size
	 *            the value to be returned by getSize().
	 */
	protected AbstractPiece(AbstractIterator iter, Point size) {
		this(iter.device, iter.gc, size);
	}

	public final Point getSize() {
		return new Point(size.x, size.y);
	}
}
