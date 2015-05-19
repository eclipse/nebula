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
import org.eclipse.nebula.widgets.richtext.painter.TagProcessingState.TextAlignment;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * {@link PaintInstruction} to reset settings that were applied for a paragraph.
 */
public class ResetParagraphInstruction implements PaintInstruction {

	protected TagProcessingState state;
	protected int paragraphSpace;

	public ResetParagraphInstruction(int paragraphSpace, TagProcessingState state) {
		this.state = state;
		this.paragraphSpace = paragraphSpace;
	}

	@Override
	public void paint(GC gc, Rectangle area) {
		// goto next line
		state.increaseY(state.getCurrentLineHeight());
		state.increaseY(paragraphSpace);
		state.setMarginLeft(0);
		state.resetX();
		state.setTextAlignment(TextAlignment.LEFT);
	}

}
