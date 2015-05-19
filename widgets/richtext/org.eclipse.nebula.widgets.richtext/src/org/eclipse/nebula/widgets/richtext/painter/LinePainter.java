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
package org.eclipse.nebula.widgets.richtext.painter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.nebula.widgets.richtext.painter.instructions.FontMetricsProvider;
import org.eclipse.nebula.widgets.richtext.painter.instructions.PaintInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.TextPaintInstruction;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class LinePainter {

	private Collection<PaintInstruction> instructions = new ArrayList<>();

	private FontMetrics biggestMetrics = null;

	private int contentWidth = 0;
	private int trimmedContentWidth = 0;

	private int justifySpace = 0;
	private int roundingPixels = 0;

	private List<String> words = new ArrayList<>();

	public void paint(GC gc, Rectangle area) {
		// calculate maximum line height
		Font beforeFont = gc.getFont();
		FontMetrics biggestMetrics = gc.getFontMetrics();
		for (PaintInstruction instruction : instructions) {
			if (instruction instanceof FontMetricsProvider) {
				FontMetrics metrics = ((FontMetricsProvider) instruction).getFontMetrics(gc);
				if (metrics.getHeight() > biggestMetrics.getHeight()) {
					biggestMetrics = metrics;
				}
			}

			// collect all words in a line
			if (instruction instanceof TextPaintInstruction) {
				words.addAll(((TextPaintInstruction) instruction).getWords());
			}
		}

		// calculate the justify space
		int wordCount = (this.words.size() > 1) ? (this.words.size() - 1) : 1;
		justifySpace = (area.width - getTrimmedContentWidth()) / wordCount;
		roundingPixels = area.width - (getTrimmedContentWidth() + (justifySpace * wordCount));

		// reset the font after size calculations
		gc.setFont(beforeFont);

		// remember the biggest metrics for base line calculation
		this.biggestMetrics = biggestMetrics;

		// after the parsing is done, execute the painting operations
		for (PaintInstruction instruction : instructions) {
			instruction.paint(gc, area);
		}
	}

	public void addInstruction(PaintInstruction instruction) {
		this.instructions.add(instruction);
	}

	public FontMetrics getBiggestMetrics() {
		return this.biggestMetrics;
	}

	public int getLineHeight() {
		return this.biggestMetrics.getHeight();
	}

	/**
	 * @return The static content width taking spaces into account.
	 */
	public int getContentWidth() {
		return this.contentWidth;
	}

	/**
	 * Increase the static content with with spaces.
	 * 
	 * @param width
	 *            The width that should be added to the static content width.
	 */
	public void increaseContentWidth(int width) {
		this.contentWidth += width;
	}

	/**
	 * @return The static content width without spaces.
	 */
	public int getTrimmedContentWidth() {
		return this.trimmedContentWidth;
	}

	/**
	 * Increase the static content with without spaces.
	 * 
	 * @param width
	 *            The width that should be added to the static content width.
	 */
	public void increaseTrimmedContentWidth(int width) {
		this.trimmedContentWidth += width;
	}

	/**
	 * 
	 * @return The number of pixels that need to be added to render a text justified.
	 */
	public int getNextJustifySpace() {
		int result = this.justifySpace;
		if (roundingPixels > 0) {
			result += roundingPixels--;
		}
		return result;
	}
}
