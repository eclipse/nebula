/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr�tteberg.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Hallvard Tr�tteberg - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.geomap.jface;

/**
 * Interface for providing the tool tip of an element shown by the GeoMapViewer
 * 
 * @since 3.3
 *
 */
public interface IToolTipProvider {

	/**
	 * Gets the tool tip for the given element
	 * 
	 * @param element
	 * @return the tool tip for the given element
	 */
	public Object getToolTip(Object element);
}
