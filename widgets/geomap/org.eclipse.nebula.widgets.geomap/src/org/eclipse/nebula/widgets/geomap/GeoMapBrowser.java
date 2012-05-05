/*******************************************************************************
 * Copyright (c) 2008, 2012 Stepan Rutz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stepan Rutz - initial implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.geomap;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.nebula.widgets.geomap.internal.InfoPage;
import org.eclipse.nebula.widgets.geomap.internal.PageContainer;
import org.eclipse.nebula.widgets.geomap.internal.ResultsPage;
import org.eclipse.nebula.widgets.geomap.internal.SearchPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
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

        geoMap.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                infoPage.updateInfos();
            }
        });
        geoMap.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                infoPage.updateInfos();
            }
        });
        geoMap.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {
                infoPage.updateInfos();
            }
        });
    }

    public GeoMap getGeoMap() {
        return geoMap;
    }

    public SearchPage getSearchPage() {
        return searchPage;
    }
    
    public InfoPage getInfoPage() {
        return infoPage;
    }
    
    public ResultsPage getResultsPage() {
        return resultsPage;
    }
    
    public PageContainer getPageContainer() {
        return pageContainer;
    }
    
    private void createMenu(Shell shell) {
        Menu bar = new Menu (shell, SWT.BAR);
        shell.setMenuBar (bar);
        MenuItem fileItem = new MenuItem (bar, SWT.CASCADE);
        fileItem.setText ("&File");
        Menu submenu = new Menu (shell, SWT.DROP_DOWN);
        fileItem.setMenu (submenu);
        MenuItem item = new MenuItem (submenu, SWT.PUSH);
        item.addListener (SWT.Selection, new Listener () {
            public void handleEvent (Event e) {
                Runtime.getRuntime().halt(0);
            }
        });
        item.setText ("E&xit\tCtrl+W");
        item.setAccelerator(SWT.MOD1 + 'W');
    }
}
