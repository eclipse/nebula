/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.figures;

/**
 * A listener on an annotation when annotation position was changed.
 * 
 * @author Xihui Chen
 *
 */
public interface IAnnotationListener {

	/**
	 * This event indicates a change in the axis' value range
	 */
	public void annotationMoved(double oldX, double oldY, double newX, double newY);

}
