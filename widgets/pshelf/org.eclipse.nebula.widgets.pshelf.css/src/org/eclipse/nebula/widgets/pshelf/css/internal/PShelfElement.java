/*******************************************************************************
 * Copyright (c) 2013 Tom Schindl. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html 
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
