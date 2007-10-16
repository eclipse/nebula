/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.widgets.calendarcombo;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CalendarComboTester {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Button Composite Tester");
		shell.setSize(200, 400);
		
		shell.setLayout(new FillLayout());
		Composite inner = new Composite(shell, SWT.None);
		GridLayout gl = new GridLayout(1, true);		
		inner.setLayout(gl);

		Label foo = new Label(inner, SWT.NONE);
		foo.setText("Whatever date is set on this one...");
		final CalendarCombo one = new CalendarCombo(inner, SWT.READ_ONLY);
		Label foo2 = new Label(inner, SWT.NONE);
		foo2.setText("Will be the start for this one...");
		new CalendarCombo(inner, SWT.READ_ONLY, "", true, one);
		new Label(inner, SWT.NONE);
		new CalendarCombo(inner, SWT.NONE);
		new CalendarCombo(inner, SWT.NONE, "Disabled", false);

		one.addCalendarListener(new ICalendarListener() {
			public void popupClosed() {
			}

			public void dateChanged(Calendar date) {
				if (date != null)
					System.err.println(date.getTime().toString());
				else
					System.err.println("null");
			}			
		});
		
		shell.open();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}


}
