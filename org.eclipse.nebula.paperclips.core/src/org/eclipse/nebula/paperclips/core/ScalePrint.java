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

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;

/**
 * A decorator print that scales it's target larger or smaller.
 * <p>
 * <em>Note</em>: On Windows, this class depends on a bugfix available as of
 * Eclipse build 3.2, release candidate 3 (2006-04-28). Prior to this release,
 * using ScalePrint triggers the bug, causing the document to scale very large
 * on paper. This bug manifests itself only on paper, not with on-screen
 * viewing.
 * 
 * @author Matthew Hall
 */
public class ScalePrint implements Print {
	final Print target;
	final Double scale;

	/**
	 * Constructs a ScalePrint which scales down it's target to print at it's
	 * preferred size. This constructor is equivalent to calling new
	 * ScalePrint(target, null).
	 * 
	 * @param target
	 *            the print to scale down.
	 */
	public ScalePrint(Print target) {
		this(target, null);
	}

	/**
	 * Constructs a ScalePrint which scales it's target by the given factor.
	 * 
	 * @param target
	 * @param scale
	 *            the scale factor (must be >0). A value of 2.0 draws at double
	 *            the size, and a value of 0.5 draws at half the size. A null
	 *            value automatically scales down so the target is rendered at
	 *            it's preferred size.
	 */
	public ScalePrint(Print target, Double scale) {
		Util.notNull(target);
		if (scale != null && !(scale.doubleValue() > 0))
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"Scale " + scale + " must be > 0"); //$NON-NLS-1$ //$NON-NLS-2$

		this.target = target;
		this.scale = scale;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scale == null) ? 0 : scale.hashCode());
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
		ScalePrint other = (ScalePrint) obj;
		if (scale == null) {
			if (other.scale != null)
				return false;
		} else if (!scale.equals(other.scale))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	/**
	 * Returns the print being scaled.
	 * 
	 * @return the print being scaled.
	 */
	public Print getTarget() {
		return target;
	}

	/**
	 * Returns the scale by which the target will be scaled, or null (indicating
	 * automatic scale down to fit).
	 * 
	 * @return the scale by which the target will be scaled, or null (indicating
	 *         automatic scale down to fit).
	 */
	public Double getScale() {
		return scale;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new ScaleIterator(this, device, gc);
	}
}

class ScaleIterator implements PrintIterator {
	private final Device device;
	private final PrintIterator target;
	private final Double scale;

	private final Point minimumSize;
	private final Point preferredSize;

	ScaleIterator(ScalePrint print, Device device, GC gc) {
		Util.notNull(print, device, gc);

		this.device = device;
		this.target = print.target.iterator(device, gc);
		this.scale = print.scale;

		Point min = target.minimumSize();
		Point pref = target.preferredSize();
		if (scale == null) { // auto-scale
			minimumSize = new Point(1, 1);
			preferredSize = pref;
		} else { // specific scale
			double s = scale.doubleValue();
			minimumSize = new Point((int) Math.ceil(min.x * s), (int) Math
					.ceil(min.y * s));
			preferredSize = new Point((int) Math.ceil(pref.x * s), (int) Math
					.ceil(pref.y * s));
		}
	}

	private ScaleIterator(ScaleIterator that) {
		this.device = that.device;
		this.target = that.target.copy();
		this.scale = that.scale;

		this.minimumSize = that.minimumSize;
		this.preferredSize = that.preferredSize;
	}

	public Point minimumSize() {
		return minimumSize;
	}

	public Point preferredSize() {
		return preferredSize;
	}

	public boolean hasNext() {
		return target.hasNext();
	}

	public PrintPiece next(int width, int height) {
		// Find out what scale we're going to iterate at.
		double scale;
		Point pref = target.preferredSize();
		if (this.scale == null)
			scale = Math.min(Math.min((double) width / (double) pref.x,
					(double) height / (double) pref.y), 1.0);
		else
			scale = this.scale.doubleValue();

		// Calculate the width and height to be passed to the target.
		final int scaledWidth = (int) Math.ceil(width / scale);
		final int scaledHeight = (int) Math.ceil(height / scale);

		PrintPiece target = PaperClips.next(this.target, scaledWidth,
				scaledHeight);

		if (target == null)
			return null;

		return new ScalePiece(device, target, scale, width, height);
	}

	public PrintIterator copy() {
		return new ScaleIterator(this);
	}
}

final class ScalePiece implements PrintPiece {
	private final Device device;
	private final PrintPiece target;
	private final double scale;
	private final Point size;

	private Transform oldTransform;
	private Transform transform;

	ScalePiece(Device device, PrintPiece target, double scale, int maxWidth,
			int maxHeight) {
		Util.notNull(device, target);
		this.device = device;
		this.target = target;
		this.scale = scale;
		Point targetSize = target.getSize();
		this.size = new Point(Math.min((int) Math.ceil(targetSize.x * scale),
				maxWidth), Math.min((int) Math.ceil(targetSize.y * scale),
				maxHeight));
	}

	public Point getSize() {
		return new Point(size.x, size.y);
	}

	private Transform getOldTransform() {
		if (oldTransform == null)
			oldTransform = new Transform(device);
		return oldTransform;
	}

	private Transform getTransform() {
		if (transform == null)
			transform = new Transform(device);
		return transform;
	}

	public void paint(GC gc, int x, int y) {
		Transform oldTransform = getOldTransform();
		gc.getTransform(oldTransform);

		Transform transform = getTransform();
		gc.getTransform(transform);
		transform.translate(x, y);
		transform.scale((float) scale, (float) scale);
		gc.setTransform(transform);

		target.paint(gc, 0, 0);

		gc.setTransform(oldTransform);
	}

	public void dispose() {
		if (oldTransform != null) {
			oldTransform.dispose();
			oldTransform = null;
		}
		if (transform != null) {
			transform.dispose();
			transform = null;
		}
		target.dispose();
	}
}