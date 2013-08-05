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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.paperclips.core.CompositeEntry;
import org.eclipse.nebula.paperclips.core.CompositePiece;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.internal.util.PrintSizeStrategy;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.nebula.paperclips.core.text.internal.TextPrintPiece;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A class for printing styled text. Text of varying size and style are aligned
 * along the baseline.
 * 
 * @author Matthew Hall
 */
public class StyledTextPrint implements Print {
	private final List elements = new ArrayList();
	private TextStyle style = new TextStyle();

	/**
	 * Constructs a new StyledTextPrint.
	 */
	public StyledTextPrint() {
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StyledTextPrint other = (StyledTextPrint) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		if (style == null) {
			if (other.style != null)
				return false;
		} else if (!style.equals(other.style))
			return false;
		return true;
	}

	/**
	 * Sets the text style that will be applied to text added through the
	 * {@link #append(String)}
	 * 
	 * @param style
	 *            the new text style.
	 * @return this StyledTextPrint, for chaining method calls.
	 */
	public StyledTextPrint setStyle(TextStyle style) {
		Util.notNull(style);
		this.style = style;
		return this;
	}

	/**
	 * Appends the given text to the end of the document, using the default
	 * style. This method is equivalent to calling append(text, getStyle()).
	 * 
	 * @param text
	 *            the text to append.
	 * @return this StyledTextPrint, for chaining method calls.
	 */
	public StyledTextPrint append(String text) {
		return append(text, style);
	}

	/**
	 * Appends the given text to the end of the document, using the given style.
	 * 
	 * @param text
	 *            the text to append.
	 * @param style
	 *            the text style.
	 * @return this StyledTextPrint, for chaining method calls.
	 */
	public StyledTextPrint append(String text, TextStyle style) {
		TextPrint textPrint = new TextPrint(text, style);
		textPrint.setWordSplitting(false);
		return append(textPrint);
	}

	/**
	 * Appends a line break to the document. If a line break produces a blank
	 * line, that line will take the height of the font in the default text
	 * style.
	 * 
	 * @return this StyledTextPrint, for chaining method calls.
	 */
	public StyledTextPrint newline() {
		return append(new LineBreakPrint(style.getFontData()));
	}

	/**
	 * Appends the given element to the document.
	 * 
	 * @param element
	 *            the element to append.
	 * @return this StyledTextPrint, for chaining method calls.
	 */
	public StyledTextPrint append(Print element) {
		elements.add(element);
		return this;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new StyledTextIterator((Print[]) elements
				.toArray(new Print[elements.size()]), device, gc);
	}
}

class StyledTextIterator implements PrintIterator {
	private final PrintIterator[] elements;
	private final Point minimumSize;
	private final Point preferredSize;

	private int cursor = 0;

	StyledTextIterator(Print[] elements, Device device, GC gc) {
		this.elements = new PrintIterator[elements.length];
		for (int i = 0; i < elements.length; i++)
			this.elements[i] = elements[i].iterator(device, gc);
		minimumSize = computeSize(PrintSizeStrategy.MINIMUM);
		preferredSize = computeSize(PrintSizeStrategy.PREFERRED);
	}

	private StyledTextIterator(StyledTextIterator that) {
		elements = new PrintIterator[that.elements.length - that.cursor];
		minimumSize = that.minimumSize;
		preferredSize = that.preferredSize;
		for (int i = 0; i < elements.length; i++)
			elements[i] = that.elements[that.cursor + i].copy();
	}

	private Point computeSize(PrintSizeStrategy strategy) {
		Point result = new Point(0, 0);
		for (int i = 0; i < elements.length; i++) {
			Point current = strategy.computeSize(elements[i]);
			result.x = Math.max(result.x, current.x);
			result.y = Math.max(result.y, current.y);
		}
		return result;
	}

	public Point minimumSize() {
		return new Point(minimumSize.x, minimumSize.y);
	}

	public Point preferredSize() {
		return new Point(preferredSize.x, preferredSize.y);
	}

	public boolean hasNext() {
		advanceCursor();
		return cursor < elements.length;
	}

	public PrintPiece next(int width, int height) {
		if (width < 0 || height < 0)
			return null;

		int y = 0;

		List rows = new ArrayList();
		while (y < height) {
			PrintPiece row = nextRow(width, height - y);
			if (row == null)
				break;
			rows.add(new CompositeEntry(row, new Point(0, y)));
			y += row.getSize().y;
		}

		if (rows.size() == 0)
			return null;

		return new CompositePiece(rows);
	}

	private PrintPiece nextRow(int width, int height) {
		int x = 0;
		int maxAscent = 0;
		int maxDescent = 0;

		final int backupCursor = cursor;
		final List backup = new ArrayList();

		List rowElements = new ArrayList();
		while (hasNext()) { // hasNext advances cursor internally
			PrintIterator element = elements[cursor];
			Point preferredSize = element.preferredSize();
			if (preferredSize.y > height)
				break;

			PrintIterator elementBackup = element.copy();
			PrintPiece piece = PaperClips.next(element, width - x,
					preferredSize.y);
			if (piece == null)
				break;

			rowElements.add(piece);
			backup.add(elementBackup);

			maxAscent = Math.max(maxAscent, getAscent(piece));
			maxDescent = Math.max(maxDescent, getDescent(piece));
			if (maxAscent + maxDescent > height) {
				restoreBackup(backupCursor, backup);
				return null;
			}

			if (element.hasNext())
				break;

			x += piece.getSize().x;
		}

		return createRowResult(maxAscent, rowElements);
	}

	private PrintPiece createRowResult(int rowAscent, List rowElements) {
		if (rowElements.size() == 0)
			return null;

		List entries = new ArrayList();
		int x = 0;
		for (int i = 0; i < rowElements.size(); i++) {
			PrintPiece piece = (PrintPiece) rowElements.get(i);
			int ascent = getAscent(piece);
			entries.add(new CompositeEntry(piece, new Point(x, rowAscent
					- ascent)));
			x += piece.getSize().x;
		}

		return new CompositePiece(entries);
	}

	private void restoreBackup(final int backupCursor, final List backup) {
		for (int i = 0; i < backup.size(); i++)
			elements[backupCursor + i] = (PrintIterator) backup.get(i);
		cursor = backupCursor;
	}

	private int getAscent(PrintPiece piece) {
		if (piece instanceof TextPrintPiece)
			return ((TextPrintPiece) piece).getAscent();
		return piece.getSize().y;
	}

	private int getDescent(PrintPiece piece) {
		if (piece instanceof TextPrintPiece)
			return piece.getSize().y - ((TextPrintPiece) piece).getAscent();
		return 0;
	}

	private void advanceCursor() {
		while (cursor < elements.length && !elements[cursor].hasNext())
			cursor++;
	}

	public PrintIterator copy() {
		return new StyledTextIterator(this);
	}
}