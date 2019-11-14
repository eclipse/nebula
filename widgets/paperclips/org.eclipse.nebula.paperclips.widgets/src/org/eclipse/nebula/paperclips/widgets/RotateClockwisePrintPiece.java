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
package org.eclipse.nebula.paperclips.widgets;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;

class RotateClockwisePrintPiece implements PrintPiece {
	private final Device device;
	private final PrintPiece target;
	private final Point size;

	RotateClockwisePrintPiece(Device device, PrintPiece target) {
		if (device == null || target == null)
			PaperClips.error(SWT.ERROR_NULL_ARGUMENT);
		this.device = device;
		this.target = target;
		Point targetSize = target.getSize();
		this.size = new Point(targetSize.y, targetSize.x);
	}

	public void dispose() {
		target.dispose();
	}

	public Point getSize() {
		return new Point(size.x, size.y);
	}

	public void paint(GC gc, int x, int y) {
		Transform oldTransform = null;
		Transform newTransform = null;
		try {
			oldTransform = new Transform(device);
			gc.getTransform(oldTransform);

			newTransform = new Transform(device);
			gc.getTransform(newTransform);
			newTransform.translate(x, y);
			newTransform.translate(size.x, 0);
			newTransform.rotate(90);
			gc.setTransform(newTransform);

			target.paint(gc, 0, 0);

			gc.setTransform(oldTransform);
		} finally {
			if (oldTransform != null)
				oldTransform.dispose();
			if (newTransform != null)
				newTransform.dispose();
		}
	}
}