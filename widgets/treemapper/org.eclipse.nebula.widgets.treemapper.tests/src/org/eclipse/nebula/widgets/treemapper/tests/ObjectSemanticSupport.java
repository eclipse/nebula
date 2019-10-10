/*******************************************************************************
* Copyright (c) 2011 PetalsLink
*
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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

	/**
	 * @see org.eclipse.nebula.widgets.treemapper.ISemanticTreeMapperSupport#createSemanticMappingObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String createSemanticMappingObject(String leftItem, String rightItem) {
		return leftItem;
	}

	/**
	 * @see org.eclipse.nebula.widgets.treemapper.ISemanticTreeMapperSupport#resolveLeftItem(java.lang.Object)
	 */
	@Override
	public String resolveLeftItem(String semanticMappingObject) {
		return semanticMappingObject;
	}

	/**
	 * @see org.eclipse.nebula.widgets.treemapper.ISemanticTreeMapperSupport#resolveRightItem(java.lang.Object)
	 */
	@Override
	public String resolveRightItem(String semanticMappingObject) {
		return semanticMappingObject;
	}

}
