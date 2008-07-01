package org.eclipse.nebula.widgets.ganttchart;

import java.util.ArrayList;
import java.util.List;

public class HeaderSet {

	private List headers;
	
	public HeaderSet() {
		headers = new ArrayList();
	}
	
	public void addHeaderLevel(HeaderLevel hl) {
		headers.add(hl);
	}
	
	public void removeHeaderLevel(HeaderLevel hl) {
		headers.remove(hl);
	}
	
	public List getHeaderLevels() {
		return headers;
	}
}
