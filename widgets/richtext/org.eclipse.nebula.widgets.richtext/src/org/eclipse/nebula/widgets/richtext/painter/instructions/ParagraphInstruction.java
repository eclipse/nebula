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
 * {@link PaintInstruction} that is used to set up a new paragraph.
 */
public class ParagraphInstruction implements PaintInstruction {

	protected AlignmentStyle alignment;
	protected TagProcessingState state;
	protected int paragraphSpace;

	public ParagraphInstruction(AlignmentStyle alignment, int paragraphSpace, TagProcessingState state) {
		this.alignment = alignment;
		this.state = state;
		this.paragraphSpace = paragraphSpace;
	}

	@Override
	public void paint(GC gc, Rectangle area) {
		state.activateNextLine();

		// increase y
		state.increaseY(paragraphSpace);
		state.increaseParagraphCount();

		// full reset x
		state.resetX();

		state.increaseX(alignment.marginLeft);
		state.setMarginLeft(alignment.marginLeft);
		// calculate the start x position dependent
		// on the alignment
		state.setTextAlignment(alignment.alignment);
		state.calculateX(area.width);
	}

}
