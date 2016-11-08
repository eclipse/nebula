package org.eclipse.nebula.widgets.geomap.tests;

import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;

public class SWTBotGeoMap extends SWTBotCanvas<GeoMap> {

	public SWTBotGeoMap(GeoMap geoMap) throws WidgetNotFoundException {
		super(geoMap);
	}

	public void pan(int x1, int y1, int x2, int y2) {
		mouseDrag(x1, y1, x2, y2, SWT.BUTTON1, 1);
	}
	
	public void center(int x, int y) {
		mouseClick(x, y, SWT.BUTTON1 | SWT.CTRL, 1);
	}
	
	public void zoomIn(int x, int y) {
		mouseClick(x, y, SWT.BUTTON1, 1);
	}
	
	public void zoomOut(int x, int y) {
		mouseClick(x, y, SWT.BUTTON3, 1);
	}
	
	public void zoomIn(int x1, int y1, int x2, int y2) {
		mouseDrag(x1, y1, x2, y2, SWT.BUTTON1 | SWT.SHIFT, 1);
	}
}
