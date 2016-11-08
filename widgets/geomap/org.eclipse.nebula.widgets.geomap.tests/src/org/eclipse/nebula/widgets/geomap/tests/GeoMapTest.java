package org.eclipse.nebula.widgets.geomap.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.nebula.widgets.geomap.internal.GeoMapPositioned;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GeoMapTest {

	protected Widget createUI(Display display) {
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("GeoMap Widget - SWT Native Map Browsing, Map data from openstreetmap.org");
		shell.setSize(600, 710);
		shell.setLocation(10, 10);
		shell.setLayout (new FillLayout());
		
		createControls(shell);
		shell.open();
		
		return shell;
	}

	protected void createControls(Shell shell) {
		new GeoMap(shell, SWT.NONE);
	}

	private Display display;
	private Widget parent;
	
	private SWTBot bot;
	private GeoMap geoMap;
	private GeoMapPositioned geoMapPositioned;
	private SWTBotGeoMap geoMapBot;
	
	@Before
	public void setUp() {
		SWTBotPreferences.PLAYBACK_DELAY = 1000; // slow down tests...Otherwise we won't see anything
		display = Display.getCurrent();
		parent = createUI(display);
		
		bot = new SWTBot(parent);
		geoMapPositioned = geoMap = bot.widget(Is.isA(GeoMap.class));
		geoMapBot = new SWTBotGeoMap(geoMap);
	}
	
	@After
	public void tearDown() {
		handleEvents();
//		display.dispose();
	}
	
	protected void handleEvents() {
		while (display != null && (! display.isDisposed()) && (! parent.isDisposed())) {
			if (! display.readAndDispatch()) {
				break; // display.sleep();
			}
		}
	}

	@Test
	public void testPan() {
		Point position1 = geoMapPositioned.getMapPosition();
		int vx = 0, vy = 0, dx = 10, dy = 20;
		geoMapBot.pan(vx, vy, vx + dx, vy + dy);
		assertEquals(new Point(position1.x - dx, position1.y - dy), geoMapPositioned.getMapPosition());
		geoMapBot.pan(vx + dx, vy + dy, vx, vy);
		assertEquals(position1, geoMapPositioned.getMapPosition());
	}

	@Test
	public void testCenter() {
		Point mapPosition = geoMapPositioned.getMapPosition();
		int dx = 10, dy = 20;
		geoMapBot.center(dx, dy);
		assertEquals(new Point(mapPosition.x + dx, mapPosition.y + dy), geoMap.getCenterPosition());
	}
	
	protected void testMapPositionZoom(int x1, int y1, int z1, int x2, int y2, int z2, int vx, int vy) {
		// check zoom level
		assertEquals(z1 + 1, z2);
		// check map position
		assertEquals(new Point(x1 + vx, y1 + vy), new Point((x2 + vx) / 2, (y2 + vy) / 2));
	}
	
	protected void testMapPositionZoom(Point p1, int z1, Point p2, int z2, int vx, int vy) {
		testMapPositionZoom(p1.x, p1.y, z1, p2.x, p2.y, z2, vx, vy);
	}
	
	protected void testZoomInPoint(int vx, int vy) {
		Point position1 = geoMapPositioned.getMapPosition();
		int zoom = geoMapPositioned.getZoom();
		geoMapBot.zoomIn(vx, vy);
		testMapPositionZoom(position1, zoom, geoMapPositioned.getMapPosition(), geoMapPositioned.getZoom(), vx, vy);
	}

	@Test
    public void testZoomInPoint() {
		Point size = geoMap.getSize();
		testZoomInPoint(size.x / 2, size.y / 2);
    }

	protected void testZoomInRectangle(int x1, int y1, int x2, int y2) {
		Point position1 = geoMapPositioned.getMapPosition(), size = geoMap.getSize();
		int zoom = geoMapPositioned.getZoom();
		geoMapBot.zoomIn(x1, y1, x2, y2);
		// check zoom level
		assertEquals(zoom + 1, geoMapPositioned.getZoom());
		// check map position
		Point position2 = geoMapPositioned.getMapPosition();
		// the center of the rectangle
		Point center1 = new Point(position1.x + (x1 + x2) / 2, position1.y + (y1 + y2) / 2);
		// the center of the new viewport (divided by the zoom factor)
		Point center2 = new Point((position2.x + size.x / 2) / 2, (position2.y + size.y / 2) / 2);
		assertEquals(center1, center2);
	}
	
    @Test
    public void testZoomInRectangle() {
    	Point size = geoMap.getSize();
    	testZoomInRectangle(size.x / 4 + 5, size.y / 4 + 5, size.x * 3 / 4 - 5, size.y * 3 / 4 - 5);
    }
    
    @Test
    public void testZoomOut() {
    	Point position = geoMapPositioned.getMapPosition(), size = geoMap.getSize();
    	int zoom = geoMapPositioned.getZoom();
    	int vx = size.x / 2, vy = size.y / 2;
		geoMapBot.zoomOut(size.x / 2, size.y / 2);
		// check zoom level
    	assertEquals(zoom - 1, geoMapPositioned.getZoom());
    	// check map position
    	testMapPositionZoom(geoMapPositioned.getMapPosition(), geoMapPositioned.getZoom(), position, zoom, vx, vy);
    }
}
