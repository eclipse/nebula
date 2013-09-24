/*
 * Copyright (c) 2007 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */

package org.eclipse.nebula.paperclips.core.internal;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;

public final class RotatePiece implements PrintPiece {
	private final Device device;
	private final PrintPiece target;
	private final int angle;
	private final Point size;

	private Transform oldTransform;
	private Transform transform;

	public RotatePiece(Device device, PrintPiece target, int angle, Point size) {
		Util.notNull(device, target, size);
		this.device = device;
		this.target = target;
		this.angle = angle;
		this.size = size;
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
		rotateTransform(transform);
		gc.setTransform(transform);

		target.paint(gc, 0, 0);

		gc.setTransform(oldTransform);
	}

	private void rotateTransform(Transform transform) {
		switch (angle) {
		case 90:
			transform.translate(0, size.y);
			break;
		case 180:
			transform.translate(size.x, size.y);
			break;
		case 270:
			transform.translate(size.x, 0);
			break;
		default:
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"Rotation angle must be 90, 180 or 270."); //$NON-NLS-1$
		}
		transform.rotate(-angle); // reverse the angle since Transform.rotate
		// goes clockwise
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