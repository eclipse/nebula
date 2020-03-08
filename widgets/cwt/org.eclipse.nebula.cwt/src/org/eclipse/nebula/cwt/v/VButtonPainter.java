/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.cwt.v;

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

	private VButton button;

	@Override
	public void paintBackground(VControl control, Event e) {
		button = (VButton) control;
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
			outline = button.selectedBorderColor == null
					? button.defaultSelectedBorderColor
					: button.selectedBorderColor;
			bkBlue = button.selectedBackgroundColor == null
					? button.defaultSelectedBackgroundColor
					: button.selectedBackgroundColor;
		} else if ((state & VControl.STATE_ACTIVE) == VControl.STATE_ACTIVE) {
			outline = button.hoverBorderColor == null
					? button.defaultHoverBorderColor
					: button.hoverBorderColor;
			bkBlue = button.hoverBackgroundColor == null
					? button.defaultHoverBackgroundColor
					: button.hoverBackgroundColor;
		}

		if (outline != null && bkBlue != null) {
			gc.setBackground(bkBlue);
			gc.setForeground(outline);

			gc.fillRectangle(0, 0, bounds.width - 1, bounds.height - 1);
			gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);

		}
	}
}
