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