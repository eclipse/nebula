/*
 * Copyright (c) 2009 Matthew Hall and others.
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
import org.eclipse.swt.graphics.Point;

/**
 * Helper Print for debugging documents which fail to layout. Clients may set
 * breakpoints inside the methods in this class (as well as DebugIterator and
 * DebugPiece), then step into the target object's methods to trace the problem.
 * 
 * @author Matthew Hall
 * @deprecated Reminder to remove references to DebugPrint when you're done
 *             debugging a print job.
 */
public class DebugPrint implements Print {
	private final Print target;

	/**
	 * @param target
	 *            the Print object to debug
	 */
	public DebugPrint(Print target) {
		this.target = target;
	}

	public PrintIterator iterator(Device device, GC gc) {
		PrintIterator iterator = target.iterator(device, gc);
		return new DebugIterator(iterator);
	}
}

class DebugIterator implements PrintIterator {
	private final PrintIterator target;

	DebugIterator(PrintIterator target) {
		this.target = target;
	}

	public PrintIterator copy() {
		return new DebugIterator(target.copy());
	}

	public boolean hasNext() {
		return target.hasNext();
	}

	public Point minimumSize() {
		return target.minimumSize();
	}

	public PrintPiece next(int width, int height) {
		PrintPiece piece = target.next(width, height);
		return piece == null ? null : new DebugPiece(piece);
	}

	public Point preferredSize() {
		return target.preferredSize();
	}
}

class DebugPiece implements PrintPiece {
	private final PrintPiece target;

	DebugPiece(PrintPiece target) {
		this.target = target;
	}

	public void dispose() {
		target.dispose();
	}

	public Point getSize() {
		return target.getSize();
	}

	public void paint(GC gc, int x, int y) {
		target.paint(gc, x, y);
	}
}