/*******************************************************************************
 * Copyright (c) 2006-2009 Nicolas Richeton.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.animation.movement;

/**
 * Moves at a constant speed.
 * 
 * @author Nicolas Richeton
 */
public class LinearInOut extends AbstractMovement {

	double increment;

	/**
	 * @see org.sharemedia.gui.viewers.impl.gl.IMovement#getValue(int)
	 */
	public double getValue(double step) {
		return min + increment * step;
	}

	/**
	 * @see org.sharemedia.gui.viewers.impl.gl.IMovement#init(float, float, int)
	 */
	public void init(double min, double max, int steps) {
		increment = (max - min) / steps;
		super.init(min, max, steps);
	}

}
