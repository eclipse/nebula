/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.duallist.snippets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.duallist.DLItem;
import org.eclipse.nebula.widgets.opal.duallist.DualList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple snipper for the ItemSelector Widget
 *
 */
public class DualListTextSnippet {

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Dual List Snippet");
		shell.setSize(600, 600);
		shell.setLayout(new GridLayout(1, false));

		final DualList dl = new DualList(shell, SWT.NONE);
		dl.setItems(createItems(shell));
		dl.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				System.out.println("Selection Listener called");

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});

		dl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}

	private static List<DLItem> createItems(final Shell shell) {
		final List<DLItem> list = new ArrayList<DLItem>();

		String defaultFontName = null;
		int defaultHeight = -1;
		for (final FontData fontData : shell.getFont().getFontData()) {
			if (defaultFontName == null) {
				defaultFontName = fontData.getName();
			}
			if (defaultHeight == -1) {
				defaultHeight = fontData.getHeight();
			}
		}

		final Font font = new Font(shell.getDisplay(), defaultFontName, defaultHeight, SWT.BOLD);

		list.add(new DLItem("Austria"));
		list.add(new DLItem("Belgium"));
		list.add(new DLItem("Bulgaria"));
		list.add(new DLItem("Cyprus"));
		list.add(new DLItem("Czech Republic"));
		list.add(new DLItem("Denmark"));
		list.add(new DLItem("Estonia"));
		list.add(new DLItem("Finland"));
		list.add(new DLItem("France"));
		list.add(new DLItem("Germany"));
		list.add(new DLItem("Greece"));
		list.add(new DLItem("Hungary"));
		list.add(new DLItem("Ireland"));
		list.add(new DLItem("Italy"));
		list.add(new DLItem("Latvia"));
		list.add(new DLItem("Lithuania"));
		list.add(new DLItem("Luxembourg"));
		list.add(new DLItem("Malta"));
		list.add(new DLItem("Netherlands"));
		list.add(new DLItem("Poland"));
		list.add(new DLItem("Portugal"));
		list.add(new DLItem("Romania"));
		list.add(new DLItem("Slovakia"));
		list.add(new DLItem("Slovenia"));
		list.add(new DLItem("Spain"));
		list.add(new DLItem("Sweden"));
		list.add(new DLItem("United Kingdom"));

		shell.addDisposeListener(e -> {
			font.dispose();
		});

		return list;
	}

}
