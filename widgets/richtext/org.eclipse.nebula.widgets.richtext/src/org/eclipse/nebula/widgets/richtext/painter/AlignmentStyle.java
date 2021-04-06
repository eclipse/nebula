/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
