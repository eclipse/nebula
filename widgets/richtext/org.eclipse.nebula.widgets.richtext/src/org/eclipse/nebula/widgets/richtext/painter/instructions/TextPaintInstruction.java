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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.richtext.painter.LinePainter;
import org.eclipse.nebula.widgets.richtext.painter.TagProcessingState;
import org.eclipse.nebula.widgets.richtext.painter.TagProcessingState.TextAlignment;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * {@link PaintInstruction} to paint text based on the current {@link TagProcessingState}.
 */
public class TextPaintInstruction implements PaintInstruction {

	private TagProcessingState state;
	private String text;

	private List<String> words = new ArrayList<>();

	public TextPaintInstruction(TagProcessingState state, String text) {
		this.state = state;
		this.text = text;

		// extract and store the trimmed words in the text
		String word = text.trim();
		if (word.length() > 0) {
			String[] splitText = word.split("\\s");
			for (String splitWord : splitText) {
				String trimmed = splitWord.trim();
				if (trimmed.length() > 0) {
					words.add(trimmed);
				}
			}
		}
	}

	@Override
	public void paint(GC gc, Rectangle area) {
		if (!this.state.isRendering()) {
			return;
		}

		FontMetrics metrics = gc.getFontMetrics();
		FontMetrics metricsToUse = metrics;
		int baseline = metrics.getAscent() + metrics.getLeading();

		int yAdvance = 0;
		if (this.state.getCurrentLineHeight() > metrics.getHeight()) {
			int biggerBaseline = this.state.getCurrentBiggestFontMetrics().getAscent() + this.state.getCurrentBiggestFontMetrics().getLeading();
			yAdvance = biggerBaseline - baseline;

			metricsToUse = this.state.getCurrentBiggestFontMetrics();
		}

		Point pointer = this.state.getPointer();

		// on alignment justify render word by word
		int textLength = 0;
		if (TextAlignment.JUSTIFY.equals(this.state.getTextAlignment())) {
			LinePainter line = this.state.getCurrentLine();
			for (String word : this.words) {
				gc.drawText(word, pointer.x, pointer.y + yAdvance, (this.state.getPrevBgColor() == null));
				int length = gc.textExtent(word).x + line.getNextJustifySpace();
				pointer.x += length;
				textLength += length;
			}
		}
		else {
			textLength = getTextLength(gc);
			gc.drawText(text, pointer.x, pointer.y + yAdvance, (this.state.getPrevBgColor() == null));
		}

		if (this.state.isUnderlineActive()) {
			int underlineY = pointer.y + baseline + yAdvance + (metricsToUse.getDescent() / 2);
			gc.drawLine(pointer.x, underlineY, pointer.x + textLength, underlineY);
		}
		if (this.state.isStrikethroughActive()) {
			int strikeY = pointer.y + yAdvance + (metrics.getHeight() / 2) + (metrics.getLeading() / 2);
			gc.drawLine(pointer.x, strikeY, pointer.x + textLength, strikeY);
		}

		if (!TextAlignment.JUSTIFY.equals(this.state.getTextAlignment())) {
			pointer.x += textLength;
		}
	}

	public int getTextLength(GC gc) {
		// TODO caching of textExtent
		return gc.textExtent(text).x;
	}

	public int getTrimmedTextLength(GC gc) {
		int result = 0;
		for (String word : this.words) {
			// TODO caching of textExtent
			result += gc.textExtent(word).x;
		}
		return result;
	}

	public String getText() {
		return this.text;
	}

	public List<String> getWords() {
		return this.words;
	}
}
