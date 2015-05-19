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

import org.eclipse.nebula.widgets.richtext.painter.AlignmentStyle;
import org.eclipse.nebula.widgets.richtext.painter.TagProcessingState;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * {@link PaintInstruction} that is similar to a {@link ParagraphInstruction} and additionally adds
 * setting up list rendering states.
 */
public class ListInstruction extends ParagraphInstruction {

	protected int listIndentation;
	protected boolean ordered;

	public ListInstruction(int listIndent, boolean ordered, AlignmentStyle alignment, int paragraphSpace, TagProcessingState state) {
		super(alignment, paragraphSpace, state);
		this.listIndentation = listIndent;
		this.ordered = ordered;
	}

	@Override
	public void paint(GC gc, Rectangle area) {
		int marginLeft = state.getMarginLeft();

		super.paint(gc, area);

		// if a new list is opened within a list, we need to move the pointer to the next line
		// in that case we also subtract the paragraphSpace that is automatically added by the
		// ParagraphInstruction
		// we also need to re-apply the left margin that was reset by the super call
		if (state.getListDepth() > 0) {
			state.increaseY(state.getCurrentLineHeight() - paragraphSpace);
			state.setMarginLeft(marginLeft);
		}

		// set the list states in the TagProcessingState
		state.initCurrentListNumber();
		state.setListMargin(listIndentation);
		state.setOrderedList(ordered);
	}
}
