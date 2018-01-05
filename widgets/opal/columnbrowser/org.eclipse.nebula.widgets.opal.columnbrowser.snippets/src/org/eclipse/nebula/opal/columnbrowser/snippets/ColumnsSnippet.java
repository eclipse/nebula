/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.opal.columnbrowser.snippets;

import org.eclipse.nebula.widgets.opal.columnbrowser.ColumnBrowserWidget;
import org.eclipse.nebula.widgets.opal.columnbrowser.ColumnItem;
import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.dialog.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates the ColumnBrowser widget
 */
public class ColumnsSnippet {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(2, false));

		final ColumnBrowserWidget cbw = new ColumnBrowserWidget(shell, SWT.NONE);
		cbw.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		final ColumnItem item = createColors(cbw);
		createSports(cbw);

		createShowSelectionButton(shell, cbw);
		createForceSelection(shell, cbw, item);

		shell.setSize(640, 350);
		shell.pack();
		shell.open();
		SWTGraphicUtil.centerShell(shell);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private static ColumnItem createColors(final ColumnBrowserWidget cbw) {
		final ColumnItem root = new ColumnItem(cbw);
		root.setText("Colors");

		final ColumnItem b = new ColumnItem(root);
		b.setText("b");

		final ColumnItem bl = new ColumnItem(b);
		bl.setText("l");

		final ColumnItem blu = new ColumnItem(bl);
		blu.setText("u");

		final ColumnItem blue = new ColumnItem(blu);
		blue.setText("e");

		final ColumnItem r = new ColumnItem(root);
		r.setText("r");

		final ColumnItem re = new ColumnItem(r);
		re.setText("e");

		final ColumnItem red = new ColumnItem(re);
		red.setText("d");

		final ColumnItem ro = new ColumnItem(r);
		ro.setText("o");

		final ColumnItem ros = new ColumnItem(ro);
		ros.setText("s");

		final ColumnItem rose = new ColumnItem(ros);
		rose.setText("e");

		final ColumnItem g = new ColumnItem(root);
		g.setText("g");

		final ColumnItem gr = new ColumnItem(g);
		gr.setText("r");

		final ColumnItem gre = new ColumnItem(gr);
		gre.setText("e");

		final ColumnItem gree = new ColumnItem(gre);
		gree.setText("e");

		final ColumnItem green = new ColumnItem(gree);
		green.setText("n");

		return green;

	}

	private static void createSports(final ColumnBrowserWidget cbw) {
		final ColumnItem root = new ColumnItem(cbw);
		root.setText("Sports");

		final ColumnItem football = new ColumnItem(root);
		football.setText("Football");

		final ColumnItem rugby = new ColumnItem(root);
		rugby.setText("Rugby");

		final ColumnItem handball = new ColumnItem(root);
		handball.setText("Hand Ball");

	}

	private static void createShowSelectionButton(final Shell shell, final ColumnBrowserWidget cbw) {
		final Button button = new Button(shell, SWT.PUSH);
		button.setLayoutData(new GridData(GridData.END, GridData.FILL, true, false));
		button.setText("Show selection");

		button.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Dialog.inform("Selection",
						"You have selected " + (cbw.getSelection() == null ? "nothing" : cbw.getSelection().getText()));
			}
		});

	}

	private static void createForceSelection(final Shell shell, final ColumnBrowserWidget cbw, final ColumnItem item) {
		final Button button = new Button(shell, SWT.PUSH);
		button.setLayoutData(new GridData(GridData.END, GridData.FILL, false, false));
		button.setText("Force selection");

		button.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				cbw.select(item);
			}
		});

	}

}
