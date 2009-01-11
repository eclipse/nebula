package org.eclipse.nebula.cwt.svg;

import java.util.ArrayList;
import java.util.List;



public class SvgElement {

	static List<SvgElement> getAncestry(SvgElement element) {
		List<SvgElement> l = new ArrayList<SvgElement>();
		l.add(element);
		SvgElement parent = element.getContainer();
		while(parent != null) {
			l.add(0, parent);
			parent = parent.getContainer();
		}
		return l;
	}

	private SvgContainer container;
	private String id;

	SvgElement(SvgContainer container, String id) {
		this.container = container;
		this.id = id;
		if(container != null) {
			container.add(this);
			if(!(this instanceof SvgFragment)) {
				container.getFragment().put(this);
			}
		}
	}

	final SvgContainer getContainer() {
		return container;
	}

	SvgElement getElement(String id) {
		return getFragment().getElement(id);
	}
	
	SvgFragment getFragment() {
		if(container != null) {
			return container.getFragment();
		}
		return null;
	}
	
	public final String getId() {
		return id;
	}

	float[] getViewport() {
		return container.getViewport();
	}

	void setContainer(SvgContainer container) {
		this.container = container;
	}
	
}
