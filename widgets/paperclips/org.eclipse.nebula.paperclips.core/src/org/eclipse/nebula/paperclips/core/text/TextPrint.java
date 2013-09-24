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
package org.eclipse.nebula.paperclips.core.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.internal.util.ResourcePool;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.nebula.paperclips.core.text.internal.TextPiece;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A Print for displaying text.
 * <p>
 * TextPrints are never greedy with layout space, even with center- or
 * right-alignment. (Greedy prints take up all the available space on the page.)
 * Therefore, when center- or right-alignment is required, it is necessary to
 * wrap the text in a Print which will enforce the same alignment. Usually this
 * is a center:default:grow or right:default:grow column in a GridPrint.
 * 
 * @author Matthew Hall
 */
public class TextPrint implements Print {
	/** The default text for a TextPrint. Value is "". */
	public static final String DEFAULT_TEXT = ""; //$NON-NLS-1$

	/** The default font data for a TextPrint. Value is device-dependent. */
	public static final FontData DEFAULT_FONT_DATA = new FontData();

	/** The default alignment for TextPrint. Value is SWT.LEFT. */
	public static final int DEFAULT_ALIGN = SWT.LEFT;

	private static final TextStyle DEFAULT_STYLE = new TextStyle();

	String text;
	TextStyle style;
	boolean wordSplitting;

	/**
	 * Constructs a TextPrint with the default properties.
	 */
	public TextPrint() {
		this(DEFAULT_TEXT);
	}

	/**
	 * Constructs a TextPrint with the given text.
	 * 
	 * @param text
	 *            the text to print.
	 */
	public TextPrint(String text) {
		this(text, DEFAULT_STYLE);
	}

	/**
	 * Constructs a TextPrint with the given text and font data.
	 * 
	 * @param text
	 *            the text to print.
	 * @param fontData
	 *            the font that will be used to print the text.
	 */
	public TextPrint(String text, FontData fontData) {
		this(text, DEFAULT_STYLE.font(fontData));
	}

	/**
	 * Constructs a TextPrint with the give text and alignment.
	 * 
	 * @param text
	 *            the text to print.
	 * @param align
	 *            the horizontal text alignment. Must be one of {@link SWT#LEFT}
	 *            , {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 */
	public TextPrint(String text, int align) {
		this(text, DEFAULT_STYLE.align(align));
	}

	/**
	 * Constructs a TextPrint with the given text, font data, and alignment.
	 * 
	 * @param text
	 *            the text to print.
	 * @param fontData
	 *            the font that will be used to print the text.
	 * @param align
	 *            the horizontal text alignment. Must be one of {@link SWT#LEFT}
	 *            , {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 */
	public TextPrint(String text, FontData fontData, int align) {
		this(text, DEFAULT_STYLE.font(fontData).align(align));
	}

	/**
	 * Constructs a TextPrint with the given text and style.
	 * 
	 * @param text
	 *            the text to print.
	 * @param style
	 *            the style to apply to the text.
	 */
	public TextPrint(String text, TextStyle style) {
		Util.notNull(text, style);
		this.text = text;
		this.style = style;
		this.wordSplitting = true;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + (wordSplitting ? 1231 : 1237);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextPrint other = (TextPrint) obj;
		if (style == null) {
			if (other.style != null)
				return false;
		} else if (!style.equals(other.style))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (wordSplitting != other.wordSplitting)
			return false;
		return true;
	}

	/**
	 * Returns the text that will be printed.
	 * 
	 * @return the text that will be printed.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text that will be printed.
	 * 
	 * @param text
	 *            the text to print.
	 */
	public void setText(String text) {
		Util.notNull(text);
		this.text = text;
	}

	/**
	 * Returns the text style.
	 * 
	 * @return the text style.
	 */
	public TextStyle getStyle() {
		return style;
	}

	/**
	 * Sets the text style to the argument.
	 * 
	 * @param style
	 *            the new text style.
	 */
	public void setStyle(TextStyle style) {
		Util.notNull(style);
		this.style = style;
	}

	/**
	 * Returns the font that will be used to print the text.
	 * 
	 * @return the font that will be used to print the text.
	 */
	public FontData getFontData() {
		return style.getFontData();
	}

