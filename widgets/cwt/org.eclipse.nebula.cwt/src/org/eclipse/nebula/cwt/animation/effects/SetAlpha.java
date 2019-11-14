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

import org.eclipse.nebula.cwt.animation.AnimationRunner;
import org.eclipse.nebula.cwt.animation.movement.IMovement;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Shell;

public class SetAlpha extends AbstractEffect {

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
			int duration, IMovement movement, Runnable onStop, Runnable onCancel) {
		SetAlpha effect = new SetAlpha(w, w.getAlpha(), alpha, duration,
				movement, onStop, onCancel);
		runner.runEffect(effect);
	}

	/**
	 * Add a listener that will fade the window when it get closed.
	 * 
	 * @param shell
	 * @param duration
	 * @param easing
	 */
	public static void fadeOnClose(final Shell shell, final int duration,
			final IMovement easing) {

		final Runnable closeListener = new Runnable() {
			public void run() {
				shell.dispose();
			}
		};

		shell.addShellListener(new ShellListener() {

			public void shellIconified(ShellEvent e) {
				// Do nothing
			}

			public void shellDeiconified(ShellEvent e) {
				// Do nothing
			}

			public void shellDeactivated(ShellEvent e) {
				// Do nothing
			}

			public void shellClosed(ShellEvent e) {
				e.doit = false;

				setAlpha(new AnimationRunner(), shell, 0, duration, easing,
						closeListener, null);
			}

			public void shellActivated(ShellEvent e) {
				// Do nothing
			}

		});

	}

	int start, end, step;

	Shell shell = null;

	public SetAlpha(Shell shell, int start, int end, long lengthMilli,
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

		shell.setAlpha((int) (start + step
				* easingFunction.getValue((int) currentTime)));
	}

}