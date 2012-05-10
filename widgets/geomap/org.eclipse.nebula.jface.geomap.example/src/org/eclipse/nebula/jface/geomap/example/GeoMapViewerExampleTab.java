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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.jface.geomap.GeoMapViewer;
import org.eclipse.nebula.jface.geomap.LabelImageProvider;
import org.eclipse.nebula.jface.geomap.LocationProvider;
import org.eclipse.nebula.widgets.geomap.PointD;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class GeoMapViewerExampleTab extends AbstractExampleTab {

	private GeoMapViewer geoMapViewer;

	public Control createControl(Composite parent) {
		geoMapViewer = new GeoMapViewer(parent, SWT.NONE);
		configureMapViewer();
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
	
	@Override
	public boolean getInitialHorizontalFill() {
		return true;
	}

	@Override
	public boolean getInitialVerticalFill() {
		return true;
	}

	private static class ContributorLocation {
		public final String name;
		public final PointD location;
		public final String locationText;
		public ContributorLocation(String name, PointD location, String locationText) {
			super();
			this.name = name;
			this.location = location;
			this.locationText = locationText;
		}
	}
	
	private ContributorLocation[] contributorLocations = {
			new ContributorLocation("Hallvard Traetteberg", new PointD(10.33,63.45), 	"Trondheim, Norway"),
			new ContributorLocation("Stepan Rutz",		 	new PointD(6.78,50.93), 	"Frechen, Germany"),
			new ContributorLocation("Wim Jongman", 			new PointD(4.61,52.4), 		"Haarlem, Netherlands"),
			new ContributorLocation("Dirk Fauth", 			new PointD(8.94,48.89), 	"Stuttgart, Germany"),
			new ContributorLocation("Tom Schindl", 			new PointD(11.36,47.28), 	"Innsbruck, Austria"), 
	};
	
	private int indexOfLocation(Object element) {
		for (int i = 0; i < contributorLocations.length; i++) {
			if (element == contributorLocations[i]) {
				return i;
			}
		}
		return -1;
	}
	
	private void configureMapViewer() {
		geoMapViewer.setLabelProvider(new LabelImageProvider() {
			@Override
			public String getText(Object element) {
				return ((ContributorLocation) element).name;
			}
			@Override
			public Object getToolTip(Object element) {
				if (element instanceof ContributorLocation) {
					ContributorLocation contributorLocation = (ContributorLocation) element;
					return contributorLocation.name + " @ " + contributorLocation.locationText;
				}
				return null;
			}
		});
		geoMapViewer.setLocationProvider(new LocationProvider() {
			public PointD getLonLat(Object element) {
				int pos = indexOfLocation(element);
				return pos >= 0 ? contributorLocations[pos].location : null;
			}
			public boolean setLonLat(Object element, double lon, double lat) {
				return false;
			}
		});
		geoMapViewer.setContentProvider(new ArrayContentProvider());
		geoMapViewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				geoMapViewer.setInput(contributorLocations);
			}
		});
	}
}
