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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * Shake a control (like a login failure on Mac OSX)
 * 
 * @author Nicolas Richeton
 * 
 */
public class ShakeEffect extends AbstractEffect {

	/**
	 * @deprecated
	 * @param w
	 * @param duration
	 * @param movement
	 * @param onStop
	 * @param onCancel
	 */
	public static void shake(AnimationRunner runner, Control w, int duration,
			IMovement movement, Runnable onStop, Runnable onCancel) {
		IEffect effect = new ShakeEffect(w, w.getLocation(), new Point(w
				.getLocation().x + 10, w.getLocation().y + 10), duration,
				movement, onStop, onCancel);
		runner.runEffect(effect);
	}

	Point src, dest, diff;

	Control control = null;

	public ShakeEffect(Control control, Point src, Point dest,
			long lengthMilli, IMovement movement, Runnable onStop,
			Runnable onCancel) {
		super(lengthMilli, movement, onStop, onCancel);
		this.src = src;
		this.dest = dest;
		this.control = control;
		this.diff = new Point(dest.x - src.x, dest.y - src.y);

		easingFunction.init(0, 1, (int) lengthMilli);
	}

	public void applyEffect(final long currentTime) {
		if (!control.isDisposed()) {
			control.setLocation((int) (src.x - diff.x
					* easingFunction.getValue(currentTime)), src.y);
		}
	}
}