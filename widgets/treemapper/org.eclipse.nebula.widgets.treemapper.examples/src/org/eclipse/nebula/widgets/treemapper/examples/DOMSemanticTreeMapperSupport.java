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

import org.eclipse.nebula.widgets.treemapper.ISemanticTreeMapperSupport;
import org.eclipse.nebula.widgets.treemapper.examples.DOMSemanticTreeMapperSupport.DOMMappingBean;
import org.w3c.dom.Node;

/**
 * @author Mickael Istria (EBM WebSourcing (PetalsLink))
 *
 */
public class DOMSemanticTreeMapperSupport implements ISemanticTreeMapperSupport<DOMMappingBean, Node, Node> {

	public static final class DOMMappingBean {
		public Node left;
		public Node right;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.treemapper.ISemanticTreeMapperSupport#createSemanticMappingObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public DOMMappingBean createSemanticMappingObject(Node leftItem, Node rightItem) {
		DOMMappingBean res = new DOMMappingBean();
		res.left = leftItem;
		res.right = rightItem;
		return res;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.treemapper.ISemanticTreeMapperSupport#resolveLeftItem(java.lang.Object)
	 */
	@Override
	public Node resolveLeftItem(DOMMappingBean semanticMappingObject) {
		return semanticMappingObject.left;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.treemapper.ISemanticTreeMapperSupport#resolveRightItem(java.lang.Object)
	 */
	@Override
	public Node resolveRightItem(DOMMappingBean semanticMappingObject) {
		return semanticMappingObject.right;
	}
}
