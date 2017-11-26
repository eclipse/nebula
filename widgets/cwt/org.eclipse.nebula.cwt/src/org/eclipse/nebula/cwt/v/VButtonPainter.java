/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.cwt.v;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

/**
 * Class to paint a button.
 *
 */
public class VButtonPainter extends VControlPainter {

	@Override
	public void paintBackground(VControl control, Event e) {
		VButton button = (VButton) control;
		if (button.hasState(VControl.STATE_ACTIVE | VControl.STATE_SELECTED)) {
			if (e.gc.getAlpha() == 255) {
				doPaintBackground(e.gc, button.bounds, button.getState());
			} else {
				Image img = new Image(e.display, new Rectangle(0, 0,
						button.bounds.width, button.bounds.height));
				GC gc = new GC(img);
				doPaintBackground(gc, img.getBounds(), button.getState());
				e.gc.drawImage(img, button.bounds.x, button.bounds.y);
				gc.dispose();
				img.dispose();
			}
		} else {
			super.paintBackground(control, e);
		}
	}

	/**
	 * @param gc
	 * @param bounds
	 * @param state
	 */
	private void doPaintBackground(GC gc, Rectangle bounds, int state) {
		Color outline = null, bkBlue = null;

		if ((state & VControl.STATE_SELECTED) == VControl.STATE_SELECTED) {
			outline = new Color(gc.getDevice(), 1, 85, 153);
			bkBlue = new Color(gc.getDevice(), 204, 228, 247);
		} else if ((state & VControl.STATE_ACTIVE) == VControl.STATE_ACTIVE) {
			outline = new Color(gc.getDevice(), 1, 121, 215);
			bkBlue = new Color(gc.getDevice(), 229, 241, 251);
		}

		if (outline != null && bkBlue != null) {
			gc.setBackground(bkBlue);
			gc.setForeground(outline);

			gc.fillRectangle(0, 0, bounds.width - 1, bounds.height - 1);
			gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);

			outline.dispose();
			bkBlue.dispose();
		}
	}
}
