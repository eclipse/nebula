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
package org.eclipse.nebula.widgets.treemapper;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

/**
 *
 * This interface is the binding between the business object that holds the mapping,
 * and the targeted bounds of the mapping.
 * It is used to resolve bounds of the mapping as they can be found in the trees, and also
 * to generate the business object that represents the mapping when user creates a new
 * one in the {@link TreeMapper} widget
 * 
 * @author Mickael Istria (EBM WebSourcing (PetalsLink)) - Initial API and implementation
 * @since 0.1.0
 *
 * @param <M> The type of the mapping object
 * @param <L> The type of the left bound of the mapping, as available in the left {@link TreeViewer}'s {@link ITreeContentProvider}
 * @param <R> The type of the right bound of the mapping, as available in the right {@link TreeViewer}'s {@link ITreeContentProvider}
 */
public interface ISemanticTreeMapperSupport<M, L, R> {

	/**
	 * Creates the business object that represents the newly created mapping.
	 * @param leftItem the left bound of the mapping, as available in the left {@link TreeViewer} and provided by the {@link ITreeContentProvider}
	 * @param rightItem the right bound of the mapping, as available in the left {@link TreeViewer} and provided by the {@link ITreeContentProvider}
	 * @return the new object that represents your mapping, or null if this mapping is illegal (nothing happens then)
	 */
	public M createSemanticMappingObject(L leftItem, R rightItem);
	
	
	/**
	 * @param semanticMappingObject The mapping item
	 * @return its left bound
	 */
	public L resolveLeftItem(M semanticMappingObject);
	
	/**
	 * @param semanticMappingObject The maping item
	 * @return its left bound
	 */
	public R resolveRightItem(M semanticMappingObject);
}
