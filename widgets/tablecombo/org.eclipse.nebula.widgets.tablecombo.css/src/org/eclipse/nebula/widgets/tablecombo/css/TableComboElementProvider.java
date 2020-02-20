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
package org.eclipse.nebula.widgets.tablecombo.css;

import org.eclipse.e4.ui.css.core.dom.IElementProvider;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.w3c.dom.Element;

@SuppressWarnings("restriction")
public class TableComboElementProvider implements IElementProvider {

	@Override
	public Element getElement(final Object element, final CSSEngine engine) { 
		return new TableComboElement((TableCombo) element, engine);
	}

}
