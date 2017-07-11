/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jeremy Dowdall <jeremyd@aspencloud.com>	- initial API and implementation
 * John Janus	  <j.janus@lighthouse-it.de>	- This snippet
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime.snippets;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CDTSnippet10 {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell( display );
		shell.setText( "CDateTime" );
		shell.setLayout( new GridLayout() );
		
		final String pattern = "EE, dd.MM.yyyy";
		
		final Label lbl1 = new Label( shell, SWT.NONE );
		lbl1.setText( "Date is rolling:" );
		lbl1.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
		
		final CDateTime cdt1 = new CDateTime( shell, CDT.BORDER );
		cdt1.setPattern( pattern );
		cdt1.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
		
		// CDateTimeBuilder builder = CDateTimeBuilder.getStandard();
		// builder.setFooter( Footer.Today().align( SWT.RIGHT ), Footer.Clear().align( SWT.LEFT ) );
		
		final Label lbl2 = new Label( shell, SWT.NONE );
		lbl2.setText( "Date is adding:" );
		lbl2.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
		
		final CDateTime cdt2 = new CDateTime( shell, CDT.BORDER | CDT.ADD_ON_ROLL );
		// cdt2.setBuilder( builder );
		cdt2.setPattern( pattern );
		cdt2.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
		
		final Label lbl3 = new Label( shell, SWT.NONE );
		lbl3.setText( "Select each 'DAY' and press 'UP_ARROW'!" );
		lbl3.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
		
		Calendar cal = new GregorianCalendar();
		cal.set( 2017, 0, 31 );
		cdt1.setSelection( cal.getTime() );
		cdt2.setSelection( cal.getTime() );
		
		shell.pack();
		Point size = shell.getSize();
		Rectangle screen = display.getMonitors()[0].getBounds();
		shell.setBounds(
				( screen.width - size.x ) / 2,
				( screen.height - size.y ) / 2,
				size.x,
				size.y );
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
