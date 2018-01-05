/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com)
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.heapmanager.snippets;

import org.eclipse.nebula.widgets.opal.heapmanager.HeapManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple snippet for the TextAssist Widget
 */
public class HeapManagerSnippet {
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		new HeapManager(shell, SWT.NONE);

		final int[] counter = new int[1];
		counter[0] = 1;

		display.timerExec(10, new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 10000; i++) {
					@SuppressWarnings("unused")
					final String[] temp = new String[1000];
				}
				counter[0]++;
				if (counter[0] < 100) {
					display.timerExec(10, this);
				}
			}
		});

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
