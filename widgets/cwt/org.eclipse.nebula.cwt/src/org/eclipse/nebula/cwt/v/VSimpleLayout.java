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

public class VSimpleLayout extends VLayout {

	@Override
	protected Point computeSize(VPanel panel, int wHint, int hHint, boolean flushCache) {
		Point size = new Point(panel.marginLeft+panel.marginRight, panel.marginTop+panel.marginBottom);
		
		VControl[] children = panel.getChildren();
		if(children.length > 0) {
			Point childSize = children[0].computeSize(wHint, hHint, flushCache);
			size.x += childSize.x;
			size.y += childSize.y;
		}
		
		return size;
	}

	@Override
	protected void layout(VPanel panel, boolean flushCache) {
		VControl[] children = panel.getChildren();
		if(children.length > 0) {
			children[0].setBounds(panel.getClientArea());
			if(children[0] instanceof VPanel) {
				((VPanel) children[0]).layout(flushCache);
			}
		}
	}

}
