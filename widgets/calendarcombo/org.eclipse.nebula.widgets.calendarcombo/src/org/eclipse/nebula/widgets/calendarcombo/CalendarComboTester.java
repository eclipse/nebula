/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
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
		foo.setText("Range Start");
		final CalendarCombo one = new CalendarCombo(inner, SWT.READ_ONLY, true);
		Label foo2 = new Label(inner, SWT.NONE);
		foo2.setText("Range Recipient");				
		CalendarCombo two = new CalendarCombo(inner, SWT.READ_ONLY, one);
		
		// tell combo one that number two will be the recipient of date changes
		one.setDependingCombo(two);
		
		new Label(inner, SWT.NONE);
		new CalendarCombo(inner, SWT.NONE);
		CalendarCombo disabled = new CalendarCombo(inner, SWT.NONE);
		disabled.setEnabled(false);
		disabled.setText("Disabled");

		one.addCalendarListener(new ICalendarListener() {
			public void popupClosed() {
			}

			public void dateChanged(Calendar date) {
				if (date != null)
					System.err.println(date.getTime().toString());
				else
					System.err.println("null");
			}

			public void dateRangeChanged(Calendar start, Calendar end) {
				if (start == null || end == null)
					System.err.println("Null range selected");
				else
					System.err.println("Range selected, from " + start.getTime() + " to " + end.getTime());
			}			
			
			
		});
		
		shell.open();

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}


}
