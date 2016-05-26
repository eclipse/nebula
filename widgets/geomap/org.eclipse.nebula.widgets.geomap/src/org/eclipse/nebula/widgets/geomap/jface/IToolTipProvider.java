/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr�tteberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Hallvard Tr�tteberg - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.geomap.jface;

/**
 * Interface for providing the tool tip of an element shown by the GeoMapViewer
 * @since 3.3
 *
 */
public interface IToolTipProvider {
	
	/**
	 * Gets the tool tip for the given element
	 * @param element
	 * @return the tool tip for the given element
	 */
	public Object getToolTip(Object element); 
}
