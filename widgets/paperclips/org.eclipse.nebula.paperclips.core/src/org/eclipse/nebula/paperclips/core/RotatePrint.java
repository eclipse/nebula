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

import org.eclipse.nebula.paperclips.core.internal.RotatePiece;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A decorator print that rotates it's target by increments of 90 degrees.
 * <p>
 * <em>Note</em>: On Windows, this class depends on a bugfix available as of
 * Eclipse build 3.2, release candidate 3 (2006-04-28). Prior to this release,
 * using RotatePrint triggers the bug, causing the document to scale very large
 * on paper. This bug only manifests itself on paper, not with on-screen
 * viewing.
 * <p>
 * RotatePrints are horizontally and vertically greedy. Greedy prints take up
 * all the available space on the page.
 * 
 * @author Matthew Hall
 */
public final class RotatePrint implements Print {
	private final Print target;
	private final int angle;

	/**
	 * Constructs a RotatePrint that rotates it's target 90 degrees
	 * counter-clockwise.
	 * 
	 * @param target
	 *            the print to rotate.
	 */
	public RotatePrint(Print target) {
		this(target, 90);
	}

	/**
	 * Constructs a RotatePrint.
	 * 
	 * @param target
	 *            the print to rotate.
	 * @param angle
	 *            the angle by which the target will be rotated, expressed in
	 *            degrees counter-clockwise. Positive values rotate
	 *            counter-clockwise, and negative values rotate clockwise. Must
	 *            be a multiple of 90.
	 */
	public RotatePrint(Print target, int angle) {
		Util.notNull(target);
		this.target = target;
		this.angle = checkAngle(angle);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + angle;
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
		RotatePrint other = (RotatePrint) obj;
		if (angle != other.angle)
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	/**
	 * Returns the print to be rotated.
	 * 
	 * @return the print to be rotated.
	 */
	public Print getTarget() {
		return target;
	}

	/**
	 * Returns the angle by which the target will be rotated (one of 0, 90, 180,
	 * or 270).
	 * 
	 * @return the angle by which the target will be rotated.
	 */
	public int getAngle() {
		return angle;
	}

	private static int checkAngle(int angle) {
		// Make sure angle is a multiple of 90.
		if (Math.abs(angle) % 90 != 0)
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"Angle must be a multiple of 90 degrees"); //$NON-NLS-1$

		// Bring angle within the range [0, 360)
		if (angle < 0)
			angle = 360 - (-angle % 360);
		if (angle >= 360)
			angle = angle % 360;

		return angle;
	}

	public PrintIterator iterator(Device device, GC gc) {
		if (angle == 0)
			return target.iterator(device, gc);
		return new RotateIterator(target, angle, device, gc);
	}
}

final class RotateIterator implements PrintIterator {
	private final Device device;
	private final PrintIterator target;
	private final int angle;

	private final Point minimumSize;
	private final Point preferredSize;

	RotateIterator(Print target, int angle, Device device, GC gc) {
		Util.notNull(target, device, gc);

		this.device = device;
		this.target = target.iterator(device, gc);
		this.angle = checkAngle(angle); // returns 90, 180, or 270 only

		Point min = this.target.minimumSize();
		Point pref = this.target.preferredSize();

		if (this.angle == 180) {
			this.minimumSize = new Point(min.x, min.y);
			this.preferredSize = new Point(pref.x, pref.y);
		} else { // flip x and y sizes if rotating by 90 or 270 degrees
			this.minimumSize = new Point(min.y, min.x);
			this.preferredSize = new Point(pref.y, pref.x);
		}
	}

	private RotateIterator(RotateIterator that) {
		this.device = that.device;
		this.target = that.target.copy();
		this.angle = that.angle;
		this.minimumSize = that.minimumSize;
		this.preferredSize = that.preferredSize;
	}

	private static int checkAngle(int angle) {
		switch (angle) {
		case 90:
		case 180:
		case 270:
			break;
		default:
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"Angle must be 90, 180, or 270"); //$NON-NLS-1$
		}
		return angle;
	}

	public Point minimumSize() {
		return new Point(minimumSize.x, minimumSize.y);
	}

	public Point preferredSize() {
		return new Point(preferredSize.x, preferredSize.y);
	}

	public boolean hasNext() {
		return target.hasNext();
	}

	public PrintPiece next(int width, int height) {
		PrintPiece target;
		if (angle == 180) // angle may only be init'd to 90, 180, of 270
			target = PaperClips.next(this.target, width, height);
		else
			// flip width and height if rotating by 90 or 270
			target = PaperClips.next(this.target, height, width);

		if (target == null)
			return null;

		return new RotatePiece(device, target, angle, new Point(width, height));
	}

	public PrintIterator copy() {
		return new RotateIterator(this);
	}
}
