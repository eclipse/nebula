/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.painter.instructions;

import org.eclipse.nebula.widgets.richtext.painter.ResourceHelper;
import org.eclipse.nebula.widgets.richtext.painter.TagProcessingState;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * {@link PaintInstruction} that applies style information to the {@link GC} via span tag
 * attributes.
 */
public class SpanStylePaintInstruction implements PaintInstruction, FontMetricsProvider {

	private TagProcessingState state;

	/**
	 * The foreground color that should be applied on paint.
	 */
	private Color foregroundColor;
	/**
	 * The background color that should be applied on paint.
	 */
	private Color backgroundColor;
	/**
	 * The font size in points that should be applied on paint.
	 */
	private Integer fontSize;
	/**
	 * The font type that should be applied on paint.
	 */
	private String fontType;

	public SpanStylePaintInstruction(TagProcessingState state) {
		this.state = state;
	}

	@Override
	public void paint(GC gc, Rectangle area) {
		if (this.foregroundColor != null) {
			// remember the previous set value
			// to be able to reset on close
			this.state.setPrevColor(gc.getForeground());
			// set the style value
			gc.setForeground(this.foregroundColor);
		}

		if (this.backgroundColor != null) {
			// remember the previous set value
			// to be able to reset on close
			this.state.setPrevBgColor(gc.getBackground());
			// set the style value
			gc.setBackground(this.backgroundColor);
		}

		if (this.fontSize != null || this.fontType != null) {
			Font currentFont = gc.getFont();

			// remember the previous set value
			// to be able to reset on close
			this.state.addPreviousFont(currentFont);

			// set the style value
			gc.setFont(ResourceHelper.getFont(currentFont, this.fontType, this.fontSize));
		}
	}

	@Override
	public FontMetrics getFontMetrics(GC gc) {
		Font currentFont = gc.getFont();
		this.state.addPreviousFont(currentFont);
		gc.setFont(ResourceHelper.getFont(currentFont, this.fontType, this.fontSize));
		return gc.getFontMetrics();
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public void setFontType(String fontType) {
		this.fontType = fontType;
	}

}
