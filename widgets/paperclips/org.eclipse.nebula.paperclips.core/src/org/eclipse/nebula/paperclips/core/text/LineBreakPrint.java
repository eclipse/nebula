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
package org.eclipse.nebula.paperclips.core.text;

import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.internal.piece.EmptyPiece;
import org.eclipse.nebula.paperclips.core.internal.util.ResourcePool;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A class for adding line breaks corresponding to a particular font size.
 * Currently this class is used internally by StyledTextPrint to implement the
 * newline() feature.
 * 
 * @author Matthew Hall
 */
public class LineBreakPrint implements Print {
	final FontData font;

	/**
	 * Constructs a new LineBreakPrint on the given font.
	 * 
	 * @param font
	 *            the font which determines the height of the line break.
	 */
	public LineBreakPrint(FontData font) {
		Util.notNull(font);
		this.font = font;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((font == null) ? 0 : font.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LineBreakPrint other = (LineBreakPrint) obj;
		if (font == null) {
			if (other.font != null)
				return false;
		} else if (!font.equals(other.font))
			return false;
		return true;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new LineBreakIterator(this, device, gc);
	}
}

class LineBreakIterator implements PrintIterator {
	private static final int MIN_HEIGHT = 0;
	private static final int MIN_WIDTH = 1;

	private final int lineHeight;
	private boolean hasNext = true;

	LineBreakIterator(LineBreakPrint print, Device device, GC gc) {
		this(calculateLineHeight(print, device, gc));
	}

	private LineBreakIterator(int lineHeight) {
		this.lineHeight = lineHeight;
	}

	private static int calculateLineHeight(LineBreakPrint print, Device device,
			GC gc) {
		Font oldFont = gc.getFont();

		gc.setFont(ResourcePool.forDevice(device).getFont(print.font));
		int result = gc.getFontMetrics().getHeight();

		gc.setFont(oldFont);

		return result;
	}

	public Point minimumSize() {
		return new Point(MIN_WIDTH, MIN_HEIGHT);
	}

	public Point preferredSize() {
		return new Point(MIN_WIDTH, lineHeight);
	}

	public boolean hasNext() {
		return hasNext;
	}

	public PrintPiece next(int width, int height) {
		if (width < MIN_WIDTH || height < MIN_HEIGHT)
			return null;

		hasNext = false;
		return new EmptyPiece(new Point(width, Math.min(height, lineHeight)));
	}

	public PrintIterator copy() {
		return hasNext ? new LineBreakIterator(lineHeight) : this;
	}
}