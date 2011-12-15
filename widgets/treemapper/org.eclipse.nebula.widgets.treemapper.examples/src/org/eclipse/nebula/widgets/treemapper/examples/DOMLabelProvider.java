/*******************************************************************************
* Copyright (c) 2011 EBM WebSourcing (PetalsLink)
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Mickael Istria, EBM WebSourcing (PetalsLink) - initial API and implementation
*******************************************************************************/
package org.eclipse.nebula.widgets.treemapper.examples;

import org.eclipse.jface.viewers.LabelProvider;
import org.w3c.dom.Element;
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
