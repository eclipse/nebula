/****************************************************************************
 * Copyright (c) 2008, 2019 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *    Stefan Nöbauer - Bug 548149
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime.snippets;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.cdatetime.CDateTimeBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;



public class CDTSnippet11 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("CDateTime");
		shell.setLayout(new GridLayout());

		CDateTimeBuilder builder = CDateTimeBuilder.getStandard();
		builder.setMinDate(Calendar.getInstance());
		
		Calendar max = Calendar.getInstance();
		max.add(Calendar.DAY_OF_MONTH, 5);
		builder.setMaxDate(max);

		final CDateTime cdt1 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN);
		cdt1.setBuilder(builder);
		cdt1.setSelection(new Date());
		cdt1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		
		final CDateTime cdt2 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN);
		cdt2.setBuilder(builder);
		cdt2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		cdt2.setSelection(new Date());
		cdt2.setEditable(false);
		
		final CDateTime cdt3 = new CDateTime(shell, CDT.BORDER | CDT.SIMPLE);
		cdt3.setBuilder(builder);
		cdt3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		cdt3.setEditable(false);
		
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
