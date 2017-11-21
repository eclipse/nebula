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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

public class VButtonPainter extends VControlPainter {

	@Override
	public void paintBackground(VControl control, Event e) {
		VButton button = (VButton) control;
		if(!button.paintNative) {
			if(!button.paintInactive || button.hasState(VButton.STATE_ACTIVE | VButton.STATE_SELECTED)
					|| (button == VTracker.getFocusControl())) {
				int state = 0;
				int style = SWT.PUSH;
				if(button.hasState(VButton.STATE_SELECTED)) {
					state |= VButton.HOT;
					state |= VButton.PRESSED;
				} else if(button.hasState(VButton.STATE_ACTIVE)) {
					state |= VButton.HOT;
				}
				if(button == VTracker.getFocusControl()) {
					state |= VButton.FOCUSED;
				}
				if(e.gc.getAlpha() == 255) {
					drawBackground(e.gc, button.bounds, state, style);
				} else {
					// Bug 260624 - ButtonDrawData#draw does not respect alpha setting
					Image img = new Image(e.display, new Rectangle(0, 0, button.bounds.width, button.bounds.height));
					GC gc = new GC(img);
					drawBackground(gc, img.getBounds(), state, style);
					e.gc.drawImage(img, button.bounds.x, button.bounds.y);
					gc.dispose();
					img.dispose();
				}
			} else {
				super.paintBackground(control, e);
			}
		} else {
			super.paintBackground(control, e);
		}
	}

	/**
	 * @param gc
	 * @param bounds
	 * @param state
	 * @param style
	 */
	private void drawBackground(GC gc, Rectangle bounds,  int state, int style) {
		
		if((state & VButton.STATE_SELECTED) == VButton.STATE_SELECTED) {
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.fillRoundRectangle(bounds.x, bounds.y, bounds.height, bounds.width, 5, 5);
		}
	}
}
