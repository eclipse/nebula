/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.painter;

import javax.xml.stream.XMLResolver;
import javax.xml.stream.events.EntityReference;

/**
 * Implementations of this interface are intended to transform an {@link EntityReference} to a
 * String representation.
 * <p>
 * This is similar to the usage of a {@link XMLResolver}, but as the rich text control doesn't
 * deliver a well-formed HTML document, but only a HTML snippet, we need to perform the work
 * manually.
 * </p>
 */
public interface EntityReplacer {

	/**
	 * Returns the String representation of the given {@link EntityReference}.
	 * 
	 * @param reference
	 *            The {@link EntityReference} for which the String representation is requested.
	 * @return The String representation for the given {@link EntityReference}.
	 */
	String getEntityReferenceValue(EntityReference reference);
}
