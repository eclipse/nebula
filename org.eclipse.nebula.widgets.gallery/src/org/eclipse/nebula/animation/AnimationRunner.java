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

package org.eclipse.nebula.animation;

import org.eclipse.nebula.animation.effects.IEffect;
import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * An animation runner which can run only one effect at the same time.
 * </p>
 * 
 * <p>
 * Example :
 * </p>
 * <p>
 * <code>
 * AnimationRunner runner = new AnimationRunner();<br/>
 * AlphaEffect effect = new AlphaEffect(shell, 0 , 255 , 4000 , new ExpoOut(), null , null );<br/>
 * runner.runEffect( effect);
 * </code>
 * </p>
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
				if (effect != null && !effect.isDone()) {
					Display.getCurrent().timerExec(delay, this);
					effect.doEffect();
				} else {
					running = false;
				}
			}

		});
	}
}
