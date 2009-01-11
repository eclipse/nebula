package org.eclipse.nebula.cwt.svg;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;

public class SvgGradient extends SvgElement {

	static final int X1 = 0;
	static final int Y1 = 1;
	static final int X2 = 3;
	static final int Y2 = 4;

	static final int CX = 3;
	static final int CY = 4;
	static final int FX = 3;
	static final int FY = 4;
	static final int R = 5;

	static final int PAD = 0;
	static final int REFLECT = 1;
	static final int REPEAT = 2;

	float[] data;
	String linkId;
	List<SvgGradientStop> stops;
	
	GC gc;
	float[] bounds;
	Pattern pattern;
	boolean boundingBox = true;
	int spreadMethod = PAD;
	SvgTransform transform;

	SvgGradient(SvgContainer container, String id) {
		super(container, id);
		stops = new ArrayList<SvgGradientStop>();
	}

	public void apply(boolean foreground) {
		SvgGradientStop[] stops = getStops();
		if(stops.length == 1) {
			apply(gc, stops[0], foreground);
		} else if(stops.length > 1) {
			if(pattern == null) {
				apply(gc, stops[stops.length-1], foreground);
			} else {
				if(foreground) {
					gc.setForegroundPattern(pattern);
				} else {
					gc.setBackgroundPattern(pattern);
				}
			}
		}
	}
	
	private void apply(GC gc, SvgGradientStop stop, boolean foreground) {
		Color c = createColor(gc, stop.color);
		if(foreground) {
			gc.setForeground(c);
		} else {
			gc.setBackground(c);
		}
		c.dispose();
		gc.setAlpha((int)(255 * stop.opacity));
	}

	public void create(SvgShape shape, GC gc) {
		this.gc = gc;
		if(boundingBox) {
			bounds = shape.getBounds();
		} else {
			bounds = shape.getViewport();
		}
		pattern = SwtAdapter.createPattern(this);
	}

	private Color createColor(GC gc, int color) {
		return new Color(gc.getDevice(), color >> 16, (color & 0x00FF00) >> 8, color & 0x0000FF);
	}

	public void dispose() {
		if(pattern != null) {
			gc.setBackgroundPattern(null);
			pattern.dispose();
		}
		gc = null;
		bounds = null;
		pattern = null;
	}

	public SvgGradientStop[] getStops() {
		if(linkId != null) {
			Object def = getFragment().getElement(linkId);
			if(def instanceof SvgGradient) {
				SvgGradientStop[] linkStops = ((SvgGradient) def).getStops();
				if(linkStops.length > 0) {
					return linkStops;
				}
			}
		}
		return stops.toArray(new SvgGradientStop[stops.size()]);
	}

	public SvgTransform getTransform() {
		return transform;
	}

	void setLinkId(String id) {
		if(id != null && id.length() > 2 && '#' == id.charAt(0)) {
			linkId = id.substring(1);
		} else {
			linkId = null;
		}
	}
	
	public void setSpreadMethod(String s) {
		if(s != null) {
			if("pad".equals(s)) {
				spreadMethod = PAD;
			} else if("reflect".equals(s)) {
				spreadMethod = REFLECT;
			} else if("repeat".equals(s)) {
				spreadMethod = REPEAT;
			}
		}
	}
	
	public void setTransform(SvgTransform transform) {
		this.transform = transform;
	}
	
	public void setUnits(String s) {
		if(s != null) {
			boundingBox = "objectBoundingBox".equals(s);
		}
	}
	
}
