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

package org.eclipse.nebula.cwt.animation.effects;

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
