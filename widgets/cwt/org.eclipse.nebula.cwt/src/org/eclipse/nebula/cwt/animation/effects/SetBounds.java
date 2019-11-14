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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public class SetBounds extends AbstractEffect {

	Rectangle src, dest, diff;

	Control control = null;

	public SetBounds(Control control, Rectangle src, Rectangle dest,
			long lengthMilli, IMovement movement, Runnable onStop,
			Runnable onCancel) {
		super(lengthMilli, movement, onStop, onCancel);
		this.src = src;
		this.dest = dest;
		this.control = control;
		this.diff = new Rectangle(dest.x - src.x, dest.y - src.y, dest.width
				- src.width, dest.height - src.height);

		easingFunction.init(0, 1, (int) lengthMilli);
	}

	public void applyEffect(final long currentTime) {
		if (!control.isDisposed()) {
			control.setBounds((int) (src.x + diff.x
					* easingFunction.getValue(currentTime)),
					(int) (src.y + diff.y
							* easingFunction.getValue(currentTime)),
					(int) (src.width + diff.width
							* easingFunction.getValue(currentTime)),
					(int) (src.height + diff.height
							* easingFunction.getValue(currentTime)));
		}
	}
}