	/**
	 * Sets the font that will be used to print the text.
	 * 
	 * @param fontData
	 *            the font that will be used to print the text.
	 */
	public void setFontData(FontData fontData) {
		setStyle(style.font(fontData));
	}

	/**
	 * Returns the horizontal text alignment. Possible values include
	 * {@link SWT#LEFT}, {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 * 
	 * @return the horizontal text alignment.
	 */
	public int getAlignment() {
		return style.getAlignment();
	}

	/**
	 * Sets the horizontal text alignment.
	 * 
	 * @param alignment
	 *            the horizontal text alignment. Must be one of {@link SWT#LEFT}
	 *            , {@link SWT#CENTER} or {@link SWT#RIGHT}.
	 */
	public void setAlignment(int alignment) {
		setStyle(style.align(alignment));
	}

	/**
	 * Returns the foreground color. A null value indicates that the foreground
	 * color is inherited.
	 * 
	 * @return the foreground color.
	 */
	public RGB getForeground() {
		return style.getForeground();
	}

	/**
	 * Sets the foreground color to the argument.
	 * 
	 * @param foreground
	 *            the new foreground color. A null value causes the foreground
	 *            color to be inherited.
	 */
	public void setForeground(RGB foreground) {
		setStyle(style.foreground(foreground));
	}

	/**
	 * Returns the background color. A null value indicates that the background
	 * is transparent.
	 * 
	 * @return the background color.
	 */
	public RGB getBackground() {
		return style.getBackground();
	}

	/**
	 * Sets the background color to the argument.
	 * 
	 * @param background
	 *            the new background color. A null value causes the background
	 *            to be transparent.
	 */
	public void setBackground(RGB background) {
		style = style.background(background);
	}

	/**
	 * Returns the underline flag.
	 * 
	 * @return the underline flag.
	 */
	public boolean getUnderline() {
		return style.getUnderline();
	}

	/**
	 * Sets the underline flag to the argument.
	 * 
	 * @param underline
	 *            the underline flag.
	 */
	public void setUnderline(boolean underline) {
		style = style.underline(underline);
	}

	/**
	 * Returns the strikout flag.
	 * 
	 * @return the strikout flag.
	 */
	public boolean getStrikeout() {
		return style.getStrikeout();
	}

	/**
	 * Sets the strikeout flag to the argument.
	 * 
	 * @param strikeout
	 *            the strikeout flag.
	 */
	public void setStrikeout(boolean strikeout) {
		style = style.strikeout(strikeout);
	}

	/**
	 * Returns whether word splitting is enabled. Default is true.
	 * 
	 * @return whether word splitting is enabled.
	 */
	public boolean getWordSplitting() {
		return wordSplitting;
	}

	/**
	 * Sets whether word splitting is enabled.
	 * 
	 * @param wordBreaking
	 *            whether to allow word splitting.
	 */
	public void setWordSplitting(boolean wordBreaking) {
		this.wordSplitting = wordBreaking;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new TextIterator(this, device, gc);
	}
}

class TextIterator implements PrintIterator {
	private final Device device;
	private final GC gc;

	final String text;
	final String[] lines;
	final TextStyle style;
	final boolean wordSplitting;
	final Point minimumSize;
	final Point preferredSize;

	int row;
	int col;

	TextIterator(TextPrint print, Device device, GC gc) {
		this.device = device;
		this.gc = gc;

		this.text = print.text;
		this.lines = print.text.split("(\r)?\n"); //$NON-NLS-1$
		this.style = print.style;
		this.wordSplitting = print.wordSplitting;
		this.minimumSize = maxExtent(text.split("\\s")); //$NON-NLS-1$
		this.preferredSize = maxExtent(lines);

		this.row = 0;
		this.col = 0;
	}

	TextIterator(TextIterator that) {
		this.device = that.device;
		this.gc = that.gc;

		this.text = that.text;
		this.lines = that.lines;
		this.style = that.style;
		this.wordSplitting = that.wordSplitting;
		this.minimumSize = that.minimumSize;
		this.preferredSize = that.preferredSize;

		this.row = that.row;
		this.col = that.col;
	}

	public boolean hasNext() {
		return row < lines.length;
	}

	public PrintPiece next(int width, int height) {
		if (!hasNext())
			PaperClips.error("No more content."); //$NON-NLS-1$

		Font oldFont = initGC();
		PrintPiece result = internalNext(width, height);
		restoreGC(oldFont);

		return result;
	}

