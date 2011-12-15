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
package org.eclipse.nebula.cwt.svg;

import org.eclipse.swt.graphics.Color;

class SvgFill extends SvgPaint {

	Integer rule = null;

	SvgFill(SvgGraphic parent) {
		super(parent);
	}
	
	void apply() {
		if(paintServer != null) {
			paintServer.apply(false);
		} else {
			Color c = new Color(gc.getDevice(), color >> 16, (color & 0x00FF00) >> 8, color & 0x0000FF);
			gc.setBackground(c);
			c.dispose();
			gc.setFillRule(rule);
			gc.setAlpha((int)(255 * opacity));
		}
	}

}
