/*******************************************************************************
 * Copyright (c) 2006-2009 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.animation.effects;

/**
 * All animation effects must implement this interface.
 * 
 * @author Nicolas Richeton
 */
public interface IEffect {
	/**
	 * Apply effect to the target according to the current time.
	 */
	public void doEffect();

	/**
	 * @return true if the effect as already reached its end.
	 */
	public boolean isDone();

	/**
	 * Set the effect as done and run the cancel runnable.
	 */
	public void cancel();
}
