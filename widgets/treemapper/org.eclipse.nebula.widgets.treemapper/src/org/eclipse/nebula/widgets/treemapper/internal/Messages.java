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
package org.eclipse.nebula.widgets.treemapper.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author Mickael Istria (PetalsLink)
 *
 */
public class Messages extends NLS {

	public static String widgetInconsistency;

	static {
		initializeMessages("messages", Messages.class);
	}
}
