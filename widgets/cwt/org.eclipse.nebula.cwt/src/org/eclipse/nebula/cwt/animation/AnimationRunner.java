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

package org.eclipse.nebula.cwt.animation;

import org.eclipse.nebula.cwt.animation.effects.IEffect;
import org.eclipse.swt.widgets.Display;

/**
 * An animation runner which can run only one effect at the same time.
 * 
 * @author Nicolas Richeton
 */
public class AnimationRunner {

	/**
	 * Default is 50 fps.
	 */
	int delay = 20;
	IEffect effect;
	boolean running = false;

	/**
	 * Create a new animation runner using the default framerate (50 fps)
	 */
	public AnimationRunner() {
		this(50);
	}

	/**
	 * Create a new animation runner, which can run only one effect at the same
	 * time.
	 * 
	 * @param framerate
	 *            the animation framerate.
	 */
	public AnimationRunner(int framerate) {
		delay = 1000 / framerate;
	}

	/**
	 * Start a new effect, cancelling the previous one if any.
	 * 
	 * @param effect
	 */
	public void runEffect(IEffect effect) {
		cancel();
		this.effect = effect;
		startEffect();
	}

	/**
	 * Stops the current effect if any, and execute the corresponding onCancel
	 * runnable.
	 */
	public void cancel() {
		if (effect != null && !effect.isDone()) {
			effect.cancel();
			effect = null;
		}
	}

	private void startEffect() {
		if (running)
			return;

		running = true;
		Display.getCurrent().syncExec(new Runnable() {
			public void run() {
				if (!effect.isDone()) {
					Display.getCurrent().timerExec(delay, this);
					effect.doEffect();
				} else {
					running = false;
				}
			}

		});
	}
}
