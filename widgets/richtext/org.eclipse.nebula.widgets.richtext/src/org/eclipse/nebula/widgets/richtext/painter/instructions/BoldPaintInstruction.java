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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * {@link PaintInstruction} to apply a bold version of the current {@link Font} to the {@link GC}.
 */
public class BoldPaintInstruction implements PaintInstruction, FontMetricsProvider {

	private TagProcessingState state;

	public BoldPaintInstruction(TagProcessingState state) {
		this.state = state;
	}

	@Override
	public void paint(GC gc, Rectangle area) {
		Font currentFont = gc.getFont();
		this.state.addPreviousFont(currentFont);
		gc.setFont(ResourceHelper.getBoldFont(currentFont));
	}

	@Override
	public FontMetrics getFontMetrics(GC gc) {
		// apply the font
		paint(gc, null);
		// return the metrics
		return gc.getFontMetrics();
	}

}
