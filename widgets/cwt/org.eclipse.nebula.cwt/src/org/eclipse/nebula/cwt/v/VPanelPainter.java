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
import org.eclipse.swt.widgets.Event;

public class VPanelPainter implements IControlPainter {

	public void dispose() {
		// nothing to do
	}
	
	public void paintBackground(VControl control, Event e) {
		if(control.background != null && !control.background.isDisposed()) {
			e.gc.setBackground(control.background);
			e.gc.fillRectangle(control.bounds);
		}
	}
	
	public void paintBorders(VControl control, Event e) {
		if(control.hasStyle(SWT.BORDER)) {
			if(control.foreground != null) {
				e.gc.setForeground(control.foreground);
			} else {
				e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_WIDGET_BORDER));
			}
			e.gc.drawRectangle(control.bounds.x, control.bounds.y, control.bounds.width-1, control.bounds.height-1);
		}
	}
	
	public void paintContent(VControl control, Event e) {
		for(VControl child : ((VPanel) control).getChildren()) {
			child.paintControl(e);
		}
	}

}
