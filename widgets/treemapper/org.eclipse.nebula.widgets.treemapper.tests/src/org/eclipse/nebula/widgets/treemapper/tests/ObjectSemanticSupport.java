/*******************************************************************************
* Copyright (c) 2011 PetalsLink
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Mickael Istria, PetalsLink - initial API and implementation
*******************************************************************************/
package org.eclipse.nebula.widgets.treemapper.tests;

import org.eclipse.nebula.widgets.treemapper.ISemanticTreeMapperSupport;

/**
 * @author Mickael Istria (PetalsLink)
 *
 */
public class ObjectSemanticSupport implements ISemanticTreeMapperSupport<String, String, String> {

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.treemapper.ISemanticTreeMapperSupport#createSemanticMappingObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String createSemanticMappingObject(String leftItem, String rightItem) {
		return leftItem;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.treemapper.ISemanticTreeMapperSupport#resolveLeftItem(java.lang.Object)
	 */
	@Override
	public String resolveLeftItem(String semanticMappingObject) {
		return semanticMappingObject;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.treemapper.ISemanticTreeMapperSupport#resolveRightItem(java.lang.Object)
	 */
	@Override
	public String resolveRightItem(String semanticMappingObject) {
		return semanticMappingObject;
	}

}
