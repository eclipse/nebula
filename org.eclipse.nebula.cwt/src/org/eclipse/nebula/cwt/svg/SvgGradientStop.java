package org.eclipse.nebula.cwt.svg;



public class SvgGradientStop extends SvgElement {

	/**
	 * always a value between 0 and 1
	 */
	Float offset;
	Integer color;
	Float opacity;
	
	SvgGradientStop(SvgGradient gradient, String id) {
		super(gradient.getContainer(), id);
	}
	
	int alpha() {
		return (int) (255 * opacity);
	}
	
	int blue() {
		return color & 0x0000FF;
	}
	
	int green() {
		return (color & 0x00FF00) >> 8;
	}
	
	int red() {
		return color >> 16;
	}
	
}
