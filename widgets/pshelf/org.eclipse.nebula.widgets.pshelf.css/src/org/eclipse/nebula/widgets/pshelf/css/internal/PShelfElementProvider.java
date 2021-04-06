/*******************************************************************************
 * Copyright (c) 2013 Tom Schindl.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0 
 * 
 * Contributors: Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.pshelf.css.internal;

import org.eclipse.e4.ui.css.core.dom.IElementProvider;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.w3c.dom.Element;

@SuppressWarnings("restriction")
public class PShelfElementProvider implements IElementProvider {

	@Override
	public Element getElement(Object element, CSSEngine engine) {
		return new PShelfElement((PShelf) element, engine);
	}

}
