package org.eclipse.nebula.cwt.svg;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class SvgDocument extends SvgContainer {

	public static SvgDocument load(InputStream in) {
		return SvgLoader.load(in);
	}

	public static SvgDocument load(String src) {
		return SvgLoader.load(src);
	}

	private Map<String, SvgFragment> fragmentMap;

	SvgDocument() {
		super(null, null);
		fragmentMap = new HashMap<String, SvgFragment>(3);
	}

	@Override
	void add(SvgElement element) {
		if(element instanceof SvgFragment) {
			elements.add((SvgFragment) element);
			fragmentMap.put(element.getId(), (SvgFragment) element);
		}
	}
	
	public void apply(GC gc, Rectangle bounds) {
		for(SvgElement element : elements) {
			((SvgFragment) element).apply(gc, bounds);
		}
	}

	@Override
	public String getDescription() {
		return elements.isEmpty() ? null : ((SvgFragment) elements.get(0)).getDescription();
	}
	
	public SvgFragment getFragment() {
		return elements.isEmpty() ? null : (SvgFragment) elements.get(0);
	}

	public SvgFragment getFragment(String id) {
		return fragmentMap.get(id);
	}

	public SvgFragment[] getFragments() {
		return elements.toArray(new SvgFragment[elements.size()]);
	}

	@Override
	public String getTitle() {
		return elements.isEmpty() ? null : ((SvgFragment) elements.get(0)).getTitle();
	}
	
	public boolean hasFragment(String id) {
		return fragmentMap.containsKey(id);
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

}
