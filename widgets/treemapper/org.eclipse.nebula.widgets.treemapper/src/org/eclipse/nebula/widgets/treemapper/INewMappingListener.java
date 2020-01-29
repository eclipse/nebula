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
package org.eclipse.nebula.widgets.treemapper;


/**
 * Listener for the creation of new mapping in a {@link TreeMapper}. To be used when the
 * list of mapping specified to {@link TreeMapper#setInput(Object, Object, java.util.List)}
 * is not intended to be used as a reference to the concrete list of mappings. 
 * 
 * @author Mickael Istria (EBM WebSourcing (PetalsLink))
 * @since 0.1.0
 * 
 * @param <M> the datatype of the mappings object 
 */
public interface INewMappingListener<M> {

	/**
	 * Notify that the user just added a new mapping.
	 * @param mapping the newly created mapping.
	 */
	void mappingCreated(M mapping);

}
