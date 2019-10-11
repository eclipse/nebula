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
package org.eclipse.nebula.cwt.animation.movement;


/**
 * This is not an easing equation. This movement goes from f(0)=0 to f(t)=0 with
 * intermediate values between -amplitude and amplitude an decreasing with time.
 * 
 * @author Nicolas Richeton
 * 
 */
public class SinusDecreaseVariation extends AbstractMovement {

	int variations = 1;
	double amplitude;

	public SinusDecreaseVariation(int nb, double amplitude) {
		super();
		variations = nb;
		this.amplitude = amplitude;
	}

	
	public double getValue(double step) {
		return amplitude * (1 - step / duration) * Math.sin(step / duration * Math.PI * (double) variations);
	}

}
