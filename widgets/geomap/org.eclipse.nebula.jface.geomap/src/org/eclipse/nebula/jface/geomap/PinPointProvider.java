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
package org.eclipse.nebula.jface.geomap;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A default implementation of a LabelProvider that also implements IPinPointProvider.
 * @author hal
 *
 */
public class PinPointProvider extends LabelProvider implements IPinPointProvider {

	private final Point defaultPinPoint;

	/**
	 * A PinPointProvider that returns the corresponding pin point.
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 */
	public PinPointProvider(int x, int y) {
		 defaultPinPoint = new Point(0, 0);
	}

	/**
	 * A PinPointProvider that returns 0, 0 for the pin point.
	 */
	public PinPointProvider() {
		this(0, 0);
	}
	
	/**
	 * The default implementation just returns the value of the defaultPinPoint field.
	 */
	public Point getPinPoint(Object element) {
		return defaultPinPoint;
	}
	
	/**
	 * Helper method for computing the point based on the size of the image.
	 * The float arguments alignX and alignY are multiplied with the width and height of the image, respectively. 
	 * @param element the element to provide the point for
	 * @param alignX a float that is multiplied with the width of the image, to give the x coordinate of the point 
	 * @param alignY a float that is multiplied with the height of the image, to give the y coordinate of the point
	 * @return
	 */
	protected Point getPinPoint(Object element, float alignX, float alignY) {
		Rectangle bounds = getImage(element).getBounds();
		return new Point((int) (bounds.width * alignX), (int) (bounds.height * alignY));
	}
}
