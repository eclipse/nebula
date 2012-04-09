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

package org.eclipse.nebula.widgets.geomap.snippets;

import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class GeoMapSnippet01 {
	
	public static void main (String [] args) throws Exception {
	    Display display = new Display ();
	    Shell shell = new Shell(display);
	    shell.setText("GeoMap Widget - SWT Native Map Browsing, Map data from openstreetmap.org");
	    shell.setSize(600, 710);
	    shell.setLocation(10, 10);
	    shell.setLayout (new FillLayout());
	    
	    new GeoMap(shell, SWT.NONE);
	    shell.open ();
	    while (! shell.isDisposed ()) {
	        if (! display.readAndDispatch()) {
	        	display.sleep ();
	        }
	    }
	    display.dispose ();
	}
}
