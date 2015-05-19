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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Element for tracking nested span tags and the attributes they specify.
 */
public class SpanElement {

	public enum SpanType {
		FONT, COLOR, BG_COLOR
	}

	public Collection<SpanElement.SpanType> types = new ArrayList<>();
}