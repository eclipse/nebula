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
 * Abstract implementation of IMovement.
 * 
 * @author Nicolas Richeton
 */
public abstract class AbstractMovement implements IMovement {

	protected double min;
	protected double max;
	protected double duration;

	/**
	 * @see org.eclipse.nebula.animation.movement.IMovement#getValue(double)
	 */
	public abstract double getValue(double step);

	/**
	 * @see org.eclipse.nebula.animation.movement.IMovement#init(double, double,
	 *      int)
	 */
	public void init(double minValue, double maxValue, int steps) {
		this.min = minValue;
		this.max = maxValue;
		this.duration = steps;
	}

}
