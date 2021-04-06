/*
 * Copyright (c) 2007 Matthew Hall and others.
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