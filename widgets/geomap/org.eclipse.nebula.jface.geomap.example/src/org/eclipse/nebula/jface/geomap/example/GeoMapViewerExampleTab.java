/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    hal - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.jface.geomap.example;

import org.eclipse.swt.SWT;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.jface.geomap.GeoMapViewer;
import org.eclipse.nebula.jface.geomap.GoogleIconDescriptor;
import org.eclipse.nebula.jface.geomap.LocationProvider;
import org.eclipse.nebula.jface.geomap.PinPointProvider;
import org.eclipse.nebula.widgets.geomap.PointD;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class GeoMapViewerExampleTab extends AbstractExampleTab {

	private GeoMapViewer geoMapViewer;

	public GeoMapViewerExampleTab() {
	}

	public Control createControl(Composite parent) {
		geoMapViewer = new GeoMapViewer(parent, SWT.NONE);
		configureMapViewer();
		geoMapViewer.getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (imageRegistry != null) {
					imageRegistry.dispose();
				}
			}
		});
		return geoMapViewer.getControl();
	}

	public String[] createLinks() {
		return new String[] {
				"<a href=\"http://www.eclipse.org/nebula/widgets/geomap/geomap.php\">GeoMap Home Page</a>",
				"<a href=\"http://www.eclipse.org/nebula/widgets/geomap/snippets.php\">Snippets</a>",
				"<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Nebula&component=GeoMap&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=\">Bugs</a>" };
	}

	@Override
	public void createParameters(Composite parent) {
        GridLayoutFactory.swtDefaults().margins(0,0).numColumns(1).applyTo(parent);

		Group group = new Group(parent, SWT.NONE);
		group.setText("Interaction");
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		group.setLayout(new GridLayout(2, false));

		Label moveSelectionModeLabel = new Label(group, SWT.NONE);
		moveSelectionModeLabel.setText("Move selection mode: ");
		moveSelectionModeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		final Combo moveSelectionModeControl = new Combo(group, SWT.CHECK);
		moveSelectionModeControl.setItems(new String[]{"Cannot move selection", "Allow, check readonly on mouse down", "Allow, just try to set new location"});
		moveSelectionModeControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		moveSelectionModeControl.select(1);
		moveSelectionModeControl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				geoMapViewer.setMoveSelectionMode(moveSelectionModeControl.getSelectionIndex());
			}
		});
	}
	
	private ImageRegistry imageRegistry;
	
	private ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry();
			imageRegistry.put("Gl¿shaugen", GoogleIconDescriptor.letterPin('G', false, new RGB(180,   0, 0)));
			imageRegistry.put("Dragvoll", 	GoogleIconDescriptor.letterPin('D', false, new RGB(  0, 180, 0)));
		}
		return imageRegistry;
	}

	private String[] locationNames = {"Gl¿shaugen", "Dragvoll"};
	private PointD[] locationPoints = {new PointD(10.40272, 63.41861), new PointD(10.46976, 63.40857)}; 
	
	private int indexOfLocation(Object element) {
		for (int i = 0; i < locationNames.length; i++) {
			if (element.equals(locationNames[i])) {
				return i;
			}
		}
		return -1;
	}
	
	private void configureMapViewer() {
		geoMapViewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return locationNames;
			}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			public void dispose() {}
		});
		final ImageRegistry imageRegistry = getImageRegistry();
		geoMapViewer.setLabelProvider(new PinPointProvider() {
			@Override
			public Image getImage(Object element) {
				return imageRegistry.get(element.toString());
			}
			@Override
			public Point getPinPoint(Object element) {
				return getPinPoint(element, 0.5f, 1.0f);
			}
		});
		geoMapViewer.setLocationProvider(new LocationProvider() {
			public PointD getLonLat(Object element) {
				int pos = indexOfLocation(element);
				return pos >= 0 ? locationPoints[pos] : null;
			}
			public boolean setLonLat(Object element, double lon, double lat) {
				int pos = indexOfLocation(element);
				if (pos > 0) {
					locationPoints[pos] = new PointD(lon, lat);
					return true;
				}
				return false;
			}
		});
		geoMapViewer.getGeoMap().setZoom(13);
		geoMapViewer.getGeoMap().getDisplay().asyncExec(new Runnable() {
			public void run() {
				geoMapViewer.setSelection(new StructuredSelection("Gl¿shaugen"), true);
			}
		});
	}
}