	private PrintPiece internalNext(int width, int height) {
		FontMetrics fm = gc.getFontMetrics();

		final int lineHeight = fm.getHeight();
		if (height < lineHeight)
			return null;

		final int maxLines = height / lineHeight;
		String[] nextLines = nextLines(width, maxLines);
		if (nextLines.length == 0)
			return null;

		int maxWidth = maxExtent(nextLines).x;
		Point size = new Point(maxWidth, nextLines.length * lineHeight);
		int ascent = fm.getAscent() + fm.getLeading();

		return new TextPiece(device, style, nextLines, size, ascent);
	}

	private Font initGC() {
		Font oldFont = gc.getFont();
		FontData fontData = style.getFontData();
		if (fontData != null)
			gc.setFont(ResourcePool.forDevice(device).getFont(fontData));
		return oldFont;
	}

	private void restoreGC(Font oldFont) {
		gc.setFont(oldFont);
	}

	private String[] nextLines(final int width, final int maxLines) {
		List nextLines = new ArrayList(Math.min(lines.length, maxLines));

		while ((nextLines.size() < maxLines) && (row < lines.length)) {
			String line = lines[row].substring(col);

			// Find out how much text will fit on one line.
			int charCount = findLineBreak(gc, line, width);

			// If none of the text could fit in the current line, terminate this
			// iteration.
			if (line.length() > 0 && charCount == 0)
				break;

			// Get the text that fits on this line.
			String thisLine = line.substring(0, charCount);
			nextLines.add(thisLine);

			// Move cursor past the text we just consumed.
			col += charCount;

			skipWhitespace();

			advanceToNextRowIfCurrentRowCompleted();
		}

		return (String[]) nextLines.toArray(new String[nextLines.size()]);
	}

	private void skipWhitespace() {
		while (col < lines[row].length()
				&& Character.isWhitespace(lines[row].charAt(col)))
			col++;
	}

	private void advanceToNextRowIfCurrentRowCompleted() {
		if (col >= lines[row].length()) {
			row++;
			col = 0;
		}
	}

	public Point minimumSize() {
		return new Point(minimumSize.x, minimumSize.y);
	}

	public Point preferredSize() {
		return new Point(preferredSize.x, preferredSize.y);
	}

	private Point maxExtent(String[] text) {
		Font oldFont = gc.getFont();
		try {
			initGC();

			FontMetrics fm = gc.getFontMetrics();
			int maxWidth = 0;

			for (int i = 0; i < text.length; i++) {
				String textPiece = text[i];
				maxWidth = Math.max(maxWidth, gc.stringExtent(textPiece).x);
			}

			return new Point(maxWidth, fm.getHeight());
		} finally {
			restoreGC(oldFont);
		}
	}

	private int findLineBreak(GC gc, String text, int width) {
		// Offsets within the string
		int loIndex = 0;
		int hiIndex = text.length();

		// Pixel width of entire string
		int pixelWidth = gc.stringExtent(text).x;

		// Does the whole string fit?
		if (pixelWidth <= width)
			// I'll take it
			return hiIndex;

		// Do a binary search to find the maximum characters that will fit
		// within the given width.
		while (loIndex < hiIndex) {
			int midIndex = (loIndex + hiIndex + 1) / 2;
			int midWidth = gc.stringExtent(text.substring(0, midIndex)).x;

			if (midWidth < width)
				// don't add 1, the next character could make it too big
				loIndex = midIndex;
			else if (midWidth > width)
				// subtract 1, we already know midIndex makes it too big
				hiIndex = midIndex - 1;
			else {
				// perfect fit
				loIndex = hiIndex = midIndex;
			}
		}

		return findWordBreak(text, loIndex);
	}

	int findWordBreak(String text, int maxLength) {
		// If the max length is the string length, no break
		// (we mainly check this to avoid an exception in for-loop)
		if (maxLength == text.length())
			return maxLength;

		// Otherwise, break string at the last whitespace at or before
		// maxLength.
		for (int i = maxLength; i >= 0; i--)
			if (Character.isWhitespace(text.charAt(i)))
				return i;

		// No whitespace? Break at max length (if word breaking is allowed)
		if (wordSplitting)
			return maxLength;

		return 0;
	}

	public PrintIterator copy() {
		return new TextIterator(this);
	}
}