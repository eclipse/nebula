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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * {@link PaintInstruction} to move to the next line for rendering.
 */
public class NewLineInstruction implements PaintInstruction {

	private TagProcessingState state;

	public NewLineInstruction(TagProcessingState state) {
		this.state = state;
	}

	@Override
	public void paint(GC gc, Rectangle area) {
		state.increaseY(state.getCurrentLine().getLineHeight());
		// goto next line
		state.activateNextLine();
		state.calculateX(area.width);
	}

}
