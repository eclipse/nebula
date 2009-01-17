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
import org.eclipse.swt.internal.theme.ButtonDrawData;
import org.eclipse.swt.internal.theme.DrawData;
import org.eclipse.swt.internal.theme.Theme;
import org.eclipse.swt.widgets.Event;

@SuppressWarnings("restriction")
public class VButtonPainter extends VControlPainter {

	@Override
	public void paintBackground(VControl control, Event e) {
		VButton button = (VButton) control;
		if(button.paintNative) {
			if(button.paintInactive || button.hasState(VButton.STATE_ACTIVE | VButton.STATE_SELECTED)
					|| (button == VTracker.getFocusControl())) {
				ButtonDrawData data = new ButtonDrawData();
				data.style = SWT.PUSH;
				if(button.hasState(VButton.STATE_SELECTED)) {
					data.state[0] |= DrawData.HOT;
					data.state[0] |= DrawData.PRESSED;
				} else if(button.hasState(VButton.STATE_ACTIVE)) {
					data.state[0] |= DrawData.HOT;
				}
				if(button == VTracker.getFocusControl()) {
					data.state[0] |= DrawData.FOCUSED;
				}
				if(e.gc.getAlpha() == 255) {
					Theme theme = new Theme(e.display);
					theme.drawBackground(e.gc, button.bounds, data);
					theme.dispose();
				} else {
					// Bug 260624 - ButtonDrawData#draw does not respect alpha setting
					Image img = new Image(e.display, new Rectangle(0, 0, button.bounds.width, button.bounds.height));
					GC gc = new GC(img);
					Theme theme = new Theme(e.display);
					theme.drawBackground(gc, img.getBounds(), data);
					e.gc.drawImage(img, button.bounds.x, button.bounds.y);
					theme.dispose();
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

}
