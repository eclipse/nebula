/*******************************************************************************
 * Copyright (c) 2008 Nicolas Richeton.
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
 * Moves fast at first then slow down until it reaches the max value.
 * 
 * @author Nicolas Richeton
 * 
 */
public class ExpoOut extends AbstractMovement {

	float increment;

	/**
	 * @see org.sharemedia.gui.viewers.impl.gl.IMovement#getValue(int)
	 */
	public double getValue(double step) {
		float currentCos = 1.0f - (float) Math.exp(((float) step) * increment);
		if (step != duration)
			return min + max * currentCos;
		else
			return max;
	}

	/**
	 * @see org.sharemedia.gui.viewers.impl.gl.IMovement#init(float, float, int)
	 */
	public void init(double min, double max, int steps) {
		increment = -10.0f / steps;
		super.init(min, max, steps);
	}

}
