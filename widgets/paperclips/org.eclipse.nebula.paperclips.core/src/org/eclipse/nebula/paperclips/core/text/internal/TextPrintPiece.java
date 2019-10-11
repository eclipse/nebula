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
package org.eclipse.nebula.paperclips.core.text.internal;

import org.eclipse.nebula.paperclips.core.PrintPiece;

public interface TextPrintPiece extends PrintPiece {
	/**
	 * Returns the ascent of the first line of text, in pixels.
	 * 
	 * @return the ascent of the first line of text, in pixels.
	 */
	public int getAscent();
}
