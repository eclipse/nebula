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

/**
 * An abstract PrintIterator class which maintains references to the device and
 * gc arguments passed to {@link Print#iterator(Device, GC) }.
 * 
 * @author Matthew Hall
 */
public abstract class AbstractIterator implements PrintIterator {
	/**
	 * The device being printed to.
	 */
	protected final Device device;

	/**
	 * A GC used for measuring document elements.
	 */
	protected final GC gc;

	/**
	 * Constructs an AbstractIterator with the given Device and GC.
	 * 
	 * @param device
	 *            the device being printed to.
	 * @param gc
	 *            a GC used for drawing on the print device.
	 */
	protected AbstractIterator(Device device, GC gc) {
		Util.notNull(device, gc);
		this.device = device;
		this.gc = gc;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param that
	 *            the AbstractIterator being copied.
	 */
	protected AbstractIterator(AbstractIterator that) {
		this.device = that.device;
		this.gc = that.gc;
	}
}