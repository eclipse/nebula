/*
 * Copyright (c) 2006 Matthew Hall and others.
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
import org.eclipse.nebula.paperclips.core.internal.util.PaperClipsUtil;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * A PageDecoration which displays the page number. This convenience class helps
 * avoid the need for writing a new PageDecoration class if only a page number
 * is needed. Getter and setter methods are provided for all the properties
 * available in the PagePrint class itself.
 * 
 * @author Matthew Hall
 */
public class PageNumberPageDecoration implements PageDecoration {
	FontData fontData = new FontData();
	int align = SWT.LEFT;
	RGB rgb = new RGB(0, 0, 0); // black
	PageNumberFormat format = new DefaultPageNumberFormat();

	/**
	 * Constructs a PageNumberPageDecoration with default font, alignment, and
	 * page number format.
	 */
	public PageNumberPageDecoration() {
	}

	/**
	 * Constructs a PageNumberPageDecoration with the given alignment.
	 * 
	 * @param align
	 *            horizontal text alignment.
	 */
	public PageNumberPageDecoration(int align) {
		setAlign(align);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + align;
		result = prime * result
				+ ((fontData == null) ? 0 : fontData.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((rgb == null) ? 0 : rgb.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageNumberPageDecoration other = (PageNumberPageDecoration) obj;
		if (align != other.align)
			return false;
		if (fontData == null) {
			if (other.fontData != null)
				return false;
		} else if (!fontData.equals(other.fontData))
			return false;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		if (rgb == null) {
			if (other.rgb != null)
				return false;
		} else if (!rgb.equals(other.rgb))
			return false;
		return true;
	}

	/**
	 * Returns the font.
	 * 
	 * @return the font.
	 */
	public FontData getFontData() {
		return fontData;
	}

	/**
	 * Sets the font.
	 * 
	 * @param fontData
	 *            the new font.
	 */
	public void setFontData(FontData fontData) {
		Util.notNull(fontData);
		this.fontData = fontData;
	}

	/**
	 * Returns the horizontal text alignment.
	 * 
	 * @return the horizontal text alignment.
	 */
	public int getAlign() {
		return align;
	}

	/**
	 * Sets the horizontal text alignment.
	 * 
	 * @param align
	 *            the horizontal text alignment.
	 */
	public void setAlign(int align) {
		align = checkAlign(align);
		this.align = align;
	}

	private int checkAlign(int align) {
		return PaperClipsUtil.firstMatch(align, new int[] { SWT.LEFT,
				SWT.CENTER, SWT.RIGHT }, SWT.LEFT);
	}

	/**
	 * Returns the text color.
	 * 
	 * @return the text color.
	 */
	public RGB getRGB() {
		return rgb;
	}

	/**
	 * Sets the text color.
	 * 
	 * @param rgb
	 *            the new text color.
	 */
	public void setRGB(RGB rgb) {
		Util.notNull(rgb);
		this.rgb = rgb;
	}

	/**
	 * Returns the page number format.
	 * 
	 * @return the page number format.
	 */
	public PageNumberFormat getFormat() {
		return format;
	}

	/**
	 * Sets the page number format.
	 * 
	 * @param format
	 *            the page number format.
	 */
	public void setFormat(PageNumberFormat format) {
		Util.notNull(format);
		this.format = format;
	}

	public Print createPrint(PageNumber pageNumber) {
		PageNumberPrint result = new PageNumberPrint(pageNumber);
		result.setFontData(fontData);
		result.setAlign(align);
		result.setPageNumberFormat(format);
		result.setRGB(rgb);
		return result;
	}
}