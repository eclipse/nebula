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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * Painter for a Vlabel object
 */
public class VLabelPainter extends VControlPainter {

	@Override
	public void paintContent(VControl control, Event e) {
		if (control.hasStyle(SWT.SEPARATOR)) {
			Display display = control.getDisplay();
			Rectangle bounds = control.getBounds();
			if (control.hasStyle(SWT.HORIZONTAL)) {
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
				e.gc.drawLine(bounds.x - 1, bounds.y, bounds.x + bounds.width, bounds.y);
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
				e.gc.drawLine(bounds.x + 1, bounds.y + 1, bounds.x + bounds.width - 2, bounds.y + 1);
			} else {
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
				e.gc.drawLine(bounds.x, bounds.y - 1, bounds.x, bounds.y + bounds.height);
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
				e.gc.drawLine(bounds.x + 1, bounds.y + 1, bounds.x + 1, bounds.y + bounds.height - 2);
			}
		} else {
			super.paintContent(control, e);
		}
	}
}
