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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class VStackLayout extends VLayout {

	private VControl defaultControl;
	private VControl topControl;
	
	@Override
	protected Point computeSize(VPanel panel, int wHint, int hHint, boolean flushCache) {
		if(topControl == null) {
			VControl[] children = panel.getChildren();
			if(children.length > 0) {
				topControl = children[0];
			}
		}
		if(topControl != null) {
			return topControl.computeSize(wHint, hHint);
		}
		return new Point(0,0);
	}

	@Override
	protected void layout(VPanel panel, boolean flushCache) {
		if(topControl == null) {
			VControl[] children = panel.getChildren();
			if(children.length > 0) {
				topControl = children[0];
			}
		}
		if(topControl != null) {
			Rectangle bounds = panel.getBounds();
			topControl.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	public void setDefault(VControl control, boolean override) {
		if(override || defaultControl == null) {
			defaultControl = control;
		}
	}
	
	public void setTopControl(VControl control) {
		setTopControl(control, 0);
	}
	
	public void setTopControl(VControl control, int duration) {
		if(defaultControl == null) {
			defaultControl = control;
		}
		if(control == null) {
			control = defaultControl;
		}

		if(control != topControl) {
			VPanel parent = control.getParent();
			for(VControl child : parent.getChildren()) {
				if(child != control && child != topControl) {
					child.setVisible(false);
				}
			}

			if(topControl != null) {
				if(duration > 0) {
					control.moveBelow(topControl);
					topControl.setVisible(false, duration);
					control.setVisible(true, duration);
				} else {
					topControl.setVisible(false);
					control.setVisible(true);
				}
			}
	
			topControl = control;

			parent.layout(true);
		}
	}
	
}
