/*******************************************************************************
* Copyright (c) 2011 EBM WebSourcing (PetalsLink)
*
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*
* Contributors:
* Mickael Istria, EBM WebSourcing (PetalsLink) - initial API and implementation
*******************************************************************************/
package org.eclipse.nebula.widgets.treemapper.examples;

import org.eclipse.jface.viewers.LabelProvider;
import org.w3c.dom.Node;

/**
 * @author Mickael Istria (EBM WebSourcing (PetalsLink))
 *
 */
public class DOMLabelProvider extends LabelProvider {

	@Override
	public String getText(Object item) {
		if (item instanceof Node) {
			return ((Node)item).getNodeName();
		}
		return super.getText(item);
	}
}
