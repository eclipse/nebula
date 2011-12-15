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
package org.eclipse.nebula.paperclips.core.page;

import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.internal.util.PaperClipsUtil;
import org.eclipse.nebula.paperclips.core.internal.util.ResourcePool;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.nebula.paperclips.core.text.TextStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * Displays the page number and page count within the context of a
 * {@link PagePrint}. To properly display page numbers, instances of this class
 * should be created using the {@link PageNumber} argument which is passed to
 * the {@link PageDecoration#createPrint(PageNumber)} method by PagePrint.
 * <p>
 * PageNumberPrints are never greedy with layout space, even with center- or
 * right-alignment. (Greedy prints take up all the available space on the page.)
 * Therefore, when center- or right-alignment is required, it is necessary to
 * wrap the page number in a Print which will enforce the same alignment.
 * Usually this is a center:default:grow or right:default:grow column in a
 * GridPrint.
 * 
 * @author Matthew Hall
 * @see PagePrint
 * @see PageDecoration
 * @see PageNumber
 * @see PageNumberFormat
 * @see DefaultPageNumberFormat
 */
public class PageNumberPrint implements Print {
	/** The default font data for a PageNumberPrint. Value is device-dependent. */
	public static final FontData DEFAULT_FONT_DATA = new FontData();

	/** The default alignment for a PageNumberPrint. Value is SWT.LEFT. */
	public static final int DEFAULT_ALIGN = SWT.LEFT;

	/** The default text style. Value is device-dependent. */
	public static final TextStyle DEFAULT_TEXT_STYLE = new TextStyle().font(
			DEFAULT_FONT_DATA).align(DEFAULT_ALIGN);

	PageNumber pageNumber;
	TextStyle textStyle;
	PageNumberFormat format;

	/**
	 * Constructs a PageNumberPrint for the given page number.
	 * 
	 * @param pageNumber
	 *            the page number of the page this Print will appear on.
	 */
	public PageNumberPrint(PageNumber pageNumber) {
		this(pageNumber, DEFAULT_TEXT_STYLE);
	}

	/**
	 * Constructs a PageNumberPrint for the given page number and font.
	 * 
	 * @param pageNumber
	 *            the page number of the page this Print will appear on.
	 * @param fontData
	 *            the font that this Print will appear in.
	 */
	public PageNumberPrint(PageNumber pageNumber, FontData fontData) {
		this(pageNumber, DEFAULT_TEXT_STYLE.font(fontData));
	}

	/**
	 * Constructs a PageNumberPrint for the given page number and alignment.
	 * 
	 * @param pageNumber
	 *            the page number of the page this Print will appear on.
	 * @param align
	 *            the horizontal alignment of the text.
	 */
	public PageNumberPrint(PageNumber pageNumber, int align) {
		this(pageNumber, DEFAULT_TEXT_STYLE.align(align));
	}

	/**
	 * Constructs a PageNumberPrint for the given page number, font and
	 * alignment.
	 * 
	 * @param pageNumber
	 *            the page number of the page this Print will appear on.
	 * @param fontData
	 *            the font that this Print will appear in.
	 * @param align
	 *            the horizontal alignment of the text.
	 */
	public PageNumberPrint(PageNumber pageNumber, FontData fontData, int align) {
		this(pageNumber, DEFAULT_TEXT_STYLE.font(fontData).align(align));
	}

	/**
	 * Constructs a PageNumberPrint for the given page number and text style.
	 * 
	 * @param pageNumber
	 *            the page number of the page this Print will appear on.
	 * @param textStyle
	 *            the text style that this Print will appear in.
	 */
	public PageNumberPrint(PageNumber pageNumber, TextStyle textStyle) {
		setPageNumber(pageNumber);
		setTextStyle(textStyle);
		setPageNumberFormat(new DefaultPageNumberFormat());
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pageNumber == null) ? 0 : pageNumber.hashCode());
		result = prime * result
				+ ((textStyle == null) ? 0 : textStyle.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageNumberPrint other = (PageNumberPrint) obj;

		if (pageNumber == null) {
			if (other.pageNumber != null)
				return false;
		} else if (!pageNumber.equals(other.pageNumber))
			return false;

		if (textStyle == null) {
			if (other.textStyle != null)
				return false;
		} else if (!textStyle.equals(other.textStyle))
			return false;

		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;

		return true;
	}

	/**
	 * Sets the page number to the argument.
	 * 
	 * @param pageNumber
	 *            the new page number.
	 */
	public void setPageNumber(PageNumber pageNumber) {
		Util.notNull(pageNumber);
		this.pageNumber = pageNumber;
	}

	/**
	 * Returns the page number of this Print.
	 * 
	 * @return the page number of this Print.
	 */
	public PageNumber getPageNumber() {
		return pageNumber;
	}

	/**
	 * Sets the text font to the argument.
	 * 
	 * @param fontData
	 *            the new text font.
	 */
	public void setFontData(FontData fontData) {
		Util.notNull(fontData);
		setTextStyle(textStyle.font(fontData));
	}

	/**
	 * Returns the text font.
	 * 
	 * @return the text font.
	 */
	public FontData getFontData() {
		return textStyle.getFontData();
	}

	/**
	 * Sets the horizontal text alignment to the argument.
	 * 
	 * @param align
	 *            the horizontal alignment. Must be one of {@link SWT#LEFT },
	 *            {@link SWT#CENTER } or {@link SWT#RIGHT }.
	 */
	public void setAlign(int align) {
		setTextStyle(textStyle.align(checkAlign(align)));
	}

	/**
	 * Returns the horizontal text alignment.
	 * 
	 * @return the horizontal text alignment.
	 */
	public int getAlign() {
		return textStyle.getAlignment();
	}

	private int checkAlign(int align) {
		return PaperClipsUtil.firstMatch(align, new int[] { SWT.LEFT,
				SWT.CENTER, SWT.RIGHT }, SWT.LEFT);
	}

	/**
	 * Returns the text style that will be used to render the page number
	 * 
	 * @return the text style that will be used to render the page number
	 */
	public TextStyle getTextStyle() {
		return textStyle;
	}

	/**
	 * Sets the text style that will be used to render the page number
	 * 
	 * @param textStyle
	 *            the text style
	 */
	public void setTextStyle(TextStyle textStyle) {
		Util.notNull(textStyle);
		this.textStyle = textStyle;
	}

	/**
	 * Sets the format that will be used to convert the page number to a text
	 * string.
	 * 
	 * @param format
	 *            the new page number format.
	 */
	public void setPageNumberFormat(PageNumberFormat format) {
		Util.notNull(format);
		this.format = format;
	}

	/**
	 * Returns the page number format. This property determines how the
	 * PageNumber will be converted into a String representing the page number.
	 * The default value of this property formats page numbers as follows:<br>
	 * 
	 * <pre>
	 * Page 1 of 5
	 * </pre>
	 * 
	 * @return the page number format.
	 */
	public PageNumberFormat getPageNumberFormat() {
		return format;
	}

	/**
	 * Sets the text color.
	 * 
	 * @param foreground
	 *            the new text color.
	 */
	public void setRGB(RGB foreground) {
		Util.notNull(foreground);
		setTextStyle(textStyle.foreground(foreground));
	}

	/**
	 * Returns the text color.
	 * 
	 * @return the text color.
	 */
	public RGB getRGB() {
		return textStyle.getForeground();
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new PageNumberIterator(this, device, gc);
	}
}

