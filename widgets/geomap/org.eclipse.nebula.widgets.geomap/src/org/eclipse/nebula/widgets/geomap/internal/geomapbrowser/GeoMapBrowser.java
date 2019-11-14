/*******************************************************************************
 * Copyright (c) 2008, 2012 Stepan Rutz.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stepan Rutz - initial implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.geomap.internal.geomapbrowser;

import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.nebula.widgets.geomap.GeoMapListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author stepan.rutz@gmx.de
 * @version $Revision$
 */
public class GeoMapBrowser extends Composite {

	private SashForm sashForm;
	private PageContainer pageContainer;
	private GeoMap geoMap;
	private SearchPage searchPage;
	private ResultsPage resultsPage;
	private InfoPage infoPage;

	public GeoMapBrowser(Composite parent, int style) {
		super(parent, style);

		setLayout(new FillLayout());

		sashForm = new SashForm(this, SWT.HORIZONTAL);
		sashForm.setLayout(new FillLayout());

		pageContainer = new PageContainer(sashForm, SWT.NONE);
		geoMap = new GeoMap(sashForm, SWT.NONE);

		sashForm.setWeights(new int[] { 100, 200 });

		searchPage = new SearchPage(this);
		resultsPage = new ResultsPage(this);
		infoPage = new InfoPage(this);
		pageContainer.setPages(searchPage, resultsPage, infoPage);
		pageContainer.showPage(0);

		geoMap.addGeoMapListener(new GeoMapListener() {
			@Override
			public void zoomChanged(GeoMap geoMap) {
				infoPage.updateInfos();
			}

			@Override
			public void centerChanged(GeoMap geoMap) {
				infoPage.updateInfos();
			}
		});

		geoMap.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				infoPage.updateInfos();
			}
		});
		geoMap.addMouseMoveListener(e -> infoPage.updateInfos());
	}

	public void createMenu(Shell shell) {
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
		fileItem.setText("&File");
		Menu submenu = new Menu(shell, SWT.DROP_DOWN);
		fileItem.setMenu(submenu);
		MenuItem item = new MenuItem(submenu, SWT.PUSH);
		item.addListener(SWT.Selection, e -> Runtime.getRuntime().halt(0));
		item.setText("E&xit\tCtrl+W");
		item.setAccelerator(SWT.MOD1 + 'W');
	}

	GeoMap getGeoMap() {
		return geoMap;
	}

	PageContainer getPageContainer() {
		return pageContainer;
	}

	Page getInfoPage() {
		return infoPage;
	}

	ResultsPage getResultsPage() {
		return resultsPage;
	}
}
