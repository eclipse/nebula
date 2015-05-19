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

import org.eclipse.nebula.widgets.richtext.painter.TagProcessingState.TextAlignment;

/**
 * Alignment style information that is extracted out of the style attribute of span, ul and ol tags.
 */
public class AlignmentStyle {

	public int marginLeft = 0;
	public TextAlignment alignment = TextAlignment.LEFT;
}
