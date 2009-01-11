package org.eclipse.nebula.cwt.svg;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class SvgFragment extends SvgContainer {

	Float x;
	Float y;
	Float width;
	Float height;
	/**
	 * viewBox[0] == x
	 * viewBox[1] == y
	 * viewBox[2] == w
	 * viewBox[3] == h
	 */
	float[] viewBox;
	SvgTransform boundsTransform;
	boolean preserveAspectRatio;
	
	private Map<String, SvgElement> elementMap;

	SvgFragment(SvgContainer container, String id) {
		super(container, id);
		elementMap = new HashMap<String, SvgElement>();
		boundsTransform = new SvgTransform();
		boundsTransform.data = new float[] { 1, 0, 0, 1, 0, 0 };
	}
	
	public void apply(GC gc, Rectangle bounds) {
		if(viewBox != null) {
			float sx = bounds.width / viewBox[2];
			float sy = bounds.height / viewBox[3];
			boundsTransform.scale(sx, sy);
		} else if(width != null && height != null){
			float sx = bounds.width / width;
			float sy = bounds.height / height;
			boundsTransform.scale(sx, sy);
		}
		super.apply(gc);
		boundsTransform.data = new float[] { 1, 0, 0, 1, 0, 0 };
	}
	
	public SvgElement getElement(String id) {
		return elementMap.get(id);
	}
	
	@Override
	public SvgFragment getFragment() {
		return this;
	}
	
	public Map<String, String> getStyles(String className) {
		SvgElement element = elementMap.get("style");
		if(element instanceof SvgStyle) {
			Map<String, Map<String, String>> classes = ((SvgStyle) element).styles;
			if(classes != null) {
				return classes.get(className);
			}
		}
		return null;
	}

	@Override
	public float[] getViewport() {
		if(x == null || y == null) {
			return new float[] { 0, 0, width, height };
		} else {
			return new float[] { x, y, width, height };
		}
	}
	
	public boolean hasElement(String id) {
		return elementMap.containsKey(id);
	}

	public boolean isOutermost() {
		return getContainer() == null;
	}
	
	public void put(SvgElement element) {
		String id = element.getId();
		if(id != null) {
			elementMap.put(id, element);
		}
	}
	
}
