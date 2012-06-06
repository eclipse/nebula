/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr¾tteberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Hallvard Tr¾tteberg - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.geomap.jface;

import org.eclipse.swt.graphics.Point;

/**
 * Interface for telling a GeoMapViewer the position of the tip of an icon, e.g. a pin,
 * relative to the icon's topleft corner. Since the GeoMapViewer does not have a separate
 * IPinPointProvider, your ILabelProvider should implement this interface to properly
 * position the icon.
 * @author hal
 *
 */
public interface IPinPointProvider {
	/**
	 * Provides the relative position of the hot spot for the an image of an element, e.g. a pin on a map.
	 * @param element the element for which to find the hot spot
	 * @return the relative position, or null for 0, 0
	 */
	public Point getPinPoint(Object element); 
}
