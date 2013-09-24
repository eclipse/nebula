/*
 * Copyright (c) 2005 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.text.internal;

import org.eclipse.nebula.paperclips.core.internal.util.ResourcePool;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.nebula.paperclips.core.text.TextStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class TextPiece implements TextPrintPiece {
	private final Point size;
	private final String[] lines;
	private final TextStyle style;
	private final int ascent;

	private final ResourcePool resources;

	public TextPiece(Device device, TextStyle style, String[] text, Point size,
			int ascent) {
		Util.notNull(device, size, style);
		Util.noNulls(text);
		this.size = size;
		this.lines = text;
		this.style = style;
		this.ascent = ascent;

		this.resources = ResourcePool.forDevice(device);
	}

	public Point getSize() {
		return new Point(size.x, size.y);
	}

	public int getAscent() {
		return ascent;
	}

	public void paint(final GC gc, final int x, final int y) {
		Font oldFont = gc.getFont();
		Color oldForeground = gc.getForeground();
		Color oldBackground = gc.getBackground();

		final int width = getSize().x;
		final int align = style.getAlignment();

		try {
			boolean transparent = initGC(gc);

			FontMetrics fm = gc.getFontMetrics();
			int lineHeight = fm.getHeight();

			boolean strikeout = style.getStrikeout();
			boolean underline = style.getUnderline();
			int lineThickness = Math.max(1, fm.getDescent() / 3);
			int strikeoutOffset = fm.getLeading() + fm.getAscent() / 2;
			int underlineOffset = ascent + lineThickness;

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				int lineWidth = gc.stringExtent(line).x;
				int offset = getHorzAlignmentOffset(align, lineWidth, width);

				gc.drawString(lines[i], x + offset, y + lineHeight * i,
						transparent);
				if (strikeout || underline) {
					Color saveBackground = gc.getBackground();
					gc.setBackground(gc.getForeground());
					if (strikeout)
						gc.fillRectangle(x + offset, y + lineHeight * i
								+ strikeoutOffset, lineWidth, lineThickness);
					if (underline)
						gc.fillRectangle(x + offset, y + lineHeight * i
								+ underlineOffset, lineWidth, lineThickness);
					gc.setBackground(saveBackground);
				}
			}
		} finally {
			restoreGC(gc, oldFont, oldForeground, oldBackground);
		}
	}

	private boolean initGC(final GC gc) {
		initGCFont(gc);
		initGCForeground(gc);
		boolean transparent = initGCBackground(gc);
		return transparent;
	}

	private void restoreGC(final GC gc, Font font, Color foreground,
			Color background) {
		gc.setFont(font);
		gc.setForeground(foreground);
		gc.setBackground(background);
	}

	private int getHorzAlignmentOffset(int align, int lineWidth, int totalWidth) {
		if (align == SWT.CENTER)
			return (totalWidth - lineWidth) / 2;
		else if (align == SWT.RIGHT)
			return totalWidth - lineWidth;
		return 0;
	}

	private boolean initGCBackground(GC gc) {
		Color background = resources.getColor(style.getBackground());
		boolean transparent = (background == null);
		if (!transparent)
			gc.setBackground(background);
		return transparent;
	}

	private void initGCForeground(GC gc) {
		Color foreground = resources.getColor(style.getForeground());
		if (foreground != null)
			gc.setForeground(foreground);
	}

	private void initGCFont(GC gc) {
		Font font = resources.getFont(style.getFontData());
		if (font != null)
			gc.setFont(font);
	}

	public void dispose() {
	} // Shared resources, nothing to dispose.
}