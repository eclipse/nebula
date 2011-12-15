/****************************************************************************
 * Copyright (c) 2008 Jeremy Dowdall
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


public class EmptySnippet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		final Rectangle screen = display.getMonitors()[0].getBounds();

		shell.setText("Nebula CDateTime");
		shell.setLayout(new GridLayout());

		GridLayout layout = new GridLayout();
		shell.setLayout(layout);

		Button b = new Button(shell, SWT.PUSH);
		b.setText("create CDateTime");
		b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CDateTime cdt = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN);
				cdt.setSelection(new Date());
				cdt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				shell.pack();
				Point size = shell.getSize();
				shell.setBounds(
						(screen.width-size.x)/2,
						(screen.height-size.y)/2,
						size.x,
						size.y
				);
			}
		});
		
		shell.pack();
		Point size = shell.getSize();
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
