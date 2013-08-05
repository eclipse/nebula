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
