package org.eclipse.nebula.cwt.svg;

import org.eclipse.swt.graphics.Color;

public class SvgFill extends SvgPaint {

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
