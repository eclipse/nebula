package org.eclipse.nebula.cwt.svg;

import java.util.Map;

public class SvgStyle extends SvgElement {

	Map<String, Map<String, String>> styles;
	
	SvgStyle(SvgContainer container) {
		super(container, "style");
	}

}
