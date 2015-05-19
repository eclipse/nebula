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

import org.eclipse.nebula.widgets.richtext.painter.TagProcessingState;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * {@link PaintInstruction} to reset a {@link Font} by setting a previous used font to the
 * {@link GC}.
 */
public class ResetFontPaintInstruction implements PaintInstruction, FontMetricsProvider {

	private TagProcessingState state;

	public ResetFontPaintInstruction(TagProcessingState state) {
		this.state = state;
	}

	@Override
	public void paint(GC gc, Rectangle area) {
		gc.setFont(this.state.pollPreviousFont());
	}

	@Override
	public FontMetrics getFontMetrics(GC gc) {
		paint(gc, null);
		return gc.getFontMetrics();
	}

}
