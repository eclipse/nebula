package org.eclipse.nebula.widgets.geomap.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.nebula.widgets.geomap.GeoMapUtil;
import org.eclipse.nebula.widgets.geomap.PointD;
import org.eclipse.nebula.widgets.geomap.jface.GeoMapViewer;
import org.eclipse.nebula.widgets.geomap.jface.LabelImageProvider;
import org.eclipse.nebula.widgets.geomap.jface.LocationProvider;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GeoMapViewerTest {

	protected Widget createUI(Display display) {
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("GeoMap Widget - SWT Native Map Browsing, Map data from openstreetmap.org");
		shell.setSize(600, 710);
		shell.setLocation(10, 10);
		shell.setLayout(new FillLayout());

		createControls(shell);
		shell.open();

		return shell;
	}

	private GeoMapViewer viewer;
	private Map<Object, PointD> locations;
	private LabelImageProvider labelProvider;

	protected void createControls(Shell shell) {
		viewer = new GeoMapViewer(shell, SWT.NONE);
		locations = new HashMap<>();
		initLocations();
		viewer.setLocationProvider(new LocationProvider() {
			@Override
			public boolean setLonLat(Object element, double lon, double lat) {
				locations.put(element, new PointD(lon, lat));
				return true;
			}

			@Override
			public PointD getLonLat(Object element) {
				return locations.get(element);
			}
		});
		viewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				Collection<?> col = null;
				if (inputElement instanceof Collection<?>) {
					col = (Collection<?>) inputElement;
				} else if (inputElement instanceof Map<?, ?>) {
					col = ((Map<?, ?>) inputElement).keySet();
				} else if (inputElement instanceof Object[]) {
					col = Arrays.asList((Object[]) inputElement);
				}
				return col.toArray();
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}
		});
		viewer.setInput(locations.keySet());
		labelProvider = new LabelImageProvider();
		viewer.setLabelProvider(labelProvider);

		// corresponds to viewer.zoomTo(locations.keySet().toArray(), 12);
		getGeoMap().setZoom(5);
		getGeoMap().setMapPosition(3967, 2136);

		viewer.addSelectionChangedListener(event -> GeoMapViewerTest.this.selection = event.getSelection());
	}

	private ISelection selection = null;
	private String me = "Hallvard Traetteberg";

	private void initLocations() {
		locations.put(me, new PointD(10.4234, 63.4242));
		locations.put("Stepan Rutz", new PointD(6.8222, 50.9178));
		locations.put("Wim Jongman", new PointD(4.6410, 52.3894));
	}

	private Display display;
	private Widget parent;

	private SWTBot bot;
	private SWTBotGeoMap geoMapBot;

	@Before
	public void setUp() {
		SWTBotPreferences.PLAYBACK_DELAY = 1000; // slow down tests...Otherwise we won't see anything
		display = Display.getCurrent();
		parent = createUI(display);

		bot = new SWTBot(parent);
		GeoMap geoMap = bot.widget(Is.isA(GeoMap.class));
		geoMapBot = new SWTBotGeoMap(geoMap);
	}

	@After
	public void tearDown() {
		handleEvents();
		// display.dispose();
	}

	protected void handleEvents() {
		while (display != null && !display.isDisposed() && !parent.isDisposed()) {
			if (!display.readAndDispatch()) {
				break; // display.sleep();
			}
		}
	}

	// for manual testing

	private void run() {
		display = Display.getDefault();
		parent = createUI(display);
		while (!parent.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		if (!display.isDisposed()) {
			display.dispose();
		}
	}

	public static void main(String[] args) {
		new GeoMapViewerTest().run();
	}

	//

	public GeoMap getGeoMap() {
		return viewer.getGeoMap();
	}

	@Test
	public void testSelectMe() {
		Point mapPosition = getGeoMap().getMapPosition();
		Point position = GeoMapUtil.computePosition(viewer.getLocationProvider().getLonLat(me), getGeoMap().getZoom());
		// should hit the bubble, which is above and to the right
		int dx = 20, dy = -20;
		geoMapBot.mouseClick(position.x - mapPosition.x + dx, position.y - mapPosition.y + dy, SWT.BUTTON1, 1);
		Assert.assertTrue(selection instanceof IStructuredSelection);
		Assert.assertEquals(me, ((IStructuredSelection) selection).getFirstElement());
	}

	@Test
	public void testDragMe() {
		Point mapPosition = getGeoMap().getMapPosition();
		Point position1 = GeoMapUtil.computePosition(viewer.getLocationProvider().getLonLat(me), getGeoMap().getZoom());
		// should hit the bubble, which is above and to the right
		int dx = 20, dy = -20, x0 = position1.x - mapPosition.x + dx, y0 = position1.y - mapPosition.y + dy;
		int mx = 50, my = 70, x1 = x0 + mx, y1 = y0 + my;
		geoMapBot.mouseDrag(x0, y0, x1, y1, SWT.BUTTON1, 1);
		Assert.assertTrue(selection instanceof IStructuredSelection);
		Assert.assertEquals(me, ((IStructuredSelection) selection).getFirstElement());

		Point position2 = GeoMapUtil.computePosition(viewer.getLocationProvider().getLonLat(me), getGeoMap().getZoom());
		// accept round-off errors
		Assert.assertEquals((double) mx, (double) (position2.x - position1.x), 1);
		Assert.assertEquals((double) my, (double) (position2.y - position1.y), 1);
	}
}
