/****************************************************************************
 * Copyright (c) 2012 Scott Klein
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Scott Klein <scott.klein@goldenhour.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime.snippets;

import java.util.TimeZone;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet shows how to use the timezone features of the CDateTime widget.
 * Please notice that you are able to tab to the timezone field were you can
 * roll through them.
 * 
 */
public class CDTSnippet09 { 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("CDateTime");
		shell.setLayout(new FillLayout());

		// build list of allowed time zones -- since there are over 600 of them
		// we will add US time zones as an example of the developer
		// selectively allowing particular ones using political boundaries
		TimeZone[] timezones = new TimeZone[] {
				TimeZone.getTimeZone("US/Alaska"),
				TimeZone.getTimeZone("US/Hawaii"),
				TimeZone.getTimeZone("US/Pacific"),
				TimeZone.getTimeZone("US/Arizona"),
				TimeZone.getTimeZone("US/Mountain"),
				TimeZone.getTimeZone("US/Central"),
				TimeZone.getTimeZone("US/Eastern") };

		CDateTime dateTime = new CDateTime(shell, SWT.NONE);

		// This *requires* us to set a pattern that *must* include the Zone
		// Offset key (z)
		// otherwise the time zones functionality will be off
		dateTime.setPattern("MM/dd/yyyy HH:mm.ss z", timezones);

		// It is always a good idea to manually set the controls' initial time
		// zone
		// using one of the time zones from your list of allowed time zones.
		// When using the time zone functionality we do not alter the initial
		// time zone
		// in the setPattern( String, TimeZone[]) call
		dateTime.setTimeZone(timezones[0]);

		shell.pack();
		Point size = shell.getSize();
		Rectangle screen = display.getMonitors()[0].getBounds();
		shell.setBounds((screen.width - size.x) / 2,
				(screen.height - size.y) / 2, 180, 54);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}