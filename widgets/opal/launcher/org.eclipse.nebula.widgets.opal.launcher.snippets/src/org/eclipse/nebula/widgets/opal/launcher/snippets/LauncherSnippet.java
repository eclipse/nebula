/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.launcher.snippets;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.dialog.Dialog;
import org.eclipse.nebula.widgets.opal.launcher.Launcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple snippet for the Launcher Widget
 *
 */
public class LauncherSnippet {

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));

		final Label title = new Label(shell, SWT.NONE);
		title.setText("Launcher");
		title.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		final Launcher l = new Launcher(shell, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		l.addItem("Address Book", createImage(shell, "x-office-address-book.png"));
		l.addItem("Calendar", createImage(shell, "x-office-calendar.png"));
		l.addItem("Presentation", createImage(shell, "x-office-presentation.png"));
			l.addItem("Spreadsheet", createImage(shell, "x-office-spreadsheet.png"));

			l.addSelectionListener(new SelectionAdapter() {

				/**
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(final SelectionEvent e) {
					Dialog.inform("Selection", "You have selected item #" + l.getSelection());
				}

			});

			final Label under = new Label(shell, SWT.NONE);
			under.setText("Double-click an icon to launch the program");
			under.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

			shell.setSize(new Point(436, 546));
			shell.open();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
			}
		}
		display.dispose();

	}

	private static Image createImage(Shell shell, String input) {
		final Image img = new Image(shell.getDisplay(), LauncherSnippet.class.getResourceAsStream("icons/" + input));
		shell.addListener(SWT.Dispose, e -> SWTGraphicUtil.safeDispose(img));
		return img;
	}

}
