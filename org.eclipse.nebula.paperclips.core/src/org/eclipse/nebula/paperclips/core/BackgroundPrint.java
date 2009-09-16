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

import org.eclipse.nebula.paperclips.core.internal.util.ResourcePool;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A decorator that paints a background color behind it's target.
 * 
 * @author Matthew Hall
 */
public class BackgroundPrint implements Print {
	Print target;
	RGB background;

	/**
	 * Constructs a BackgroundPrint with the given target and background color.
	 * 
	 * @param target
	 *            the
	 * @param background
	 */
	public BackgroundPrint(Print target, RGB background) {
		Util.notNull(target, background);
		this.target = target;
		this.background = background;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((background == null) ? 0 : background.hashCode());
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
		BackgroundPrint other = (BackgroundPrint) obj;
		if (background == null) {
			if (other.background != null)
				return false;
		} else if (!background.equals(other.background))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	/**
	 * Returns the wrapped print to which the background color is being applied.
	 * 
	 * @return the wrapped print to which the background color is being applied.
	 */
	public Print getTarget() {
		return target;
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
	 * Sets the background color.
	 * 
	 * @param background
	 *            the new background color.
	 */
	public void setBackground(RGB background) {
		Util.notNull(background);
		this.background = background;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new BackgroundIterator(this, device, gc);
	}
}

class BackgroundIterator implements PrintIterator {
	private final PrintIterator target;
	private final RGB background;
	private final Device device;

	BackgroundIterator(BackgroundPrint print, Device device, GC gc) {
		Util.notNull(print, device, gc);
		this.device = device;
		this.target = print.target.iterator(device, gc);
		this.background = print.background;
	}

	BackgroundIterator(BackgroundIterator that) {
		this.target = that.target.copy();
		this.background = that.background;
		this.device = that.device;
	}

	public Point minimumSize() {
		return target.minimumSize();
	}

	public Point preferredSize() {
		return target.preferredSize();
	}

	public boolean hasNext() {
		return target.hasNext();
	}

	public PrintPiece next(int width, int height) {
		PrintPiece targetPiece = PaperClips.next(target, width, height);
		if (targetPiece == null)
			return null;
		return new BackgroundPiece(targetPiece, background, device);
	}

	public PrintIterator copy() {
		return new BackgroundIterator(this);
	}
}

class BackgroundPiece implements PrintPiece {
	private final PrintPiece target;
	private final Device device;
	private final RGB background;

	BackgroundPiece(PrintPiece target, RGB background, Device device) {
		Util.notNull(target, background, device);
		this.target = target;
		this.device = device;
		this.background = background;
	}

	public Point getSize() {
		return target.getSize();
	}

	public void paint(GC gc, int x, int y) {
		paintBackground(gc, x, y);
		target.paint(gc, x, y);
	}

	private void paintBackground(GC gc, int x, int y) {
		Color oldBackground = gc.getBackground();

		gc.setBackground(ResourcePool.forDevice(device).getColor(background));
		Point size = getSize();
		gc.fillRectangle(x, y, size.x, size.y);

		gc.setBackground(oldBackground);
	}

	public void dispose() {
		target.dispose();
	}
}