class PageNumberIterator implements PrintIterator {
	private final Device device;
	private final GC gc;

	final PageNumber pageNumber;
	final TextStyle textStyle;
	final PageNumberFormat format;
	final Point size;

	boolean hasNext = true;

	PageNumberIterator(PageNumberPrint print, Device device, GC gc) {
		this.device = device;
		this.gc = gc;

		this.pageNumber = print.pageNumber;
		this.textStyle = print.textStyle;
		this.format = print.format;

		// Calculate the size for the largest possible page number string.
		Font oldFont = gc.getFont();
		try {
			gc.setFont(ResourcePool.forDevice(device).getFont(
					textStyle.getFontData()));

			size = gc.textExtent(format.format(new PageNumber() {
				public int getPageCount() {
					return 9999;
				}

				public int getPageNumber() {
					return 9998;
				} // (zero-based index)
			}));
		} finally {
			gc.setFont(oldFont);
		}
	}

	PageNumberIterator(PageNumberIterator that) {
		this.device = that.device;
		this.gc = that.gc;

		this.pageNumber = that.pageNumber;
		this.textStyle = that.textStyle;
		this.format = that.format;
		this.size = that.size;
		this.hasNext = that.hasNext;
	}

	public boolean hasNext() {
		return hasNext;
	}

	public Point minimumSize() {
		return size;
	}

	public Point preferredSize() {
		return size;
	}

	public PrintPiece next(int width, int height) {
		if (width < size.x || height < size.y)
			return null;

		Point size = new Point(this.size.x, this.size.y);
		int align = textStyle.getAlignment();
		if (align == SWT.CENTER || align == SWT.RIGHT)
			size.x = width;

		PageNumberPiece piece = new PageNumberPiece(this, device, size);
		hasNext = false;

		return piece;
	}

	public PrintIterator copy() {
		return new PageNumberIterator(this);
	}
}

class PageNumberPiece implements PrintPiece {
	private final Device device;
	private final Point size;

	private final PageNumber pageNumber;
	private final TextStyle textStyle;
	private final PageNumberFormat format;

	PageNumberPiece(PageNumberIterator iter, Device device, Point size) {
		this.device = device;
		this.size = size;
		this.pageNumber = iter.pageNumber;
		this.textStyle = iter.textStyle;
		this.format = iter.format;
	}

	public Point getSize() {
		return new Point(size.x, size.y);
	}

	public void paint(final GC gc, final int x, final int y) {
		Font oldFont = gc.getFont();
		Color oldForeground = gc.getForeground();

		Point size = getSize();

		try {
			ResourcePool resources = ResourcePool.forDevice(device);
			gc.setFont(resources.getFont(textStyle.getFontData()));
			gc.setForeground(resources.getColor(textStyle.getForeground()));

			String text = format.format(pageNumber);
			gc.drawText(text, x
					+ getHorzAlignmentOffset(gc.textExtent(text).x, size.x), y,
					true);
		} finally {
			gc.setFont(oldFont);
			gc.setForeground(oldForeground);
		}
	}

	private int getHorzAlignmentOffset(int textWidth, int totalWidth) {
		int offset = 0;
		switch (textStyle.getAlignment()) {
		case SWT.CENTER:
			offset = (totalWidth - textWidth) / 2;
			break;
		case SWT.RIGHT:
			offset = totalWidth - textWidth;
			break;
		}
		return offset;
	}

	public void dispose() {
	} // Shared resources, nothing to dispose
}