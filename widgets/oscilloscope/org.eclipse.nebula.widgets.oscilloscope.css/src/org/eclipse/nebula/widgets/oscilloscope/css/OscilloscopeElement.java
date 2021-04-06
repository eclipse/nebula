/*******************************************************************************
 * Copyright (c) 2020 Laurent Caron.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent Caron <laurent dot caron at gmail dot com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope.css;

import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.dom.CompositeElement;
import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;

@SuppressWarnings("restriction")
public class OscilloscopeElement extends CompositeElement {

	public OscilloscopeElement(final Oscilloscope composite, final CSSEngine engine) {
		super(composite, engine);
	}

}
