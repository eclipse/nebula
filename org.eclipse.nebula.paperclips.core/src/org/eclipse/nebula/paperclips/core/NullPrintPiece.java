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

package org.eclipse.nebula.paperclips.core;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

final class NullPrintPiece implements PrintPiece {
	public Point getSize() {
		return new Point(0, 0);
	}

	public void paint(GC gc, int x, int y) {
	}

	public void dispose() {
	}
}