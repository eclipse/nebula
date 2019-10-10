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

import org.eclipse.nebula.cwt.animation.movement.IMovement;

/**
 * Abstract implementation for IEffect.
 * 
 * @author Nicolas Richeton
 * 
 */
public abstract class AbstractEffect implements IEffect {

	protected Runnable runnableOnStop = null;
	protected Runnable runnableOnCancel = null;

	protected long length = 0;
	protected long startTime = -1;
	protected boolean done = false;

	protected IMovement easingFunction;

	public AbstractEffect(long lengthMilli, IMovement movement,
			Runnable onStop, Runnable onCancel) {
		this.length = lengthMilli;
		easingFunction = movement;
		this.runnableOnCancel = onCancel;
		this.runnableOnStop = onStop;
	}

	/**
	 * Apply this effect.
	 * 
	 * @param currentTime
	 */
	public abstract void applyEffect(final long currentTime);

	/**
	 * Run the onCancel runnable if any.
	 */
	protected void doCancel() {
		if (runnableOnCancel != null)
			runnableOnCancel.run();
	}

	/**
	 * Run the onStop runnable if any.
	 */
	protected void doStop() {
		if (runnableOnStop != null)
			runnableOnStop.run();
	}

	public long getCurrentTime() {
		long time = System.currentTimeMillis();

		if (startTime == -1)
			startTime = time;

		long currentTime = time - startTime;

		if (currentTime > length)
			currentTime = length;

		return currentTime;
	}

	/**
	 * Check if the effect has ended. In that case, start the onStop runnable.
	 */
	public void processEnd() {
		if (done)
			return;

		if (getCurrentTime() == length) {
			done = true;
			doStop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.cwt.animation.effects.IEffect#cancel()
	 */
	public void cancel() {
		done = true;
		doCancel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.cwt.animation.effects.IEffect#doEffect()
	 */
	public void doEffect() {
		final long currentTime = getCurrentTime();

		applyEffect(currentTime);

		processEnd();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.cwt.animation.effects.IEffect#isDone()
	 */
	public boolean isDone() {
		return done;
	}
}
