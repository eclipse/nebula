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

	/**
	 * @see org.eclipse.nebula.treemapper.ISemanticTreeMapperSupport#createSemanticMappingObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public DOMMappingBean createSemanticMappingObject(Node leftItem, Node rightItem) {
		DOMMappingBean res = new DOMMappingBean();
		res.left = leftItem;
		res.right = rightItem;
		return res;
	}

	/**
	 * @see org.eclipse.nebula.treemapper.ISemanticTreeMapperSupport#resolveLeftItem(java.lang.Object)
	 */
	@Override
	public Node resolveLeftItem(DOMMappingBean semanticMappingObject) {
		return semanticMappingObject.left;
	}

	/**
	 * @see org.eclipse.nebula.treemapper.ISemanticTreeMapperSupport#resolveRightItem(java.lang.Object)
	 */
	@Override
	public Node resolveRightItem(DOMMappingBean semanticMappingObject) {
		return semanticMappingObject.right;
	}
    
    /**
	 * @see org.eclipse.nebula.treemapper.ISemanticTreeMapperSupport#signalOnMissingItem()
	 */
    @Override
    public boolean signalOnMissingItem() {
        return true;
    }
}
