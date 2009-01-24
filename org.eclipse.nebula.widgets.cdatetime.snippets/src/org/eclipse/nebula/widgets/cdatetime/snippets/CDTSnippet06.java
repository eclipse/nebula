/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime.snippets;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;



public class CDTSnippet06 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 20;
		layout.marginWidth = 20;
		shell.setLayout(layout);

		final CDateTime cdt1 = new CDateTime(shell, CDT.TIME_MEDIUM | CDT.SIMPLE);
		cdt1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Button b = new Button(shell, SWT.CHECK);
		b.setText("Current Time");
		b.setToolTipText("Keep the clock synchronized with the current system time");
		b.setSelection(true);
		b.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cdt1.setEditable(!((Button) e.widget).getSelection());
				if(cdt1.getEditable()) {
					cdt1.setSelection(new Date());
				}
			}
		});
		
		final CDateTime cdt2 = new CDateTime(shell, CDT.TIME_MEDIUM | CDT.SIMPLE | CDT.CLOCK_24_HOUR);
		cdt2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		b = new Button(shell, SWT.CHECK);
		b.setText("Current Time");
		b.setToolTipText("Keep the clock synchronized with the current system time");
		b.setSelection(true);
		b.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cdt2.setEditable(!((Button) e.widget).getSelection());
				if(cdt2.getEditable()) {
					cdt2.setSelection(new Date());
				}
			}
		});

		final Timer timer = new Timer("Clock Demo");
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if(!display.isDisposed()) {
					display.syncExec(new Runnable() {
						public void run() {
							if(!cdt1.getEditable()) {
								cdt1.setSelection(new Date());
							}
							if(!cdt2.getEditable()) {
								cdt2.setSelection(new Date());
							}
						}
					});
				} else {
					timer.cancel();
				}
			}
		};
		timer.schedule(task, 1000, 1000);
		
		cdt1.setEditable(false);
		cdt2.setEditable(false);
		
		shell.pack();
		Point size = shell.getSize();
		Rectangle screen = display.getMonitors()[0].getBounds();
		shell.setBounds(
				(screen.width-size.x)/2,
				(screen.height-size.y)/2,
				size.x,
				size.y
		);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
