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

import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.dom.CompositeElement;
import org.eclipse.nebula.widgets.pshelf.PShelf;

@SuppressWarnings("restriction")
public class PShelfElement extends CompositeElement {

	public PShelfElement(PShelf composite, CSSEngine engine) {
		super(composite, engine);
	}

}
