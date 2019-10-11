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

package org.eclipse.nebula.animation.effects;

import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.movement.IMovement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Set the alpha value of a Shell
 * 
 * @author Nicolas Richeton
 * 
 */
public class AlphaEffect extends AbstractEffect {

	/**
	 * @deprecated
	 * @param w
	 * @param alpha
	 * @param duration
	 * @param movement
	 * @param onStop
	 * @param onCancel
	 */
	public static void setAlpha(AnimationRunner runner, Shell w, int alpha,
			int duration, IMovement movement, Runnable onStop,
			Runnable onCancel) {
		AlphaEffect effect = new AlphaEffect(w, w.getAlpha(), alpha, duration,
				movement, onStop, onCancel);
		runner.runEffect(effect);
	}

	/**
	 * Add a listener that will fade the window when it get closed.
	 * 
	 * @param shell
	 * @param duration
	 * @param easing
	 * @deprecated Use {@link #fadeOnClose(Shell,int,IMovement,AnimationRunner)}
	 *             instead
	 */
	public static void fadeOnClose(final Shell shell, final int duration,
			final IMovement easing) {
		fadeOnClose(shell, duration, easing, null);
	}

	/**
	 * Add a listener that will fade the window when it get closed.
	 * 
	 * @param shell
	 * @param duration
	 * @param easing
	 * @param runner
	 *            : The AnimationRunner to use, or null
	 * 
	 */
	public static void fadeOnClose(final Shell shell, final int duration,
			final IMovement easing, AnimationRunner runner) {

		final AnimationRunner useRunner;
		if (runner != null) {
			useRunner = runner;
		} else {
			useRunner = new AnimationRunner();
		}

		final Runnable closeListener = () -> shell.dispose();

		shell.addListener(SWT.Close, e -> {
			e.doit = false;
			setAlpha(useRunner, shell, 0, duration, easing, closeListener,
					null);
		});

	}

	int start, end, step;

	Shell shell = null;

	public AlphaEffect(Shell shell, int start, int end, long lengthMilli,
			IMovement movement, Runnable onStop, Runnable onCancel) {
		super(lengthMilli, movement, onStop, onCancel);

		this.start = start;
		this.end = end;
		step = end - start;
		this.shell = shell;
		easingFunction.init(0, 1, (int) lengthMilli);

	}

	public void applyEffect(final long currentTime) {
		if (shell.isDisposed())
			return;

		shell.setAlpha((int) (start
				+ step * easingFunction.getValue((int) currentTime)));
	}

}