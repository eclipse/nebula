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

import org.eclipse.nebula.widgets.richtext.painter.SpanElement;
import org.eclipse.nebula.widgets.richtext.painter.TagProcessingState;
import org.eclipse.nebula.widgets.richtext.painter.SpanElement.SpanType;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * {@link PaintInstruction} to reset style information that were set via span tag.
 */
public class ResetSpanStylePaintInstruction implements PaintInstruction, FontMetricsProvider {

	private TagProcessingState state;
	private SpanElement span;

	public ResetSpanStylePaintInstruction(TagProcessingState state, SpanElement span) {
		this.state = state;
		this.span = span;
	}

	@Override
	public void paint(GC gc, Rectangle area) {
		for (SpanType st : span.types) {
			switch (st) {
				case COLOR:
					gc.setForeground(this.state.getPrevColor());
					this.state.setPrevColor(null);
					break;
				case BG_COLOR:
					gc.setBackground(this.state.getPrevBgColor());
					this.state.setPrevBgColor(null);
					break;
				case FONT:
					gc.setFont(this.state.pollPreviousFont());
					break;
			}
		}
	}

	@Override
	public FontMetrics getFontMetrics(GC gc) {
		if (span.types.contains(SpanType.FONT)) {
			gc.setFont(this.state.pollPreviousFont());
		}
		return gc.getFontMetrics();
	}

}
